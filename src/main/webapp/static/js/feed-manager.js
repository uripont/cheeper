const FeedManager = (function() {
  function loadFeed(view) {
    $('#feed').load(view === 'for-you' ? '/feed-for-you' : '/feed-following');
  }

  function handleViewChange() {
    $(document).on('click', '.choose-view p', function() {
      $('.choose-view p').removeClass('selected').addClass('not-selected');
      $(this).addClass('selected').removeClass('not-selected');
      const view = $(this).data('view');
      loadFeed(view);
    });
  }

  return {
    loadFeed: loadFeed,
    init: function() {
      handleViewChange();
    }
  };
})();
