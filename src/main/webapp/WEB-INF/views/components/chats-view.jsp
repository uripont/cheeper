<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="chats-view">
    <div class="chat-list">
        <div class="chat-list-header">
            <h2>Messages</h2>
        </div>

        <!-- Placeholder for chat list -->
        <div class="placeholder-message">
            <p>This is a placeholder for the chat conversations list.</p>
            <p>Each conversation will show:</p>
            <ul>
                <li>User avatar</li>
                <li>Username</li>
                <li>Last message preview</li>
                <li>Timestamp</li>
                <li>Unread count</li>
            </ul>
        </div>
    </div>

    <div class="chat-content">
        <!-- Placeholder for selected chat -->
        <div class="placeholder-message">
            <h3>Chat Content</h3>
            <p>Select a conversation to view messages</p>
            <p>Will include:</p>
            <ul>
                <li>Message history</li>
                <li>Message input field</li>
                <li>Send button</li>
                <li>Media attachment options</li>
            </ul>
        </div>
    </div>
</div>

<script>
    $(document).ready(function() {
        // Load private chat users list in right sidebar
        App.loadView('chats', { component: 'private-chat-users' }, '#rightSidebar');
    });
</script>
