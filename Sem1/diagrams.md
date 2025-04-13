## Profile Type Structure

```mermaid
classDiagram
    class Profile {
        +String email
        +String name
        +Date birthday
        +String profilePicture
        +String biography
        +List<String> socialLinks
        +UserType type
    }
    
    class Student {
        +List<String> degrees
        +List<String> enrolledSubjects
    }
    
    class UniversityEntity {
        +String department
        +String officialEmail
    }
    
    class Association {
        +String verificationStatus
        +Date verificationDate
    }
    
    Profile <|-- Student
    Profile <|-- UniversityEntity
    Profile <|-- Association
```

## OAuth Google Sign-in Registration Process

### 1. Initial OAuth Authentication

```mermaid
sequenceDiagram
    actor User
    participant Client
    participant GoogleAuth

    User->>Client: Clicks "Sign in with Google"
    Client->>GoogleAuth: Redirect to Google OAuth
    GoogleAuth->>User: Show consent screen
    User->>GoogleAuth: Grant permissions
    GoogleAuth->>Client: Return OAuth code
```

### 2. Token Verification and User Info Retrieval

```mermaid
sequenceDiagram
    participant Client
    participant Server
    participant GoogleAuth

    Client->>Server: Send OAuth code
    Server->>GoogleAuth: Verify token
    GoogleAuth->>Server: Return user info
    Note over Server: Validate email format
```

### 3. User Type Classification

```mermaid
sequenceDiagram
    participant Server
    participant Client
    
    alt University Email
        Server->>Server: Auto-assign type<br/>(Student/Entity)
    else Regular Email
        Server->>Server: Mark as Association<br/>Pending verification
    end
    Server->>Client: Request additional info
    Client->>User: Display profile completion form
```

### 4. Profile Validation and Creation

```mermaid
sequenceDiagram
    actor User
    participant Client
    participant Server
    participant Database

    User->>Client: Fill mandatory fields
    
    par Field Validation
        Client->>Client: Client-side validation<br/>(format, length)
        Server->>Server: Server-side validation<br/>(uniqueness, security)
    end
    
    alt Validation Failed
        Server->>Client: Return errors with<br/>preserved valid data
        Client->>User: Show error highlights
    else Validation Passed
        Server->>Database: Create profile
        Database->>Server: Confirm creation
        Server->>Client: Registration success
        Client->>User: Redirect to dashboard
    end
```
