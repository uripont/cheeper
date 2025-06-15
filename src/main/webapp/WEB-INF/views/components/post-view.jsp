<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/post.css">

<div class="post-view">
    <!-- Main post -->
    <div class="post-container">
        <div class="post-header">
            <h2>Post Details</h2>
        </div>

        <c:if test="${not empty post}">
            <div class="post-item" data-post-id="${post.id}">
                <div class="post-header">
                    <div class="user-info">
                        <strong>User ${post.userId}</strong>
                        <span>
                            <fmt:formatDate value="${post.createdAt}" pattern="MMM dd, yyyy HH:mm"/>
                        </span>
                    </div>
                </div>
                <div class="post-content">
                    <p>${post.content}</p>
                </div>
                <div class="post-actions">
                    <button class="action-btn like-btn" title="Like">
                        <img src="${pageContext.request.contextPath}/static/images/heart.png" alt="Like" width="18" height="18">
                    </button>
                    <button class="action-btn reply-btn" title="Reply">
                        <img src="${pageContext.request.contextPath}/static/images/reply.png" alt="Reply" width="18" height="18">
                    </button>
                </div>
            </div>
        </c:if>

        <c:if test="${empty post}">
            <div class="placeholder-message">
                <p>Post not found or error loading post.</p>
            </div>
        </c:if>
    </div>

    <!-- Reply form -->
    <div class="reply-form-container">
        <h3>Reply to this post</h3>
        <form id="reply-form" action="/post" method="post">
            <input type="hidden" name="source_id" value="${post.id}"/>
            <textarea name="content" placeholder="Write your reply..." required></textarea>
            <button id="reply-btn" type="submit">Reply</button>
        </form>
    </div>

    <!-- Comments section -->
    <div class="comments-section">
        <h3>Comments</h3>
        <div id="comments-timeline" class="timeline-container">
            <!-- The comments timeline will be loaded here -->
        </div>
    </div>
</div>

<script>
    $(document).ready(function () {
        const postId = $('input[name="source_id"]').val();
        if (postId) {
            App.loadView('timeline', {
                type: 'comments',
                postId: postId
            }, '#comments-timeline');
        }

        $('#reply-form').on('submit', function (e) {
            e.preventDefault();
            const formData = $(this).serialize();
            $.post('/post', formData)
                .done(function () {
                    App.loadView('post', { id: postId }, '#main-panel');
                })
                .fail(function () {
                    alert('Error submitting reply');
                });
        });
    });
</script>
