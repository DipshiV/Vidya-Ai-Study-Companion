Vidya AI Study Companion - Learning Portal
A comprehensive role-based learning portal built for Teachers, Students, and Admins with secure login, file uploads, AI-based summary generation, translation to native languages, and management features.


Features
🔐 Authentication & Authorization
JWT-based authentication
Role-based access control (STUDENT, TEACHER, ADMIN)
Secure token storage in localStorage
Automatic redirection based on user role

👨‍🎓 Student Dashboard
View All Summaries: Display summaries with language toggle (English, Hindi, Marathi, Gujarati)
Generate Summary: Select material and language to generate AI-powered summaries

Delete Summary: Remove unwanted summaries
Language Support: Multi-language summary generation and viewing

👨‍🏫 Teacher Dashboard
Upload Study Material: Upload PDF files with title and tag selection
View Uploaded Materials: Display all uploaded materials with metadata
Material Management: Track uploaded materials with timestamps

🛠️ Admin Dashboard
Tag Management: Add and delete tags for material categorization
User Management: View and manage users (optional feature)
System Administration: Full administrative control
Technologies Used
Frontend: React.js with React Router DOM
Styling: Bootstrap CSS
HTTP Client: Axios
Authentication: JWT with Bearer token
State Management: React Hooks (useState, useEffect)
API Endpoints
Authentication
POST /api/auth/login - User login
Student APIs
GET /summary/student/{studentId}/all - Get all student summaries
POST /summary/generate - Generate new summary
DELETE /summary/delete/{summaryId} - Delete summary
Teacher APIs
GET /material/teacher/{teacherId} - Get teacher's materials
POST /material/upload/{teacherId} - Upload new material
Admin APIs
GET /tags/all - Get all tags
POST /tags/add - Add new tag
DELETE /tags/delete/{tagId} - Delete tag
GET /users/all - Get all users (optional)
DELETE /users/delete/{userId} - Delete user (optional)
Setup Instructions
Prerequisites
Node.js (v14 or higher)
npm or yarn
Installation
Clone the repository


git clone <repository-url>
cd learning-portal
Install dependencies

npm install
Configure API Base URL

Open src/services/authService.js
Update API_BASE_URL to point to your backend server
Default: http://localhost:8080/api
Start the development server

npm start
Open the application

Navigate to http://localhost:3000
The app will automatically redirect to the login page
Demo Credentials
For testing purposes, you can use these demo credentials:


Student: student1 / password
Teacher: teacher1 / password
Admin: admin1 / password
Project Structure
src/
├── components/
│   ├── Login.js              # Login component
│   ├── StudentDashboard.js   # Student dashboard
│   ├── TeacherDashboard.js   # Teacher dashboard
│   ├── AdminDashboard.js     # Admin dashboard
│   ├── ProtectedRoute.js     # Route protection
│   └── Navbar.js            # Navigation bar
├── services/
│   ├── authService.js        # Authentication service
│   └── apiService.js         # API service
├── App.js                    # Main app component
└── App.css                   # Custom styles

Key Features

🔒 Security
JWT token authentication
Role-based route protection
Automatic token refresh handling
Secure logout functionality

📱 Responsive Design
Mobile-friendly interface
Bootstrap-based responsive layout
Touch-friendly navigation

🌐 Multi-language Support
Summary generation in multiple languages
Language toggle for viewing summaries
Support for English, Hindi, Marathi, and Gujarati

📁 File Management
PDF file upload for study materials
File validation and error handling
Secure file storage integration

🎨 Modern UI/UX
Clean and intuitive interface
Loading states and error handling
Success/error notifications
Confirmation dialogs for destructive actions
Backend Requirements
The frontend expects a backend API with the following specifications:

Authentication Response Format
{
  "token": "jwt_token_here",
  "userId": "user_id",
  "username": "username",
  "role": "STUDENT|TEACHER|ADMIN"
}
Summary Response Format
{
  "id": "summary_id",
  "title": "Summary Title",
  "summary": "Summary content",
  "materialTitle": "Material Title",
  "language": "en|hi|mr|gu"
}
Material Response Format
{
  "id": "material_id",
  "title": "Material Title",
  "fileName": "file.pdf",
  "tagName": "Tag Name",
  "uploadedDate": "2024-01-01T00:00:00Z"
}
Development
Available Scripts
npm start - Start development server
npm test - Run tests
npm run build - Build for production
npm run eject - Eject from Create React App
Environment Variables
Create a .env file in the root directory:

REACT_APP_API_BASE_URL=http://localhost:8080/api
Contributing
Fork the repository
Create a feature branch
Make your changes
Add tests if applicable
Submit a pull request
License
This project is licensed under the MIT License.

Support
For support and questions, please open an issue in the repository.
