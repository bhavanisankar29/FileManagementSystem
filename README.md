# File Management System

A secure web-based platform for users to upload, manage, and download files with authentication. Built with Spring Boot and modern web technologies.

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Configuration](#configuration)
- [API Endpoints](#api-endpoints)
- [Security](#security)
- [OTP System](#otp-system)

## ✨ Features

### Core Features
- **User Registration** - Create new user accounts with validation
- **Verify user** - OTP-based user verification
- **Login / Logout** - Secure authentication with session management
- **Forgot Password** - OTP-based password recovery
- **File Upload** - Upload files securely to the server
- **File Download** - Download uploaded files with proper authorization
- **File Sharing** - Generate public share links for individual files (token-based, revocable)
- **File Delete** - Delete individual or all files
- **Dashboard** - View all uploaded files with management options
- **Email Notifications** - OTP delivery via Gmail
> **Note:** The uploaded files are stored in the **upload** folder in the project structure.

### Security Features
- **Password Hashing** - BCrypt encryption for all passwords
- **CSRF Protection** - Cross-Site Request Forgery protection enabled
- **Spring Security** - Comprehensive authentication and authorization

## 🛠 Tech Stack

| Component | Technology |
|-----------|-----------|
| **Backend** | Spring Boot |
| **Frontend** | Thymeleaf + HTML/CSS |
| **Database** | MySQL |
| **Security** | Spring Security |
| **Mail Service** | JavaMailSender (Gmail) |
| **Build Tool** | Maven |
| **Java Version** | 21 |

## 📁 Project Structure

```
file-management-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/filemanagement/
│   │   │       ├── config/              # Spring Configuration Classes
│   │   │       ├── controller/          # MVC Controllers
│   │   │       ├── dto/                 # Data Transfer Objects
│   │   │       ├── entity/              # JPA Entity Models
│   │   │       ├── exceptions/          # Custom Exception Classes
│   │   │       ├── repository/          # Database Access Layer
│   │   │       ├── security/            # Spring Security Configuration
│   │   │       └── service/             # Business Logic Layer
│   │   └── resources/
│   │       ├── templates/               # Thymeleaf HTML Templates
│   │       ├── static/                  # CSS/JS/Images
│   │       └── application.properties   # Configuration File
│   └── test/
├── pom.xml                              # Maven Dependencies
└── README.md
```

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK)** - Version 21 or higher
- **Maven** - Version 3.8.0 or higher
- **MySQL Server** - Version 8.0 or higher
- **Git** - For version control
- **Gmail Account** - For OTP email delivery

### Verify Installation

```bash
java -version
mvn -version
mysql --version
```

## 🚀 Installation & Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/bhavanisankar29/FileManagementSystem.git
cd FileManagementSystem
```

### Step 2: Create MySQL Database

```bash
mysql -u root -p
```

Run the following SQL commands:

```sql
CREATE DATABASE file_db;
```

> **Note:** The application will automatically create required tables on first run.

### Step 3: Build the Project

```bash
mvn clean install
```

### Step 4: Run the Application

```bash
mvn spring-boot:run
```

Or if you've already built:

```bash
java -jar target/file-management-system-1.0.0.jar
```

The application will start on **http://localhost:8080**

### Step 5: Access the Application

Open your web browser and navigate to:

```
http://localhost:8080
```

## ⚙️ Configuration

### application.properties Setup

Navigate to `src/main/resources/application.properties` and configure the following:

#### MySQL Configuration
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/file_db
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

#### Mail Configuration (Gmail)
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-google-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

> **Gmail Setup Tips:**
> 1. Enable 2-Step Verification on your Gmail account
> 2. Generate App Password: https://myaccount.google.com/apppasswords
> 3. Use the 16-character password in `spring.mail.password`

#### Server Configuration
```properties
server.port=8000
server.servlet.context-path=/
```

#### File Upload Configuration
```properties
# Maximum file upload size (example: 10MB)
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

## 📡 API Endpoints

All endpoints follow the MVC pattern and serve HTML pages with embedded forms.

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/` | Home page |
| `GET` | `/login` | Login page |
| `POST` | `/login` | Process login (Form submission) |
| `GET` | `/signup` | Sign up registration page |
| `POST` | `/signup` | Process user registration |
| `GET` | `/dashboard` | User's file management dashboard |
| `POST` | `/upload` | Upload a new file |
| `GET` | `/download/{id}` | Download specific file by ID (authenticated) |
| `POST` | `/share/{id}` | Create or refresh a public share link for a file (authenticated) |
| `POST` | `/unshare/{id}` | Revoke/disable a public share link for a file (authenticated) |
| `GET` | `/shared/{token}` | Download a shared file using public share token (public, no auth required) |
| `POST` | `/delete/{id}` | Delete specific file by ID |
| `POST` | `/delete-all` | Delete all user's files |
| `GET` | `/forgot-password` | Forgot password page |
| `POST` | `/forgot-password` | Request password reset OTP |
| `POST` | `/verify-otp` | Verify OTP for password reset |
| `POST` | `/reset-password` | Reset password with new credentials |
| `GET` | `/logout` | User logout (clears session) |
| `POST` | `/resend-otp` | Resend otp to the user's email |


## 🔒 Security

### Password Security
- All passwords are hashed using **BCrypt** algorithm
- Passwords are never stored in plain text
- Minimum password requirements enforced at registration

### CSRF Protection
- CSRF tokens are automatically generated and validated
- Enabled with: `csrf(Customizer.withDefaults())`
- Required for all POST/PUT/DELETE requests

### File Security
- Files are stored securely on the server
- Access control enforced - users can only access their own files
- File IDs are validated before download/deletion
- **Public share links** use randomly generated UUID tokens that are cryptographically secure
- Share links can be revoked by the owner at any time
- Only the owner can create or revoke share links
- Public share downloads do not require authentication but are limited to the specific share token

## 📧 OTP System

### OTP Specifications
- **Length:** 6 digits
- **Expiry Time:** 5 minutes
- **One-Time Use:** OTP is deleted after successful verification
- **Delivery:** Sent via Gmail SMTP

## 🔗 File Sharing System

### Overview
The file sharing feature allows authenticated users to generate public share links for their files. Anyone with the share link can download the file without needing to log in.

### How It Works

#### Sharing a File
1. User logs into the dashboard
2. User clicks the **Share** button next to a file
3. System generates a unique UUID token and stores it in the database
4. A public share link is displayed to the user:
   ```
   http://localhost:8080/shared/{token}
   ```
5. User can copy and share this link with anyone

#### Downloading a Shared File
1. Anyone (authenticated or not) can access the share link
2. The file downloads without requiring login
3. Download is verified against the share token in the database
4. If the token is invalid or the file was unshared, download is denied

#### Revoking a Share Link
1. User clicks the **Unshare** button next to a shared file
2. System immediately clears the share token from the database
3. The previous share link becomes invalid
4. No one can download the file using the old link anymore

### Security Considerations
- Share tokens are randomly generated UUIDs (cryptographically secure)
- Share tokens are unique and indexed in the database
- Only the owner can create or revoke share links
- Deleting a file automatically revokes its share link
- Share links are not listed anywhere; they are only known by the receiver

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.


## 👨‍💻 Author

Bhavani Sankar Katta - https://github.com/bhavanisankar29

