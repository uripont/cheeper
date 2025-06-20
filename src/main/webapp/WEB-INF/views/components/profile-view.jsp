<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.webdev.cheeper.model.User, com.webdev.cheeper.model.Student" %>
<%@ page import="com.webdev.cheeper.model.User, com.webdev.cheeper.model.Entity" %>
<%@ page import="com.webdev.cheeper.model.User, com.webdev.cheeper.model.Association" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cheeper" uri="http://cheeper.webdev/tags" %>

<div class="profile-view">
    <div class="profile-container" data-user-id="${profile.id}">
        <div class="profile-header">
            <div class="profile-picture-frame">
                <img class="profile-picture" 
                     src="${pageContext.request.contextPath}/local-images/${profile.picture != null ? 'profile/'.concat(profile.picture) : 'default.png'}" 
                     alt="Profile Picture" />
            </div>

            <div class="profile-info">
                <h1 class="profile-name">${profile.fullName}</h1>
                <div class="profile-detail-item">
                    <span class="profile-detail-label">Username:</span>
                    <span class="profile-detail-value">@${profile.username}</span>
                </div>

                <div class="follow-stats">
                    <button class="follow-stat-btn">
                        <span id="followersCount">${followersCount}</span> Followers
                    </button>
                    <button class="follow-stat-btn">
                        <span id="followingCount">${followingCount}</span> Following
                    </button>
                    <c:if test="${not readOnly}">
                        <a href="${pageContext.request.contextPath}/edit-profile" class="follow-standard-btn" style="color: white; text-decoration: none; display: inline-block; margin-left: 10px;">
                            Edit Profile
                        </a>
                        <a href="${pageContext.request.contextPath}/logout" class="follow-standard-btn logout-btn" style="color: white; text-decoration: none; display: inline-block; margin-left: 10px;">
                            Logout
                        </a>
                    </c:if>
                </div>

                <c:if test="${readOnly}">
                    <button class="follow-standard-btn" 
                            data-userid="${profile.id}" 
                            ${isFollowing ? 'data-following="true"' : ''} 
                            data-hover-text="Unfollow">
                        ${isFollowing ? 'Following' : 'Follow'}
                    </button>
                </c:if>
            </div>
        </div>

        <c:if test="${not empty profile.biography}">
            <div class="profile-detail-item biography">
                <p>${profile.biography}</p>
            </div>
        </c:if>

        <div class="profile-details">
            <div class="profile-detail-item">
                <span class="profile-detail-label">Role:</span>
                <span class="profile-detail-value">${profile.roleType}</span>
            </div>
            <div class="profile-detail-item">
                <span class="profile-detail-label">Email:</span>
                <span class="profile-detail-value">${profile.email}</span>
            </div>

            <%-- Student-specific information --%>
            <c:if test="${profile['class'].simpleName eq 'Student'}">
                <c:if test="${not empty profile.socialLinks}">
                    <div class="profile-detail-item">
                        <span class="profile-detail-label">Social Links:</span>
                        <span class="profile-detail-value">
                            <ul>
                                <c:forEach var="link" items="${profile.socialLinks}">
                                    <li><a href="${link.value}" target="_blank" rel="noopener noreferrer">${link.key}</a></li>
                                </c:forEach>
                            </ul>
                        </span>
                    </div>
                </c:if>

                <c:if test="${not empty profile.degrees}">
                    <div class="profile-detail-item">
                        <span class="profile-detail-label">Degrees:</span>
                        <span class="profile-detail-value">
                            <ul>
                                <c:forEach var="degree" items="${profile.degrees}">
                                    <li>[${degree.key}] ${degree.value}</li>
                                </c:forEach>
                            </ul>
                        </span>
                    </div>
                </c:if>

                <c:if test="${not empty profile.enrolledSubjects}">
                    <div class="profile-detail-item">
                        <span class="profile-detail-label">Subjects:</span>
                        <span class="profile-detail-value">
                            <ul>
                                <c:forEach var="subject" items="${profile.enrolledSubjects}">
                                    <li>[${subject.key}] ${subject.value}</li>
                                </c:forEach>
                            </ul>
                        </span>
                    </div>
                </c:if>
            </c:if>

            <%-- Entity-specific information --%>
            <c:if test="${profile['class'].simpleName eq 'Entity'}">
                <div class="profile-detail-item">
                    <span class="profile-detail-label">Department:</span>
                    <span class="profile-detail-value">${profile.department}</span>
                </div>
            </c:if>
        </div>
    </div>

    <!-- Timeline Section -->
    <div id="profile-timeline-container" class="timeline-container">
        <!-- Timeline will be loaded here -->
    </div>
</div>

<script>
    // Handle follower/following button clicks with App.loadView
    $('.follow-stat-btn').first().on('click', function() {
        App.loadView('users', { 
            context: 'followers',
            userId: '${profile.id}'
        }, '#rightSidebar');
    });
    
    $('.follow-stat-btn').last().on('click', function() {
        App.loadView('users', { 
            context: 'following',
            userId: '${profile.id}'
        }, '#rightSidebar');
    });
    
    $(document).ready(function() {
        // Load user's timeline
        App.loadView('timeline', { 
            type: 'profile',
            userId: '${profile.id}'
        }, '#profile-timeline-container');

        // Handle hover effects for follow button
        const handleFollowButtonHover = () => {
            const button = $('.follow-standard-btn[data-userid]');
            const originalText = button.text();
            const hoverText = button.data('hoverText');
            
            button.hover(
                function() {
                    if ($(this).attr('data-following') === 'true' && !$(this).prop('disabled')) {
                        $(this).text(hoverText);
                    }
                },
                function() {
                    if ($(this).attr('data-following') === 'true' && !$(this).prop('disabled')) {
                        $(this).text(originalText);
                    }
                }
            );
        };

        handleFollowButtonHover();

        // Handle follow button clicks
        // Handle follow button clicks
        $('.follow-standard-btn[data-userid]').on('click', function() {
            const button = $(this);
            const userId = button.data('userid');
            
            // Prevent multiple clicks while processing
            if (button.prop('disabled')) return;
            button.prop('disabled', true);
            
            const isFollowing = button.attr('data-following') === 'true';
            const endpoint = isFollowing ? 'unfollow' : 'follow';
            
            // Save original text
            const originalText = button.text();
            button.text('Loading...');
            
            fetch('${pageContext.request.contextPath}/' + endpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'Accept': 'application/json',
                },
                body: 'followingId=' + userId
            })
            .then(response => {
                console.log('Response status:', response.status);
                console.log('Response headers:', response.headers);
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                const contentType = response.headers.get("content-type");
                console.log('Content-Type:', contentType);
                if (contentType && contentType.includes("application/json")) {
                    return response.json();
                }
                throw new TypeError("Server did not return JSON response");
            })
            .then(data => {
                if (data.success) {
                    const newIsFollowing = !isFollowing;
                    button.attr('data-following', newIsFollowing.toString());
                    button.text(newIsFollowing ? 'Following' : 'Follow');
                    
                    // Update follower count
                    fetch('${pageContext.request.contextPath}/profile-counts?userId=' + userId, {
                        headers: {
                            'Accept': 'application/json'
                        }
                    })
                        .then(response => {
                            const contentType = response.headers.get("content-type");
                            if (contentType && contentType.includes("application/json")) {
                                return response.json();
                            }
                            throw new TypeError("Server did not return JSON response for profile counts");
                        })
                        .then(data => {
                            $('#followersCount').text(data.followers);
                            $('#followingCount').text(data.following);
                        });
                } else {
                    console.error('Error:', data.message);
                    // Revert button state if operation failed
                    button.text(originalText);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                // Show error alert to user
                alert('Failed to ' + endpoint + ' user. Please try again.');
                // Revert button state on error
                button.text(originalText);
            })
            .finally(() => {
                button.prop('disabled', false);
            });
        });
    });
</script>