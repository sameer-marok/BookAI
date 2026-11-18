
const CHUNK_SIZE = 1000;
const CHUNK_OVERLAP = 150;
// Function to split text into chunks with a specified size and overlap
// Preferring paragraph and word boundaries 
export function chunkText(text: string): string[] {
    if (CHUNK_OVERLAP >= CHUNK_SIZE) {
        throw new Error("Chunk overlap must be smaller than chunk size");
    }

    const chunks: string[] = [];
    let start = 0;

    while (start < text.length) {
        let end = Math.min(start + CHUNK_SIZE, text.length);

        if (end < text.length) {
            // Prefer a paragraph boundary if one is reasonably close
            const paragraphBreak = text.lastIndexOf("\n\n", end);

            if (paragraphBreak > start + CHUNK_SIZE / 2) {
                end = paragraphBreak;
            } else {
                // Otherwise, find the last word boundary
                const wordBreak = text.lastIndexOf(" ", end);

                if (wordBreak > start + CHUNK_OVERLAP) {
                    end = wordBreak;
                }
            }
        }

        const chunk = text.slice(start, end).trim();

        if (chunk) {
            chunks.push(chunk);
        }

        if (end >= text.length) {
            break;
        }

        // Start the next chunk with overlapping text
        start = end - CHUNK_OVERLAP;

        // Avoid starting in the middle of a word
        const nextSpace = text.indexOf(" ", start);

        if (nextSpace !== -1 && nextSpace < end) {
            start = nextSpace + 1;
        }
    }

    return chunks;
}
