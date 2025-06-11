<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.webdev.cheeper.model.User, com.webdev.cheeper.model.Student" %>
<%@ page import="com.webdev.cheeper.model.User, com.webdev.cheeper.model.Entity" %>
<%@ page import="com.webdev.cheeper.model.User, com.webdev.cheeper.model.Association" %>

<%
    User profile = (User) request.getAttribute("profile");
    if (profile == null) {
        out.print("<p>Profile not found.</p>");
        return;
    }

    Student student = (profile instanceof Student) ? (Student) profile : null;
    Association association = (profile instanceof Association) ? (Association) profile : null;
    Entity entity = (profile instanceof Entity) ? (Entity) profile : null;

    int followersCount = request.getAttribute("followersCount") != null ? (int) request.getAttribute("followersCount") : 0;
    int followingCount = request.getAttribute("followingCount") != null ? (int) request.getAttribute("followingCount") : 0;
%>

<div class="profile-container" data-user-id="<%= profile.getId() %>">

  <div class="profile-header">
    <div class="profile-picture-frame">
      <img src="<%= request.getContextPath() + "/local-images/" + (profile.getPicture() != null ? profile.getPicture() : "default.png") %>"
           alt="Profile Picture" class="profile-picture"
           onerror="this.onerror=null;this.src='<%= request.getContextPath() %>/local-images/default.png';" />
    </div>

    <div class="profile-info">
      <h1 class="profile-name"><%= profile.getFullName() %></h1>
      <div class="profile-detail-item">
        <span class="profile-detail-label">Username:</span>
        <span class="profile-detail-value"><%= profile.getUsername() %></span>
      </div>

	<div class="follow-stats">
	  <button class="follow-stat-btn" onclick="App.loadFollowList('followers', <%= profile.getId() %>)">
	    <span id="followersCount"><%= followersCount %></span> Followers
	  </button>
	  <button class="follow-stat-btn" onclick="App.loadFollowList('following', <%= profile.getId() %>)">
	    <span id="followingCount"><%= followingCount %></span> Following
	  </button>
	</div>
  </div>

  <% if (profile.getBiography() != null && !profile.getBiography().isEmpty()) { %>
    <div class="profile-detail-item biography">
      <p><%= profile.getBiography() %></p>
    </div>
  <% } %>

  <div class="profile-details">
    <div class="profile-detail-item">
      <span class="profile-detail-label">Role:</span>
      <span class="profile-detail-value"><%= profile.getRoleType() != null ? profile.getRoleType().toString() : "" %></span>
    </div>
    <div class="profile-detail-item">
      <span class="profile-detail-label">Email:</span>
      <span class="profile-detail-value"><%= profile.getEmail() %></span>
    </div>

    <% if (student != null) { %>
      <% if (!student.getSocialLinks().isEmpty()) { %>
        <div class="profile-detail-item">
          <span class="profile-detail-label">Social Links:</span>
          <span class="profile-detail-value">
            <ul>
              <% for (var entry : student.getSocialLinks().entrySet()) { %>
                <li><a href="<%= entry.getValue() %>" target="_blank" rel="noopener noreferrer"><%= entry.getKey() %></a></li>
              <% } %>
            </ul>
          </span>
        </div>
      <% } %>

      <% if (!student.getDegrees().isEmpty()) { %>
        <div class="profile-detail-item">
          <span class="profile-detail-label">Degrees:</span>
          <span class="profile-detail-value">
            <ul>
              <% for (var entry : student.getDegrees().entrySet()) { %>
                <li>[<%= entry.getKey() %>] <%= entry.getValue() %></li>
              <% } %>
            </ul>
          </span>
        </div>
      <% } %>

      <% if (!student.getEnrolledSubjects().isEmpty()) { %>
        <div class="profile-detail-item">
          <span class="profile-detail-label">Subjects:</span>
          <span class="profile-detail-value">
            <ul>
              <% for (var entry : student.getEnrolledSubjects().entrySet()) { %>
                <li>[<%= entry.getKey() %>] <%= entry.getValue() %></li>
              <% } %>
            </ul>
          </span>
        </div>
      <% } %>
    <% } %>
    
    <% if (entity != null) { %>
        <div class="profile-detail-item">
          <span class="profile-detail-label">Department:</span>
          <span class="profile-detail-value"><%= entity.getDepartment() %></span>
        </div>
    <% } %>
    

  </div>

</div>

