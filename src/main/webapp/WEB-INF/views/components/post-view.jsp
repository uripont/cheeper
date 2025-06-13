<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="post-view">
    <!-- Main post container -->
    <div class="post-container">
        <div class="post-header">
            <h2>Post Details</h2>
        </div>

        <!-- Post placeholder -->
        <div class="placeholder-message">
            <p>This is a placeholder for the post details.</p>
            <p>Will include:</p>
            <ul>
                <li>Author information and avatar</li>
                <li>Post timestamp</li>
                <li>Post content</li>
                <li>Media attachments (if any)</li>
                <li>Engagement metrics (likes, replies, etc.)</li>
                <li>Action buttons (like, share, etc.)</li>
            </ul>
        </div>
    </div>

    <!-- Reply form -->
    <div class="reply-form-container">
        <div class="placeholder-message">
            <p>Reply form will be here</p>
            <ul>
                <li>Text input for reply</li>
                <li>Submit button</li>
            </ul>
        </div>
    </div>

    <!-- Comments section -->
    <div class="comments-section">
        <h3>Comments</h3>
        <div id="comments-timeline" class="timeline-container">
            <!-- Comments timeline will be loaded here -->
        </div>
    </div>
</div>

<script>
    $(document).ready(function() {
        // Load comments timeline
        const postId = new URLSearchParams(window.location.search).get('id');
        if (postId) {
            App.loadView('timeline', { 
                type: 'comments',
                postId: postId 
            }, '#comments-timeline');
        }
    });
</script>
