# 🛒 Core Domain Features

## 1.Catalog (Products & Categories)
- Create/read/update/delete products 
- Assign products to categories 
- Basic search, pagination, and filtering 
- Price, stock, description, images

## 2. Users & Authentication
- User registration (email + password)
- Login (JWT or session later, but can skip security for now)
- Roles: CUSTOMER, ADMIN

## 3. Cart
- Add/remove/update items in cart 
- Calculate total (with quantity, price)
- Guest cart (optional)

## 4. Orders
- Create order from cart 
- Order contains items, quantities, price at purchase time 
- Track order status: PENDING, PAID, SHIPPED, CANCELLED

## 5. Checkout & Payments (MVP = Fake Payment)
- Place order → mark as PENDING 
- Simulate payment success → mark as PAID 
- Keep it fake for MVP, replace later with Stripe/PayPal

# 🛠️ Non-Functional MVP Features
- Database migrations (Flyway) → versioned schema 
- Logging (Logback JSON logs, correlation IDs)
- Validation (Bean Validation on DTOs/entities)
- Error handling (@ControllerAdvice + Problem Details / RFC7807)
- API documentation (OpenAPI/Swagger with springdoc)
- Testing 
  - Unit tests for domain logic 
  - Integration tests with Testcontainers for PostgreSQL 
  - E2E tests hitting REST endpoints 
- CI/CD basics: GitHub Actions → run tests + build Docker image

## 🧑‍💼 Admin MVP Features

- Admin can create/update/delete products and categories
- Admin can view all orders

## 🔮 Optional (Phase 2+)

- Inventory reservation (avoid overselling)
- Discounts, coupons, promotions
- Shipping service (address, tracking)
- User profiles (addresses, preferences)
- Email notifications (order confirmation)
- Search improvements (Elasticsearch/OpenSearch)
- Caching (Redis for product catalog)

## ✅ Suggested MVP Scope for You

Since your main focus is DDD + testing + architecture, the absolute must-have MVP should be:

- Products + Categories
- Users (just Customer + Admin)
- Cart
- Orders (with fake payment step)
- Logging, Validation, Error Handling, Tests, CI
That’s enough to look “enterprise-like” and give you rich areas for:
- DDD boundaries (Catalog, Order, Cart, User)
- Extensive testing layers
- CI/CD discipline
- Future-proofing for advanced features