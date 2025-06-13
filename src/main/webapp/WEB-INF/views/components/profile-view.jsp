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
                <cheeper:profileImage picture="${profile.picture}" cssClass="profile-picture" />
            </div>

            <div class="profile-info">
                <h1 class="profile-name">${profile.fullName}</h1>
                <div class="profile-detail-item">
                    <span class="profile-detail-label">Username:</span>
                    <span class="profile-detail-value">@${profile.username}</span>
                </div>

                <div class="follow-stats">
                    <button class="follow-stat-btn" onclick="App.loadFollowList('followers', ${profile.id})">
                        <span id="followersCount">${followersCount}</span> Followers
                    </button>
                    <button class="follow-stat-btn" onclick="App.loadFollowList('following', ${profile.id})">
                        <span id="followingCount">${followingCount}</span> Following
                    </button>
                </div>

                <c:if test="${readOnly}">
                    <button class="follow-standard-btn" data-userid="${profile.id}">
                        ${isFollowing ? 'Unfollow' : 'Follow'}
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
    $(document).ready(function() {
        // Load user's timeline
        App.loadView('timeline', { 
            type: 'profile',
            userId: '${profile.id}'
        }, '#profile-timeline-container');

        // Handle follow button clicks
        $('.follow-standard-btn').on('click', function() {
            const userId = $(this).data('userid');
            const action = $(this).text().trim().toLowerCase();
            const button = $(this);

            $.post(`/${action}`, { followingId: userId })
                .done(function() {
                    // Toggle button text
                    button.text(action === 'follow' ? 'Unfollow' : 'Follow');
                    
                    // Update follower count
                    $.get(`/profile-counts?userId=${userId}`, function(data) {
                        $('#followersCount').text(data.followers);
                        $('#followingCount').text(data.following);
                    });
                })
                .fail(function() {
                    alert(`Failed to ${action} user`);
                });
        });
    });
</script>
