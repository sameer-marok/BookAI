import { PutObjectCommand } from "@aws-sdk/client-s3";
import { getSignedUrl } from "@aws-sdk/s3-request-presigner";

import { s3 } from "../config/s3.js";
import { env } from "../config/env.js";

type CreateUploadUrlParams = {
    fileKey: string;
};

// Returns a signed URL for uploading a file to S3 with the specified file key
export async function createUploadUrl({
    fileKey,
}: CreateUploadUrlParams): Promise<string> {

    const command = new PutObjectCommand({
        Bucket: env.aws.s3Bucket,
        Key: fileKey,
        ContentType: "application/pdf",
    });

    return getSignedUrl(s3, command, {
        // URL expires in 5 minutes
        expiresIn: 300,
    });
}
