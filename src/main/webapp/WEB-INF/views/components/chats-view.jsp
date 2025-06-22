<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
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

            <%-- Main chat area --%>
            <c:choose>
                <c:when test="${not empty room}">
                    <div class="messages-preview"> <%-- This will be the scrollable area --%>
                        <div class="messages-list"> <%-- Container for bubbles --%>
                            <c:forEach var="message" items="${messages}">
                                <div class="message-bubble ${message.senderId eq currentUser.id ? 'my-message' : 'other-message'}">
                                    <div class="message-content">${message.content}</div>
                                    <div class="message-time">
                                        <fmt:formatDate value="${message.createdAt}" pattern="HH:mm"/>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                        <%-- Removed "No messages yet" paragraph, messages-list is always present --%>
                    </div>

                    <%-- Message Input Form --%>
                    <div class="message-input-container"> <%-- This will be fixed at the bottom --%>
                        <form id="messageForm" action="${pageContext.request.contextPath}/views/chats" method="post">
                            <input type="hidden" name="action" value="send-message">
                            <input type="hidden" name="roomId" value="${room.id}">
                            <input type="hidden" name="otherUserId" value="${otherUser.id}"> <%-- Pass otherUserId for redirect --%>
                            <input type="hidden" id="currentUserId" value="${currentUser.id}"> <%-- Add current user ID for JS --%>
                            <input type="text" name="content" placeholder="Type your message..." required class="message-input-field"> <%-- Input class --%>
                            <button type="submit" class="send-button">Send</button> <%-- Button class --%>
                        </form>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="placeholder-message">
                        <h3>Welcome to Chat</h3>
                        <p>Select a user to start messaging</p>
                    </div>
                </c:otherwise>
            </c:choose>
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

        // Function to format timestamp
        function formatTimestamp(timestamp) {
            var date = new Date(timestamp);
            var hours = date.getHours().toString().padStart(2, '0');
            var minutes = date.getMinutes().toString().padStart(2, '0');
            return hours + ':' + minutes;
        }

        // Function to append a new message to the chat view
        function appendMessage(message) {
            var currentUserId = $('#currentUserId').val();
            var messageClass = message.senderId == currentUserId ? 'my-message' : 'other-message';
            var formattedTime = formatTimestamp(message.createdAt);

            var messageHtml = '<div class="message-bubble ' + messageClass + '">' +
                              '<div class="message-content">' + message.content + '</div>' +
                              '<div class="message-time">' + formattedTime + '</div>' +
                              '</div>';

            // Append the new message
            $('.messages-list').append(messageHtml);

            // Scroll to the bottom
            var messagesPreview = $('.messages-preview');
            messagesPreview.scrollTop(messagesPreview[0].scrollHeight);
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
                dataType: "json", // Expect JSON response
                success: function(response) {
                    // On success, append the new message to the chat view
                    console.log("Message sent successfully:", response);
                    appendMessage(response); // Append the message from the JSON response

                    // Clear the input field after successful send
                    form.find('input[name="content"]').val('');
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
