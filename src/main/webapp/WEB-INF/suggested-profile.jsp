<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.webdev.cheeper.model.User, com.webdev.cheeper.model.Student, com.webdev.cheeper.model.Entity, com.webdev.cheeper.model.Association" %>

<%
    User profile = (User) request.getAttribute("profile");
    if (profile == null) {
        out.print("<p>Profile not found.</p>");
        return;
    }

    Student     student     = (profile instanceof Student)     ? (Student)     profile : null;
    Entity      entity      = (profile instanceof Entity)      ? (Entity)      profile : null;
    Association association = (profile instanceof Association) ? (Association) profile : null;

    int followersCount = request.getAttribute("followersCount")  != null
                       ? (Integer) request.getAttribute("followersCount")  : 0;
    int followingCount = request.getAttribute("followingCount") != null
                       ? (Integer) request.getAttribute("followingCount") : 0;

    boolean readOnly = request.getAttribute("readOnly") != null && (Boolean) request.getAttribute("readOnly");
%>

<link rel="stylesheet" href="<%= request.getContextPath() %>/static/css/profile.css" />

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
        <button class="follow-stat-btn" 
                onclick="<%= readOnly ? "" : "App.loadFollowList('followers', " + profile.getId() + ")" %>" 
                <%= readOnly ? "disabled" : "" %>>
          <span id="followersCount"><%= followersCount %></span> Followers
        </button>
        <button class="follow-stat-btn" 
                onclick="<%= readOnly ? "" : "App.loadFollowList('following', " + profile.getId() + ")" %>" 
                <%= readOnly ? "disabled" : "" %>>
          <span id="followingCount"><%= followingCount %></span> Following
        </button>
      </div>
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

    <% if (student != null && !student.getDegrees().isEmpty()) { %>
      <div class="profile-detail-item">
        <span class="profile-detail-label">Degree:</span>
        <span class="profile-detail-value">
          <%= student.getDegrees().values().iterator().next() %>
        </span>
      </div>
    <% } %>

    <% if (entity != null && entity.getDepartment() != null) { %>
      <div class="profile-detail-item">
        <span class="profile-detail-label">Department:</span>
        <span class="profile-detail-value"><%= entity.getDepartment() %></span>
      </div>
    <% } %>
  </div>
</div>


