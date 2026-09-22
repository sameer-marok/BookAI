import { cert, initializeApp } from "firebase-admin/app";

// Firebase service account configuration
 const projectId = process.env.FIREBASE_PROJECT_ID;
 const clientEmail = process.env.FIREBASE_CLIENT_EMAIL;
 const privateKey = process.env.FIREBASE_PRIVATE_KEY;
 if (!projectId || !clientEmail || !privateKey) {
     throw new Error("Firebase credentials are not configured");
 }
const serviceAccount = {
    projectId,
    clientEmail,
    privateKey: privateKey?.replace(/\\n/g, "\n"),
};

// Initialize Firebase app with service account credentials
initializeApp({
    credential: cert(serviceAccount),
});