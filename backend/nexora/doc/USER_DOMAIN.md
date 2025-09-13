# User Domain Documentation

## Overview

The User domain implements a comprehensive user management system following Domain-Driven Design (DDD) principles and SOLID design patterns. This domain provides user registration, authentication, authorization, and management capabilities with role-based access control.

## Architecture

The User domain follows a layered architecture with clear separation of concerns:

```
┌─────────────────────────────────────────┐
│           Interface Layer               │
│  (REST Controllers, DTOs)              │
├─────────────────────────────────────────┤
│          Application Layer              │
│    (Use Cases, Workflows)              │
├─────────────────────────────────────────┤
│            Domain Layer                 │
│  (Entities, Value Objects, Services)   │
├─────────────────────────────────────────┤
│         Infrastructure Layer            │
│    (JPA Repositories, Adapters)        │
└─────────────────────────────────────────┘
```

## Domain Model

### Core Entities

#### User (Aggregate Root)
The `User` entity serves as the aggregate root for the User domain. It encapsulates all user-related data and behavior.

**Properties:**
- `id`: Unique identifier (UUID)
- `firstName`: User's first name (2-50 characters)
- `lastName`: User's last name (2-50 characters)
- `email`: User's email address (Email value object)
- `password`: User's password (Password value object)
- `role`: User's role (Role value object)
- `active`: Account status (boolean)
- `emailVerified`: Email verification status (boolean)
- `lastLoginAt`: Last login timestamp
- `createdAt`: Account creation timestamp
- `updatedAt`: Last update timestamp

**Business Methods:**
- `getFullName()`: Returns concatenated first and last name
- `activate()`: Activates the user account
- `deactivate()`: Deactivates the user account
- `verifyEmail()`: Marks email as verified
- `updateLastLogin()`: Updates last login timestamp
- `changePassword(Password)`: Changes user password
- `changeRole(Role)`: Changes user role
- `updateProfile(String, String)`: Updates profile information
- `canPerformAdminOperations()`: Checks admin privileges
- `canPerformCustomerOperations()`: Checks customer privileges

### Value Objects

#### Email
Encapsulates email validation and behavior.

**Validation Rules:**
- Cannot be null or empty
- Must match valid email format regex
- Automatically trims whitespace
- Immutable once created

**Example:**
```java
Email email = new Email("user@example.com");
```

#### Password
Handles password security and validation.

**Validation Rules:**
- Minimum 8 characters
- Cannot be null or empty
- Provides secure string representation (toString returns "[PROTECTED]")

**Methods:**
- `fromPlainText(String)`: Creates password from plain text
- `matches(String)`: Verifies password against plain text

**Example:**
```java
Password password = Password.fromPlainText("securepassword123");
boolean isValid = password.matches("securepassword123");
```

#### Role
Represents user roles with predefined constants and privilege checking.

**Available Roles:**
- `CUSTOMER`: Standard user role
- `ADMIN`: Administrative role with full privileges
- `MANAGER`: Management role with admin privileges

**Role Hierarchy:**
1. **ADMIN** (Highest) - Full system access
2. **MANAGER** (High) - Administrative privileges
3. **CUSTOMER** (Standard) - Basic user privileges

**Privilege Methods:**
- `isAdmin()`: Returns true for ADMIN and MANAGER roles
- `isCustomer()`: Returns true for CUSTOMER role
- `isManager()`: Returns true for MANAGER role

**Example:**
```java
Role adminRole = Role.ADMIN;
Role customerRole = new Role("CUSTOMER");

boolean hasAdminPrivileges = adminRole.isAdmin(); // true
boolean canManage = customerRole.isManager(); // false
```

## Business Rules

### User Registration
1. **Email Uniqueness**: Email must be unique across all users
2. **Password Security**: Password must be at least 8 characters
3. **Role Validation**: Role must be one of: CUSTOMER, ADMIN, MANAGER
4. **Initial State**: New users start as inactive until email verification
5. **Name Requirements**: First and last names are required (2-50 characters)

### User Authentication
1. **Account Status**: User must be active to authenticate
2. **Password Verification**: Password must match stored value
3. **Login Tracking**: Last login timestamp is updated on successful authentication

### User Updates
1. **Email Uniqueness**: Email changes must maintain uniqueness
2. **Role Changes**: Only admins can change user roles
3. **Self-Protection**: Users cannot change their own role
4. **Authorization**: Users can only update their own profile or admins can update any profile

### Password Management
1. **Current Password**: Must provide current password to change
2. **New Password**: Must be different from current password
3. **Security Requirements**: New password must meet minimum requirements

### Account Management
1. **Activation**: Inactive users can be activated
2. **Deactivation**: Only admins can deactivate users
3. **Self-Protection**: Users cannot deactivate their own account
4. **Email Verification**: Users can verify their email address

## API Endpoints

### User Registration
```
POST /api/v1/users/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "securepassword123",
  "role": "CUSTOMER"
}
```

### User Authentication
```
POST /api/v1/users/authenticate
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "securepassword123"
}
```

### Get User by ID
```
GET /api/v1/users/{userId}
```

### Update User
```
PUT /api/v1/users/{userId}
X-Current-User-Id: {currentUserId}
Content-Type: application/json

{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@example.com",
  "password": "newpassword123",
  "role": "CUSTOMER"
}
```

### Change Password
```
PATCH /api/v1/users/{userId}/password
Content-Type: application/json

{
  "currentPassword": "oldpassword123",
  "newPassword": "newpassword123"
}
```

### Change Role (Admin Only)
```
PATCH /api/v1/users/{userId}/role
X-Current-User-Id: {adminUserId}
Content-Type: application/json

{
  "role": "ADMIN"
}
```

### Activate User
```
PATCH /api/v1/users/{userId}/activate
```

### Deactivate User (Admin Only)
```
DELETE /api/v1/users/{userId}
X-Current-User-Id: {adminUserId}
```

### Verify Email
```
PATCH /api/v1/users/{userId}/verify-email
```

### Query Operations

#### Get All Users (Paginated)
```
GET /api/v1/users?page=0&size=10
```

#### Get Users by Role
```
GET /api/v1/users/role/CUSTOMER?page=0&size=10
```

#### Get Active Users
```
GET /api/v1/users/active?page=0&size=10
```

#### Search Users by Name
```
GET /api/v1/users/search?name=John&page=0&size=10
```

#### Get User Statistics
```
GET /api/v1/users/statistics
```

**Response:**
```json
{
  "totalUsers": 150,
  "activeUsers": 120,
  "inactiveUsers": 30,
  "customerUsers": 140,
  "adminUsers": 5,
  "managerUsers": 5
}
```

## Role-Based Access Control

### Role Definitions

#### CUSTOMER
- **Purpose**: Standard user role for regular application users
- **Privileges**:
  - View own profile
  - Update own profile
  - Change own password
  - Authenticate and access customer features
- **Restrictions**:
  - Cannot view other users' profiles
  - Cannot change roles
  - Cannot deactivate accounts
  - Cannot access admin features

#### MANAGER
- **Purpose**: Management role with administrative privileges
- **Privileges**:
  - All CUSTOMER privileges
  - View all users
  - Update any user profile
  - Change user roles
  - Activate/deactivate users
  - Access management features
  - View user statistics
- **Restrictions**:
  - Cannot change own role
  - Cannot deactivate own account

#### ADMIN
- **Purpose**: Highest privilege role with full system access
- **Privileges**:
  - All MANAGER privileges
  - Full system administration
  - Complete user management
  - System configuration access
  - Audit and monitoring capabilities
- **Restrictions**:
  - Cannot change own role
  - Cannot deactivate own account

### Authorization Flow

1. **Authentication**: User provides credentials (email/password)
2. **Role Retrieval**: System retrieves user's role and permissions
3. **Permission Check**: System verifies user has required permissions
4. **Operation Execution**: If authorized, operation proceeds
5. **Audit Logging**: All operations are logged for security

## Data Transfer Objects (DTOs)

### Request DTOs

#### RegisterUserRequest
```java
{
  "firstName": "string (required, 2-50 chars)",
  "lastName": "string (required, 2-50 chars)",
  "email": "string (required, valid email)",
  "password": "string (required, min 8 chars)",
  "role": "string (required, CUSTOMER|ADMIN|MANAGER)"
}
```

#### AuthenticateUserRequest
```java
{
  "email": "string (required, valid email)",
  "password": "string (required)"
}
```

#### UpdateUserRequest
```java
{
  "firstName": "string (required, 2-50 chars)",
  "lastName": "string (required, 2-50 chars)",
  "email": "string (required, valid email)",
  "password": "string (required, min 8 chars)",
  "role": "string (required, CUSTOMER|ADMIN|MANAGER)"
}
```

#### ChangePasswordRequest
```java
{
  "currentPassword": "string (required)",
  "newPassword": "string (required, min 8 chars)"
}
```

#### ChangeRoleRequest
```java
{
  "role": "string (required, CUSTOMER|ADMIN|MANAGER)"
}
```

### Response DTOs

#### UserResponse
```java
{
  "id": "string (UUID)",
  "firstName": "string",
  "lastName": "string",
  "fullName": "string",
  "email": "string",
  "role": "string",
  "active": "boolean",
  "emailVerified": "boolean",
  "lastLoginAt": "string (ISO timestamp)",
  "createdAt": "string (ISO timestamp)",
  "updatedAt": "string (ISO timestamp)"
}
```

#### UserStatisticsResponse
```java
{
  "totalUsers": "number",
  "activeUsers": "number",
  "inactiveUsers": "number",
  "customerUsers": "number",
  "adminUsers": "number",
  "managerUsers": "number"
}
```

## Error Handling

### Common Error Scenarios

#### Validation Errors
- **400 Bad Request**: Invalid input data
- **422 Unprocessable Entity**: Business rule violations

#### Authentication Errors
- **401 Unauthorized**: Invalid credentials
- **403 Forbidden**: Insufficient permissions

#### Resource Errors
- **404 Not Found**: User not found
- **409 Conflict**: Email already exists

### Error Response Format
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Email already exists: user@example.com",
  "path": "/api/v1/users/register"
}
```

## Security Considerations

### Password Security
- **Hashing**: Passwords should be hashed using secure algorithms (BCrypt, Argon2)
- **Minimum Length**: 8 characters minimum
- **Storage**: Never store plain text passwords
- **Transmission**: Use HTTPS for all password-related operations

### Email Security
- **Validation**: Strict email format validation
- **Uniqueness**: Enforce email uniqueness across the system
- **Verification**: Require email verification for account activation

### Role Security
- **Principle of Least Privilege**: Users get minimum required permissions
- **Role Hierarchy**: Clear hierarchy with defined privilege levels
- **Self-Protection**: Users cannot escalate their own privileges

### Session Management
- **JWT Tokens**: Use secure JWT tokens for session management
- **Token Expiration**: Implement reasonable token expiration times
- **Refresh Tokens**: Use refresh tokens for long-term sessions

## Testing Strategy

### Unit Tests
- **Domain Service Tests**: Test business logic and validation rules
- **Value Object Tests**: Test validation and behavior of value objects
- **Entity Tests**: Test entity behavior and business methods

### Integration Tests
- **Repository Tests**: Test data persistence and retrieval
- **Application Service Tests**: Test use case orchestration
- **Controller Tests**: Test HTTP endpoint behavior

### Test Coverage Areas
- **Happy Path**: Successful operations
- **Validation**: Input validation and business rules
- **Authorization**: Role-based access control
- **Error Handling**: Exception scenarios
- **Edge Cases**: Boundary conditions

## Performance Considerations

### Database Optimization
- **Indexes**: Index on email, role, and active status
- **Pagination**: Use pagination for large result sets
- **Query Optimization**: Optimize queries for common operations

### Caching Strategy
- **User Data**: Cache frequently accessed user data
- **Role Permissions**: Cache role-based permissions
- **Statistics**: Cache user statistics with TTL

### Scalability
- **Horizontal Scaling**: Design for horizontal scaling
- **Database Sharding**: Consider sharding for large user bases
- **Load Balancing**: Use load balancers for high availability

## Monitoring and Observability

### Metrics
- **User Registration Rate**: Track new user registrations
- **Authentication Success Rate**: Monitor login success/failure rates
- **Role Distribution**: Track user role distribution
- **API Response Times**: Monitor endpoint performance

### Logging
- **Security Events**: Log authentication and authorization events
- **Business Events**: Log user management operations
- **Error Events**: Log and alert on errors

### Alerts
- **Failed Authentication**: Alert on suspicious login attempts
- **Role Changes**: Alert on privilege escalation
- **System Errors**: Alert on system failures

## Future Enhancements

### Planned Features
- **Multi-Factor Authentication**: Add MFA support
- **OAuth Integration**: Support for OAuth providers
- **User Groups**: Implement user group management
- **Audit Trail**: Comprehensive audit logging
- **Password Policies**: Configurable password requirements

### Scalability Improvements
- **Event Sourcing**: Implement event sourcing for audit trails
- **CQRS**: Separate read and write models
- **Microservices**: Split into dedicated user service
- **API Gateway**: Implement API gateway for routing

## Conclusion

The User domain provides a robust, secure, and scalable foundation for user management in the Nexora application. It follows industry best practices for security, maintainability, and performance while providing comprehensive role-based access control and user management capabilities.

The implementation adheres to Domain-Driven Design principles, ensuring clear separation of concerns and maintainable code structure. The comprehensive test coverage and documentation ensure reliability and ease of maintenance for future development.
