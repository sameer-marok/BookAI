import OpenAI from "openai";
import { env } from "../config/env.js";
import { generateEmbeddings } from "./embeddingService.js";
import { searchBookChunks } from "./vectorSearchService.js";

const openai = new OpenAI({
    apiKey: env.openai.apiKey,
});

// Service for answering questions about books using retrieved relevant chunks and OpenAI's GPT model
export async function answerBookQuestion(
    question: string,
    bookId: string
): Promise<string> {

    // 1. Generate embedding for the user's question
    const [queryEmbedding] = await generateEmbeddings([question]);

    // 2. Retrieve relevant chunks from the selected book
    const relevantChunks = await searchBookChunks(bookId, queryEmbedding);

    if (relevantChunks.length === 0) {
        return "I couldn't find relevant information in this book to answer your question.";
    }

    console.log("Retrieved chunks:", relevantChunks);

    // 3. Combine retrieved chunks into context
    const context = relevantChunks
        .map(chunk => chunk.text)
        .join("\n\n");

    // 4. Generate an answer using the retrieved context
    const response = await openai.responses.create({
        model: "gpt-5-mini",
        instructions: `
            You are an assistant that answers questions about books.
            Answer the user's question using only the provided book context.
            If the context does not contain enough information to answer,
            politely explain that you couldn't find the answer in the book.

            Do not:
            - Ask the user to provide or share more text.
            - Mention "provided context", "provided excerpts", or retrieved chunks.
            - Invent information or make assumptions to fill gaps.

            Keep answers natural, concise, and conversational.
        `,
        input: `
            Book context:
            ${context}

            User question:
            ${question}
        `,
    });

    return response.output_text;
}