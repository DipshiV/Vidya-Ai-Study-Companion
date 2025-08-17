📚 Vidya AI Study Companion - Learning Portal

A comprehensive role-based learning portal built for Teachers, Students, and Admins with secure login, file uploads, AI-powered summary generation, multi-language translation, and management features.

✨ Features
🔐 Authentication & Authorization

JWT-based authentication

Role-based access control (STUDENT, TEACHER, ADMIN)

Secure token storage in localStorage

Automatic redirection based on user role

👨‍🎓 Student Dashboard

View All Summaries: Display summaries with language toggle (English, Hindi, Marathi, Gujarati)

Generate Summary: Select material and language to generate AI-powered summaries

Delete Summary: Remove unwanted summaries

Multi-Language Support: Summaries available in multiple languages

👨‍🏫 Teacher Dashboard

Upload Study Material: Upload PDF files with title and tag selection

View Uploaded Materials: Display all uploaded materials with metadata

Material Management: Track uploaded materials with timestamps

🛠️ Admin Dashboard

Tag Management: Add and delete tags for categorization

User Management: View and manage users (optional)

System Administration: Full administrative control

🛠 Technologies Used

Frontend: React.js + React Router DOM

Styling: Bootstrap CSS

HTTP Client: Axios

Authentication: JWT with Bearer token

State Management: React Hooks (useState, useEffect)

🌐 API Endpoints
🔑 Authentication

POST /api/auth/login → User login

👨‍🎓 Student APIs

GET /summary/student/{studentId}/all → Get all summaries

POST /summary/generate → Generate new summary

DELETE /summary/delete/{summaryId} → Delete summary

👨‍🏫 Teacher APIs

GET /material/teacher/{teacherId} → Get teacher’s materials

POST /material/upload/{teacherId} → Upload new material

🛠️ Admin APIs

GET /tags/all → Get all tags

POST /tags/add → Add new tag

DELETE /tags/delete/{tagId} → Delete tag

GET /users/all → Get all users (optional)

DELETE /users/delete/{userId} → Delete user (optional)

⚡ Setup Instructions
🔧 Prerequisites

Node.js (v14 or higher)

npm or yarn

🚀 Installation
# Clone the repository
git clone <repository-url>
cd learning-portal

# Install dependencies
npm install

# Configure API Base URL
# Open src/services/authService.js and update:
API_BASE_URL = "http://localhost:8080/api"

# Start the development server
npm start


Open 👉 http://localhost:3000

👥 Demo Credentials
Role	Username	Password
Student	student1	password
Teacher	teacher1	password
Admin	admin1	password
📂 Project Structure
src/
├── components/
│   ├── Login.js              # Login component
│   ├── StudentDashboard.js   # Student dashboard
│   ├── TeacherDashboard.js   # Teacher dashboard
│   ├── AdminDashboard.js     # Admin dashboard
│   ├── ProtectedRoute.js     # Route protection
│   └── Navbar.js             # Navigation bar
├── services/
│   ├── authService.js        # Authentication service
│   └── apiService.js         # API service
├── App.js                    # Main app component
└── App.css                   # Custom styles

🔑 Key Features
🔒 Security

JWT authentication

Role-based route protection

Secure logout functionality

📱 Responsive Design

Mobile-friendly interface

Bootstrap responsive layout

🌐 Multi-Language Support

AI-powered summaries in English, Hindi, Marathi, Gujarati

Toggle to switch language

📁 File Management

PDF upload with validation

Error handling & secure storage

🎨 Modern UI/UX

Clean & intuitive interface

Success/error notifications

Confirmation dialogs

📦 Backend Requirements
🔑 Authentication Response
{
  "token": "jwt_token_here",
  "userId": "user_id",
  "username": "username",
  "role": "STUDENT|TEACHER|ADMIN"
}

📄 Summary Response
{
  "id": "summary_id",
  "title": "Summary Title",
  "summary": "Summary content",
  "materialTitle": "Material Title",
  "language": "en|hi|mr|gu"
}

📂 Material Response
{
  "id": "material_id",
  "title": "Material Title",
  "fileName": "file.pdf",
  "tagName": "Tag Name",
  "uploadedDate": "2024-01-01T00:00:00Z"
}

⚙️ Development Scripts
npm start       # Start development server
npm test        # Run tests
npm run build   # Build for production
npm run eject   # Eject from CRA

🌍 Environment Variables

Create a .env file:

REACT_APP_API_BASE_URL=http://localhost:8080/api

🤝 Contributing

Fork the repo

Create a feature branch

Commit your changes

Push & open a Pull Request

📜 License

This project is licensed under the MIT License.

💬 Support

For issues, please open a GitHub issue in this repository.
