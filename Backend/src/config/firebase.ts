import { cert, initializeApp } from "firebase-admin/app";
import { env } from "./env";

// Firebase service account configuration
 const projectId = env.firebase.projectId;
 const clientEmail = env.firebase.clientEmail;
 const privateKey = env.firebase.privateKey;
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