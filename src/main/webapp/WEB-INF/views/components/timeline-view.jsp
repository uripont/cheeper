<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/timeline.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/post.css">

<div class="timeline-view">
    <!--Timeline Header (for DEBUG purpouses delete before finishing)
    <div class="timeline-view__header">
        Timeline - ${timeline_type}
        <div style="font-size: 0.8em; font-weight: normal; color: #657786;">
            Posts found: ${posts.size()}
        </div>
    </div> -->

    <c:choose>
        <c:when test="${empty posts}">
            <div class="timeline-view__empty">
                <h4>No posts found</h4>
                <p>No posts available for this timeline.</p>
                <div style="margin-top: 20px; font-size: 0.9em;">
                    <p><strong>Timeline Type:</strong> ${timeline_type}</p>
                    <p><strong>Current User:</strong> ${currentUser.username}</p>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="timeline-view__posts">
                <c:forEach var="post" items="${posts}">
                    <div class="post-item" data-post-id="${post.id}">
                        <div class="post-header">
                            <div class="user-info">
                                <c:set var="author" value="${postAuthors[post.id]}" />
                                <c:choose>
                                    <c:when test="${not empty author}">
                                        <img src="${pageContext.request.contextPath}/local-images/profile/${author.picture}" alt="${author.fullName}" class="user-avatar">                                
                                        <div class="user-details">
                                            <strong>${author.fullName}</strong>
                                            <span class="username">@${author.username}</span>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/local-images/profiles/default.png" alt="Unknown User" class="user-avatar">
                                        <div class="user-details">
                                            <strong>Unknown User</strong>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                <span class="timestamp">
                                    <fmt:formatDate value="${post.createdAt}" pattern="MMM dd, yyyy HH:mm"/>
                                </span>

                                <!-- Delete button - show if current user owns the post or is an ENTITY -->
                                <c:if test="${currentUser != null && (currentUser.id == post.userId || currentUser.roleType == 'ENTITY')}">
                                    <button class="delete-btn" title="Delete post" data-post-id="${post.id}">
                                        <img src="${pageContext.request.contextPath}/static/images/trash.circle.fill.png" alt="Delete" >
                                    </button>
                                    <button class="edit-btn" title="Edit post" data-post-id="${post.id}">
                                        <img src="${pageContext.request.contextPath}/static/images/edit.circle.fill.png" alt="Edit" width="18" height="18">
                                    </button>
                                </c:if>

                            </div>
                        </div>
                        <div class="post-content">
                            <p>${post.content}</p>

                            <!--Image displauy here-->
                            <c:if test="${not empty post.image}">
                                <div class="post-image">
                                    <img src="${pageContext.request.contextPath}/local-images/posts/${post.image}" 
                                         alt="Post image" 
                                         style="max-width: 100%; max-height: 300px; border-radius: 8px; margin-top: 10px;">
                                </div>
                            </c:if>
                            
                        </div>
                        <div class="post-actions">
                            <button class="action-btn like-btn" title="Like" data-post-id="${post.id}">
                                <c:choose>
                                    <c:when test="${userLikes[post.id]}">
                                        <img src="${pageContext.request.contextPath}/static/images/heart.fill.red.png" alt="Liked" width="18" height="18">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="${pageContext.request.contextPath}/static/images/heart.png" alt="Like" width="18" height="18">
                                    </c:otherwise>
                                </c:choose>
                                <c:choose>
                                    <c:when test="${likeCounts[post.id] == 0}">
                                        <span class="like-count" style="display:none;">0</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="like-count">${likeCounts[post.id]}</span>
                                    </c:otherwise>
                                </c:choose>
                            </button>
                            <button class="action-btn reply-btn" title="Reply">
                                <img src="${pageContext.request.contextPath}/static/images/reply.png" alt="Reply" width="18" height="18">
                            </button>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script>
    $(document).ready(function () {
        // Delete button logic
        $('.timeline-view').on('click', '.delete-btn', function () {
            const postElement = $(this).closest('.post-item');
            const postId = postElement.data('post-id');
            
            console.log('Delete button clicked for post ID:', postId); // Debug
            
            if (confirm('Are you sure you want to delete this post?')) {
                $.ajax({
                    url: '/post?postId=' + postId, 
                    method: 'DELETE',
                    success: function(response) {
                        console.log('Delete response:', response); // Debug
                        if (response.success) {
                            postElement.fadeOut(300, function() {
                                $(this).remove();
                            });
                        } else {
                            alert('Failed to delete post: ' + response.message);
                        }
                    },
                    error: function(xhr, status, error) {
                        console.error('Delete error:', xhr.responseText); // Debug
                        alert('Error deleting post. Please try again.');
                    }
                });
            }
        });

        // Like button logic using AJAX
        $('.timeline-view').on('click', '.like-btn', function () {
            const postElement = $(this).closest('.post-item');
            const postId = postElement.data('post-id');
            const likeBtn = $(this);
            const likeImg = likeBtn.find('img');
            const likeCountEl = likeBtn.find('.like-count');

            if (postId) {
                $.ajax({
                    url: '/like',
                    method: 'POST',
                    contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
                    data: { postId: postId },
                    success: function (response) {
                        if (response.liked) {
                            likeImg.attr('src', '${pageContext.request.contextPath}/static/images/heart.fill.red.png');
                            likeImg.attr('alt', 'Liked');
                        } else {
                            likeImg.attr('src', '${pageContext.request.contextPath}/static/images/heart.png');
                            likeImg.attr('alt', 'Like');
                        }

                        if (response.likeCount !== undefined) {
                            likeCountEl.text(response.likeCount);
                            if (response.likeCount > 0) {
                                likeCountEl.show(); // Show it if there are likes
                            } else {
                                likeCountEl.hide(); // Hide it if there are no likes
                            }
                        } else {
                            likeCountEl.text('');
                        }
                    },
                    error: function (xhr, status, error) {
                        console.error('Error toggling like:', xhr.responseText);
                        alert('Unable to toggle like. Please try again.');
                    }
                });
            }
        });

        // Reply button logic
        $('.timeline-view').on('click', '.reply-btn', function () {
            const postId = $(this).closest('.post-item').data('post-id');
            if (postId) {
                App.loadView('post', { id: postId }, '#main-panel');
            }
        });
        
        // Añadir al script existente en timeline-view.jsp
        $('.timeline-view').on('click', '.edit-btn', function () {
            const postId = $(this).closest('.post-item').data('post-id');
            if (postId) {
                App.loadView('edit-post', { postId: postId }, '#main-panel');
            }
        });
    });
</script>
