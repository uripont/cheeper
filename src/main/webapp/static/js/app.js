const App = (function() {
  function handleNavigation() {
    $(document).on('click', '.menu', function(event) {
      event.preventDefault();
      const url = $(this).attr('href');
      $('.menu').removeClass('active');
      $(this).addClass('active');
      $('#chooseView').toggle(url === 'home');
      $('#feed').load(url, function() {
        if (url === 'profile') {
          const currentUserId = $('#feed .profile-container').data('user-id');
          if (currentUserId) {
            ProfileManager.loadFollowList('followers', currentUserId);
          } else {
            $('#suggestedProfilesContainer').html('<p class="error">User ID not found.</p>');
          }
        } else {
          ProfileManager.loadSuggestedProfiles();
        }
      });
    });
  }

  function init() {
    $.ajaxSetup({ cache: false });
    $('#feed').load('/feed-for-you');
    
    // Initialize all modules
    FeedManager.init();
    ProfileManager.init();
    FormHandler.init();
    
    // Setup main navigation
    handleNavigation();
    
    // Load initial suggested profiles
    ProfileManager.loadSuggestedProfiles();
  }

  return {
    init: init
  };
})();
