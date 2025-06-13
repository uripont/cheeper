<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cheeper" uri="/WEB-INF/cheeper.tld" %>

<div class="users-list-view">
    <div class="users-list-header">
        <h3>${context}</h3>
        <c:if test="${context eq 'Search Users'}">
            <div class="search-input-container">
                <input type="text" class="search-input" placeholder="Search users..." 
                       value="${searchQuery}" oninput="handleSearch(this.value)">
            </div>
        </c:if>
    </div>

    <div class="users-container">
        <c:choose>
            <c:when test="${not empty users}">
                <c:forEach var="user" items="${users}">
                    <div class="user-item">
                        <img src="${user.picture}" alt="${user.fullName}" class="user-avatar">
                        <div class="user-info">
                            <h4>${user.fullName}</h4>
                            <div class="username">@${user.username}</div>
                            <c:if test="${not empty user.biography}">
                                <div class="biography">${user.biography}</div>
                            </c:if>
                        </div>
                        <div class="user-actions">
                            <c:if test="${currentUser != null && currentUser.id != user.id}">
                                <button class="follow-button ${user.followed ? 'following' : ''}" 
                                        onclick="toggleFollow(${user.id}, this)">
                                    ${user.followed ? 'Following' : 'Follow'}
                                </button>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="placeholder-message">
                    <p>No users found</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
let searchTimeout;

function handleSearch(query) {
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
        window.location.href = '/views/users?context=search&q=' + encodeURIComponent(query);
    }, 500);
}

function toggleFollow(userId, button) {
    fetch('/follow', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'userId=' + userId
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            button.classList.toggle('following');
            button.textContent = button.classList.contains('following') ? 'Following' : 'Follow';
        }
    })
    .catch(error => console.error('Error:', error));
}
