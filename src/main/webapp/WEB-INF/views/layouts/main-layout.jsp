<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>Cheeper</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/main-page.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/profile.css" />
    <link rel="icon" href="${pageContext.request.contextPath}/static/images/upf.jpg" />
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/feed-manager.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/profile-manager.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/form-handler.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/app.js"></script>
</head>

<body>
    <!-- Left/Icon Sidebar -->
    <div class="sidebar" id="navigation">
        <a href="${pageContext.request.contextPath}/app/home" class="menu" data-view="home">
            <img src="${pageContext.request.contextPath}/static/images/house.fill.png" alt="Home" />
        </a>
        <a href="${pageContext.request.contextPath}/app/explore" class="menu" data-view="explore">
            <img src="${pageContext.request.contextPath}/static/images/magnifyingglass.png" alt="Explore" />
        </a>
        <a href="${pageContext.request.contextPath}/app/create" class="menu" data-view="create">
            <img src="${pageContext.request.contextPath}/static/images/plus.png" alt="Create" />
        </a>
        <a href="${pageContext.request.contextPath}/app/profile" class="menu" data-view="profile">
            <img src="${pageContext.request.contextPath}/static/images/person.png" alt="Profile" />
        </a>
    </div>

    <!-- Main layout container -->
    <div class="main-layout">
        <!-- Dynamic main content area -->
        <div class="main-content">
            <div id="main-panel">
                <!-- Dynamic content loaded here via AJAX -->
                <c:if test="${view == 'feed'}">
                    <div class="choose-view" id="chooseView">
                        <p class="selected" data-view="for-you">For You</p>
                        <p class="not-selected" data-view="following">Following</p>
                    </div>
                    <div id="feed" class="feed">
                        <!-- Timeline content loaded here -->
                    </div>
                </c:if>
            </div>
        </div>

        <!-- Dynamic right sidebar -->
        <div class="right-sidebar" id="rightSidebar">
            <!-- Dynamic content loaded here via AJAX -->
            <div id="suggestedProfilesContainer"></div>
        </div>
    </div>

    <script>
        $(document).ready(function() {
            App.init();
            
            // Set active menu item based on current view
            $('.menu').removeClass('active');
            $('[data-view="${view}"]').addClass('active');
        });
    </script>
</body>
</html>
