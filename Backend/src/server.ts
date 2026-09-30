import "dotenv/config";
import app from "./app.js";
import { env } from "./config/env.js";
import { connectToDatabase } from "./config/mongodb.js";

async function startServer() {
    // connect to the MongoDB database
    await connectToDatabase();

    // start the server after db connection is established
    app.listen(env.port, () => {
        console.log(`Server running on http://localhost:${env.port}`);
    });
}

startServer();