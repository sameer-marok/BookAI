# BookAI

BookAI is an Android application that lets users upload books and have AI-powered conversations about their content.

The project is being built to explore modern Android development, backend development, and RAG-based AI systems.

## Tech Stack

### Android
- Kotlin
- Jetpack Compose
- MVVM
- Firebase Authentication
- Retrofit
- Coroutines / Flow

### Backend
- Node.js
- Express
- TypeScript
- MongoDB
- Redis / BullMQ
- Firebase Admin

### AI
- Embeddings
- Vector Search
- Gemini
- RAG (Retrieval-Augmented Generation)

### Planned
- Vapi for voice interactions

## Architecture

```text
Android App
    ↓
Express API
    ↓
Firebase Authentication
    ↓
MongoDB
    ↓
PDF Processing
    ↓
Embeddings + Vector Search
    ↓
Gemini
