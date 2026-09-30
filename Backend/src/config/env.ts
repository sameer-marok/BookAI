function getEnv(name: string): string {
    const value = process.env[name];

    if (!value) {
        throw new Error(`Missing environment variable: ${name}`);
    }

    return value;
}

export const env = {
    // we did not use getEnv for the port
    // because it is optional and has a default value of 3000
    port: Number(process.env.PORT) || 3000,

    firebase: {
        projectId: getEnv("FIREBASE_PROJECT_ID"),
        clientEmail: getEnv("FIREBASE_CLIENT_EMAIL"),
        privateKey: getEnv("FIREBASE_PRIVATE_KEY").replace(/\\n/g, "\n"),
    },
    mongodb: {
        uri: getEnv("MONGODB_URI"),
    },
};