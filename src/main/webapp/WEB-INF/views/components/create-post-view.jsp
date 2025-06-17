<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/create-post.css">
<div class="form-container">
    <h1>Create a new post</h1>

    <form id="createPostForm" action="${pageContext.request.contextPath}/post" method="POST" enctype="multipart/form-data">
        <input type="hidden" name="fullName" value="<%= session.getAttribute("name") != null ? session.getAttribute("name") : "" %>">

        <label for="content">What's on your mind?</label>
        <textarea id="content" name="content" rows="4" required
                  placeholder="Share your thoughts here... (Ctrl+Enter to post)">${param.content}</textarea>

        <div class="error-message">
            <c:if test="${not empty error}">
                <p style="color: red">${error}</p>
            </c:if>
        </div>
        <button type="button" id="addImageBtn">Add Image</button>
        <button type="submit" id="postBtn">Post</button>
    </form>
</div>

<script>
$(document).ready(function() {
    const form = document.getElementById('createPostForm');
    const textarea = document.getElementById('content');
    const postBtn = document.getElementById('postBtn');

    // Handle keyboard shortcuts
    textarea.addEventListener('keydown', function(e) {
        // Ctrl+Enter or Cmd+Enter (Mac) to submit
        if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
            e.preventDefault();
            
            // Validate content
            if (textarea.value.trim() === '') {
                alert('Please write something before posting!');
                return;
            }
            
            // Visual feedback
            postBtn.disabled = true;
            postBtn.textContent = 'Posting...';
            postBtn.style.opacity = '0.6';
            
            // Submit form
            form.submit();
        }
    });

    // Handle form submission
    form.addEventListener('submit', function(e) {
        // Visual feedback for regular submit
        postBtn.disabled = true;
        postBtn.textContent = 'Posting...';
        postBtn.style.opacity = '0.6';
    });

    // Auto-resize textarea
    textarea.addEventListener('input', function() {
        this.style.height = 'auto';
        this.style.height = this.scrollHeight + 'px';
    });

    // Character counter (optional)
    textarea.addEventListener('input', function() {
        const remaining = 280 - this.value.length; // Twitter-like limit
        if (remaining < 0) {
            this.style.borderColor = '#e74c3c';
        } else {
            this.style.borderColor = '#333';
        }
    });
});
</script>
