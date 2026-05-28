# Hospital System

A full-stack hospital management system built with React and Spring Boot.

The project allows management of:

- Patients
- Doctors
- Appointments

The application simulates a real hospital workflow with appointment scheduling, doctor availability validation, and patient search by CPF.

---

# Features

## Patients

- Create patient
- Edit patient
- Delete patient
- Search patient by:
  - Name
  - Surname
  - CPF
- Patient listing

---

## Doctors

- Create doctor
- Edit doctor
- Delete doctor
- Search doctor by:
  - Name
  - CRM
  - Specialty
- Doctor listing

---

## Appointments

- Create appointments
- Edit appointments
- Delete appointments
- Search appointments
- Validate doctor availability
- Search patient by CPF before scheduling
- Appointment status management:
  - Scheduled
  - Completed
  - Cancelled

---

# Technologies

## Frontend

- React
- React Router
- React Hook Form
- CSS

---

## Backend

- Java
- Spring Boot
- REST API
- DTO Pattern

---

# API Endpoints

## Patients

| Method | Endpoint             | Description        |
| ------ | -------------------- | ------------------ |
| GET    | `/patients`          | Get all patients   |
| GET    | `/patients/cpf?cpf=` | Get patient by CPF |
| POST   | `/patients`          | Create patient     |
| PUT    | `/patients/{id}`     | Update patient     |
| DELETE | `/patients/{id}`     | Delete patient     |

---

## Doctors

| Method | Endpoint        | Description     |
| ------ | --------------- | --------------- |
| GET    | `/doctors`      | Get all doctors |
| POST   | `/doctors`      | Create doctor   |
| PUT    | `/doctors/{id}` | Update doctor   |
| DELETE | `/doctors/{id}` | Delete doctor   |

---

## Appointments

| Method | Endpoint             | Description          |
| ------ | -------------------- | -------------------- |
| GET    | `/appointments`      | Get all appointments |
| POST   | `/appointments`      | Create appointment   |
| PUT    | `/appointments/{id}` | Update appointment   |
| DELETE | `/appointments/{id}` | Delete appointment   |

---

# Appointment Flow

1. Search patient by CPF
2. Select doctor
3. Select date and time
4. Create appointment
5. System validates doctor availability

---

# Running the Project

## Backend

```bash
cd backend
./mvnw spring-boot:run
```

---

## Frontend

```bash
cd frontend
npm install
npm run dev
```

---

# Future Improvements

- Database integration (PostgreSQL)
- Spring Data JPA
- Authentication and authorization
- Calendar view for appointments
- Doctor schedules
- Pagination
- Advanced search
- Notifications
- Dashboard
- Responsive design improvements

---

# Learning Goals

This project was created to practice:

- Full-stack development
- REST APIs
- CRUD operations
- DTO architecture
- React state management
- Form validation
- Entity relationships
- Frontend/backend integration

---

# Author

Developed by [Leila H](https://github.com/itsleila)
