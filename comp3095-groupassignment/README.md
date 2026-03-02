# COMP3095 - Student Wellness Hub

A microservices-based Student Wellness Hub platform for George Brown College students.

## Architecture

This project consists of 3 microservices:

1. **wellness-resource-service** (Port 8081)
   - Database: PostgreSQL
   - Cache: Redis
   - Manages mental health resources catalog

2. **goal-tracking-service** (Port 8082)
   - Database: MongoDB
   - Tracks students' personal wellness goals

3. **event-service** (Port 8083)
   - Database: PostgreSQL
   - Manages community wellness events

## Prerequisites

- Docker Desktop
- Java 17 or higher (for local development)
- Gradle (optional, wrapper included)

## Running the Application

### Using Docker Compose (Recommended)

```bash
docker-compose up --build
```

This will start all services:
- wellness-resource-service: http://localhost:8081
- goal-tracking-service: http://localhost:8082
- event-service: http://localhost:8083
- PostgreSQL: localhost:5432
- MongoDB: localhost:27017
- Redis: localhost:6379

### Stopping the Application

```bash
docker-compose down
```

To remove volumes as well:

```bash
docker-compose down -v
```

## API Endpoints

### Wellness Resource Service (8081)
- `POST /api/resources` - Create resource
- `GET /api/resources` - Get all resources
- `GET /api/resources/{id}` - Get resource by ID
- `GET /api/resources/category/{category}` - Get resources by category
- `PUT /api/resources/{id}` - Update resource
- `DELETE /api/resources/{id}` - Delete resource

### Goal Tracking Service (8082)
- `POST /api/goals` - Create goal
- `GET /api/goals` - Get all goals
- `GET /api/goals/{id}` - Get goal by ID
- `GET /api/goals/student/{studentId}` - Get goals by student
- `PUT /api/goals/{id}` - Update goal
- `DELETE /api/goals/{id}` - Delete goal

### Event Service (8083)
- `POST /api/events` - Create event
- `GET /api/events` - Get all events
- `GET /api/events/{id}` - Get event by ID
- `GET /api/events/category/{category}` - Get events by category
- `PUT /api/events/{id}` - Update event
- `DELETE /api/events/{id}` - Delete event

## Testing

Run tests for each service:

```bash
cd wellness-resource-service
./gradlew test

cd ../goal-tracking-service
./gradlew test

cd ../event-service
./gradlew test
```

## Team Members

Arash Shalchian

Diana Mohammadi

Radin MadadNezhad Aligorkeh