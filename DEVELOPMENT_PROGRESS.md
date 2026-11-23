# RoomMate Connect – Development Progress

**Stack:** Java 21, Spring Boot 4.1.2 (Spring Security 7, Hibernate 7), MySQL 8.0.46, React + Vite + Tailwind (frontend not started)
**Team:** Backend – Ritu | Frontend – Arpit
**Current phase:** Phase 2 – Authentication and Profiles (Module 1 done)

## Module status
| Module | Status |
|---|---|
| 0. Project Setup | Backend DONE. Frontend skeleton deferred |
| 1. Authentication and Authorization | DONE and tested |
| 2. User Profile Management and Roommate Preferences | Pending (next) |
| 3 to 13 | Pending |

## Database entities created
- users (id, full_name, email unique, password_hash, account_status, email_verified, created_at)
- user_roles (user_id, role; unique pair). Roles: SEEKER, TENANT, OWNER, ADMIN
- verification_tokens (user_id, token unique, expires_at, used)
- refresh_tokens (user_id, token_hash unique, expires_at, revoked)

## API endpoints implemented (base /api/v1)
| Method | Path | Access |
|---|---|---|
| GET | /health | public |
| POST | /auth/register | public |
| POST | /auth/login | public |
| POST | /auth/refresh | public (needs refresh token) |
| POST | /auth/logout | public (needs refresh token) |
| POST | /auth/verify-email | public (needs email token) |
| POST | /auth/resend-verification | public |
| GET | /auth/me | authenticated |
| any | /admin/** | ADMIN role (no controllers yet) |

## Architectural decisions
- Monorepo: /backend and /frontend
- All responses use ApiResponse<T> (success, message, data, timestamp)
- Secrets (DB password, JWT secret) live in gitignored application-local.properties
- Spring Boot 4.1.x (3.5.x out of OSS support, 4.0 ends Dec 2026)
- JWT via Spring's built-in resource server (Nimbus), HS256, access token 30 min, claims: sub (user id), email, roles
- Refresh tokens: random, stored as SHA-256 hash, rotated on every use; reuse of a revoked token revokes all of that user's tokens
- Email lowercased everywhere; ADMIN cannot be chosen at registration
- Login error message is identical for unknown email and wrong password
- 401 = not authenticated, 403 = no permission or suspended; both return JSON
- ddl-auto=update during development (revisit before deployment)
- In SecurityConfig: specific rules first, anyRequest().authenticated() always last
- Schema additions accepted earlier: user_blocks, profile_visibility and show_in_recommendations on user_profiles, OWNER/PROVIDER merged to OWNER

## Known issues and pending security work
- No rate limiting on login or resend-verification
- [EMAIL STUB] log line must be replaced by a real sender and removed before deployment
- Suspended check only in /me and refresh; other endpoints trust the token for up to 30 min
- Role changes apply only after the user gets a new token (up to 30 min)
- Access tokens stay valid after logout until they expire
- No cleanup job for expired tokens
- Admin accounts are created by SQL only
- Test class still named BackendApplicationTests

## Testing status
- Module 0 and Module 1: manual tests with Postman passed
- Automated tests: none yet
- Frontend: not started