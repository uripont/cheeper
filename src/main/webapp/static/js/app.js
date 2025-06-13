const App = (function() {
    // Handle client-side navigation
    function handleNavigation() {
        $(document).on('click', '.menu', function(event) {
            event.preventDefault();

            const url = $(this).attr('href');
            
            // Update active menu item
            $('.menu').removeClass('active');
            $(this).addClass('active');
            
            // Update browser URL without reload
            window.history.pushState({}, '', url);

            // Clear dynamic areas
            $('#main-panel').empty();
            $('#rightSidebar').empty();

            // Load appropriate views based on the route
            // TODO Views will be implemented later
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
            
            // Clear dynamic areas
            $('#main-panel').empty();
            $('#rightSidebar').empty();

            // Load appropriate views based on the route
            // (Views will be implemented later)
        });
    }

    function init() {
      
        
        // Setup navigation handling
        handleNavigation();
        handlePopState();
    }

    return {
        init: init
    };
})();
