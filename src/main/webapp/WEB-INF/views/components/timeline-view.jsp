<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/timeline.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/post.css">

<div class="timeline-view">
    <!--Timeline Header (for DEBUG purpouses delete before finishing) -->
    <div class="timeline-view__header">
        Timeline - ${timeline_type}
        <div style="font-size: 0.8em; font-weight: normal; color: #657786;">
            Posts found: ${posts.size()}
        </div>
    </div>

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
                                        <strong>${author.fullName}</strong>
                                        <span class="username">@${author.username}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <strong>Unknown User</strong>
                                    </c:otherwise>
                                </c:choose>
                                <span class="timestamp">
                                    <fmt:formatDate value="${post.createdAt}" pattern="MMM dd, yyyy HH:mm"/>
                                </span>
                            </div>
                        </div>
                        <div class="post-content">
                            <p>${post.content}</p>
                            
                            <!--Image displauy here-->
                            <c:if test="${not empty post.image}">
                                <div class="post-image">
                                    <img src="${pageContext.request.contextPath}/static/images/${post.image}" alt="Post image" style="max-width: 100%; max-height: 300px;">
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
                                <c:if test="${likeCounts[post.id] > 0}">
                                    <span class="like-count">${likeCounts[post.id]}</span>
                                </c:if>
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
        // Like button logic
        $('.timeline-view').on('click', '.like-btn', function () {
            const postElement = $(this).closest('.post-item');
            const postId = postElement.data('post-id');
            const likeBtn = $(this);
            const likeImg = likeBtn.find('img');
            const likeCountEl = likeBtn.find('.like-count');

            if (postId) {
                $.post('/like', { postId: postId })
                    .done(function (response) {
                        if (response.liked) {
                            likeImg.attr('src', '${pageContext.request.contextPath}/static/images/heart.fill.red.png');
                            likeImg.attr('alt', 'Liked');
                        } else {
                            likeImg.attr('src', '${pageContext.request.contextPath}/static/images/heart.png');
                            likeImg.attr('alt', 'Like');
                        }
                        likeCountEl.text(response.likeCount);
                    })
                    .fail(function () {
                        console.error('Error toggling like');
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
    });
</script>