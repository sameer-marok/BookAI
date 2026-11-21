import mongoose from "mongoose";

const bookChunkSchema = new mongoose.Schema(
    {
        bookId: {
            type: mongoose.Schema.ObjectId,
            ref: "Book",
            required: true
        },
        userId: {
            type: mongoose.Schema.Types.ObjectId,
            ref: "User",
            required: true,
        },
        chunkIndex: {
            type: Number,
            required: true,
        },
        text: {
            type: String,
            required: true,
        },
        embedding: {
            type: [Number],
            required: true,
        },
    },
    {
        timestamps: true
    }
);
// Create a compound index to ensure unique chunk indices for each book
bookChunkSchema.index(
    { bookId: 1, chunkIndex: 1 },
    { unique: true }
);

export const BookChunk = mongoose.model("BookChunk", bookChunkSchema);