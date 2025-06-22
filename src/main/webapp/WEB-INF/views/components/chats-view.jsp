<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="chats-view">
    <div class="chat-list">
        <div class="chat-list-header">
            <h2>Messages</h2>
        </div>

        <c:if test="${empty room}">
            <div class="placeholder-message">
                <p>Select a user to start chatting</p>
            </div>
        </c:if>
    </div>

    <div class="chat-content">
        <c:choose>
            <c:when test="${not empty room}">
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

                <div class="messages-container">
                    <c:forEach var="message" items="${messages}">
                        <div class="message ${message.senderId eq currentUser.id ? 'sent' : 'received'}">
                            <div class="message-content">
                                ${message.content}
                            </div>
                            <div class="message-time">
                                <fmt:formatDate value="${message.createdAt}" pattern="HH:mm"/>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <div class="message-input">
                    <form id="messageForm" action="javascript:void(0);" onsubmit="sendMessage(event)">
                        <input type="text" id="messageContent" placeholder="Type a message..." required>
                        <button type="submit">Send</button>
                    </form>
                </div>
            </c:when>
            <c:otherwise>
                <div class="placeholder-message">
                    <h3>Welcome to Chat</h3>
                    <p>Select a conversation to start messaging</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
    function sendMessage(event) {
        event.preventDefault();
        const content = $('#messageContent').val().trim();
        if (!content) return;

        const roomId = ${room != null ? room.id : 'null'};
        if (!roomId) return;

        // TODO: Add an endpoint to handle message sending
        console.log('Message sending will be implemented in next step');
        $('#messageContent').val('');
    }

    function scrollToBottom() {
        const container = $('.messages-container');
        container.scrollTop(container.prop('scrollHeight'));
    }

    $(document).ready(function() {
        // Load private chat users list in right sidebar
        App.loadView('chats', { component: 'private-chat-users' }, '#rightSidebar');
        
        // Scroll to bottom of messages when loading a conversation
        if ($('.messages-container').length) {
            scrollToBottom();
        }
    });
</script>
