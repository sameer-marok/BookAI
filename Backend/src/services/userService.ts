import {User} from "../models/User"

export async function findOrCreateUser(
    firebaseUid:string,
    email: string
) {
    // Try to find an existing user by their Firebase UID
    // or create a new one if it doesn't exist
    const user = await User.findOneAndUpdate(
        { firebaseUid }, // Filter by Firebase UID
        { $setOnInsert: { firebaseUid, email } }, // Insert if not found
        {
            new: true, // Return the updated document
            upsert: true, // Create a new document if none exists
            runValidators: true, // Validate before updating
        }
    );

    return user
}