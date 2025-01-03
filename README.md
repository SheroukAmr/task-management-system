# Task Management System

This is a Task Management System built using Spring Boot and MongoDB Atlas (NoSQL).
It allows you to create, read, update, and delete tasks, search and filter tasks, with features like user authentication
and authorization, and email notifications.

## Features

- User authentication and authorization using JWT tokens.
- CRUD operations for managing tasks (create, read, update, delete).
- MongoDB Atlas for storing data.
- Secure API endpoints with role-based access.
- Email notifications when tasks are created, updated, or deleted, and scheduled reminders for due dates.

## Technologies Used

- **Spring Boot**: For building the backend.
- **Spring Security**: For authentication and authorization.
- **MongoDB**: For the NoSQL database (via MongoDB Atlas).
- **JWT**: For secure token-based authentication.
- **Spring Data MongoDB**: For MongoDB interaction.
- **Spring Boot's email support** with JavaMailSender and SMTP.
- **Postman**: For API testing.

## Prerequisites

Before you begin, make sure you have the following installed on your machine:

- **JDK 17** or later: To run the Spring Boot application.
- **Maven**: For building and running the application.
- **MongoDB Atlas** account (shared credentials in `application.properties`).
- **Postman** or any other API client for testing the API.

## Setup Instructions

### 1. Clone the Repository

Clone this repository to your local machine using the following command:

```bash
git clone https://github.com/SheroukAmr/task-management-system.git
```

### 2. Configure

update the server.port in application.properties (I'm using 8085)

### 3. Run the Application

```bash
 mvn spring-boot:run
 ```

### 5. Test the API

# Note Please use the shared postman collections for testing

## I shared 2 collections

- **Register / login /token collection** you will be able to register and then login using the created
  credentials, then a token will be generated to the user after login
  I created a script that will create an AuthToken variable in the environments with the newly created token
  and this variable will be used as authentication for the rest of APIs
- **TaskCollection** includes all the implemented APIs with authentication set to the environment variable "AuthToken"
  Please create an environment variable "local.url" with the localhost URL you will best with
  the application by default will run on "http://localhost:8085"

## 1. Registration: POST /taskManagement/register

- Body: JSON with userName, userPassword, and role (User already created),
  if the role is not given it will be set to user by default
- The password will be encrypted after user creation 
- **{
  "userName": "Sh",
  "userPassword": "password",
  "role":"admin"
  }**

## 2. Login: POST /taskManagement/login

- Body: JSON with userName and password.
- **{
  "userName": "Sh",
  "password": "password"
  }**
  The response will create a JWT token and set it to environment variable "AuthToken"
  and the same token will be created in response header
## 3. GET /taskManagement/Users

## 4. Create Task: POST /tasks

- Body: JSON with task details (title, description, status, priority, dueDate, assigneeEmail).
- **{
  "title" :"Third",
  "description" :"My First Task",
  "status" :"in progress",
  "priority" :"low",
  "dueDate":"2025-08-11",
  "assigneeEmail" :"sherouk.amr16@gmail.com"
  }**

### Please note that are some validation on the task creation

- Title is mandatory
- Description must not exceed 500 characters
- Status is required
- Priority is required and but one of: high, medium, low
- Due date must be in the present or future and in pattern "yyyy-MM-dd"

- An Email will be send to the task assigneeEmail from sherouk136069@gmail
  with a notification in task creation

## 5. Get All Tasks: GET /taskManagement/tasks

## 6. Get a Tasks by ID: GET /taskManagement/tasks/{id}

## 7. Update Task: PUT /taskManagement/tasks/{id}

- Body: JSON with updated task details.
- **{
  "title" :"Third",
  "description" :"My Third Task",
  "status" :"Done",
  "priority" :"low",
  "dueDate":"2025-08-11",
  "assigneeEmail" :"sherouk.amr16@gmail.com"
  }**
- An Email will be send to the task assigneeEmail from sherouk136069@gmail
  with a notification that the task has been updated.

## 8. Delete Task: DELETE /taskManagement/tasks/{id}

-
    - An Email will be send to the task assigneeEmail from sherouk136069@gmail
      with a notification that the task has been deleted.

## 9. Search for a Task: GET /taskManagement/tasks/search

- 2 parameters title & description
  **/taskManagement/tasks/search?title=First&description=Task**

## 10. Filter tasks with pagination support :GET /taskManagement/tasks/filter/page

- With Parameters
  title
  status
  dueDateStart
  dueDateEnd
  page
  size
- **/taskManagement/tasks/filter/page?title=Task
  1&status=in%20progress&dueDateStart=2025-07-01&dueDateEnd=2025-09-30&page=0&size=2**