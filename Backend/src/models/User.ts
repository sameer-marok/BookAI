import mongoose from "mongoose";
// define the schema for the User model
const userSchema = new mongoose.Schema(
    {
        firebaseUid: {
            type: String,
            required: true,
            unique: true,
        },
        email: {
            type: String,
            required: true,
        },
    },
    // schema options
    {
        timestamps: true
    }
)
// create and export the User model based on the userSchema
export const User = mongoose.model("User", userSchema)