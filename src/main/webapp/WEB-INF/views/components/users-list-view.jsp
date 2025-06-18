<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cheeper" uri="/WEB-INF/cheeper.tld" %>

<div class="users-list-view">
    <div class="users-list-header">
        <h3>${context}</h3>
        <c:if test="${context.startsWith('Search')}">
            <div class="search-input-container">
                <input type="text" class="search-input" placeholder="Search users..." 
                       value="${searchQuery}" oninput="handleSearch(this.value)">
                <div class="search-suggestions" id="searchSuggestions" style="display: none;"></div>
            </div>
        </c:if>
    </div>

    <div class="users-container">
        <c:choose>
            <c:when test="${not empty users}">
                <c:forEach var="user" items="${users}">
                    <div class="user-item" onclick="viewUserProfile('${user.username}')">
                        <img src="${user.picture}" alt="${user.fullName}" class="user-avatar">
                        <div class="user-info">
                            <h4>${user.fullName}</h4>
                            <div class="username">@${user.username}</div>
                            <c:if test="${not empty user.biography}">
                                <div class="biography">${user.biography}</div>
                            </c:if>
                        </div>
                        <div class="user-actions" onclick="event.stopPropagation()">
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
                    <c:choose>
                        <c:when test="${not empty searchQuery}">
                            <p>No users found for "${searchQuery}"</p>
                        </c:when>
                        <c:otherwise>
                            <p>No users found</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
let searchTimeout;

function handleSearch(query) {
    clearTimeout(searchTimeout);
    
    if (query.trim().length === 0) {
        // Load default users list
        App.loadView('users', { context: 'search' }, '#main-panel');
        return;
    }
    
    if (query.trim().length < 2) {
        return;
    }
    
    searchTimeout = setTimeout(() => {
        // Load users with search query
        App.loadView('users', { 
            context: 'search', 
            q: query.trim() 
        }, '#main-panel');
    }, 300);
}

function viewUserProfile(username) {
    // Navigate to user profile using App system
    App.loadView('profile', { username: username }, '#main-panel');
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
</script>