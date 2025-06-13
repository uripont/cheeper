<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="feed-view">
    <div class="feed-header">
        <div class="feed-tabs">
            <button class="tab-button active" data-feed-type="for-you">For You</button>
            <button class="tab-button" data-feed-type="following">Following</button>
        </div>
    </div>

    <div id="feed-timeline-container" class="timeline-container">
        <!-- Timeline will be loaded here -->
    </div>
</div>

<script>
    $(document).ready(function() {
        // Load initial timeline (For You)
        App.loadView('timeline', { type: 'for-you' }, '#feed-timeline-container');

        // Handle tab switching
        $('.tab-button').click(function() {
            // Update active state
            $('.tab-button').removeClass('active');
            $(this).addClass('active');

            // Load appropriate timeline
            const feedType = $(this).data('feed-type');
            App.loadView('timeline', { type: feedType }, '#feed-timeline-container');
        });
    });
</script>
