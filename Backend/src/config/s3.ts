import { S3Client } from "@aws-sdk/client-s3";
import { env } from "./env.js";

// Initialize the AWS S3 client with credentials
export const s3 = new S3Client({
    region: env.aws.region,
    credentials: {
        accessKeyId: env.aws.accessKeyId,
        secretAccessKey: env.aws.secretAccessKey,
    },
});