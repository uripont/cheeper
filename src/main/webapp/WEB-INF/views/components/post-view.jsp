<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="post-view">
    <!-- Main post container -->
    <div class="post-container">
        <div class="post-header">
            <h2>Post Details</h2>
        </div>

        <!-- Main post using the same structure as timeline -->
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
                    <button>Like</button>
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
    <div class="reply-form-container" style="margin-top: 20px;">
        <h3>Reply to this post</h3>
        <form action="/post" method="post">
            <input type="hidden" name="source_id" value="${post.id}"/>
            <textarea name="content" placeholder="Write your reply..."></textarea>
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
        const postId = new URLSearchParams(window.location.search).get('id') || '${postId}';
        if (postId) {
            App.loadView('timeline', { 
                type: 'comments',
                postId: postId 
            }, '#comments-timeline');
        }
    });
</script>
