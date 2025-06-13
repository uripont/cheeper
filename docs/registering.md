# Sign in and Sign up flow

## OAuth flow

```mermaid
sequenceDiagram
    participant B as Browser
    participant IS as IndexServlet
    participant LG as login-with-google.jsp
    participant GOLS as GoogleOAuthLoginServlet
    participant G as Google OAuth
    participant GOCS as GoogleOAuthCallbackServlet
    participant US as UserService
    
    B->>IS: Access /
    IS->>IS: Check session
    alt Valid Session
        IS->>B: Redirect to /app/home
    else No Valid Session
        IS->>LG: Redirect to /auth
        LG->>B: Display login page
        B->>GOLS: Click "Sign in with Google"
        GOLS->>G: Redirect with client_id & scopes
        Note over G: User authenticates
        G->>GOCS: Return with auth code
        GOCS->>G: Exchange code for token
        G->>GOCS: Return user info
        GOCS->>US: checkUserExists(email)
        alt New User
            GOCS->>B: Redirect to registration
        else Existing User
            GOCS->>B: Redirect to /app/home
        end
    end
```

## Registration flow

```mermaid
stateDiagram
    [*] --> RegistrationStart
    RegistrationStart --> TypeSelect

    TypeSelect --> StudentForm: Student
    TypeSelect --> EntityForm: Entity
    TypeSelect --> AssociationForm: Association

    state StudentForm {
        [*] --> StudentFormJSP: /onboarding/student-form.jsp
        StudentFormJSP --> StudentController: StudentForm.java
        StudentController --> StudentService: validate()
        StudentService --> StudentRepository: save()
    }

    state EntityForm {
        [*] --> EntityFormJSP: /onboarding/entity-form.jsp
        EntityFormJSP --> EntityController: EntityForm.java
        EntityController --> EntityService: validate()
        EntityService --> EntityRepository: save()
    }

    state AssociationForm {
        [*] --> AssociationFormJSP: /onboarding/association-form.jsp
        AssociationFormJSP --> AssociationController: AssociationForm.java
        AssociationController --> AssociationService: validate()
        AssociationService --> AssociationRepository: save()
    }

    StudentForm --> MainPage: Success
    EntityForm --> MainPage: Success
    AssociationForm --> MainPage: Success
    MainPage --> [*]
```

## Session management

The application uses Jakarta EE's `HttpSession` for managing user sessions
- Stores user information:
    - name: User's full name
    - email: User's email address
    - role: User's role type (STUDENT/ENTITY/ASSOCIATION)
- All protected routes check for valid session
