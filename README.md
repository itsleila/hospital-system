# 🏥 Hospital System

A full-stack hospital management system built with **React, Spring Boot and a microservices-oriented architecture**.

The project manages patients, doctors, appointments and appointment notifications while demonstrating concepts such as **Domain-Driven Design (DDD), event-driven communication, service discovery, API Gateway, persistence, containerization, orchestration, observability and CI/CD**.


# Overview

The system is organized around the hospital management domain.

The main business contexts are:

- **Patients** — patient information management
- **Doctors** — doctor and specialty management
- **Appointments** — appointment scheduling and lifecycle management
- **Notifications** — asynchronous processing of appointment-related notifications

From a DDD perspective:

```text
Hospital Management
│
├── Patients
│   └── Supporting Subdomain
│
├── Doctors
│   └── Supporting Subdomain
│
└── Appointments
    └── Core Domain
```

The **Appointments** context represents the Core Domain because it contains the main business rules of the application, including scheduling and doctor availability validation.

# Architecture

The current architecture consists of:

```text
                    ┌─────────────────┐
                    │     Frontend    │
                    │      React      │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │   API Gateway   │
                    └───────┬─────────┘
                            │
                 ┌──────────┴──────────┐
                 │                     │
                 ▼                     ▼
       ┌───────────────────┐   ┌───────────────────┐
       │  Hospital System  │   │   Notification    │
       │       API         │   │      Service      │
       └─────────┬─────────┘   └─────────┬─────────┘
                 │                       │
                 ▼                       ▼
           PostgreSQL               PostgreSQL
                 │
                 │ AppointmentEvent
                 ▼
            ┌──────────┐
            │ RabbitMQ │
            └────┬─────┘
                 │
                 └─────────────────────► Notification Service


               ┌────────────────────┐
               │  Discovery Server  │
               │       Eureka       │
               └────────────────────┘
```

The frontend communicates with the system through the **API Gateway**.

Appointment-related events are published asynchronously through **RabbitMQ**, allowing the Notification Service to process notifications independently from the main API.



# Features

## 👤 Patients

- Create patient
- Edit patient
- Delete patient
- Patient listing
- Search patient by:
  - Name
  - Surname
  - CPF
- Persistent storage with PostgreSQL


## 👨‍⚕️ Doctors

- Create doctor
- Edit doctor
- Delete doctor
- Doctor listing
- Search doctor by:
  - Name
  - CRM
  - Specialty
- Persistent storage with PostgreSQL

## 📅 Appointments

- Create appointments
- Edit appointments
- Cancel appointments
- Search appointments
- Validate doctor availability
- Search patient by CPF before scheduling
- Maintain appointment history
- Appointment status management:
  - `SCHEDULED`
  - `COMPLETED`
  - `CANCELLED`
- Publish domain events when appointments are:
  - Created
  - Updated
  - Cancelled

Appointment cancellation does not physically remove the record from the database. Instead, its status is changed to `CANCELLED`, preserving the appointment history.


## 🔔 Notifications

Notifications are managed by an independent Spring Boot microservice.

Supported notification types include:

- `APPOINTMENT_CREATED`
- `APPOINTMENT_UPDATED`
- `APPOINTMENT_CANCELLED`
- `APPOINTMENT_REMINDER`

Notification statuses include:

- `PENDING`
- `SENT`
- `FAILED`
- `CANCELLED`

The Notification Service also supports:

- Notification history
- Filtering by status
- Retry of failed notifications
- Notification cancellation
- Event idempotency using `eventId`


#  Event-Driven Communication

Appointment notifications are created asynchronously using **RabbitMQ**.

The Hospital System API acts as a producer:

```text
AppointmentService
       ↓
AppointmentEvent
       ↓
RabbitMQ
```

The Notification Service acts as a consumer:

```text
RabbitMQ
   ↓
AppointmentEventListener
   ↓
NotificationService
   ↓
PostgreSQL
```

RabbitMQ configuration:

```text
Exchange:
hospital.appointments.exchange

Routing Keys:
appointment.created
appointment.updated
appointment.cancelled

Queue:
notification.appointments.queue

Binding:
appointment.*
```

This architecture reduces coupling between services.

If the Notification Service becomes temporarily unavailable, the Hospital System API can continue processing appointments while messages remain queued for later consumption.

-

# Event Idempotency

Message brokers may redeliver messages.

To prevent duplicate notifications, every appointment event contains a unique:

```text
eventId
```

Before creating a notification, the Notification Service verifies whether that event has already been processed.

```text
Event received
      ↓
eventId already exists?
      │
   ┌──┴──┐
   │     │
  No    Yes
   │     │
   ▼     ▼
Save   Ignore duplicate
```

A unique database constraint also protects the `eventId`.


# Persistence

The system uses:

- PostgreSQL
- Spring Data JPA
- Hibernate
- Hibernate Envers

The main API and Notification Service maintain independent databases:

```text
Hospital System API
        ↓
hospital-db


Notification Service
        ↓
notification-db
```

A service does not directly access another service's database.

Hibernate Envers is used to provide entity revision history and auditing.


# Service Discovery

**Netflix Eureka** is used for service discovery.

The following applications register with the Discovery Server:

```text
Hospital System API
Notification Service
API Gateway
```

This allows services to be discovered without depending on fixed physical addresses.


# API Gateway

The API Gateway provides a centralized HTTP entry point for the frontend.

Example:

```text
React Frontend
      ↓
API Gateway
      ↓
Hospital System API / Notification Service
```

Example routes:

```text
/appointments/**
      ↓
Hospital System API


/notifications/**
      ↓
Notification Service
```


#  API Endpoints

## Patients

| Method | Endpoint | Description |
|---|---|---|
| GET | `/patients` | Get all patients |
| GET | `/patients/cpf?cpf=` | Get patient by CPF |
| POST | `/patients` | Create patient |
| PUT | `/patients/{id}` | Update patient |
| DELETE | `/patients/{id}` | Delete patient |

---

## Doctors

| Method | Endpoint | Description |
|---|---|---|
| GET | `/doctors` | Get all doctors |
| POST | `/doctors` | Create doctor |
| PUT | `/doctors/{id}` | Update doctor |
| DELETE | `/doctors/{id}` | Delete doctor |

---

## Appointments

| Method | Endpoint | Description |
|---|---|---|
| GET | `/appointments` | Get all appointments |
| POST | `/appointments` | Create appointment |
| PUT | `/appointments/{id}` | Update appointment |
| PATCH | `/appointments/{id}/cancel` | Cancel appointment |

Creating, updating or cancelling an appointment may automatically publish an event to RabbitMQ.

---

## Notifications

| Method | Endpoint | Description |
|---|---|---|
| GET | `/notifications` | Get all notifications |
| GET | `/notifications/{id}` | Get notification by ID |
| GET | `/notifications/status/{status}` | Filter notifications by status |
| POST | `/notifications/{id}/retry` | Retry a failed notification |
| PATCH | `/notifications/{id}/cancel` | Cancel a notification |

Notifications are normally created through RabbitMQ events and therefore do not require a public `POST /notifications` endpoint.


# Appointment Flow

The main appointment workflow is:

```text
1. Search patient by CPF
        ↓
2. Select doctor
        ↓
3. Select date and time
        ↓
4. Validate doctor availability
        ↓
5. Create appointment
        ↓
6. Persist appointment
        ↓
7. Publish AppointmentEvent
        ↓
8. RabbitMQ
        ↓
9. Notification Service
        ↓
10. Persist notification
```


# Technologies

## Frontend

- React
- React Router
- React Hook Form
- JavaScript
- CSS
- Vite
- Nginx for containerized frontend delivery

---

## Backend

- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- Hibernate
- Hibernate Envers
- REST APIs
- DTO Pattern
- Maven

---

## Microservices

- Spring Cloud
- Netflix Eureka
- Spring Cloud Gateway
- OpenFeign
- RabbitMQ
- Spring AMQP

OpenFeign was used during the synchronous communication stage of the project. Appointment notification creation later evolved to asynchronous communication through RabbitMQ.

---

## Database

- PostgreSQL
- Testcontainers PostgreSQL for integration tests

---

## Testing

- JUnit
- Mockito
- Spring Boot Test
- Testcontainers

Tests cover scenarios such as:

- Business rules
- Repository persistence
- Appointment operations
- Notification creation
- Notification retry
- Notification cancellation
- Event idempotency
- Application context validation

---

# 🐳 Docker

The project is prepared for containerized execution.

Each application has its own `Dockerfile`:

```text
hospital-system-api/
hospital-notification-service/
api-gateway/
discovery-server/
hospital-system-web/
```

Official Docker images are used for infrastructure components:

```text
PostgreSQL
RabbitMQ
```

Docker Compose can be used to start the complete local environment.

Example internal container communication:

```text
hospital-db:5432
notification-db:5432
rabbitmq:5672
discovery-server:8888
```

Application configuration is externalized through environment variables so the same application can run locally, with Docker or inside Kubernetes.

---

# ☸️ Kubernetes

Kubernetes manifests are stored in:

```text
kubernetes/
├── namespace.yaml
├── configmap.yaml
├── hospital-api.yaml
├── notification-service.yaml
├── gateway.yaml
├── discovery.yaml
├── frontend.yaml
├── hospital-db.yaml
├── notification-db.yaml
└── rabbitmq.yaml
```

Stateless applications use:

```text
Deployment
+
Service
```

Stateful infrastructure such as PostgreSQL and RabbitMQ uses:

```text
StatefulSet
+
Service
+
PersistentVolumeClaim
```

Persistent volumes ensure that database and RabbitMQ data are not lost when Pods are recreated.

---

# ⚙️ Configuration Management

Non-sensitive configuration can be stored in Kubernetes `ConfigMap` resources.

Examples:

```text
EUREKA_URL
RABBITMQ_HOST
RABBITMQ_PORT
HOSPITAL_DB_URL
NOTIFICATION_DB_URL
OTEL_ENDPOINT
```

Sensitive values are stored using Kubernetes `Secret` resources:

```text
HOSPITAL_DB_USERNAME
HOSPITAL_DB_PASSWORD

NOTIFICATION_DB_USERNAME
NOTIFICATION_DB_PASSWORD

RABBITMQ_USER
RABBITMQ_PASSWORD
```


# Monitoring and Observability

The services use **Spring Boot Actuator and Micrometer** to expose operational information.

Available endpoints include:

```text
/actuator/health
/actuator/prometheus
```

The observability architecture is designed around:

```text
Prometheus → Metrics
Loki       → Logs
Tempo      → Distributed Traces
Grafana    → Visualization
```

OpenTelemetry is used for distributed tracing.

Example trace:

```text
Frontend
   ↓
API Gateway
   ↓
Hospital System API
   ↓
RabbitMQ
   ↓
Notification Service
```

RabbitMQ observations are also enabled for message publishing and consumption.


# RabbitMQ Observability

Hospital System API:

```properties
spring.rabbitmq.template.observation-enabled=true
```

Notification Service:

```properties
spring.rabbitmq.listener.simple.observation-enabled=true
```

This makes RabbitMQ message operations part of the application's observability data.

---

# Continuous Integration

GitHub Actions is used for Continuous Integration.

Workflow:

```text
.github/workflows/ci.yml
```

On configured pushes and pull requests, GitHub Actions automatically validates the application.

Backend workflow:

```text
Checkout
   ↓
Java 21
   ↓
Maven
   ↓
mvn clean verify
```

Projects validated include:

```text
hospital-system-api
hospital-notification-service
api-gateway
discovery-server
```

Frontend validation includes:

```text
npm ci
npm test
npm run build
```

If compilation or tests fail, the CI workflow fails before deployment.

---

# CI/CD Pipeline

The project is structured to support the following pipeline:

```text
Developer
    │
    │ git push
    ▼
GitHub
    │
    ▼
Continuous Integration
    │
    ├── Compile
    ├── Run tests
    └── Validate frontend
    │
    ▼
Docker Build
    │
    ▼
GitHub Container Registry
    │
    ▼
Kubernetes Deployment
    │
    ▼
Updated Application
```

Workflow files:

```text
.github/workflows/
├── ci.yml
└── docker-publish.yml
```

Container images can be published to the **GitHub Container Registry (GHCR)**.

Example:

```text
ghcr.io/<username>/hospital-system-api:<version>
```

Using the Git commit SHA as an image tag provides traceability between source code and deployed containers.

---

# Running the Project Locally

The project contains multiple applications and infrastructure components.

## Discovery Server

```bash
cd discovery-server
./mvnw spring-boot:run
```

Default port:

```text
8888
```

---

## Hospital System API

```bash
cd hospital-system-api
./mvnw spring-boot:run
```

Default port:

```text
8081
```

---

## Notification Service

```bash
cd hospital-notification-service
./mvnw spring-boot:run
```

Default port:

```text
8082
```

---

## API Gateway

```bash
cd api-gateway
./mvnw spring-boot:run
```

Default port:

```text
8080
```

---

## Frontend

```bash
cd hospital-system-web

npm install
npm run dev
```

The Vite development server is typically available at:

```text
http://localhost:5173
```

---

## RabbitMQ

RabbitMQ can be started using Docker.

The application uses:

```text
AMQP:
localhost:5672

Management UI:
http://localhost:15672
```

---

# 🐳 Running with Docker

After the Docker environment is configured, the complete infrastructure can be started with:

```bash
docker compose up -d --build
```

Check running containers:

```bash
docker compose ps
```

Stop the environment:

```bash
docker compose down
```

Persistent volumes can be used to preserve PostgreSQL and RabbitMQ data between container recreations.


---

# 📁 Project Structure

```text
hospital-system/
│
├── .github/
│   └── workflows/
│       ├── ci.yml
│       ├── docker-publish.yml
│       └── deploy-kubernetes.yml
│
├── api-gateway/
│
├── discovery-server/
│
├── hospital-system-api/
│
├── hospital-notification-service/
│
├── hospital-system-web/
│
├── kubernetes/
│
├── docker-compose.yml
│
└── README.md
```

---

# 🎯 Learning Goals

This project was developed to practice and demonstrate:

- Full-stack development
- REST API development
- Domain-Driven Design
- Core and Supporting Subdomains
- Bounded Contexts
- CRUD operations
- DTO architecture
- Entity relationships
- Spring Data JPA
- PostgreSQL persistence
- Hibernate auditing
- Microservices architecture
- Service Discovery
- API Gateway
- Synchronous communication
- Event-driven architecture
- RabbitMQ
- Asynchronous processing
- Event idempotency
- Unit testing
- Integration testing
- Testcontainers
- Docker
- Kubernetes
- Persistent storage
- Configuration management
- Observability
- Distributed tracing
- Git and GitHub
- Continuous Integration
- Continuous Delivery

---

# 👩‍💻 Author

Developed by [Leila H](https://github.com/itsleila)
