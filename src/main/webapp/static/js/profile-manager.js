const ProfileManager = (function() {
  function loadSuggestedProfiles() {
    $.get('/suggested-profiles', function(data) {
      const container = $('#suggestedProfilesContainer');
      container.empty();

      const header = `
        <div class="suggested-header-row">
          <h2>Suggested Profiles</h2>
          <button class="shuffle-btn" onclick="ProfileManager.shuffleSuggestedProfiles()">🎲</button>
        </div>
      `;

      if (!data || data.length === 0) {
        container.append(header);
        container.append('<p class="no-profiles">No profiles to suggest</p>');
        return;
      }

      container.append(header);

      data.forEach(profile => {
        const basePath = window.location.origin + '/local-images';
        const imagePath = profile.picture && profile.picture !== 'default.png'
          ? basePath + '/profiles/' + profile.picture
          : basePath + '/default.png';

        container.append(`
          <div class="suggested-profile" data-user-id="${profile.id}">
            <img src="${imagePath}" alt="${profile.fullName}" class="clickable-profile" data-username="${profile.username}">
            <div class="suggested-profile-info clickable-profile" data-username="${profile.username}">
              <div class="suggested-profile-name">${profile.fullName}</div>
              <div class="suggested-profile-username">@${profile.username}</div>
              <div class="suggested-profile-role">${profile.role || ''}</div>  
            </div>
            <button class="follow-standard-btn">Follow</button>
          </div>
        `);
      });
    });
  }

  function loadFollowList(viewType, userId) {
    const url = `/${viewType}?userId=${userId}`;
    $.get(url, function(html) {
      const title = viewType.charAt(0).toUpperCase() + viewType.slice(1);
      
      const backButtonHtml = `<button class="standard-btn back-to-suggestions-btn">Back to Suggestions</button>`;
      
      $('#suggestedProfilesContainer').html(`
        <div class="${viewType}-section">
          <div class="suggested-header-row">
            <h2>${title}</h2>
            ${backButtonHtml}
          </div>
          <div class="profiles-list">
            ${html}
          </div>
        </div>
      `);
    }).fail(function() {
      $('#suggestedProfilesContainer').html('<p class="error">Could not load user list.</p>');
    });
  }

  function updateFollowCounts(userId) {
    $.get(`/profile-counts?userId=${userId}`, function(data) {
      $('#followersCount').text(data.followers);
      $('#followingCount').text(data.following);
    }).fail(function() {
      console.warn('Failed to update follow counts');
    });
  }

  function handleProfileClick() {
    $(document).on('click', '.clickable-profile', function() {
      const username = $(this).data('username');
      $.get(`/suggested-profile?username=${username}`, function(data) {
        $('#feed').html(data);
      });
    });
  }

  function handleFollow() {
    $(document).on('click', '.follow-standard-btn', function() {
      const button = $(this);
      const profileElement = button.closest('.suggested-profile');
      const userId = profileElement.data('user-id');
      const action = button.text().trim().toLowerCase();
      const url = action === 'unfollow' ? '/unfollow' : '/follow';

      $.post(url, { followingId: userId })
        .done(function() {
          if (action === 'unfollow') {
            profileElement.remove();
          } else {
            button.text('Unfollow');
          }

          const currentProfileUserId = $('#followersCount')
            .closest('.follow-stats')
            .find('.follow-stat-btn')
            .first()
            .attr('onclick')
            .match(/\d+/)[0];

          if (currentProfileUserId) {
            updateFollowCounts(currentProfileUserId);
          }
        })
        .fail(function() {
          alert('Failed to ' + action + ' user.');
        });
    });
  }

  function handleBackToSuggestions() {
    $(document).on('click', '.back-to-suggestions-btn', function() {
      loadSuggestedProfiles();
    });
  }

  return {
    loadSuggestedProfiles: loadSuggestedProfiles,
    loadFollowList: loadFollowList,
    updateFollowCounts: updateFollowCounts,
    shuffleSuggestedProfiles: loadSuggestedProfiles,
    init: function() {
      handleProfileClick();
      handleFollow();
      handleBackToSuggestions();
    }
  };
})();
