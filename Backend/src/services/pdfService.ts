
import { GetObjectCommand } from "@aws-sdk/client-s3";
import { PDFParse } from "pdf-parse";
import { s3 } from "../config/s3.js";
import { env } from "../config/env.js";

const MAX_PDF_SIZE = 20 * 1024 * 1024;

// Service to extract text from a PDF stored in S3
export async function extractPdfText(fileKey: string): Promise<string> {
    // 1. Download PDF from S3
    const response = await s3.send(
        new GetObjectCommand({
            Bucket: env.aws.s3Bucket,
            Key: fileKey,
        })
    );

    if (!response.Body) {
        throw new Error("PDF file not found in S3");
    }

    // Check if the PDF size exceeds the maximum allowed size
    if (response.ContentLength && response.ContentLength > MAX_PDF_SIZE) {
        throw new Error("PDF file exceeds the maximum allowed size");
    }

    // 2. Convert S3 response into a Buffer
    const pdfBuffer = Buffer.from(
        await response.Body.transformToByteArray()
    );

    // 3. Extract text
    const parser = new PDFParse({ data: pdfBuffer });

    try {
        const result = await parser.getText();
        return result.text;
    } finally {
        // Release parser resources
        await parser.destroy();
    }
}
