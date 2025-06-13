# View Architecture in Cheeper

## Single Page Application Structure

Cheeper implements a SPA (Single Page Application) approach with a fixed shell layout and dynamically loaded content from a set of reusable view templates, similar to Twitter's design.

```mermaid
graph TD
    A[Browser] -->|Initial Load| B[GET /app]
    B -->|Returns| C[Main Layout Shell]
    C -->|Contains| D[Left/Icon Sidebar]
    C -->|Contains| E[Dynamic Areas]
    E --> F[Main Panel]
    E --> G[Right Sidebar]
```

## Directory Structure
```
src/main/webapp/WEB-INF/views/
├── components/             # Reusable view templates
│   ├── feed-view.jsp      # Feed container with tabs
│   ├── profile-view.jsp   # Profile display
│   ├── timeline-view.jsp  # Universal timeline
│   ├── users-list-view.jsp# User listings
│   ├── create-post-view.jsp# Post creation form
│   └── post-view.jsp      # Individual post display
└── layouts/
    └── main-layout.jsp    # SPA container
```

## View Templates

All views are served through the `/views/*` endpoint by ViewsController, with components designed for reuse and composition.

### Feed View
- **Template**: `feed-view.jsp`
- **Endpoint**: `GET /views/feed`
- **Responsibility**: Displays feed tabs and embeds timeline
- **Composition**: Loads timeline-view with appropriate context

### Profile View
- **Template**: `profile-view.jsp`
- **Endpoint**: `GET /views/profile`
- **Responsibility**: Displays user profile with timeline
- **Composition**: Embeds timeline-view for user's posts

### Timeline View
- **Template**: `timeline-view.jsp`
- **Endpoint**: `GET /views/timeline`
- **Parameters**:
  - `type`: for-you | following | profile | comments
  - `userId`: (optional) for profile timeline
  - `postId`: (optional) for comments timeline
- **Responsibility**: Universal post listing component

### Users List View
- **Template**: `users-list-view.jsp`
- **Endpoint**: `GET /views/users`
- **Parameters**: 
  - `context`: search | suggestions
- **Responsibility**: Displays user lists for search/suggestions

### Create Post View
- **Template**: `create-post-view.jsp`
- **Endpoint**: `GET /views/create`
- **Responsibility**: Post creation form

### Post View
- **Template**: `post-view.jsp`
- **Endpoint**: `GET /views/post`
- **Parameters**: 
  - `id`: post identifier
- **Responsibility**: Single post with comments
- **Composition**: Embeds timeline-view for comments

## View Loading and Composition

### Client-Side Navigation
```mermaid
sequenceDiagram
    participant B as Browser
    participant C as Client (app.js)
    participant S as Server
    participant V as View Template
    
    B->>C: Click navigation item
    C->>C: Update URL & active state
    C->>C: Clear dynamic areas
    
    alt Home/Profile
        C->>S: GET /views/feed or /views/profile
        S->>C: Return main view
        C->>S: GET /views/users?context=suggestions
        S->>C: Return sidebar content
    else Explore
        C->>S: GET /views/users?context=search
        S->>C: Return search view
    else Create Post
        C->>S: GET /views/create
        S->>C: Return create form
    end
```