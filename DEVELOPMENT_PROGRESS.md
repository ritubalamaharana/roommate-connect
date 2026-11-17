# RoomMate Connect – Development Progress

**Stack:** Java 21, Spring Boot 4.1.2 (Spring Security 7, Hibernate 7), MySQL 8.0.46, React + Vite + Tailwind
**Team:** Backend – Ritu | Frontend – Arpit
**Current phase:** Phase 1 – Planning and Project Setup

## Module status
| Module | Status |
|---|---|
| 0. Project Setup | Backend DONE and tested. Frontend skeleton pending (Arpit) |
| 1. Authentication and Authorization | Pending |
| All other modules | Pending |

## Database entities created
None yet. (Database `roommate_connect` and user `roommate_user` exist.)

## API endpoints implemented
- GET /api/v1/health (public)

## Architectural decisions
- Monorepo: /backend and /frontend
- All responses wrapped in ApiResponse<T> (success, message, data, timestamp)
- Secrets live in gitignored application-local.properties
- Stateless security (JWT planned); CSRF disabled for that reason
- Spring Boot 4.1.x chosen (3.5.x is out of OSS support; 4.0 ends Dec 2026)
- Schema additions accepted: user_blocks, profile_visibility and
  show_in_recommendations on user_profiles, verification tokens, OWNER/PROVIDER merged to OWNER
- ddl-auto=update during development (revisit before deployment)
- Unauthenticated requests currently return 403; to be changed to 401 JSON in Module 1

## Known bugs
None.

## Testing status
- Health endpoint 200 with DB UP: passed
- Protected endpoint without token returns 403: passed
- MySQL stopped returns 503, recovers to 200 on restart: passed
- Frontend: not started