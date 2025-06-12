# Image Handling in Cheeper

## Custom JSP Tags

Cheeper implements a custom JSP tag for consistent profile image rendering across the application. This system uses a Tag Library Descriptor (`cheeper.tld`) that defines the `profileImage` tag with configurable attributes like `picture`, `cssClass`, `username`, and `clickable`. A Java implementation (`ProfileImageTag.java`) generates the HTML markup, integrating with the `ImageService` for proper image path resolution. 

Example usage: `<cheeper:profileImage picture="${profile.picture}" cssClass="profile-picture" />`.

This centralizes image rendering logic, eliminating code duplication and ensuring consistent behavior across the application. Without this tag system, we would need to repeatedly implement similar HTML markup and handle image path resolution (as we previously did), default images, and CSS classes in multiple JSP files, leading to maintenance challenges when image rendering requirements change.

## Image service

The `ImageService` provides image management with configurable storage paths (`/var/lib/cheeper/images`), serve paths (`/local-images`), maximum file size (5MB), and allowed types (JPEG, PNG, GIF). The directory structure includes folders for profiles, defaults, and temporary storage. It is a centralized service for image handling, ensuring consistent behavior across the application.