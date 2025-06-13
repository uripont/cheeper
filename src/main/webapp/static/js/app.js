const App = (function() {
    // Load a view into the main panel
    function loadView(view) {
        // Make AJAX request to get the view content
        $.ajax({
            url: `/views/${view}`,
            method: 'GET',
            success: function(response) {
                // Update main panel with the received content
                $('#main-panel').html(response);
            },
            error: function(xhr, status, error) {
                console.error('Error loading view:', error);
                $('#main-panel').html('<p>Error loading content.</p>');
            }
        });
    }

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

            // Load appropriate view based on data-view attribute
            const view = $(this).data('view');
            if (view) {
                loadView(view);
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
            
            // Clear dynamic areas
            $('#main-panel').empty();
            $('#rightSidebar').empty();

            // Extract view name from path and load it
            const view = path.split('/').pop();
            if (view) {
                loadView(view);
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
        loadView: loadView
    };
})();
