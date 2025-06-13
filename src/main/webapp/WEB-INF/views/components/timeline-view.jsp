<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="timeline-view">
    <div class="timeline-header">
        <h3>Timeline</h3>
        <p class="timeline-context">Context: ${param.type}</p>
    </div>

    <div class="timeline-content">
        <!-- Universal placeholder for all contexts -->
        <div class="placeholder-message">
            <h4>Timeline Content</h4>
            <p>This unified timeline view will show:</p>
            <ul>
                <li>✦ Posts list with consistent styling</li>
                <li>✦ Same structure for all contexts</li>
                <li>✦ Only data will change based on type</li>
            </ul>
            <div class="timeline-info">
                <p><strong>Current View:</strong> ${param.type}</p>
                <c:if test="${not empty param.userId}">
                    <p><strong>User ID:</strong> ${param.userId}</p>
                </c:if>
                <c:if test="${not empty param.postId}">
                    <p><strong>Post ID:</strong> ${param.postId}</p>
                </c:if>
            </div>
        </div>
    </div>
</div>
