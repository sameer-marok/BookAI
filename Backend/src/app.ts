import express from "express";
import healthRouter from "./routes/health.js"
import authRouter from "./routes/auth.js"
import bookRouter from "./routes/books.js"
import "./config/firebase.js"; // Initialize Firebase configuration

const app = express();

// Middleware to parse JSON request bodies
app.use(express.json());

app.use("/api", healthRouter);
app.use("/api/auth", authRouter);
app.use("/api/books", bookRouter)

export default app;