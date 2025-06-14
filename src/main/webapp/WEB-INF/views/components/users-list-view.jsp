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
                                        onclick="toggleFollow(${user.id}, this)"
                                        data-hover-text="Unfollow">
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
function handleSearch(query) {
    let searchTimeout;
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
        window.location.href = '/views/users?context=search&q=' + encodeURIComponent(query);
    }, 500);
}

function toggleFollow(userId, button) {
    // Prevent multiple clicks while processing
    if (button.disabled) return;
    button.disabled = true;
    
    const isFollowing = button.classList.contains('following');
    const endpoint = isFollowing ? '/unfollow' : '/follow';
    
    // Save original text
    const originalText = button.textContent;
    button.textContent = 'Loading...';
    
    fetch(endpoint, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'followingId=' + userId
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            const newIsFollowing = !isFollowing;
            button.classList.toggle('following', newIsFollowing);
            button.textContent = newIsFollowing ? 'Following' : 'Follow';
        } else {
            console.error('Error:', data.message);
            // Revert button state if operation failed
            button.classList.toggle('following', isFollowing);
            button.textContent = originalText;
        }
    })
    .catch(error => {
        console.error('Error:', error);
        // Revert button state on error
        button.classList.toggle('following', isFollowing);
        button.textContent = originalText;
    })
    .finally(() => {
        button.disabled = false;
    });
}

// Handle hover effects for follow buttons
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.follow-button.following').forEach(button => {
        const originalText = button.textContent;
        const hoverText = button.dataset.hoverText;
        
        button.addEventListener('mouseenter', () => {
            if (button.classList.contains('following') && !button.disabled) {
                button.textContent = hoverText;
            }
        });
        
        button.addEventListener('mouseleave', () => {
            if (button.classList.contains('following') && !button.disabled) {
                button.textContent = originalText;
            }
        });
    });
});
