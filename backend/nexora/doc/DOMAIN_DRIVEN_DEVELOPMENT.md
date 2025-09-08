# 📦 Domain responsibilities
## 1. catalog

Aggregates: Product, Category

Use cases: search, list, CRUD products/categories

Entities/VOs: Price, StockQuantity

## 2. user

Aggregates: User (customer/admin)

Entities/VOs: Email, Role, Password (later hashed)

Use cases: registration, login, get profile

## 3. cart

Aggregates: Cart

Entities/VOs: CartItem

Use cases: add/remove item, calculate totals

## 4. order

Aggregates: Order

Entities/VOs: OrderItem, OrderStatus

Use cases: checkout (create order from cart), mark as paid/shipped

## 5. shared

Exceptions (DomainException, NotFoundException, ValidationException)

Domain events (e.g., OrderPlacedEvent)

Interfaces/contracts (base repository interfaces)

Utilities (maybe UUID generator, money formatting)