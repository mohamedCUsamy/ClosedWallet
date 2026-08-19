# ClosedWallet Admin Postman Tests

This document contains the admin-only API checks for the ClosedWallet project.

## Base URL

```text
http://localhost:8081
```

## Important setup

1. Make sure PostgreSQL is running.
2. Make sure the database `closedwallet` exists.
3. Make sure the app is running:

```bash
./mvnw.cmd spring-boot:run
```

4. Create an admin user manually in the database with `role = ADMIN`.
5. Log in with that user to get the JWT token.

---

## 1) Register a normal user

### Request

```http
POST /api/auth/register
Content-Type: application/json
```

### Body

```json
{
  "name": "Ahmed Ali",
  "email": "ahmed@example.com",
  "phoneNumber": "01012345678",
  "password": "123456",
  "confirmPassword": "123456"
}
```

### Expected result

- Status: `200 OK`
- A new user is created with role `USER`
- A wallet is created automatically for the user

---

## 2) Admin Login

### Request

```http
POST /api/auth/login
Content-Type: application/json
```

### Body

```json
{
  "email": "admin@closedwallet.com",
  "password": "admin123"
}
```

### Expected result

- Status: `200 OK`
- Response contains a JWT token

Add this header to subsequent admin requests:

```http
Authorization: Bearer <admin_token>
```

---

## 3) Get all users

```http
GET /api/admin/users
Authorization: Bearer <admin_token>
```

Expected:
- Status: `200 OK`
- Response: list of users with wallet info and balances

---

## 4) Get all wallets

```http
GET /api/admin/wallets
Authorization: Bearer <admin_token>
```

Expected:
- Status: `200 OK`
- Response: list of all wallets

---

## 5) Get all transactions

```http
GET /api/admin/transactions
Authorization: Bearer <admin_token>
```

Expected:
- Status: `200 OK`
- Response: all system transactions

---

## 6) Create merchant

```http
POST /api/admin/merchants
Authorization: Bearer <admin_token>
Content-Type: application/json
```

### Body

```json
{
  "name": "KFC",
  "email": "kfc@example.com",
  "phone": "01012345678",
  "logoPath": "/images/kfc.png",
  "category": "FOOD"
}
```

Expected:
- Status: `200 OK` or `201 Created`
- Merchant created
- Merchant wallet created with zero balance and status `ACTIVE`

---

## 7) Freeze wallet

```http
POST /api/admin/wallets/1/freeze
Authorization: Bearer <admin_token>
```

Expected:
- Status: `200 OK`
- Wallet status changed to `FROZEN`
- AdminAuditLog recorded the action

---

## 8) Unfreeze wallet

```http
POST /api/admin/wallets/1/unfreeze
Authorization: Bearer <admin_token>
```

Expected:
- Status: `200 OK`
- Wallet status changed to `ACTIVE`
- AdminAuditLog recorded the action

---

## 9) Forbidden for non-admin

```http
GET /api/admin/users
Authorization: Bearer <user_token>
```

Expected:
- Status: `403 Forbidden`

---

## 10) Unauthenticated request

```http
GET /api/admin/users
```

Expected:
- Status: `401 Unauthorized`

---

## 11) Rejected request error cases for Postman

### A. Missing Authorization header

```http
GET /api/admin/users
```

Expected:
- Status: `401 Unauthorized`
- Reason: no JWT token supplied

### B. Invalid or expired token

```http
GET /api/admin/users
Authorization: Bearer invalid_token_here
```

Expected:
- Status: `401 Unauthorized`
- Reason: token verification fails

### C. Non-admin user calls admin endpoint

```http
GET /api/admin/users
Authorization: Bearer <user_token>
```

Expected:
- Status: `403 Forbidden`
- Reason: user has no `ROLE_ADMIN`

### D. Wrong HTTP method used for admin action

```http
GET /api/admin/wallets/1/freeze
Authorization: Bearer <admin_token>
```

Expected:
- Status: `405 Method Not Allowed`
- Reason: endpoint is `POST`, not `GET`

### E. Wallet ID not found

```http
POST /api/admin/wallets/999999/freeze
Authorization: Bearer <admin_token>
```

Expected:
- Status: `500` at the moment, because the service throws a generic `RuntimeException("Wallet not found")`
- This should ideally become `404 Not Found` in a production-grade version

### F. Merchant creation body missing required fields

```http
POST /api/admin/merchants
Authorization: Bearer <admin_token>
Content-Type: application/json
```

Body:

```json
{
  "name": ""
}
```

Expected:
- Status: `400 Bad Request` once input validation is added
- Currently this may still fail with a generic server error if validation is not enforced

---

## Notes

- Regular registration creates users with role `USER`.
- Admin must be inserted manually in the database.
- All admin actions should be logged to `AdminAuditLog`.
- Frozen wallets must block send/receive actions.
- The most common Postman rejections you will hit are: `401`, `403`, and `405`.
