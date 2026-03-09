# Troubleshooting 400 Bad Request Error - Journal Entry Creation

## Problem
Getting **400 Bad Request** error when trying to POST a journal entry to `/journal` endpoint, even though authorization (Basic Auth) is provided.

## Root Causes Identified

### 1. **User Not Found in Database** (Most Likely)
The authenticated username you're using in Postman doesn't exist in your MongoDB database.

**How to verify:**
- Check your MongoDB `users` collection to see if the user exists
- The username you use for Basic Auth must match exactly with what's in the database

**Solution:**
Create a user first by POSTing to `/user` endpoint (this is public):
```json
POST http://localhost:8080/user
Content-Type: application/json

{
  "userName": "testuser",
  "password": "password123"
}
```

Then use these same credentials in Postman's Authorization tab (Basic Auth).

### 2. **MongoDB Transaction Issue** (Fixed)
The `@Transactional` annotation was used in `JournalEntryService.saveEntry()` method. This requires MongoDB to run as a replica set, which is not the default configuration.

**Solution Applied:**
- Removed `@Transactional` annotation from the method
- MongoDB transactions are NOT needed for this simple operation

### 3. **Silent Exception Swallowing** (Fixed)
The controller was catching exceptions but not logging them, making debugging impossible.

**Solution Applied:**
- Added `e.printStackTrace()` and error message logging in both controller and service
- Now you can see the actual error in the console

## How to Debug Further

### Step 1: Check Console Output
After the changes, restart your Spring Boot application. When you make the POST request, check the console for error messages like:
```
Error in saveEntry: User not found: username
```

### Step 2: Verify User Exists
Connect to MongoDB and check:
```javascript
use journaldb
db.users.find()
```

Make sure a user with the username you're using in Postman exists.

### Step 3: Verify Credentials
In Postman:
1. Go to the **Authorization** tab
2. Select **Basic Auth**
3. Enter the exact username and password you used when creating the user
4. **Important**: Use the plain password, NOT the encoded one from the database

### Step 4: Check Request Body
Your JSON body should be:
```json
{
  "title": "the new title",
  "content": "the new content in the current market"
}
```

**Do NOT include:**
- `id` field (auto-generated)
- `date` field (auto-set by the service)

## Testing Steps

### 1. Create a Test User
```bash
POST http://localhost:8080/user
Content-Type: application/json

{
  "userName": "testuser",
  "password": "testpass"
}
```

### 2. Create a Journal Entry
```bash
POST http://localhost:8080/journal
Authorization: Basic testuser:testpass
Content-Type: application/json

{
  "title": "My First Entry",
  "content": "This is my first journal entry"
}
```

## Expected Behavior After Fix

### Success (201 Created):
```json
{
  "id": "507f1f77bcf86cd799439011",
  "title": "My First Entry",
  "content": "This is my first journal entry",
  "date": "2026-03-07T15:30:00"
}
```

### Error Scenarios:

1. **User not found** - Check console:
   ```
   Error in saveEntry: User not found: testuser
   ```

2. **Invalid credentials** - Response:
   ```json
   {
     "timestamp": "2026-03-07T18:30:00.000Z",
     "status": 401,
     "error": "Unauthorized"
   }
   ```

3. **Invalid JSON** - Response:
   ```json
   {
     "timestamp": "2026-03-07T18:30:00.000Z",
     "status": 400,
     "error": "Bad Request"
   }
   ```

## Code Changes Made

### File: `JournalEntryService.java`
- Removed `@Transactional` annotation
- Added explicit null check for user
- Added detailed error logging with stack trace

### File: `JournalEntryController.java`
- Added error logging in catch block to print exception details

## Next Steps

1. **Restart your Spring Boot application**
2. **Create a new user** using the `/user` endpoint
3. **Try creating a journal entry** with that user's credentials
4. **Check the console** for any error messages
5. If still failing, share the console output for further debugging

## Common Mistakes

❌ Using encoded password in Basic Auth (use plain password)
❌ Username mismatch (case-sensitive)
❌ Sending extra fields in JSON (id, date)
❌ Not restarting the application after code changes
❌ Using a user that doesn't exist in the database

## MongoDB Connection String
Make sure your `application.properties` has:
```properties
spring.data.mongodb.uri=mongodb+srv://username:password@cluster.mongodb.net/?appName=Cluster0
spring.data.mongodb.database=journaldb
spring.data.mongodb.auto-index-creation=true
```

Replace `username`, `password`, and `cluster` with your actual MongoDB Atlas credentials.

