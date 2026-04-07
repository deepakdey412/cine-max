# Movie Booking Platform

Production-grade Movie Ticket Reservation Platform with Spring Boot & React.

## 🚀 Quick Setup

### Prerequisites
- Java 17+, Maven 3.6+, MySQL 8.0+, Node.js 18+

### 1. Database Setup
```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE movie_booking;
exit;
```

### 2. Backend Setup
```bash
cd backend

# Update MySQL credentials in: src/main/resources/application.properties
# Change: spring.datasource.username and spring.datasource.password

# Run backend
mvn spring-boot:run
```
**Backend runs at:** http://localhost:8080  
**Swagger API Docs:** http://localhost:8080/swagger-ui/index.html

### 3. Frontend Setup
```bash
cd frontend
npm install
npm run dev
```
**Frontend runs at:** http://localhost:5173

## 🔐 Login Credentials

**Admin:** admin@moviebooking.com / Admin@123  
**User:** Register new account or use seeded data

## 📚 Features

- JWT Authentication & Role-based Access (USER/ADMIN)
- Movie & Showtime Management (Admin)
- Interactive Seat Booking with Real-time Availability
- Booking History & Cancellation
- Reports & Analytics (Admin)
- Complete Swagger API Documentation

## 📁 Project Structure

```
backend/          # Spring Boot (Java 17)
├── controller/   # REST APIs
├── service/      # Business Logic
├── repository/   # Database Access
├── entity/       # Database Models
├── security/     # JWT & Auth
└── config/       # Configuration

frontend/         # React 18 + Vite
├── api/          # API Calls
├── pages/        # UI Pages
├── components/   # Reusable Components
└── context/      # State Management
```

## 🛠️ Tech Stack

**Backend:** Spring Boot 3, Spring Security, JWT, MySQL, Swagger  
**Frontend:** React 18, Vite, Tailwind CSS, Axios, React Router

## 📝 API Endpoints

**Auth:** `/api/auth/register`, `/api/auth/login`  
**Movies:** `/api/movies` (GET, POST, PUT, DELETE)  
**Showtimes:** `/api/showtimes` (GET, POST, PUT, DELETE)  
**Seats:** `/api/seats/showtime/{id}`  
**Reservations:** `/api/reservations` (GET, POST, PUT)  
**Admin:** `/api/admin/reports`

Full API docs: http://localhost:8080/swagger-ui/index.html

## 🐳 Docker (Optional)

```bash
cd backend
docker build -t movie-booking .
docker run -p 8080:8080 movie-booking
```

## 🔧 Troubleshooting

**MySQL Connection Error:** Check credentials in `application.properties`  
**Port 8080 in use:** Change `server.port` in `application.properties`  
**Frontend API Error:** Ensure backend is running on port 8080
