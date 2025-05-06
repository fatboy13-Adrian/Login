# User Login API with Role-Based Access Control

This is a Spring Boot backend application that provides user authentication and authorization using Spring Security. It supports different user roles such as `ADMIN`, `CUSTOMER`, `SALES_CLERK`, and `WAREHOUSE_SUPERVISOR`, and is designed to integrate with a frontend like React.

## Features
- ✅ User login using Spring Security
- ✅ Role-based access control
- ✅ Secure password hashing with Spring's PasswordEncoder
- ✅ Session-based authentication (form login)
- ✅ Custom success handler with role-based redirection/messages
- ✅ Integration-ready with frontend applications (e.g., React)

## Technologies Used
- Java 17+
- Spring Boot
- Spring Security
- Maven
- Jakarta Servlet API
- (Optional) MySQL / H2 for user data storage

### Prerequisites
- Java JDK 17+
- Maven
- Git

### Installation
1. **Clone the repository:**
```bash
git clone https://github.com/fatboy13-Adrian/Login.git
cd Login

Build the application:

bash
Copy
Edit
mvn clean install
Run the application:

bash
Copy
Edit
mvn spring-boot:run
Access the application:

Open http://localhost:8080 in your browser.

API Endpoints Overview
POST /login – Authenticates user

GET /admin/** – Admin-only access

GET /orders/** – Accessible to SALES_CLERK, WAREHOUSE_SUPERVISOR

GET /carts/**, /get-product, etc. – Customer-only access

Authentication
🔐 Session-based authentication is used by default via Spring Security's form login.

To switch to JWT, update security configuration and replace session handling with stateless token validation.

Future Enhancements
🔄 Migrate to JWT for stateless APIs (Compulsary)

🌐 Add CORS configuration for React frontend (Compulsary)

📦 Dockerize backend for deployment (Compulsary)

📄 Swagger/OpenAPI integration for API docs (Optional)