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
                <h3>Chat Room Information</h3>
                
                <!-- Room Status -->
                <div class="room-status">
                    <h4>Room Details</h4>
                    <c:choose>
                        <c:when test="${not empty room}">
                            <p class="status-found">Room Found! ID: ${room.id}</p>
                            <p>Created: <fmt:formatDate value="${room.createdAt}" pattern="MMM d, yyyy HH:mm"/></p>
                        </c:when>
                        <c:otherwise>
                            <p class="status-not-found">No room exists yet between you and ${otherUser.fullName}</p>
                            <p class="status-note">A room will be created when you send your first message</p>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Messages Preview -->
                <c:if test="${not empty room}">
                    <div class="messages-preview">
                        <h4>Messages Preview</h4>
                        <p>Total Messages: ${totalMessages}</p>
                        
                        <c:if test="${not empty messages}">
                            <div class="preview-list">
                                <c:forEach var="message" items="${messages}">
                                    <div class="preview-item">
                                        <span class="sender">${message.senderId eq currentUser.id ? 'You' : otherUser.fullName}:</span>
                                        <span class="content">${message.content}</span>
                                        <span class="time">
                                            <fmt:formatDate value="${message.createdAt}" pattern="HH:mm"/>
                                        </span>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:if>
                        <c:if test="${empty messages}">
                            <p class="no-messages">No messages yet in this conversation</p>
                        </c:if>
                    </div>
                </c:if>

                <!-- Message Input Form -->
                <c:if test="${not empty room}">
                    <div class="message-input">
                        <form id="messageForm" action="${pageContext.request.contextPath}/views/chats" method="post">
                            <input type="hidden" name="action" value="send-message">
                            <input type="hidden" name="roomId" value="${room.id}">
                            <input type="hidden" name="otherUserId" value="${otherUser.id}"> <%-- Pass otherUserId for redirect --%>
                            <input type="text" name="content" placeholder="Type your message..." required>
                            <button type="submit">Send</button>
                        </form>
                    </div>
                </c:if>
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

        // Handle message form submission
        $('#messageForm').on('submit', function(e) {
            e.preventDefault(); // Prevent default form submission

            var form = $(this);
            var url = form.attr('action');
            var formData = form.serialize();

            $.ajax({
                type: "POST",
                url: url,
                data: formData,
                success: function(response) {
                    // On success, reload the chat view to show the new message
                    // This assumes the server redirects to the GET endpoint for the chat view
                    // The redirect handles the view update
                    console.log("Message sent successfully, redirecting...");
                },
                error: function(xhr, status, error) {
                    console.error("Error sending message:", error);
                    // Handle error, e.g., show an alert
                    alert("Error sending message: " + error);
                }
            });
        });
    });
</script>
