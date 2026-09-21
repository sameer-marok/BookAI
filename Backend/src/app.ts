import express from "express";
import healthRouter from "./routes/health.js"

const app = express();

// Middleware to parse JSON request bodies
app.use(express.json());

app.use("/api", healthRouter);

export default app;