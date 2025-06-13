<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="users-list-view">
    <div class="users-list-header">
        <h3>Users</h3>
        <p class="users-context">Context: ${param.context}</p>
        <c:if test="${param.context eq 'search'}">
            <div class="search-input-container">
                <input type="text" class="search-input" placeholder="Search users...">
            </div>
        </c:if>
    </div>

    <div class="users-container">
        <!-- Universal placeholder for all contexts -->
        <div class="placeholder-message">
            <h4>User List Content</h4>
            <p>This unified users list will show:</p>
            <ul>
                <li>✦ User avatar</li>
                <li>✦ User name and handle</li>
                <li>✦ Follow/Message button</li>
                <li>✦ Brief user info</li>
            </ul>
            <div class="list-info">
                <p><strong>Current Context:</strong> ${param.context}</p>
            </div>
        </div>
    </div>
</div>
