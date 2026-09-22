import {Router} from "express"
import { authenticate } from "../middleware/auth.js"

const router = Router()

router.get("/health", (req, res) => {
    res.json({ status: "ok" });
});

// Protected route that requires authentication
router.get("/protected", authenticate, (req, res) => {
    res.json({
        message: "You are authenticated",
        user: req.user
    })
})

export default router