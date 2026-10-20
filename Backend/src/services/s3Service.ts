import { PutObjectCommand } from "@aws-sdk/client-s3";
import { s3 } from "../config/s3.js";
import { env } from "../config/env.js";
// Type definition for the parameters required to upload a PDF to S3
type UploadPdfParams = {
    fileKey: string;
    buffer: Buffer;
    contentType: string;
}

// Upload a PDF file to the configured AWS S3 bucket
export async function uploadPdf({
    fileKey,
    buffer,
    contentType,
}: UploadPdfParams): Promise<void> {
    await s3.send(
        new PutObjectCommand({ // Create a new command to upload an object to S3
            Bucket: env.aws.s3Bucket, // The name of the S3 bucket where the PDF will be uploaded
            Key: fileKey, // The key under which the PDF will be stored in the S3 bucket
            Body: buffer, // The content of the PDF file to be uploaded
            ContentType: contentType, // The MIME type of the PDF file
        }),
    );
}
