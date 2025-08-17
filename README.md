# Spring E-commerce Orders

## JDBC to JPA Migration - Step 1

### Migration Setup
- [x] Update `build.gradle.kts` with JPA dependencies
- [x] Configure `application.properties` for H2/MySQL compatibility
- [x] Set up `data.sql` for test data

---

## Product
- [x] **Entity** - JPA annotations, validation, relationships
- [x] **Repository** - JpaRepository with custom queries
- [x] **Service** - CRUD operations, business logic
- [x] **Controller** - AdminController endpoints
- [x] **Tests** - Unit and integration tests

## ProductOption
- [x] **Entity** - JPA mapping, business methods (`subtract`, `toString`)
- [x] **Repository** - JpaRepository with name validation
- [x] **Service** - Unified add/update with `saveProductOption()`
- [x] **Controller** - REST endpoints in AdminController
- [x] **Tests** - Service layer testing

## Member
- [x] **Entity** - User management, roles, cart relationship
- [x] **Repository** - JpaRepository migration from JDBC
- [x] **Service** - Authentication, user operations
- [x] **Controller** - User management endpoints
- [x] **Tests** - Entity and service testing

## Cart
- [x] **Entity** - Shopping cart with member relationship
- [x] **Repository** - JpaRepository with cart operations
- [x] **Service** - Cart management logic
- [x] **Controller** - Cart operations
- [x] **Tests** - Integration testing

## CartItem
- [x] **Entity** - Cart-product relationship, `modify()` method
- [x] **Repository** - JpaRepository with `@Modifying` queries
- [x] **Service** - Refactored `saveCartItem()` (add/update unified)
- [x] **Controller** - CRUD operations
- [x] **Tests** - Unit tests with Mockito, integration tests with data.sql

---

## Key points

### Service Layer Refactoring
- [x] **Single Responsibility** - Broke down complex methods into focused private functions
- [x] **Unified Operations** - `saveProductOption()` and `saveCartItem()` handle both add/update
- [x] **Kotlin Style** - Nullable parameters with defaults, proper type inference

### Testing Strategy
- [x] **Unit Tests** - Mock-based testing for service logic
- [x] **Integration Tests** - Full Spring context with real database
- [x] **Test Data** - Leveraging `data.sql` for consistent test scenarios

### Next Steps
- [x] **Pagination** - Add `Pageable` support to controllers
- [x] **Inventory Management** - Stock tracking and validation
- [ ] **Performance** - Query optimization and caching

---

## External API Integration - Step 2 (Feature-list)

### Step 2-1: Stripe Payment Integration
- [ ] Implement Stripe Payment API integration (/config)
- [ ] Create Order related entities
- [ ] Create Payment related entities
- [ ] Place order functionality
  - [ ] Decrease product option stock on successful payment
  - [ ] Remove ordered items from user's cart (if it exists)
  - [ ] Handle payment failures with clear error messages  (expired session, invalid payment method, insufficient balance, etc.)
- [ ] Payment error handling for declined payments (using Stripe test cards)

### Step 2-2: Orders Management
- [ ] Implement Orders API endpoints
- [ ] Display order information:
    - [] (mandatory) Order date and time, Order status, Purchased items, Checkout session Id (issued by stripe), Payment amount
    - [] (optional) other payment-related fields
- [ ] Design database schema for orders and payments

### Step 2-3: Deployment
- [ ] Create automated deployment script
- [ ] Configure CORS for client-server interaction (?)
- [ ] Handle security considerations for production deployment
    - [] for example, when server and client have different `Origin` values
- [] (optional) implement `HTTPS`

--- 
## External API Integration - Step 2 (Development-plan)

### Product Structure
```
src/main/kotlin/ecommerce/
├── controller/                 # REST API endpoints (e.g., Products, Cart)
├── service/                    # Business logic (e.g., ProductService, CartService)
├── repository/                 # Spring Data JPA repositories
├── domain/                     # JPA entities (the core business objects)
├── web/dto/                    # Data Transfer Objects (Request/Response)
└── config/                     # Spring configurations (e.g., WebMvcConfig)
```

### Domain Models
* **`Member`**: 
    * Represents a user account with an email, password, and role.
* **`Product`**: 
    * A product in the catalog with basic information like name and brand. 
    * It contains a list of `ProductOption`s.
* **`ProductOption`**: 
    * A specific variant of a product (e.g., size, color) with its own price and stock quantity. 
    * This is the purchasable unit.
* **`Cart`**: 
    * A shopping cart linked to a `Member`.
* **`CartItem`**: 
    * An item within a `Cart`, linked to a specific `ProductOption` and tracking the quantity.


### Step 2-1: Stripe Payment Integration
- [ ] Implement Stripe Payment API integration (/config)
    - [ ] class `StripeProperties(val secretKey: String)`
    - [ ] class `StripeClient(private val stripePropierties)`
- [ ] Create Order related entities
    - [ ] class `CreateOrderRequest(val customerName, val customerEmail, val items)`
- [ ] Create Payment related entities
    - [ ] class `CreatePaymentIntentRequest(val orderId: Long)`
    - [ ] class `CreatePaymentIntentResponse(val clientSecret: String)`
- [ ] Place order functionality
    - [ ] Decrease product option stock on successful payment
    - [ ] Remove ordered items from user's cart (if it exists)
    - [ ] Handle payment failures with clear error messages  (expired session, invalid payment method, insufficient balance, etc.)
        - [ ] add annotation to `Application` : `@EnableConfigurationProperties(StripeProperties::class)`
        - [ ] class `OrderController` contains
            - [ ] `createOrder`, `getOrder`, `createPaymentIntent`
        - [ ] class `OrderService`, `OrderRepository`, `OrderStatus` 
        - [ ] add your db and stripe credentials `resources/application.properties`
- [ ] Payment error handling for declined payments (using Stripe test cards)

### Step 2-2: Orders Management
- [ ] Implement Orders API endpoints
- [ ] Display order information:
    - [] (mandatory) Order date and time, Order status, Purchased items, Checkout session Id (issued by stripe), Payment amount
    - [] (optional) other payment-related fields
- [ ] Design database schema for orders and payments

### Step 2-3: Deployment
- [ ] Create automated deployment script
- [ ] Configure CORS for client-server interaction (?)
- [ ] Handle security considerations for production deployment
    - [] for example, when server and client have different `Origin` values
- [] (optional) implement `HTTPS`
