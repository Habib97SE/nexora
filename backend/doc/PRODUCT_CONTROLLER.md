# ProductController Documentation

## Overview

The `ProductController` is a REST controller that serves as the interface layer in the Domain-Driven Design (DDD) architecture. It handles HTTP requests and responses for product-related operations, providing a clean API for external clients to interact with the product management system.

## Purpose

The `ProductController` is responsible for:

1. **HTTP Request/Response Handling**: Converting HTTP requests into domain operations and domain objects into HTTP responses
2. **Input Validation**: Ensuring that incoming data meets the required format and constraints
3. **Error Handling**: Providing meaningful error responses to clients
4. **API Documentation**: Exposing a clear, RESTful API for product operations
5. **Logging**: Recording important operations and errors for monitoring and debugging

## Architecture Integration

The controller follows the DDD layered architecture:

```
┌─────────────────────────────────────┐
│           ProductController         │ ← Interface Layer
│         (REST Endpoints)            │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│      ProductApplicationService      │ ← Application Layer
│      (Orchestration & Workflow)     │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│       ProductDomainService          │ ← Domain Layer
│      (Business Logic & Rules)       │
└─────────────────┬───────────────────┘
                  │
┌─────────────────▼───────────────────┐
│        ProductRepository            │ ← Infrastructure Layer
│        (Data Persistence)           │
└─────────────────────────────────────┘
```

## Applied SOLID Principles

### 1. Single Responsibility Principle (SRP)
- **Applied**: The controller has a single responsibility: handling HTTP requests and responses for product operations
- **Implementation**: Each endpoint method handles one specific operation (create, read, update, delete, etc.)
- **Benefit**: Changes to HTTP handling logic are isolated and don't affect business logic

### 2. Open/Closed Principle (OCP)
- **Applied**: The controller is open for extension but closed for modification
- **Implementation**: New endpoints can be added without modifying existing code
- **Benefit**: New features can be added without risking regression in existing functionality

### 3. Liskov Substitution Principle (LSP)
- **Applied**: All endpoints follow the same contract pattern
- **Implementation**: Consistent return types (`ResponseEntity`) and error handling across all methods
- **Benefit**: Clients can rely on consistent behavior across all endpoints

### 4. Interface Segregation Principle (ISP)
- **Applied**: The controller provides focused, specific endpoints
- **Implementation**: Separate endpoints for different operations (CRUD, management, reporting)
- **Benefit**: Clients only depend on the endpoints they actually use

### 5. Dependency Inversion Principle (DIP)
- **Applied**: The controller depends on abstractions, not concrete implementations
- **Implementation**: Depends on `ProductApplicationService` interface, not concrete implementations
- **Benefit**: The controller is decoupled from specific business logic implementations

## RESTful API Design

### Base URL
```
/api/v1/products
```

### Endpoints

#### CRUD Operations
- `POST /api/v1/products` - Create a new product
- `GET /api/v1/products/{id}` - Get a product by ID
- `PUT /api/v1/products/{id}` - Update a product
- `DELETE /api/v1/products/{id}` - Deactivate a product

#### Query Operations
- `GET /api/v1/products` - Get all products (with pagination)
- `GET /api/v1/products/category/{categoryId}` - Get products by category
- `GET /api/v1/products/search` - Search products by name/description

#### Management Operations
- `PATCH /api/v1/products/{id}/stock` - Adjust product stock
- `PATCH /api/v1/products/{id}/category` - Change product category
- `PATCH /api/v1/products/{id}/price` - Update product price

#### Reporting Operations
- `GET /api/v1/products/statistics` - Get product statistics

## Data Transfer Objects (DTOs)

### Request DTOs
- `CreateProductRequest`: For creating new products
- `UpdateProductRequest`: For updating existing products
- `StockAdjustmentRequest`: For stock adjustments
- `ChangeCategoryRequest`: For category changes
- `UpdatePriceRequest`: For price updates

### Response DTOs
- `ProductResponse`: Standard product representation
- `ProductStatisticsResponse`: For reporting operations

## Error Handling

The controller implements comprehensive error handling:

1. **Application Service Errors**: Catches and wraps application service exceptions
2. **Validation Errors**: Handles input validation failures
3. **Not Found Errors**: Provides meaningful responses for missing resources
4. **Custom Exception**: `ProductControllerException` for controller-specific errors

## Logging Strategy

The controller implements structured logging:

- **INFO Level**: Successful operations and important state changes
- **ERROR Level**: Failed operations with detailed error information
- **Context**: Includes relevant IDs and operation details for debugging

## Testing Strategy

The controller is tested using:

1. **Unit Tests**: Testing controller logic in isolation using Mockito
2. **Mocking**: Mocking the `ProductApplicationService` dependency
3. **Comprehensive Coverage**: Testing all endpoints, success cases, and error scenarios
4. **DTO Testing**: Validating request/response DTOs

### Test Structure
- **CRUD Operations Tests**: Testing create, read, update, delete operations
- **Management Operations Tests**: Testing stock, category, and price management
- **DTO Classes Tests**: Testing data transfer objects
- **Exception Handling Tests**: Testing error scenarios

## Key Benefits

### 1. Clean API Design
- RESTful endpoints following HTTP standards
- Consistent response formats
- Clear error messages

### 2. Separation of Concerns
- HTTP handling separated from business logic
- Input validation separated from domain rules
- Error handling separated from core functionality

### 3. Maintainability
- Easy to add new endpoints
- Clear structure for modifications
- Comprehensive test coverage

### 4. Scalability
- Stateless design
- Efficient error handling
- Structured logging for monitoring

### 5. Client-Friendly
- Intuitive endpoint design
- Consistent response formats
- Meaningful error messages

## Usage Examples

### Creating a Product
```http
POST /api/v1/products
Content-Type: application/json

{
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "currency": "USD",
  "stockQuantity": 10,
  "categoryId": "category-uuid"
}
```

### Updating Product Stock
```http
PATCH /api/v1/products/product-uuid/stock
Content-Type: application/json

{
  "quantity": 5
}
```

### Getting Product Statistics
```http
GET /api/v1/products/statistics
```

## Integration with Application Layer

The controller delegates all business logic to the `ProductApplicationService`:

1. **Command Operations**: Create, update, delete operations
2. **Query Operations**: Read operations and searches
3. **Management Operations**: Stock, category, and price management
4. **Reporting Operations**: Statistics and analytics

This ensures that the controller remains focused on HTTP concerns while the application service handles business logic and orchestration.

## Security Considerations

While not implemented in this version, the controller is designed to support:

1. **Authentication**: User identification and authorization
2. **Input Validation**: Comprehensive request validation
3. **Rate Limiting**: Protection against abuse
4. **Audit Logging**: Tracking of all operations

## Future Enhancements

Potential improvements for the controller:

1. **Caching**: Response caching for frequently accessed data
2. **Pagination**: Enhanced pagination support
3. **Filtering**: Advanced filtering capabilities
4. **Bulk Operations**: Support for bulk product operations
5. **Event Publishing**: Publishing domain events for external systems

## Conclusion

The `ProductController` provides a clean, RESTful interface for product management operations while maintaining strict separation of concerns and following SOLID principles. It serves as the entry point for external clients while delegating business logic to the appropriate application and domain layers.
