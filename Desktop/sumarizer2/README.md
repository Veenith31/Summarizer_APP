# 🧠 GenAI-powered Summarization Tool

An AI-powered web application that summarizes long-form content using Gemini API, and allows contextual searching through past summaries.

---

## 🚀 Features

- 📄 Summarize text and PDF documents using Gemini API
- 🔍 Search summaries by keywords or previous context
- 🎙️ Voice-to-text input (in progress)
- 🖼️ Image input support (in progress)
- 📂 MongoDB storage for history & queries
- 🌐 Clean React frontend + Spring Boot backend

---

## 🛠️ Tech Stack

| Layer        | Tech                     |
|--------------|--------------------------|
| Frontend     | React.js, HTML, CSS      |
| Backend      | Spring Boot (Java)       |
| AI API       | Google Gemini API        |
| Database     | MongoDB                  |
| PDF Parsing  | Apache PDFBox            |
| Voice        | Google Speech-to-Text (planned) |

---

## 📁 Project Structure

summarizer2/
├── backend/
│ └── src/main/java/com/summarizer/project/
│ ├── controller/
│ ├── config/
│ ├── service/
│ ├── dto/
│ ├── model/
│ ├── repository/
├── frontend/
│ └── summarizer-frontend/
│ ├── src/
│ ├── public/
├── pom.xml
├── .gitignore
└── README.md
