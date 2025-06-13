<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="create-post-view">
    <div class="create-post-header">
        <h2>Create Post</h2>
    </div>

    <div class="create-post-form">
        <!-- Form placeholder -->
        <div class="form-placeholder">
            <p>This is a placeholder for the post creation form</p>
            <ul>
                <li>Text area for post content</li>
                <li>Character counter</li>
                <li>Media upload buttons</li>
                <li>Visibility options</li>
                <li>Post button</li>
            </ul>
        </div>

        <!-- Visual representation of the form -->
        <div class="form-preview">
            <div class="input-placeholder" style="height: 120px; background: #f5f5f5; margin: 10px 0; border-radius: 8px;">
                <p style="color: #999; padding: 10px;">What's on your mind?</p>
            </div>
            
            <div class="actions-placeholder" style="display: flex; justify-content: space-between; padding: 10px 0;">
                <div>
                    <button disabled style="opacity: 0.5;">Add Media</button>
                    <button disabled style="opacity: 0.5;">Visibility</button>
                </div>
                <button disabled style="opacity: 0.5;">Post</button>
            </div>
        </div>
    </div>
</div>
