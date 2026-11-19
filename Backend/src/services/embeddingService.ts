import OpenAI from "openai";
import { env } from "../config/env.js";

// Service to handle OpenAI embeddings
const openai = new OpenAI({
    apiKey: env.openai.apiKey,
});

// Function to generate embeddings for an array of chunks using OpenAI's API
export async function generateEmbeddings(
    texts: string[]
): Promise<number[][]> {
    
    // Process the texts in batches to avoid hitting API limits and improve efficiency
    const BATCH_SIZE = 100;
    const embeddings: number[][] = [];

    for (let i = 0; i < texts.length; i += BATCH_SIZE) {
        const batch = texts.slice(i, i + BATCH_SIZE);

        const response = await openai.embeddings.create({
            model: "text-embedding-3-small", // Using the smaller embedding model for efficiency
            input: batch, // Array of text chunks to generate embeddings for
            encoding_format: "float", // Ensure the embeddings are returned as float arrays
        });

        // response.data is an array of embedding objects, each containing index and embedding vector
        const batchEmbeddings = response.data
            .sort((a, b) => a.index - b.index) // Sort the embeddings by their original index
            .map(item => item.embedding); // Extract the embedding vector from each object

        embeddings.push(...batchEmbeddings); // Add the batch embeddings to the overall embeddings array
    }

    return embeddings;
}