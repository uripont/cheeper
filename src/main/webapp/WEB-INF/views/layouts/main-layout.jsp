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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/private-chat-users.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/chats.css" />
    <link rel="icon" href="${pageContext.request.contextPath}/static/images/default.png" />
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/js/app.js" defer></script>
</head>

<body data-context-path="${pageContext.request.contextPath}">
    <!-- Add logo at the top -->
    <a href="${pageContext.request.contextPath}/home" class="logo">
        <img src="${pageContext.request.contextPath}/static/images/red-logo.png" alt="Logo"/>
    </a>

    <!-- Left/Icon Sidebar -->
    <div class="sidebar" id="navigation">
        <a href="${pageContext.request.contextPath}/home" class="menu" data-view="home">
            <img src="${pageContext.request.contextPath}/static/images/house.png" alt="Home" data-icon-base="house" />
        </a>
        <a href="${pageContext.request.contextPath}/explore" class="menu" data-view="explore">
            <img src="${pageContext.request.contextPath}/static/images/magnifyingglass.png" alt="Explore" data-icon-base="magnifyingglass" />
        </a>
        <a href="${pageContext.request.contextPath}/create-post" class="menu" data-view="create">
            <img src="${pageContext.request.contextPath}/static/images/plus.png" alt="Create" data-icon-base="plus" />
        </a>
        <a href="${pageContext.request.contextPath}/chats" class="menu" data-view="chats">
            <img src="${pageContext.request.contextPath}/static/images/text.bubble.png" alt="Chats" data-icon-base="text.bubble" />
        </a>
        <a href="${pageContext.request.contextPath}/profile" class="menu" data-view="profile">
            <img src="${pageContext.request.contextPath}/static/images/person.png" alt="Profile" data-icon-base="person" />
        </a>
    </div>

    <!-- Main layout container -->
    <div class="main-layout">
        <!-- Main content area -->
        <div class="main-content">
            <div id="main-panel" class="feed">
            </div>
        </div>

        <!-- Right sidebar -->
        <div class="right-sidebar" id="rightSidebar">
        </div>
    </div>

    <script>
        $(document).ready(function() {
            App.init();
            
            // Initialize layout with active menu item
            $('[data-view="${view}"]').addClass('active');
            App.updateSidebarIcons();

            // Load initial content if this is home view
            if ('${view}' === 'home') {
                App.loadFeed();
            }
        });
    </script>
</body>
</html>
