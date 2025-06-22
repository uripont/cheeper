var chatWebSocket = null;

function connectToChatWebSocket(roomId, userId, contextPath, appendMessageCallback) {
    if (chatWebSocket && chatWebSocket.readyState === WebSocket.OPEN) {
        console.log("WebSocket already connected.");
        return;
    }

    var wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    var wsUrl = wsProtocol + '//' + window.location.host + contextPath + '/chat/' + roomId + '/' + userId;

    chatWebSocket = new WebSocket(wsUrl);

    chatWebSocket.onopen = function(event) {
        console.log("WebSocket connected:", event);
    };

    chatWebSocket.onmessage = function(event) {
        console.log("WebSocket message received:", event.data);
        try {
            var message = JSON.parse(event.data);
            if (typeof appendMessageCallback === 'function') {
                appendMessageCallback(message);
            }
        } catch (e) {
            console.error("Error parsing WebSocket message:", e);
        }
    };

    chatWebSocket.onclose = function(event) {
        console.log("WebSocket closed:", event);
        // Optionally try to reconnect
    };

    chatWebSocket.onerror = function(event) {
        console.error("WebSocket error:", event);
    };
}

function sendMessageViaWebSocket(messageContent, roomId, senderId) {
    if (chatWebSocket && chatWebSocket.readyState === WebSocket.OPEN) {
        var message = {
            roomId: roomId,
            senderId: senderId,
            content: messageContent
        };
        chatWebSocket.send(JSON.stringify(message));
        console.log("Message sent via WebSocket:", message);
    } else {
        console.error("WebSocket is not connected. Cannot send message.");
        alert("Chat connection lost. Please refresh the page.");
    }
}

function disconnectChatWebSocket() {
    if (chatWebSocket && chatWebSocket.readyState === WebSocket.OPEN) {
        chatWebSocket.close();
        chatWebSocket = null;
        console.log("WebSocket disconnected.");
    }
}
