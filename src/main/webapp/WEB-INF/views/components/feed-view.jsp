<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="feed-container">
    <!-- Feed type tabs -->
    <div class="feed-tabs">
        <button class="tab-button active" data-feed-type="for-you">For You</button>
        <button class="tab-button" data-feed-type="following">Following</button>
    </div>

    <!-- Timeline container where posts will be loaded -->
    <div id="timeline-container" class="timeline-container">
        <!-- Timeline view will be loaded here -->
    </div>
</div>

<script>
    // Handle tab switching
    $('.tab-button').on('click', function() {
        $('.tab-button').removeClass('active');
        $(this).addClass('active');
        
        const feedType = $(this).data('feed-type');
        // Load appropriate timeline
        App.loadView('timeline', { type: feedType });
    });
</script>
