# Atomic Systems - Goal System API

## Introduction

Atomic Systems is a REST-based Spring Boot backend application for managing personal goals and recurring systems.

The idea of the application is that users do not only track goals, but also create concrete recurring systems that help them achieve those goals.

Example:
A user creates the goal "Get fit" and adds a daily system called "Morning Workout" with multiple tasks.

## Domain

### Core Objects

- User
- Goal
- GoalSystem
- SystemTask

### Relationships

- One User can have many Goals.
- One Goal belongs to one User.
- One Goal can have many GoalSystems.
- One GoalSystem belongs to one Goal.
- One GoalSystem can have many SystemTasks.
- One SystemTask belongs to one GoalSystem.

In the Java model, the relationships are kept simple and currently modeled from child to parent:

- Goal -> User
- GoalSystem -> Goal
- SystemTask -> GoalSystem

This still creates the required foreign keys in the database:

- goals.user_id
- goal_systems.goal_id
- system_tasks.system_id

## Non-Functional Requirements

- Simple layered architecture
- Test-first / TDD-oriented development
- Good test coverage for relevant business behavior
- Clear package structure
- Meaningful validation for API requests
- No sensitive data in API responses
- Passwords must not be stored as plain text
- REST API with clear HTTP status codes
- PostgreSQL for application persistence
- H2 database for automated tests
- Git for version control

## Technical Stack

- Java 21
- Spring Boot
- Maven
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- H2 Database for tests
- Bean Validation
- Lombok
- JUnit
- JWT authentication

## Architecture

The application follows a simple layered architecture:

- `controller`: REST endpoints
- `service`: business logic
- `repository`: database access
- `model`: JPA entities
- `dto`: request and response objects
- `security`: authentication and JWT handling
- `exception`: error handling

## Development Approach

The project is developed step by step with a test-first mindset.

The usual workflow is:

1. Define the next small use case or technical behavior.
2. Write a failing test.
3. Implement the smallest amount of code needed to pass the test.
4. Refactor if necessary.
5. Continue with the next use case.

## Tasks

### Task 1: Register User

As a user, I want to register so that I can manage my own goals and systems.

Acceptance criteria:

- A user can register with username, email, and password.
- Username, email, and password are required.
- Email must have a valid format.
- Password must contain at least 8 characters.
- Email must be unique.
- Username must be unique.
- The password is not stored as plain text.
- Sensitive data is not returned in the response.
- A successful registration returns HTTP 201.
- A duplicate email or username returns HTTP 409.

Endpoint:

```text
POST /auth/register
```

### Task 2: Login User

As a user, I want to log in so that I can access protected API endpoints.

Acceptance criteria:

- Login is done with email and password.
- Valid login credentials return a JWT.
- Invalid login credentials return an appropriate error.

Endpoint:

```text
POST /auth/login
```

### Task 3: Create Goal

As a user, I want to create a goal so that I can define what I want to work on.

Acceptance criteria:

- An authenticated user can create a Goal with title and description.
- The Goal is assigned to the authenticated user.
- A Goal requires at least a title.

Endpoint:

```text
POST /goals
```

### Task 4: Get All Goals

As a user, I want to see my goals so that I can get an overview of what I am working on.

Acceptance criteria:

- A user can only see their own Goals.
- The response contains id, title, and description.

Endpoint:

```text
GET /goals
```

### Task 5: Get Goal By Id

As a user, I want to view a single goal so that I can inspect its details.

Acceptance criteria:

- A user can only access their own Goal.
- A non-existing Goal returns an appropriate error.
- The response contains id, title, and description.

Endpoint:

```text
GET /goals/{id}
```

### Task 6: Update Goal

As a user, I want to update a goal so that I can change its title or description.

Acceptance criteria:

- Only the owner can update the Goal.
- Title and description can be changed.
- A non-existing Goal returns an appropriate error.

Endpoint:

```text
PATCH /goals/{id}
```

### Task 7: Delete Goal

As a user, I want to delete a goal when I no longer want to work on it.

Acceptance criteria:

- Only the owner can delete the Goal.
- Related GoalSystems and SystemTasks are handled correctly.
- A successful delete returns no content.

Endpoint:

```text
DELETE /goals/{id}
```

### Task 8: Create GoalSystem

As a user, I want to create a recurring system for a goal so that I can define a repeatable routine.

Acceptance criteria:

- A GoalSystem has a title and a frequency.
- Frequency can be DAILY, WEEKLY, or MONTHLY.
- The GoalSystem belongs to a Goal.

Endpoint:

```text
POST /systems
```

### Task 9: Get All GoalSystems

As a user, I want to see my GoalSystems so that I can review my recurring systems.

Acceptance criteria:

- A user can only see GoalSystems that belong to their own Goals.
- The response contains id, title, frequency, and goalId.

Endpoint:

```text
GET /systems
```

### Task 10: Update GoalSystem

As a user, I want to update a GoalSystem so that I can change its title or frequency.

Acceptance criteria:

- Only the owner of the related Goal can update the GoalSystem.
- Title and frequency can be changed.

Endpoint:

```text
PATCH /systems/{id}
```

### Task 11: Delete GoalSystem

As a user, I want to delete a GoalSystem when it is no longer useful.

Acceptance criteria:

- Only the owner of the related Goal can delete the GoalSystem.
- Related SystemTasks are handled correctly.
- A successful delete returns no content.

Endpoint:

```text
DELETE /systems/{id}
```

### Task 12: Add Task To GoalSystem

As a user, I want to add tasks to a GoalSystem so that the system has concrete actions.

Acceptance criteria:

- A SystemTask has a title and completed status.
- New SystemTasks are not completed by default.
- The SystemTask belongs to a GoalSystem.

Endpoint:

```text
POST /systems/{id}/tasks
```

### Task 13: Get Task By Id

As a user, I want to view a single task so that I can inspect its current status.

Acceptance criteria:

- A user can only access tasks that belong to their own GoalSystems.
- The response contains id, title, and completed.

Endpoint:

```text
GET /tasks/{id}
```

### Task 14: Update Task

As a user, I want to update a task so that I can change its title.

Acceptance criteria:

- Only the owner of the related GoalSystem can update the task.
- The title can be changed to a non-empty value.
- Updating the title does not change the completed status.

Endpoint:

```text
PATCH /tasks/{id}
```

### Task 15: Complete Task

As a user, I want to complete a task so that I can track progress inside a GoalSystem.

Acceptance criteria:

- Only the owner of the related GoalSystem can complete the task.
- A SystemTask can be changed from completed=false to completed=true.
- The response contains id and completed.

Endpoint:

```text
PATCH /tasks/{id}/complete
```

### Task 16: Delete Task

As a user, I want to delete a task when it is no longer needed.

Acceptance criteria:

- Only the owner of the related GoalSystem can delete the task.
- A successful delete returns no content.

Endpoint:

```text
DELETE /tasks/{id}
```

## Current Status

- Project structure created
- Domain model created
- H2 test database configured
- UserRepository implemented and tested
- GoalRepository implemented and tested
- Goal CRUD implemented
- GoalSystem CRUD implemented
- SystemTask CRUD and completion implemented
- Ownership checks implemented for user-specific resources
- Request validation and centralized 404 error handling implemented
- Tasks 1 and 3-16 completed with 84 automated tests
- Registration with BCrypt password hashing implemented
- Login, JWT authentication, and PostgreSQL configuration are still pending
