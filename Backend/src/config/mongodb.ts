import mongoose from "mongoose";
import {env} from "./env.js"

export async function connectToDatabase() {
    try {
        // Connect to the MongoDB database
        await mongoose.connect(env.mongodb.uri)

        console.log("MongoDB connected")
    } catch (error) {
        console.error("MongoDB connection failed", error);
        // Exit the process with a failure code
        process.exit(1);
    }
}