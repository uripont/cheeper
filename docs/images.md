# Image Handling in Cheeper

## Image URL

Cheeper uses a simplified and consistent URL pattern for image handling:
- Default profile image: `/local-images/default.png`
- User profile pictures: `/local-images/profile/[filename]`

We've moved away from custom JSP tags in favor of direct URL patterns for better simplicity and maintainability. The image path construction is now straightforward in JSP files:

```jsp
<!-- Example from profile-view.jsp -->
<img class="profile-picture" 
     src="${pageContext.request.contextPath}/local-images/${profile.picture != null ? 'profile/'.concat(profile.picture) : 'default.png'}" 
     alt="Profile Picture" />
```

## Image Service

The `ImageService` manages image storage with:
- Storage base path: `/var/lib/cheeper/images`
- Maximum file size: 5MB
- Allowed types: JPEG, PNG, GIF
- Directory structure:
  - `/profiles/` - User uploaded profile pictures
  - `/static/images/` - Default images and system resources

## Image Serving

The `ImageServlet`, mapped to `/local-images/*`, handles all image requests:
1. `/local-images/default.png` → serves from webapp's static directory
2. `/local-images/profile/[filename]` → serves from storage directory

Example from forms:
```jsp
<div id="cropped-preview" style="margin-top: 15px; display: block;">
    <p>${user.picture != null ? 'Current Profile Picture:' : 'Preview:'}</p>
    <img id="cropped-result" 
         src="${pageContext.request.contextPath}/local-images/${user.picture != null ? 'profile/'.concat(user.picture) : 'default.png'}" 
         style="max-width: 200px; max-height: 200px;">
</div>
```

We initially used a custom tag system with `ProfileImageTag` and TLD configuration, but found it introduced unnecessary complexity:
1. Required maintaining separate Java classes for tag logic
2. Added complexity with TLD configuration
3. Made debugging more difficult
4. Provided little benefit over direct URL patterns
The current approach is:
- More straightforward - URLs directly reflect the server's file structure
- Easier to maintain - no extra abstraction layer