const App = (function() {
    // Load a view into a target container
    function loadView(view, params = {}, targetContainer = '#main-panel') {
        // Construct query string from params
        const queryString = Object.keys(params)
            .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
            .join('&');
            
        // Make AJAX request to get the view content
    console.log('Loading view:', view, 'with params:', params, 'to container:', targetContainer);
    const fullUrl = `/views/${view}${queryString ? '?' + queryString : ''}`;
    console.log('Request URL:', fullUrl);

    $.ajax({
        url: fullUrl,
        method: 'GET',
        success: function(response) {
            console.log('View loaded successfully:', view);
            // Update target container with the received content
            $(targetContainer).html(response);
        },
        error: function(xhr, status, error) {
            console.error('Error loading view:', {
                view: view,
                status: status,
                error: error,
                response: xhr.responseText,
                statusCode: xhr.status
            });
            $(targetContainer).html('<p>Error loading content.</p>');
        }
    });
    }

    // Handle client-side navigation
    function handleNavigation() {
        $(document).on('click', '.menu', function(event) {
            event.preventDefault();

            const url = $(this).attr('href');
            const view = $(this).data('view');
            
            // Update active menu item
            $('.menu').removeClass('active');
            $(this).addClass('active');
            App.updateSidebarIcons(); // Update icons after changing active class
            
            // Update browser URL without reload
            window.history.pushState({}, '', url);

            // Clear dynamic areas
            $('#main-panel').empty();
            $('#rightSidebar').empty();

            // Load appropriate view based on data-view attribute
            if (view) {
                switch(view) {
                    case 'home':
                        // Load feed and suggested users
                        loadView('feed');
                        loadView('users', { context: 'suggestions' }, '#rightSidebar');
                        break;
                    case 'explore':
                        // Load users search view
                        loadView('users', { context: 'search' });
                        break;
                    case 'create':
                        // Load post creation view
                        loadView('create');
                        break;
                    case 'profile':
                        // Load profile view and suggested users
                        loadView('profile');
                        loadView('users', { context: 'suggestions' }, '#rightSidebar');
                        break;
                    case 'chats':
                        // Load chats view and private chat users list
                        loadView('chats');
                        loadView('chats', { component: 'private-chat-users' }, '#rightSidebar');
                        break;
                    default:
                        loadView(view);
                }
            }
        });
    }

    // Handle browser back/forward buttons
    function handlePopState() {
        window.addEventListener('popstate', function() {
            // Get current path from URL
            const path = window.location.pathname;
            
            // Update active menu item
            $('.menu').removeClass('active');
            $(`a[href="${path}"]`).addClass('active');
            App.updateSidebarIcons(); // Update icons after changing active class
            
            // Clear dynamic areas
            $('#main-panel').empty();
            $('#rightSidebar').empty();

            // Extract view name from path and load it
            const view = path.split('/').pop();
            if (view) {
                switch(view) {
                    case 'home':
                        // Load feed and suggested users
                        loadView('feed');
                        loadView('users', { context: 'suggestions' }, '#rightSidebar');
                        break;
                    case 'explore':
                        // Load users search view
                        loadView('users', { context: 'search' });
                        break;
                    case 'create':
                        // Load post creation view
                        loadView('create');
                        break;
                    case 'profile':
                        // Load profile view and suggested users
                        loadView('profile');
                        loadView('users', { context: 'suggestions' }, '#rightSidebar');
                        break;
                    case 'chats':
                        // Load chats view and private chat users list
                        loadView('chats');
                        loadView('chats', { component: 'private-chat-users' }, '#rightSidebar');
                        break;
                    default:
                        loadView(view);
                }
            }
        });
    }

    // Function to load the feed with its components
    function loadFeed() {
        // Load main feed view
        loadView('feed');
        // Load suggested users in right sidebar
        loadView('users', { context: 'suggestions' }, '#rightSidebar');
    }

    function updateSidebarIcons() {
        $('.menu').each(function() {
            const $menuItem = $(this);
            const $icon = $menuItem.find('img');
            const iconBase = $icon.data('icon-base'); // Corrected typo here
            const isActive = $menuItem.hasClass('active');

            if (iconBase) {
                const newSrc = isActive
                    ? `${App.contextPath}/static/images/${iconBase}.fill.png`
                    : `${App.contextPath}/static/images/${iconBase}.png`;
                $icon.attr('src', newSrc);
            }
        });
    }

    function init() {
        // Setup navigation handling
        handleNavigation();
        handlePopState();
    }

    return {
        init: init,
        loadView: loadView,
        loadFeed: loadFeed,
        updateSidebarIcons: updateSidebarIcons,
        contextPath: $('body').data('context-path')
    };
})();
