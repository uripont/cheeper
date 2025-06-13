<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="post-view">
    <!-- Main post container -->
    <div class="post-container">
        <div class="post-header">
            <h2>Post Details</h2>
        </div>

        <!-- Placeholder for post content -->
        <div class="placeholder-message">
            <p>This is a placeholder for the post view.</p>
            <p>Will display:</p>
            <ul>
                <li>Author information</li>
                <li>Post content</li>
                <li>Engagement metrics</li>
                <li>Action buttons (like, share, etc.)</li>
            </ul>
        </div>
    </div>

    <!-- Comments timeline -->
    <div class="comments-section">
        <h3>Comments</h3>
        <div id="comments-timeline" class="timeline-container">
            <!-- Timeline view for comments will be loaded here -->
            <div class="placeholder-message">
                <p>Comments will be loaded using the timeline-view component</p>
            </div>
        </div>
    </div>
</div>

<script>
    $(document).ready(function() {
        // Load comments timeline
        const postId = new URLSearchParams(window.location.search).get('id');
        if (postId) {
            App.loadView('timeline', { type: 'comments', postId: postId });
        }
    });
</script>
