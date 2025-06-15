<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>




<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/create-post.css">
<div class="form-container">
    <h1>Create a new post</h1>

    <form id="createPostForm" action="${pageContext.request.contextPath}/post" method="POST" enctype="multipart/form-data">
        <input type="hidden" name="fullName" value="<%= session.getAttribute("name") != null ? session.getAttribute("name") : "" %>">

        <label for="content">What’s on your mind?</label>
        <textarea id="content" name="content" rows="4" required
                  placeholder="Share your thoughts here...">${param.content}</textarea>

        <!-- Optional: Image upload for future enhancement -->
        <!-- <input type="file" id="picture" name="picture" accept="image/*" /> -->

        <div class="error-message">
            <c:if test="${not empty error}">
                <p style="color: red">${error}</p>
            </c:if>
        </div>
        <button>Add Image</button>
        <button type="submit">Post</button>
    </form>
</div>
