<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="timeline">
    <div class="timeline-header">
        <c:choose>
            <c:when test="${param.type eq 'for-you'}">
                <h2>For You</h2>
            </c:when>
            <c:when test="${param.type eq 'following'}">
                <h2>Following</h2>
            </c:when>
            <c:when test="${param.type eq 'profile'}">
                <h2>Posts</h2>
            </c:when>
            <c:otherwise>
                <h2>Timeline</h2>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="posts-container">
        <!-- Placeholder for posts -->
        <div class="placeholder-message">
            <p>This is a placeholder for the timeline view.</p>
            <p>Posts will be loaded here based on the context:</p>
            <ul>
                <li>For You: Global feed</li>
                <li>Following: Posts from followed users</li>
                <li>Profile: User's posts</li>
                <li>Post: Comments/replies</li>
            </ul>
        </div>
    </div>
</div>
