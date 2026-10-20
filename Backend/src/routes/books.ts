import {Router} from 'express';
import { authenticate } from "../middleware/auth.js"
import { User } from '../models/User.js';
import { Book } from '../models/Book.js';
import multer from "multer";
import { randomUUID } from "node:crypto";
import { DeleteObjectCommand } from "@aws-sdk/client-s3";
import { uploadPdf } from "../services/s3Service.js";
import { s3 } from "../config/s3.js";
import { env } from "../config/env.js";

const router = Router();

// Configure multer for handling file uploads in memory
const upload = multer({
    storage: multer.memoryStorage(), // Store uploaded files in memory as Buffer objects
    limits: {
        fileSize: 20 * 1024 * 1024, // 20 MB
        files: 1,
    },
});

// Route to fetch all books associated with the authenticated user
router.get("/", authenticate, async (req, res) => {
    try {
        // Find the user associated with the authenticated request
        const user = await User.findOne({
            firebaseUid: req.user?.firebaseUid
        })

        if (!user) {
            return res.status(404).json({
                message: "User not found"
            })
        }

        // Fetch all books associated with the user
        const books = await Book.find({
            userId: user._id // Filter books by the user's ID (MongoDB ObjectId)
        })

        // Respond with the list of books, mapping each book to include only the id and title
        res.json(books.map(book => ({
            id: book._id.toString(),
            title: book.title,
        })))

    } catch (error) {
        console.error("Failed to fetch books", error);

        res.status(500).json({
            message: "Failed to fetch books",
        });
    }
})

// Route to upload a PDF for a specific book
router.post("/upload", authenticate, upload.single("file"), async (req, res) => {
    let fileKey: string | undefined;
    let uploadedToS3 = false;

    try {
        const { title } = req.body;
        const file = req.file; // file type is coming from multer

        if (typeof title !== "string" || !title.trim()) {
            return res.status(400).json({
                message: "Title is required",
            });
        }

        if (!file) {
            return res.status(400).json({
                message: "PDF file is required",
            });
        }

        // Basic PDF validation
        if (
            file.mimetype !== "application/pdf" ||
            file.buffer.subarray(0, 5).toString() !== "%PDF-" // Check the PDF file header for validity
        ) {
            return res.status(400).json({
                message: "A valid PDF file is required",
            });
        }

        const user = await User.findOne({
            firebaseUid: req.user?.firebaseUid,
        });

        if (!user) {
            return res.status(404).json({
                message: "User not found",
            });
        }

        // Generate a unique key for this user's PDF
        fileKey = `users/${user._id}/books/${randomUUID()}.pdf`;

        await uploadPdf({
            fileKey,
            buffer: file.buffer,
            contentType: "application/pdf",
        });

        uploadedToS3 = true;

        // Save metadata only after S3 upload succeeds
        const book = await Book.create({
            userId: user._id,
            title: title.trim(),
            fileKey,
        });

        return res.status(201).json({
            id: book._id.toString(),
            title: book.title,
        });
    } catch (error) {
        console.error("Failed to upload book", error);

        // Avoid leaving an orphaned PDF if MongoDB creation fails
        if (uploadedToS3 && fileKey) {
            try {
                await s3.send(
                    new DeleteObjectCommand({
                        Bucket: env.aws.s3Bucket,
                        Key: fileKey,
                    }),
                );
            } catch (cleanupError) {
                console.error("Failed to clean up S3 object", cleanupError);
            }
        }

        return res.status(500).json({
            message: "Failed to upload book",
        });
    }
});


export default router