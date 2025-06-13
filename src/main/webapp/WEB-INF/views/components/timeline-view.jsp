<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="timeline-view">
    <div class="timeline-header">
        <c:choose>
            <c:when test="${param.type eq 'for-you'}">
                <h3>For You Timeline</h3>
            </c:when>
            <c:when test="${param.type eq 'following'}">
                <h3>Following Timeline</h3>
            </c:when>
            <c:when test="${param.type eq 'profile'}">
                <h3>User Posts</h3>
            </c:when>
            <c:when test="${param.type eq 'comments'}">
                <h3>Comments</h3>
            </c:when>
            <c:otherwise>
                <h3>Timeline</h3>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="posts-container">
        <!-- Placeholder for posts -->
        <div class="placeholder-message">
            <p>This is a placeholder for the ${param.type} timeline.</p>
            <ul>
                <li>Each post will show:</li>
                <li>- Author info and avatar</li>
                <li>- Post content</li>
                <li>- Media attachments</li>
                <li>- Engagement metrics</li>
                <li>- Action buttons</li>
            </ul>
        </div>
    </div>
</div>
