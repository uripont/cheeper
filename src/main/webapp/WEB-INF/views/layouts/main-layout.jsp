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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/users-list.css" />
    <link rel="icon" href="${pageContext.request.contextPath}/static/images/upf.jpg" />
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/app.js" defer></script>
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
        <a href="${pageContext.request.contextPath}/app/chats" class="menu" data-view="chats">
            <img src="${pageContext.request.contextPath}/static/images/text.bubble.png" alt="Chats" />
        </a>
        <a href="${pageContext.request.contextPath}/app/profile" class="menu" data-view="profile">
            <img src="${pageContext.request.contextPath}/static/images/person.png" alt="Profile" />
        </a>
    </div>

    <!-- Main layout container -->
    <div class="main-layout">
        <!-- Main content area -->
        <div class="main-content">
            <div id="main-panel" class="feed">
                <!-- Temporary content for testing -->
                <h1>Main Content Area</h1>
                <p>This is where dynamic content will be loaded.</p>
                <p>The layout should maintain proper spacing and responsiveness.</p>
            </div>
        </div>

        <!-- Right sidebar -->
        <div class="right-sidebar" id="rightSidebar">
            <!-- Temporary content for testing -->
            <h2>Right Sidebar</h2>
            <p>This sidebar should:</p>
            <ul>
                <li>Stay visible above 1000px width</li>
                <li>Hide below 1000px</li>
                <li>Maintain fixed width</li>
            </ul>
        </div>
    </div>

    <script>
        $(document).ready(function() {
            App.init();
            
            // Initialize layout with active menu item
            $('[data-view="${view}"]').addClass('active');
        });
    </script>
</body>
</html>
