<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/create-post.css">
<head>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/cropperjs/1.5.12/cropper.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/cropperjs/1.5.12/cropper.min.js"></script>
</head>

<div class="form-container">
    <h1>Edit Post</h1>
    
    <form id="editPostForm" enctype="multipart/form-data">
        <input type="hidden" id="editPostId" name="postId" value="${post.id}">
        <input type="hidden" name="action" value="edit">
        
        <label for="editContent">Content:</label>
        <textarea id="editContent" name="content" rows="4" required placeholder="Edit your post content... (Ctrl+Enter to save)">${post.content}</textarea>
        
        <!-- Current image display -->
        <c:if test="${not empty post.image}">
            <div id="current-image-section" style="margin-top: 15px;">
                <p>Current image:</p>
                <img src="${pageContext.request.contextPath}/local-images/posts/${post.image}" 
                     alt="Current post image" 
                     style="max-width: 100%; max-height: 300px; border-radius: 8px;">
                <button type="button" id="removeImageBtn" class="btn btn-secondary" style="margin-top: 10px;">Remove Image</button>
            </div>
        </c:if>
        
        <!-- Hidden file input -->
        <input type="file" id="newImage" name="image" accept="image/*" style="display: none;">
        <input type="hidden" id="removeImageFlag" name="removeImage" value="false">

        <!-- Cropper UI -->
        <div id="image-cropper-container" style="display: none; margin-top: 15px;">
            <div style="width: 100%; max-width: 500px; height: 400px;">
                <img id="image-to-crop" style="max-width: 100%;">
            </div>
            <div style="margin-top: 10px;">
                <button type="button" id="crop-button" class="btn">Crop Image</button>
                <button type="button" id="cancel-crop" class="btn">Cancel</button>
            </div>
        </div>

        <!-- New image preview -->
        <div id="cropped-preview" style="margin-top: 15px; display: none;">
            <p>New image preview:</p>
            <img id="cropped-result" style="max-width: 100%; max-height: 300px; border-radius: 8px; margin-top: 10px;">
        </div>

        <div class="error-message">
            <!-- Error messages will be displayed here -->
        </div>
        
        <div class="form-buttons">
            <button type="button" id="addImageBtn">
                <c:choose>
                    <c:when test="${not empty post.image}">Change Image</c:when>
                    <c:otherwise>Add Image</c:otherwise>
                </c:choose>
            </button>
            <button type="button" id="cancelEditBtn" class="btn btn-secondary">Cancel</button>
            <button type="submit" id="saveEditBtn" class="btn btn-primary">Save Changes</button>
        </div>
    </form>
</div>

<script>
$(document).ready(function() {
    // Initialize Cropper.js
    let cropper;
    const TARGET_SIZE = 600;
    
    const editForm = document.getElementById('editPostForm');
    const editTextarea = document.getElementById('editContent');
    const saveBtn = document.getElementById('saveEditBtn');
    const cancelBtn = document.getElementById('cancelEditBtn');
    const addImageBtn = document.getElementById('addImageBtn');
    const removeImageBtn = document.getElementById('removeImageBtn');
    
    // Auto-resize textarea
    editTextarea.addEventListener('input', function() {
        this.style.height = 'auto';
        this.style.height = this.scrollHeight + 'px';
    });
    
    // Initialize textarea height
    editTextarea.style.height = 'auto';
    editTextarea.style.height = editTextarea.scrollHeight + 'px';
    
    // Image cropping functionality (same as create-post)
    $('#newImage').on('change', function () {
        if (this.files.length > 0) {
            const file = this.files[0];
            const reader = new FileReader();

            reader.onload = function (e) {
                $('#image-to-crop').attr('src', e.target.result);
                $('#image-cropper-container').show();

                const image = document.getElementById('image-to-crop');
                if (cropper) {
                    cropper.destroy();
                }

                cropper = new Cropper(image, {
                    viewMode: 1,
                    autoCropArea: 1,
                    responsive: true,
                    movable: true,
                    zoomable: true,
                    scalable: true,
                    rotatable: false,
                    cropBoxResizable: true,
                    cropBoxMovable: true
                });
            };

            reader.readAsDataURL(file);
        }
    });

    $('#crop-button').on('click', function () {
        if (cropper) {
            const canvas = cropper.getCroppedCanvas({
                width: TARGET_SIZE,
                fillColor: '#000'
            });

            canvas.toBlob(function (blob) {
                const fileName = $('#newImage')[0].files[0].name;
                const file = new File([blob], fileName, { type: 'image/jpeg' });

                const dataTransfer = new DataTransfer();
                dataTransfer.items.add(file);
                $('#newImage')[0].files = dataTransfer.files;

                $('#cropped-result').attr('src', canvas.toDataURL('image/jpeg'));
                $('#cropped-preview').show();
                $('#image-cropper-container').hide();
                $('#current-image-section').hide(); // Hide current image when adding new one

                cropper.destroy();
            }, 'image/jpeg', 0.9);
        }
    });

    $('#cancel-crop').on('click', function () {
        $('#image-cropper-container').hide();
        $('#newImage').val('');
        $('#cropped-preview').hide();
        if (cropper) {
            cropper.destroy();
        }
    });

    // Add/Change image button
    addImageBtn.addEventListener('click', function () {
        
        $('#newImage').val('');
        $('#removeImageFlag').val('false');
        $('#current-image-section').hide();
        $('#newImage').click();
    });

    
    // Remove current image
    if (removeImageBtn) {
        removeImageBtn.addEventListener('click', function() {
            if (confirm('Are you sure you want to remove the current image?')) {
                $('#current-image-section').hide();
                $('#removeImageFlag').val('true');
                addImageBtn.textContent = 'Add Image';
            }
        });
    }
    
    // Handle form submission
    editForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        if (editTextarea.value.trim() === '') {
            alert('Post content cannot be empty!');
            return;
        }
        
        const formData = new FormData(editForm);
        
        saveBtn.disabled = true;
        saveBtn.textContent = 'Saving...';
        
        $.ajax({
            url: '/post',
            method: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                if (response.success) {
                    showSuccessMessage('Post updated successfully!');
                    
                    // Go back to previous view or timeline
                    setTimeout(() => {
                        App.loadView('timeline', { timeline_type: 'profile' }, '#main-panel');
                    }, 1000);
                } else {
                    alert('Failed to update post: ' + response.message);
                }
            },
            error: function(xhr, status, error) {
                console.error('Error updating post:', xhr.responseText);
                
                // Try to parse error response
                let errorMessage = 'Error updating post. Please try again.';
                try {
                    const errorResponse = JSON.parse(xhr.responseText);
                    if (errorResponse.message) {
                        errorMessage = errorResponse.message;
                    }
                } catch (e) {
                    // Use default error message
                }
                
                $('.error-message').html('<p style="color: red">' + errorMessage + '</p>');
            },
            complete: function() {
                saveBtn.disabled = false;
                saveBtn.textContent = 'Save Changes';
            }
        });
    });
    
    // Handle cancel button
    cancelBtn.addEventListener('click', function() {
        if (confirm('Are you sure? Any unsaved changes will be lost.')) {
            App.loadView('timeline', { timeline_type: 'profile' }, '#main-panel');
        }
    });
    
    // Keyboard shortcuts
    editTextarea.addEventListener('keydown', function(e) {
        if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
            e.preventDefault();
            editForm.dispatchEvent(new Event('submit'));
        }
        
        if (e.key === 'Escape') {
            cancelBtn.click();
        }
    });
    
    // Character counter (same as create-post)
    editTextarea.addEventListener('input', function() {
        const remaining = 280 - this.value.length;
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