# 📔 Journal Application - Complete Project Structure & Documentation

## 🎯 Project Overview
This is a **Spring Boot REST API application** for managing personal journal entries with **user authentication and MongoDB storage**. It's a secure journal app where users can create, read, update, and delete their journal entries.

---

## 📦 Technology Stack
- **Framework**: Spring Boot 4.0.1
- **Java Version**: 21
- **Database**: MongoDB Atlas (Cloud)
- **Security**: Spring Security with BCrypt password encoding
- **Authentication**: HTTP Basic Authentication
- **Build Tool**: Maven
- **Additional Libraries**: Lombok (for reducing boilerplate code)

---

## 🗂️ Project Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENT (Postman/Browser)                 │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP Requests
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                  SPRING SECURITY LAYER                      │
│  (SpringSecurity.java - Authentication & Authorization)     │
└────────────────────────┬────────────────────────────────────┘
                         │ Authenticated Requests
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    CONTROLLER LAYER                         │
│  - JournalEntryController.java (Journal endpoints)          │
│  - UserController.java (User management endpoints)          │
│  - PublicController.java (Public/health endpoints)          │
└────────────────────────┬────────────────────────────────────┘
                         │ Business Logic Calls
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      SERVICE LAYER                          │
│  - JournalEntryService.java (Journal business logic)        │
│  - UserService.java (User business logic)                   │
│  - UserDetailsServiceImpl.java (Auth user loading)          │
└────────────────────────┬────────────────────────────────────┘
                         │ Database Operations
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    REPOSITORY LAYER                         │
│  - JournalEntryRepository.java (Journal DB access)          │
│  - UserRepository.java (User DB access)                     │
└────────────────────────┬────────────────────────────────────┘
                         │ MongoDB Operations
                         ▼
┌─────────────────────────────────────────────────────────────┐
│              MONGODB ATLAS (Cloud Database)                 │
│  Database: journaldb2                                       │
│  Collections: journal_entries, users                        │
└─────────────────────────────────────────────────────────────┘
```

---

## 📁 Detailed File Structure & Connections

### 1️⃣ **Configuration Files**

#### **pom.xml** (Project Dependencies)
- **Purpose**: Maven configuration file defining all dependencies
- **Key Dependencies**:
  - `spring-boot-starter-web`: REST API support
  - `spring-boot-starter-data-mongodb`: MongoDB integration
  - `spring-boot-starter-security`: Authentication/Authorization
  - `lombok`: Reduces boilerplate code (getters/setters)
- **Connects to**: All Java files (provides libraries)

#### **application.properties** (Application Configuration)
```
spring.data.mongodb.uri=mongodb+srv://[credentials]@cluster0...
spring.data.mongodb.database=journaldb2
```
- **Purpose**: Database connection and app settings
- **Current Setup**: 
  - Connected to MongoDB Atlas (Cloud)
  - Database name: `journaldb2`
  - Auto-index creation enabled
- **Connects to**: MongoDB (at runtime)

---

### 2️⃣ **Main Application File**

#### **JournalApplication.java**
```java
@SpringBootApplication
@EnableTransactionManagement
```
- **Purpose**: Application entry point
- **Key Features**:
  - Starts the Spring Boot application
  - Enables MongoDB transactions (for ACID compliance)
  - Creates `MongoTransactionManager` bean
- **Connects to**: Everything (bootstraps entire app)

---

### 3️⃣ **Entity Layer** (Data Models)

#### **JournalEntry.java**
```java
@Document(collection = "journal_entries")
@Data
```
- **Purpose**: Represents a journal entry document in MongoDB
- **Fields**:
  - `id` (ObjectId): Unique identifier
  - `title` (String): Entry title
  - `content` (String): Entry content
  - `date` (LocalDateTime): Auto-set timestamp
- **MongoDB Collection**: `journal_entries`
- **Connects to**: 
  - JournalEntryRepository (DB operations)
  - User entity (referenced in User's journalEntries list)

#### **User.java**
```java
@Document(collection = "users")
@Data
```
- **Purpose**: Represents a user document in MongoDB
- **Fields**:
  - `id` (ObjectId): Unique identifier
  - `userName` (String): Unique username (indexed)
  - `password` (String): Encrypted password
  - `journalEntries` (List<JournalEntry>): User's journal entries
  - `roles` (List<String>): User roles (e.g., "USER")
- **MongoDB Collection**: `users`
- **Special Annotations**:
  - `@DBRef`: Creates reference to JournalEntry documents
  - `@Indexed(unique = true)`: Ensures unique usernames
- **Connects to**: 
  - UserRepository (DB operations)
  - JournalEntry (one-to-many relationship)

---

### 4️⃣ **Repository Layer** (Database Access)

#### **JournalEntryRepository.java**
```java
extends MongoRepository<JournalEntry, ObjectId>
```
- **Purpose**: Database operations for journal entries
- **Inherited Methods**: save, findAll, findById, deleteById
- **Connects to**: 
  - JournalEntry entity
  - JournalEntryService (used by)
  - MongoDB (performs operations)

#### **UserRepository.java**
```java
extends MongoRepository<User, ObjectId>
```
- **Purpose**: Database operations for users
- **Custom Methods**:
  - `findByUserName(String userName)`: Find user by username
  - `deleteByUserName(String userName)`: Delete user by username
- **Connects to**: 
  - User entity
  - UserService & UserDetailsServiceImpl (used by)
  - MongoDB (performs operations)

---

### 5️⃣ **Service Layer** (Business Logic)

#### **JournalEntryService.java**
- **Purpose**: Business logic for journal entries
- **Key Methods**:
  - `saveEntry(JournalEntry, userName)`: Save entry and link to user (transactional)
  - `getAll()`: Get all entries
  - `findById(ObjectId)`: Find specific entry
  - `deleteById(ObjectId, userName)`: Delete entry and remove from user
- **Connects to**:
  - JournalEntryRepository (DB operations)
  - UserService (to get user and update their entries list)
  - JournalEntryController (called by)

#### **UserService.java**
- **Purpose**: Business logic for user management
- **Key Methods**:
  - `saveEntry(User)`: Save user with encrypted password
  - `findByUserName(String)`: Find user by username
  - `getAll()`, `findById()`, `deleteById()`
- **Password Handling**: Auto-encrypts passwords using BCrypt
- **Role Management**: Auto-assigns "USER" role if not specified
- **Connects to**:
  - UserRepository (DB operations)
  - PasswordEncoder (from SpringSecurity)
  - UserController & JournalEntryService (called by)

#### **UserDetailsServiceImpl.java**
```java
implements UserDetailsService
```
- **Purpose**: Spring Security authentication user loading
- **Key Method**: `loadUserByUsername(String)` - loads user for authentication
- **Connects to**:
  - UserRepository (to fetch user)
  - Spring Security (authentication process)
- **How it works**: When user logs in, Spring Security calls this to verify credentials

---

### 6️⃣ **Security Configuration**

#### **SpringSecurity.java**
```java
@Configuration
@EnableWebSecurity
```
- **Purpose**: Security configuration and access control
- **Key Beans**:
  - `SecurityFilterChain`: Defines URL access rules
  - `PasswordEncoder`: BCrypt password encoder
- **Access Rules**:
  - **Public**: `/user` (POST - user creation), `/health` (GET)
  - **Authenticated**: `/journal/**`, `/user/**` (other methods)
  - **Authentication Type**: HTTP Basic Auth
  - **Session**: Stateless (no sessions)
- **Connects to**:
  - UserService (uses passwordEncoder)
  - All Controllers (applies security)
  - UserDetailsServiceImpl (for authentication)

---

### 7️⃣ **Controller Layer** (REST API Endpoints)

#### **JournalEntryController.java**
**Base URL**: `/journal`
**Security**: All endpoints require authentication

| HTTP Method | Endpoint | Purpose | Body/Params |
|-------------|----------|---------|-------------|
| GET | `/{userName}` | Get all journals of a user | Path: userName |
| POST | `/{userName}` | Create new journal entry | Path: userName, Body: JournalEntry |
| GET | `/id/{myId}` | Get specific entry by ID | Path: myId (ObjectId) |
| PUT | `/id/{userName}/{myId}` | Update journal entry | Path: userName, myId, Body: JournalEntry |
| DELETE | `/id/{userName}/{myId}` | Delete journal entry | Path: userName, myId |

**Connects to**:
- JournalEntryService (business logic)
- UserService (to fetch user)
- Spring Security (authentication)

#### **UserController.java**
**Base URL**: `/user`
**Security**: Requires authentication

| HTTP Method | Endpoint | Purpose | Body/Params |
|-------------|----------|---------|-------------|
| PUT | `/` | Update current user | Body: User (username/password) |
| DELETE | `/` | Delete current user | Uses authenticated username |

**Special Features**:
- Uses `SecurityContextHolder.getContext().getAuthentication()` to get logged-in user
- Auto-retrieves username from authentication context

**Connects to**:
- UserService (business logic)
- UserRepository (delete operation)
- Spring Security (gets authenticated user)

#### **PublicController.java**
**Base URL**: `/public`
**Security**: No authentication required

| HTTP Method | Endpoint | Purpose | Body/Params |
|-------------|----------|---------|-------------|
| GET | `/health-check` | Health check | None |
| POST | `/create-user` | Register new user | Body: User |

**Purpose**: Public endpoints for user registration and health monitoring

**Connects to**:
- UserService (user creation)

---

## 🔄 Data Flow Examples

### Example 1: Creating a Journal Entry

```
1. User sends POST request to /journal/{userName}
   ↓
2. Spring Security intercepts → Validates credentials
   ↓
3. JournalEntryController.createEntry() receives request
   ↓
4. Calls JournalEntryService.saveEntry(entry, userName)
   ↓
5. Service sets timestamp on entry
   ↓
6. Service saves entry to MongoDB via JournalEntryRepository
   ↓
7. Service calls UserService.findByUserName(userName)
   ↓
8. Adds entry reference to User's journalEntries list
   ↓
9. Saves updated User to MongoDB
   ↓
10. Returns HTTP 201 CREATED to client
```

### Example 2: User Login Flow

```
1. User sends request with Basic Auth (username:password)
   ↓
2. Spring Security intercepts request
   ↓
3. Calls UserDetailsServiceImpl.loadUserByUsername()
   ↓
4. Service queries UserRepository.findByUserName()
   ↓
5. MongoDB returns User document
   ↓
6. Service builds Spring Security UserDetails object
   ↓
7. Spring Security compares passwords (BCrypt)
   ↓
8. If match → Authentication succeeds
   ↓
9. Request proceeds to Controller
```

---

## 🗄️ MongoDB Structure

### Database: `journaldb2`

#### Collection: `journal_entries`
```json
{
  "_id": ObjectId("..."),
  "title": "My First Entry",
  "content": "Today was a good day...",
  "date": ISODate("2026-03-06T10:30:00Z")
}
```

#### Collection: `users`
```json
{
  "_id": ObjectId("..."),
  "userName": "harsh",
  "password": "$2a$10$...", // BCrypt encrypted
  "journalEntries": [
    DBRef("journal_entries", ObjectId("...")),
    DBRef("journal_entries", ObjectId("..."))
  ],
  "roles": ["USER"]
}
```

---

## 🔐 Security Features

1. **Password Encryption**: BCrypt hashing (automatic in UserService)
2. **Authentication**: HTTP Basic Auth (username:password in headers)
3. **Authorization**: Role-based (currently only "USER" role)
4. **Session Management**: Stateless (no server sessions)
5. **CSRF Protection**: Disabled (for REST API)

---

## 🚀 How to Use the API

### 1. Register a New User (No Auth Required)
```
POST /public/create-user
Body: {"userName": "john", "password": "secret123"}
```

### 2. Login & Create Journal Entry (Auth Required)
```
POST /journal/john
Headers: Authorization: Basic am9objpzZWNyZXQxMjM=  (base64 encoded)
Body: {"title": "My Day", "content": "It was amazing!"}
```

### 3. Get All Your Journals
```
GET /journal/john
Headers: Authorization: Basic am9objpzZWNyZXQxMjM=
```

### 4. Update Your Profile
```
PUT /user
Headers: Authorization: Basic am9objpzZWNyZXQxMjM=
Body: {"userName": "john_new", "password": "newpassword"}
```

---

## 🔗 File Connection Summary

```
JournalApplication.java (Main Entry)
    ↓
SpringSecurity.java (Security Config)
    ↓
Controllers (REST Endpoints)
    ├─ JournalEntryController → JournalEntryService → JournalEntryRepository → MongoDB
    ├─ UserController → UserService → UserRepository → MongoDB
    └─ PublicController → UserService → UserRepository → MongoDB
    
Authentication Flow:
    Spring Security → UserDetailsServiceImpl → UserRepository → MongoDB
    
Entities:
    User (has many) JournalEntry (@DBRef relationship)
```

---

## 📝 Key Points to Remember

1. **Layered Architecture**: Controller → Service → Repository → Database
2. **Security**: All `/journal` and `/user` endpoints need authentication
3. **Transactions**: Journal entry creation is transactional (both entry and user update succeed or fail together)
4. **Password Safety**: Never stored in plain text, always BCrypt encrypted
5. **MongoDB**: Uses MongoDB Atlas cloud database (not local)
6. **Stateless**: No sessions stored on server (authenticate each request)

---

## 🎓 Learning Notes

- **@SpringBootApplication**: Enables auto-configuration and component scanning
- **@RestController**: Combines @Controller + @ResponseBody
- **@Service/@Component**: Marks classes as Spring beans
- **@Autowired**: Dependency injection
- **@Document**: Marks class as MongoDB document
- **@DBRef**: Creates reference between documents (like foreign key)
- **@Transactional**: Ensures operations complete together or roll back
- **MongoRepository**: Provides CRUD operations automatically

---

## 🐛 Common Issues & Solutions

### Issue: "localhost:27017" connection instead of MongoDB Atlas
**Solution**: Check `application.properties` - ensure `spring.data.mongodb.uri` is set correctly with Atlas URI

### Issue: 401 Unauthorized error
**Solution**: 
- Ensure user is registered via `/public/create-user`
- Use correct Basic Auth header (username:password base64 encoded)
- Check credentials match database

### Issue: "mvn clean install" fails with file locked
**Solution**: Stop the running Spring Boot application first, then run Maven commands

### Issue: User not found even though it exists in database
**Solution**: Verify the `userName` field matches exactly (case-sensitive)

### Issue: Circular dependency error in SpringSecurity
**Solution**: Ensure `passwordEncoder()` bean method doesn't call itself or create circular references

---

## 📊 Project Statistics

- **Total Controllers**: 3 (JournalEntryController, UserController, PublicController)
- **Total Services**: 3 (JournalEntryService, UserService, UserDetailsServiceImpl)
- **Total Repositories**: 2 (JournalEntryRepository, UserRepository)
- **Total Entities**: 2 (JournalEntry, User)
- **Total Config Classes**: 1 (SpringSecurity)
- **Total Public Endpoints**: 2 (health-check, create-user)
- **Total Protected Endpoints**: 7 (all journal and user operations)

---

This application follows **Spring Boot best practices** with clear separation of concerns and proper security implementation! 🎉

**Last Updated**: March 7, 2026

