# RoomMate Connect
*Find Your Space. Find Your People.*

## Prerequisites
Java 21, MySQL 8, Node 20+, Git. (Maven comes via the included wrapper.)

## Run the backend
1. Create the MySQL database and user (database `roommate_connect`, user `roommate_user`).
2. In `backend/src/main/resources`, copy `application-local.properties.example`
   to `application-local.properties` and set your DB password.
3. `cd backend` then `mvnw spring-boot:run` (Windows) or `./mvnw spring-boot:run`
4. Open http://localhost:8080/api/v1/health

## Run the frontend
`cd frontend`, then `npm install`, then `npm run dev` (http://localhost:5173)

## Branching
`main` = stable, `develop` = integration, `feature/<name>` = your work.
Commit format: `type(scope): description` (feat, fix, chore, docs, refactor, test).