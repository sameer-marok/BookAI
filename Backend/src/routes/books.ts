import {Router} from 'express';
import { authenticate } from "../middleware/auth.js"
import { User } from '../models/User.js';
import { Book } from '../models/Book.js';

const router = Router();

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

        res.json(books)

    } catch (error) {
        console.error("Failed to fetch books", error);

        res.status(500).json({
            message: "Failed to fetch books",
        });
    }
})

// Route to create a new book for the authenticated user
router.post("/", authenticate, async (req, res) => {
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
        // Extract the title of the new book from the request body
        const {title} = req.body

        // Validate the title of the new book
        // Ensure the title is a non-empty string
        if (typeof title !== "string" || !title.trim()) {
            return res.status(400).json({
                message: "Title is required",
            });
        }
        // Create a new book associated with the user
        const book = await Book.create({
            userId: user._id,
            title
        })

        res.status(201).json({
            book: {
                id: book._id,
                title: book.title,
            },
        });

    } catch (error) {
        console.error("Failed to create book", error);

        res.status(500).json({
            message: "Failed to create book",
        });
    }
})

export default router