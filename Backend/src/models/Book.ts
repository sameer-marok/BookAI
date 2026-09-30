import mongoose from "mongoose";

// define the schema for the Book model
const bookSchema = new mongoose.Schema(
    {
        // reference to the user who owns the book
        userId: {
            // the type of the userId field is ObjectId,
            //  referencing the User model
            type: mongoose.Schema.Types.ObjectId,
            ref: "User",
            required: true
        },

        title: {
            type: String,
            required: true
        }
    },
    {
        timestamps: true
    }
)
// create and export the Book model based on the bookSchema
export const Book = mongoose.model("Book", bookSchema);