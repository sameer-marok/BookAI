import type { ErrorRequestHandler } from "express";
import multer from "multer";

export const errorHandler: ErrorRequestHandler = (err, req, res, next) => {
    if (err instanceof multer.MulterError) {
        if (err.code === "LIMIT_FILE_SIZE") {
            res.status(413).json({
                message: "PDF must be smaller than 20 MB",
            });
            return;
        }

        res.status(400).json({
            message: "Invalid file upload",
        });
        return;
    }

    console.error("Unhandled error:", err);

    res.status(500).json({
        message: "Internal server error",
    });
};