import {Router} from "express"
import { authenticate } from "../middleware/auth.js"
import { findOrCreateUser } from "../services/userService.js";

const router = Router()

router.post("/session", authenticate, async (req, res) => {
    try {
        const email = req.user!.email!;
        // Ensure the email is available
        if (!email) {
            return res.status(400).json({
                message: "Authenticated user email is required",
            });
        }

        // Find or create the user in the database
        const user = await findOrCreateUser(
            req.user!.firebaseUid,
            email
        );

        res.json({
            user,
        });
    } catch (error) {
        console.error("Failed to create/find user", error);

        res.status(500).json({
            message: "Failed to process user",
        });
    }
})

export default router