<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="users-list-view">
    <div class="users-list-header">
        <div class="header-title-container">
            <h3>${context}</h3>
            <c:if test="${context == 'Suggested Users'}">
                <button class="refresh-button" onclick="refreshSuggestions()">🎲</button>
            </c:if>
        </div>
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
                    <div class="user-item" data-user-id="${user.id}" style="cursor: pointer;">
                        <img src="${pageContext.request.contextPath}${user.picture}" alt="${user.fullName}" class="user-avatar">
                        <div class="user-info">
                            <h4>${user.fullName}</h4>
                            <div class="username">@${user.username}</div>
                            <c:if test="${not empty user.biography}">
                                <div class="biography">${user.biography}</div>
                            </c:if>
                        </div>
                        <div class="user-actions" onclick="event.stopPropagation()">
                            <c:if test="${currentUser != null && currentUser.id != user.id}">
                                <button class="follow-button ${user.followed ? 'unfollow' : ''}" 
                                        onclick="toggleFollow(${user.id}, this)"
                                        data-hover-text="Unfollow">
                                    ${user.followed ? 'Unfollow' : 'Follow'}
                                </button>
                            </c:if>
                            <c:if test="${currentUser != null && currentUser.roleType == 'ENTITY' && currentUser.id != user.id}">
                                <button class="delete-button" onclick="deleteUser(${user.id})">
                                    X
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
(function() {
function refreshSuggestions() {
    App.loadView('users', { context: 'suggestions' }, '#rightSidebar');
}

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

function deleteUser(userId) {
    if (confirm('Are you sure you want to delete this user? This action cannot be undone and will remove all their associated data (posts, messages, etc.).')) {
        fetch('/deleteUser', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'userId=' + userId
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                // Remove the user card from the DOM
                $(`.user-item[data-user-id="${userId}"]`).remove();
                alert('User deleted successfully!');
            } else {
                console.error('Error:', data.message);
                alert('Failed to delete user: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred while deleting the user.');
        });
    }
}

function toggleFollow(userId, button) {
    // Prevent multiple clicks while processing
    if (button.disabled) return;
    button.disabled = true;
    
    const isFollowing = button.classList.contains('unfollow');
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
            button.classList.toggle('unfollow', newIsFollowing);
            button.textContent = newIsFollowing ? 'Unfollow' : 'Follow';
        } else {
            console.error('Error:', data.message);
            // Revert button state if operation failed
            button.classList.toggle('unfollow', isFollowing);
            button.textContent = originalText;
        }
    })
    .catch(error => {
        console.error('Error:', error);
        // Revert button state on error
        button.classList.toggle('unfollow', isFollowing);
        button.textContent = originalText;
    })
    .finally(() => {
        button.disabled = false;
    });
}

$(document).ready(function() {
    // Handle hover effects for follow buttons
    $('.follow-button.unfollow').each(function() {
        const button = $(this);
        const originalText = button.text();
        const hoverText = button.data('hoverText');
        
        button.hover(
            function() {
                if (button.hasClass('unfollow') && !button.prop('disabled')) {
                    button.text(hoverText);
                }
            },
            function() {
                if (button.hasClass('unfollow') && !button.prop('disabled')) {
                    button.text(originalText);
                }
            }
        );
    });

    // Make user cards clickable to load profile
    $(document).on('click', '.user-item', function(event) {
        try {
            // Get the clicked element and check if it's not the follow button
            const target = $(event.target);
            if (!target.is('.follow-button') && !target.closest('.follow-button').length) {
                // Get userId from the card's data attribute
                const userId = $(this).attr('data-user-id');
                console.log('Loading profile for user:', userId);
                
                // Load the profile view
                App.loadView('profile', { userId: userId }, '#main-panel');
            }
        } catch(error) {
            console.error('Error handling user card click:', error);
        }
    });
});
})();
