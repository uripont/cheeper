<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="chats-view">
    <c:choose>
        <c:when test="${not empty otherUser}">
            <div class="chat-header">
                <div class="chat-user-info">
                    <img src="${pageContext.request.contextPath}${otherUser.picture}" 
                         alt="${otherUser.fullName}" class="chat-user-avatar">
                    <div class="chat-user-details">
                        <div class="chat-user-name">${otherUser.fullName}</div>
                        <div class="chat-user-username">@${otherUser.username}</div>
                    </div>
                </div>
            </div>

            <div class="chat-placeholder">
                <h3>Chat Room Placeholder</h3>
                <div class="user-ids">
                    <p><strong>Your User ID:</strong> ${currentUser.id}</p>
                    <p><strong>Other User ID:</strong> ${otherUser.id}</p>
                </div>
                <p class="placeholder-note">This will be a conversation between you and ${otherUser.fullName}</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="placeholder-message">
                <h3>Welcome to Chat</h3>
                <p>Select a user to start messaging</p>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script>
    $(document).ready(function() {
        // Load private chat users list in right sidebar if not already loaded
        if ($('#rightSidebar .private-chat-users').length === 0) {
            App.loadView('chats', { component: 'private-chat-users' }, '#rightSidebar');
        }
    });
</script>
