<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/timeline.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/post.css">
<div class="timeline-view">
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
                            </button> <!-- When pushed open post-view.jsp -->
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<script>
    $(document).ready(function () {
        // Manejar clic en botón Reply
        $('.timeline-view').on('click', '.reply-btn', function () {
            const postId = $(this).closest('.post-item').data('post-id');
            if (postId) {
                App.loadView('post', { id: postId }, '#main-panel');
            }
        });
    });
</script>

