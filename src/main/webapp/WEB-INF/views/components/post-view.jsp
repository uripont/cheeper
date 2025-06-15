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
                    <button class="action-btn like-btn" title="Like" data-post-id="${post.id}">
                        <c:choose>
                            <c:when test="${isLikedByUser}">
                                <img src="${pageContext.request.contextPath}/static/images/heart.fill.red.png" alt="Liked" width="18" height="18">
                            </c:when>
                            <c:otherwise>
                                <img src="${pageContext.request.contextPath}/static/images/heart.png" alt="Like" width="18" height="18">
                            </c:otherwise>
                        </c:choose>
                        <span class="like-count">${likeCount}</span>
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
        <form action="${pageContext.request.contextPath}/post" method="post">
            <input type="hidden" name="source_id" value="${post.id}"/>
            <textarea name="content" placeholder="Write your reply..." required></textarea>
            <button type="submit">Reply</button>
        </form>
    </div>

    <!-- Comments section -->
    <div class="comments-section">
        <h3>Comments</h3>
        <div id="comments-timeline" class="timeline-container">
            <!-- Comments timeline will be loaded here -->
        </div>
    </div>
</div>

<script>
    $(document).ready(function() {
        // Load comments timeline
        const postId = '${postId}';
        if (postId) {
            App.loadView('timeline', { 
                type: 'comments',
                postId: postId 
            }, '#comments-timeline');
        }

        // Handle like button click
        $('.post-view').on('click', '.like-btn', function () {
            const postElement = $(this).closest('.post-item');
            const postId = postElement.data('post-id');
            const likeBtn = $(this);
            const likeImg = likeBtn.find('img');
            const likeCount = likeBtn.find('.like-count');

            if (postId) {
                $.post('/like', { postId: postId })
                    .done(function(response) {
                        // Update like button image and count
                        if (response.liked) {
                            likeImg.attr('src', '${pageContext.request.contextPath}/static/images/heart-fill.png');
                            likeImg.attr('alt', 'Liked');
                        } else {
                            likeImg.attr('src', '${pageContext.request.contextPath}/static/images/heart.png');
                            likeImg.attr('alt', 'Like');
                        }
                        likeCount.text(response.likeCount);
                    })
                    .fail(function() {
                        console.error('Error toggling like');
                    });
            }
        });
    });
</script>