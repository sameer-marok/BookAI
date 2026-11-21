import mongoose from "mongoose";
import { BookChunk } from "../models/BookChunk.js";

export async function searchBookChunks(
    bookId: string,
    queryEmbedding: number[]
) {
    const results = await BookChunk.aggregate([
        {   // Perform a vector search on the book chunks using the provided query embedding
            $vectorSearch: {
                index: "bookchunk_vector_index", // The name of the vector index to use
                path: "embedding", // The field in the document that contains the vector embeddings
                queryVector: queryEmbedding, // The query vector to search against the index
                numCandidates: 100, // The number of candidate vectors to consider during the search
                limit: 5, // The maximum number of search results to return
                filter: { // Filter the search results to only include chunks from the specified book
                    bookId: new mongoose.Types.ObjectId(bookId),
                },
            },
        },
            // Project the relevant fields and include the vector search score in the results
        {
            $project: {
                _id: 0, // Exclude the MongoDB document ID from the projected results
                text: 1, // Include the text of the book chunk
                chunkIndex: 1, // Include the chunk index in the projected results
                score: { $meta: "vectorSearchScore" }, // Include the vector search score in the projected results
            },
        },
    ]);

    return results;
}