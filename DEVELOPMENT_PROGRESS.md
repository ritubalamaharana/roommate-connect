# RoomMate Connect – Development Progress

**Stack:** Java 21, Spring Boot 4.1.2 (Spring Security 7, Hibernate 7), MySQL 8.0.46, React + Vite + Tailwind (frontend not started)
**Team:** Backend – Ritu | Frontend – Arpit
**Current phase:** Phase 2 done (Authentication and Profiles). Next: Phase 3 (Accommodation Listings)

## Module status
| Module | Status |
|---|---|
| 0. Project Setup | Backend DONE. Frontend skeleton deferred |
| 1. Authentication and Authorization | DONE and tested |
| 2. User Profile Management and Roommate Preferences | DONE and tested |
| 3. Property Management | Pending (next) |
| 4 to 13 | Pending |

## Database entities created
- users (id, full_name, email unique, password_hash, account_status, email_verified, created_at)
- user_roles (user_id, role; unique pair). Roles: SEEKER, TENANT, OWNER, ADMIN
- verification_tokens (user_id, token unique, expires_at, used)
- refresh_tokens (user_id, token_hash unique, expires_at, revoked)
- user_profiles (user_id unique, occupation, bio, preferred_city, preferred_locality, monthly_budget_min/max, move_in_date, accommodation_type, profile_visibility, show_in_recommendations, created_at, updated_at; index on city+locality)
- roommate_preferences (user_id unique, cleanliness, noise, social, smoking, food, sharing; all optional)

## API endpoints implemented (base /api/v1)
| Method | Path | Access |
|---|---|---|
| GET | /health | public |
| POST | /auth/register, /auth/login | public |
| POST | /auth/refresh, /auth/logout | public (needs refresh token) |
| POST | /auth/verify-email, /auth/resend-verification | public |
| GET | /auth/me | authenticated |
| GET | /profiles/me | authenticated (own full profile) |
| PUT | /profiles/me | authenticated (replaces profile) |
| GET | /profiles/preferences | authenticated |
| PUT | /profiles/preferences | authenticated (replaces answers) |
| GET | /profiles/{userId} | authenticated (public view, respects visibility) |
| any | /admin/** | ADMIN role (no controllers yet) |

## Architectural decisions
- Monorepo: /backend and /frontend
- All responses use ApiResponse<T> (success, message, data, timestamp)
- Secrets (DB password, JWT secret) live in gitignored application-local.properties
- Spring Boot 4.1.x (3.5.x out of OSS support, 4.0 ends Dec 2026)
- JWT via Spring's built-in resource server (Nimbus), HS256, access token 30 min, claims: sub (user id), email, roles
- Refresh tokens: random, stored as SHA-256 hash, rotated on every use; reuse of a revoked token revokes all of that user's tokens
- 401 = not authenticated, 403 = no permission or suspended; both return JSON
- SecurityConfig: specific rules first, anyRequest().authenticated() always last
- Ownership: the user id always comes from the token, never from the URL or body
- Profile row is created lazily on the first PUT /profiles/me (registration code untouched)
- /profiles/{userId} uses the user id. Hidden, missing, suspended and unfilled profiles all return the same 404, so ids cannot be probed
- Public profile view never includes email, account status, timestamps or privacy settings
- CONNECTIONS_ONLY acts like HIDDEN until Module 7 exists
- Lifestyle options describe living habits only. The food option is a kitchen rule, not a question about the person. No protected characteristics are used for matching
- ActiveUserService is the one place that checks "user exists and is not suspended"
- Money fields use BigDecimal (DECIMAL 10,2); timestamps are stored in UTC
- ddl-auto=update during development (revisit before deployment)

## Known issues and pending work
- No rate limiting on login or resend-verification
- [EMAIL STUB] log line must be replaced by a real sender and removed before deployment
- Suspended check is done per service via ActiveUserService; tokens of suspended users still pass the JWT filter for up to 30 min
- Role changes apply only after the user gets a new token (up to 30 min)
- Access tokens stay valid after logout until they expire
- No cleanup job for expired tokens
- Admin accounts are created by SQL only
- City and locality are compared case-sensitively until the matching module handles it
- Test class still named BackendApplicationTests
- No automated tests yet (planned: unit tests for the matching algorithm)

## Testing status
- Modules 0, 1 and 2: manual tests with Postman passed
- Automated tests: none yet
- Frontend: not started