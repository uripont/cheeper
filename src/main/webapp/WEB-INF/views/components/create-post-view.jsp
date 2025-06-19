<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/create-post.css">
<head>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/cropperjs/1.5.12/cropper.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/cropperjs/1.5.12/cropper.min.js"></script>
</head>

<div class="form-container">
    <h1>Create a new post</h1>

    <form id="createPostForm" action="${pageContext.request.contextPath}/post" method="POST" enctype="multipart/form-data">
        <input type="hidden" name="fullName" value="<%= session.getAttribute("name") != null ? session.getAttribute("name") : "" %>">

        <label for="content">What's on your mind?</label>
        <textarea id="content" name="content" rows="4" required placeholder="Share your thoughts here... (Ctrl+Enter to post)">${param.content}</textarea>
        
        <!-- Hidden file input -->
        <input type="file" id="image" name="image" accept="image/*" style="display: none;">

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

        <!-- Preview -->
        <div id="cropped-preview" style="margin-top: 15px; display: none;">
            <p>Preview:</p>
            <img id="cropped-result" style="max-width: 200px; max-height: 200px; border: 1px solid #ddd; background-color: black;">
        </div>

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
    // Initialize Cropper.js
    let cropper;
    const TARGET_SIZE = 600;

    $('#image').on('change', function () {
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
                    aspectRatio: 16 / 9,
                    viewMode: 1,
                    autoCropArea: 1,
                    responsive: true
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
                const fileName = $('#image')[0].files[0].name;
                const file = new File([blob], fileName, { type: 'image/jpeg' });

                const dataTransfer = new DataTransfer();
                dataTransfer.items.add(file);
                $('#image')[0].files = dataTransfer.files;

                $('#cropped-result').attr('src', canvas.toDataURL('image/jpeg'));
                $('#cropped-preview').show();
                $('#image-cropper-container').hide();

                cropper.destroy();
            }, 'image/jpeg', 0.9);
        }
    });

    $('#cancel-crop').on('click', function () {
        $('#image-cropper-container').hide();
        $('#image').val('');
        $('#cropped-preview').hide();
        if (cropper) {
            cropper.destroy();
        }
    });

    $('#addImageBtn').on('click', function () {
        $('#image').click();
    });


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

    /* Open file input when "Add Image" button is clicked
    $('#addImageBtn').on('click', function () {
        document.getElementById('image').click();
    });*/

    //Preview selected image
    document.getElementById('image').addEventListener('change', function () {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                document.getElementById('imagePreview').innerHTML = `<img src="${e.target.result}" style="max-width: 100%; max-height: 200px;" />`;
            };
            reader.readAsDataURL(file);
        }
    });

});
</script>