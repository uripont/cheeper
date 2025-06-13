<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="profile-view">
    <!-- Profile Information Section -->
    <div class="profile-container">
        <div class="profile-header">
            <h1>Profile</h1>
            <p>This is a placeholder for the profile view</p>
        </div>

        <div class="profile-details">
            <ul>
                <li>Profile picture</li>
                <li>Username</li>
                <li>Full name</li>
                <li>Biography</li>
                <li>Stats (followers/following)</li>
            </ul>
        </div>
    </div>

    <!-- Timeline Section -->
    <div class="profile-timeline">
        <h2>Posts</h2>
        <div id="profile-timeline-container" class="timeline-container">
            <!-- Timeline will be loaded here -->
        </div>
    </div>
</div>

<script>
    $(document).ready(function() {
        // Load the timeline placeholder
        App.loadView('timeline', { type: 'profile' }, '#profile-timeline-container');
    });
</script>
