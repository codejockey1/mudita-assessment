# Mudita-Assessment

This project conists of a simple web application that will work on web or mobile screens and automatically adjust for any size screen. The application should do the following:

Ask the user to provide the top things they want to accomplish today (e.g. go for a 20 minute run, mow the lawn, prepare for meeting with Jessica at work, help daughter with science project).

The application should show a simple progress bar or spinner while, in the background, it is using any simple LLM to optimally plan out the day for the user.

The output should then be a fun and simple visualization of what they should do, when, and ideally detail out why they should do things in this order.

For instance:

We think preparing for the Jessica meeting and helping your daughter with the science project are both tasks that can't be skipped. We also assume since it's a school day that daughter won't be home until late afternoon. We also bundled the two outdoor activities together. As such, we recommend something like this:

11AM: Prepare for meeting with Jessica at work
1PM: Go for a 20 minute run
2PM: Mow the lawn
4PM: Help daughter with Science project

## Backend Stack

- Java 17
- Spring Boot 3
- Gradle

## Frontend Stack

- Vue.js 3
- Vuetify

## Docker Setup

The project uses Docker and Docker Compose for consistent development, testing, and deployment environments. Three main services are containerized:

- Backend (Spring Boot)
- Frontend (Vue.js)
- Nginx

## Prerequisites
1. Install Docker
2. Set your OPENAI key environment variable:
export OPENAI_API_KEY=your_api_key_here

## How to run application

1. Clone the repository
2. Start the services:
   ```bash
   docker compose up -d
   ```
3. Open your browser and navigate to `http://localhost:80`

## How to run the backend locally

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build the project using Gradle:
   ```bash
   ./gradlew build
   ```

3. Run the application:
   ```bash
   ./gradlew bootRun
   ```

   The backend will start on `http://localhost:8080` by default.

   Note: Make sure you have Java 17 installed and the OPENAI_API_KEY environment variable set before running the application.

## How to run the frontend locally

1. Navigate to the frontend directory: 
    ```bash
    cd ui
    ```
2. Install dependencies: 
    ```bash
    npm install
    ```
3. Run the local Vite dev instance: 
    ```bash
    npm run dev
    ```
The frontend will start on `http://localhost:5173` by default.