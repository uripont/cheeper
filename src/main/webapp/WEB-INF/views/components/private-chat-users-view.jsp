<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="private-chat-users">
    <div class="private-chat-users-header">
        <h3>Start a Chat</h3>
    </div>
    
    <div class="private-chat-users-search">
        <input type="text" placeholder="Search users..." 
               value="${searchQuery}" oninput="handleSearch(this.value)">
    </div>

    <div class="private-chat-users-list">
        <c:choose>
            <c:when test="${not empty users}">
                <c:forEach var="user" items="${users}">
                    <div class="private-chat-user-item" data-user-id="${user.id}">
                        <img src="${pageContext.request.contextPath}/local-images/profile/${user.picture}" 
                             alt="${user.fullName}" 
                             class="private-chat-user-avatar">
                        <div class="private-chat-user-info">
                            <div class="private-chat-user-name">${user.fullName}</div>
                            <div class="private-chat-user-username">@${user.username}</div>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="private-chat-placeholder">
                    <c:choose>
                        <c:when test="${not empty searchQuery}">
                            <p>No users found for "${searchQuery}"</p>
                        </c:when>
                        <c:otherwise>
                            <p>Start chatting with your connections</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
function handleSearch(query) {
    if (query.trim().length >= 2) {
        App.loadView('chats', { 
            component: 'private-chat-users',
            q: query.trim() 
        }, '#rightSidebar');
    } else if (query.trim().length === 0) {
        App.loadView('chats', { 
            component: 'private-chat-users'
        }, '#rightSidebar');
    }
}

// Use event delegation for dynamically loaded content
$(document).on('click', '.private-chat-user-item', function() {
    let userId = $(this).data('user-id');
    console.log('User card clicked:', userId);
    
    App.loadView('chats', { 
        action: 'load-conversation',
        otherUserId: userId 
    }, '#main-panel');  // Fix selector to match app.js
});
</script>
