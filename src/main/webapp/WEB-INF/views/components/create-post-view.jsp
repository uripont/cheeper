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

    function submitPostForm() {
        const formData = new FormData(form);

        postBtn.disabled = true;
        postBtn.textContent = 'Posting...';
        postBtn.style.opacity = '0.6';

        $.ajax({
            url: '${pageContext.request.contextPath}/post',
            method: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                // Reset form
                textarea.value = '';
                textarea.style.height = 'auto';
                
                // Clear any error messages
                $('.error-message').empty();

                // Reset button
                postBtn.disabled = false;
                postBtn.textContent = 'Post';
                postBtn.style.opacity = '1';

                // Show success message
                showSuccessMessage('Post created successfully!');

                // Load the newly created post view
                if (response.postId) {
                    setTimeout(() => {
                        App.loadView('post-view', { postId: response.postId });
                    }, 1000); // Small delay to show success message first
                }
            },
            error: function(xhr, status, error) {
                console.error('Error creating post:', error);
                
                // Try to parse error response
                let errorMessage = 'Failed to create post. Please try again.';
                try {
                    const errorResponse = JSON.parse(xhr.responseText);
                    if (errorResponse.error) {
                        errorMessage = errorResponse.error;
                    }
                } catch (e) {
                    // Use default error message
                }

                // Show error in form
                $('.error-message').html('<p style="color: red">' + errorMessage + '</p>');

                // Reset button
                postBtn.disabled = false;
                postBtn.textContent = 'Post';
                postBtn.style.opacity = '1';
            }
        });
    }

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
            
            submitPostForm();
        }
    });

    // Handle form submission
    form.addEventListener('submit', function(e) {
        e.preventDefault(); // Prevent default form submission
        
        // Validate content
        if (textarea.value.trim() === '') {
            alert('Please write something before posting!');
            return;
        }
        
        submitPostForm();
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

    function showSuccessMessage(message) {
        const successDiv = $('<div></div>').css({
            position: 'fixed',
            top: '20px',
            right: '20px',
            background: '#28a745',
            color: 'white',
            padding: '12px 20px',
            borderRadius: '6px',
            zIndex: 1000,
            fontSize: '14px',
            boxShadow: '0 2px 8px rgba(0,0,0,0.2)'
        }).text(message);

        $('body').append(successDiv);

        setTimeout(() => {
            successDiv.remove();
        }, 3000);
    }
});
</script>