<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="users-list-view">
    <div class="users-list-header">
        <c:choose>
            <c:when test="${param.context eq 'search'}">
                <div class="search-container">
                    <h2>Search Users</h2>
                    <input type="text" class="search-input" placeholder="Search users...">
                </div>
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
        <!-- Placeholder content -->
        <div class="placeholder-message">
            <p>This is a placeholder for the users list.</p>
            <p>This view will show:</p>
            <ul>
                <c:choose>
                    <c:when test="${param.context eq 'search'}">
                        <li>Search input field</li>
                        <li>Search results</li>
                        <li>User cards with follow buttons</li>
                    </c:when>
                    <c:when test="${param.context eq 'suggestions'}">
                        <li>Suggested users to follow</li>
                        <li>Based on your interests</li>
                        <li>Follow buttons</li>
                    </c:when>
                </c:choose>
            </ul>
        </div>
    </div>
</div>
