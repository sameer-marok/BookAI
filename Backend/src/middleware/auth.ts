import { NextFunction, Request, Response } from "express";
import { getAuth } from "firebase-admin/auth";

// Middleware to authenticate requests using Firebase ID tokens
export async function authenticate(
    req: Request,
    res: Response,
    // Callback function to pass control to the next middleware
    next: NextFunction 
) {
    try {
        // Get the Authorization header from the request
        const authHeader = req.headers.authorization
        
        if (!authHeader?.startsWith("Bearer ")) {
            return res.status(401).json({
                message: "Authentication required",
            });
        }

        // Extract the ID token from the Authorization header
        const idToken = authHeader.split("Bearer ")[1];
        // Verify the ID token using Firebase Admin SDK
        const decodedToken = await getAuth().verifyIdToken(idToken);
        // use the decoded token to attach user information to the request
        req.user = {
            uid: decodedToken.uid,
            email: decodedToken.email,
        };
        // Pass control to the next middleware function
        next();
    } 
    catch (error) {
        console.error("Authentication error:", error);
        return res.status(401).json({
            message: "Invalid authentication token",
        });
    }
}