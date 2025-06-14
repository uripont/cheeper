<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>    

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>${param.mode eq 'edit' ? 'Edit Profile' : 'Student Registration'}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/register-style.css">
    <!-- Cropper.js CSS -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/cropperjs/1.5.12/cropper.min.css">
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <!-- Cropper.js -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/cropperjs/1.5.12/cropper.min.js"></script>
</head>
<body>

    <div class="form-container">
        <h1>${param.mode eq 'edit' ? 'Edit Your Profile' : 'Welcome to Cheeper'}</h1>
        <c:if test="${param.mode ne 'edit'}">
            <h2> 
                <%= session.getAttribute("role") != null ? session.getAttribute("role") : "Unknown Role" %>
            </h2>
        </c:if>
        
        <form id="registerForm" action="student-form?mode=${param.mode}" method="POST" enctype="multipart/form-data">
            <input type="hidden" name="mode" value="${param.mode}">
            <!-- Hidden fields for data from session -->
            <input type="hidden" name="fullName" value="<%= session.getAttribute("name") != null ? session.getAttribute("name") : "" %>">
            <input type="hidden" name="email" value="<%= session.getAttribute("email") != null ? session.getAttribute("email") : "" %>">
            
            <label for="username">Username:</label> 
            <input type="text" id="username" name="username" required minlength="3" maxlength="20" 
                   value="${student.username}" data-original="${student.username}"
                   data-user-id="${student.id}"
                   title="Username must be between 3 and 20 characters."/> 
            
            <label for="birthdate">Birthdate:</label>
            <input type="date" id="birthdate" name="birthdate" required value="${student.birthdate}" />

            <label for="biography">Biography:</label>
            <textarea id="biography" name="biography" rows="4" 
                      minlength="10" maxlength="500" required
                      placeholder="Tell us about yourself (minimum 10 characters)">${student.biography}</textarea>
            <div class="error-message" id="biography-error"></div>
            
            <div>
                <label>Upload a profile picture (optional):</label>
                <button type="button" class="add-btn" id="chooseImageBtn">Choose Profile Picture</button>
                <input type="file" id="picture" name="picture" accept="image/*" style="display: none;" />
                
                <div id="image-cropper-container" style="display: none; margin-top: 15px;">
                    <div style="width: 100%; max-width: 500px; height: 400px;">
                        <img id="image-to-crop" style="max-width: 100%;">
                    </div>
                    <div style="margin-top: 10px;">
                        <button type="button" id="crop-button" class="btn btn-primary" style="display: none;">Crop Image</button>
                        <button type="button" id="cancel-crop" class="btn btn-secondary">Cancel</button>
                    </div>
                </div>
                
                <div id="cropped-preview" style="margin-top: 15px; display: none;">
                    <p>Preview:</p>
                    <img id="cropped-result" style="max-width: 200px; max-height: 200px; border: 1px solid #ddd; background-color: black;">
                    <input type="hidden" id="cropped-image-data" name="croppedImageData">
                </div>
            </div>
            
            <!-- Social Links Section -->
            <div class="dynamic-field-container">
                <label>Social Media Links (optional):</label>
                <button type="button" class="add-btn" onclick="addSocialLinkField()">Add Social Link</button>
                <div id="socialLinksContainer">
                    <c:forEach var="entry" items="${student.socialLinks}">
                        <div class="dynamic-field">
                            <input type="text" name="socialLinkKey" placeholder="Platform (e.g., twitter)" value="${entry.key}">
                            <input type="url" name="socialLinkValue" placeholder="URL" value="${entry.value}">
                            <button type="button" class="remove-btn" onclick="removeField(this)">Remove</button>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <!-- Degrees Section -->
            <div class="dynamic-field-container">
                <label>Degrees (optional):</label>
                <button type="button" class="add-btn" onclick="addDegreeField()">Add Degree</button>
                <div id="degreesContainer">
                    <c:forEach var="entry" items="${student.degrees}">
                        <div class="dynamic-field">
                            <input type="text" name="degreeKey" placeholder="Degree Type (e.g., BSc)" value="${entry.key}">
                            <input type="text" name="degreeValue" placeholder="Field of Study" value="${entry.value}">
                            <button type="button" class="remove-btn" onclick="removeField(this)">Remove</button>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <!-- Enrolled Subjects Section -->
            <div class="dynamic-field-container">
                <label>Enrolled Subjects (optional):</label>
                <button type="button" class="add-btn" onclick="addSubjectField()">Add Subject</button>
                <div id="subjectsContainer">
                    <c:forEach var="entry" items="${student.enrolledSubjects}">
                        <div class="dynamic-field">
                            <input type="text" name="subjectKey" placeholder="Subject Code" value="${entry.key}">
                            <input type="text" name="subjectValue" placeholder="Subject Name" value="${entry.value}">
                            <button type="button" class="remove-btn" onclick="removeField(this)">Remove</button>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <button type="submit">${param.mode eq 'edit' ? 'Save Changes' : 'Register'}</button>
        </form>
    </div>
    
    <script>
        // Image Cropper functionality
        $(document).ready(function() {
            let cropper;
            const TARGET_SIZE = 400;
            
            $('#picture').on('change', function(e) {
                if (this.files && this.files.length) {
                    const file = this.files[0];
                    const reader = new FileReader();
                    
                    reader.onload = function(event) {
                        $('#image-to-crop').attr('src', event.target.result);
                        $('#image-cropper-container').show();
                        
                        const image = document.getElementById('image-to-crop');
                        if (cropper) {
                            cropper.destroy();
                        }
                        
                        cropper = new Cropper(image, {
                            aspectRatio: 1,
                            viewMode: 1,
                            autoCropArea: 0.8,
                            responsive: true,
                            ready: function() {
                                $('#crop-button').show();
                            }
                        });
                    };
                    
                    reader.readAsDataURL(file);
                }
            });
            
            $('#crop-button').on('click', function() {
                if (cropper) {
                    const canvas = cropper.getCroppedCanvas({
                        width: TARGET_SIZE,
                        height: TARGET_SIZE,
                        fillColor: '#000'
                    });
                    
                    canvas.toBlob(function(blob) {
                        const fileName = $('#picture')[0].files[0].name;
                        const file = new File([blob], fileName, {type: 'image/jpeg'});
                        
                        const dataTransfer = new DataTransfer();
                        dataTransfer.items.add(file);
                        $('#picture')[0].files = dataTransfer.files;
                        
                        $('#cropped-result').attr('src', canvas.toDataURL('image/jpeg'));
                        $('#cropped-preview').show();
                        $('#image-cropper-container').hide();
                        if (cropper) {
                            cropper.destroy();
                        }
                    }, 'image/jpeg', 0.9);
                }
            });
            
            $('#cancel-crop').on('click', function() {
                $('#image-cropper-container').hide();
                $('#picture').val('');
                if (cropper) {
                    cropper.destroy();
                }
            });
            
            $('#chooseImageBtn').on('click', function () {
                $('#picture').click();
            });

            $('#picture').on('change', function () {
                if (this.files.length > 0) {
                    $('#chooseImageBtn').text('Change Picture');
                }
            });
        });

        // Dynamic field functions
        function addSocialLinkField() {
            const container = document.getElementById('socialLinksContainer');
            const div = document.createElement('div');
            div.className = 'dynamic-field';
            div.innerHTML = `
                <input type="text" name="socialLinkKey" placeholder="Platform (e.g., twitter)">
                <input type="url" name="socialLinkValue" placeholder="URL">
                <button type="button" class="remove-btn" onclick="removeField(this)">Remove</button>
            `;
            container.appendChild(div);
        }

        function addDegreeField() {
            const container = document.getElementById('degreesContainer');
            const div = document.createElement('div');
            div.className = 'dynamic-field';
            div.innerHTML = `
                <input type="text" name="degreeKey" placeholder="Degree Type">
                <input type="text" name="degreeValue" placeholder="Field of Study">
                <button type="button" class="remove-btn" onclick="removeField(this)">Remove</button>
            `;
            container.appendChild(div);
        }

        function addSubjectField() {
            const container = document.getElementById('subjectsContainer');
            const div = document.createElement('div');
            div.className = 'dynamic-field';
            div.innerHTML = `
                <input type="text" name="subjectKey" placeholder="Subject Code" pattern="\\d{5}">
                <input type="text" name="subjectValue" placeholder="Subject Name">
                <button type="button" class="remove-btn" onclick="removeField(this)">Remove</button>
            `;
            container.appendChild(div);
        }

        function removeField(button) {
            button.parentElement.remove();
        }
    </script>
    
    <!-- Load validation scripts -->
    <script src="${pageContext.request.contextPath}/static/js/user-validation.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/student-validation.js"></script>
    
    <!-- Initialize validation -->
    <script>
        // Make context path available to validation scripts
        window.contextPath = '${pageContext.request.contextPath}';
        
        document.addEventListener('DOMContentLoaded', function() {
            const form = document.getElementById('registerForm');
            
            if (form) {
                const validateStudent = window.initStudentValidation(form);
                
                if (validateStudent) {
                    form.addEventListener('submit', async function(event) {
                        event.preventDefault();
                        
                        // Clear previous errors
                        form.querySelectorAll('input, textarea').forEach(input => {
                            input.setCustomValidity('');
                        });
                        
                        const isValid = await validateStudent();
                        
                        if (isValid) {
                            form.submit();
                        }
                    });
                }
                
                // Display server errors on load
                const serverErrors = {
                    <c:forEach var="error" items="${errors}">
                        "${error.key}": "${error.value}",
                    </c:forEach>
                };
                
                for (const [field, message] of Object.entries(serverErrors)) {
                    const input = document.querySelector(`[name="${field}"]`);
                    if (input) {
                        input.setCustomValidity(message);
                        input.reportValidity();
                    }
                }
            }
        });
    </script>
</body>
</html>
