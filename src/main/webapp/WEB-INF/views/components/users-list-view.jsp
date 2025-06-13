<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="users-list">
    <div class="users-list-header">
        <c:choose>
            <c:when test="${param.context eq 'search'}">
                <h2>Search Results</h2>
            </c:when>
            <c:when test="${param.context eq 'suggestions'}">
                <h2>Suggested Users</h2>
            </c:when>
            <c:otherwise>
                <h2>Users</h2>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="users-container">
        <!-- Placeholder for user list -->
        <div class="placeholder-message">
            <p>This is a placeholder for the users list view.</p>
            <p>Users will be displayed here based on context:</p>
            <ul>
                <li>Search Results: Users matching search query</li>
                <li>Suggestions: Recommended users to follow</li>
            </ul>
        </div>
    </div>
</div>
