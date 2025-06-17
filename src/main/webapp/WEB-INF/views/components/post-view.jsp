<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/static/css/post.css">

<div class="post-view">
    <!-- Main post -->
    <div class="post-container">
        <div class="post-header">
            <h2>Post Details</h2>
        </div>
        <c:if test="${not empty post}">
            <div class="post-item" data-post-id="${post.id}">
                <div class="post-header">
                    <div class="user-info">
                        <c:choose>
                            <c:when test="${not empty postAuthor}">
                                <strong>${postAuthor.fullName}</strong>
                                <span class="username">@${postAuthor.username}</span>
                            </c:when>
                            <c:otherwise>
                                <strong>Unknown User</strong>
                            </c:otherwise>
                        </c:choose>
                        <span>
                            <fmt:formatDate value="${post.createdAt}" pattern="MMM dd, yyyy HH:mm"/>
                        </span>
                    </div>
                </div>
                <div class="post-content">
                    <p>${post.content}</p>
                </div>
                <div class="post-actions">
                    <button class="action-btn like-btn" title="Like" data-post-id="${post.id}">
                        <c:choose>
                            <c:when test="${isLikedByUser}">
                                <img src="${pageContext.request.contextPath}/static/images/heart.fill.red.png" alt="Liked" width="18" height="18">
                            </c:when>
                            <c:otherwise>
                                <img src="${pageContext.request.contextPath}/static/images/heart.png" alt="Like" width="18" height="18">
                            </c:otherwise>
                        </c:choose>
                        <span class="like-count">${likeCount}</span>
                    </button>
                    <button class="action-btn reply-btn" title="Reply">
                        <img src="${pageContext.request.contextPath}/static/images/reply.png" alt="Reply" width="18" height="18">
                    </button>
                </div>
            </div>
        </c:if>

        <c:if test="${empty post}">
            <div class="placeholder-message">
                <p>Post not found or error loading post.</p>
            </div>
        </c:if>
    </div>

    <!-- Reply form -->
    <div class="reply-form-container">
        <h3>Reply to this post</h3>
        <form id="replyForm" action="${pageContext.request.contextPath}/post" method="post">
            <input type="hidden" name="source_id" value="${post.id}"/>
            <textarea id="replyContent" name="content" placeholder="Write your reply... (Ctrl/Cmd + Enter to reply)" required></textarea>
            <button type="submit" id="replyBtn">Reply</button>
        </form>
    </div>

    <!-- Comments section -->
    <div class="comments-section">
        <h3>Comments</h3>
        <div id="comments-timeline" class="timeline-container">
            <!-- Comments timeline will be loaded here -->
        </div>
    </div>
</div>

<script>
    $(document).ready(function() {
        const postId = '${post.id}';

        if (postId) {
            App.loadView('timeline', { 
                type: 'comments',
                postId: postId 
            }, '#comments-timeline');
        }

        // Handle like button click
        $('.post-view').on('click', '.like-btn', function () {
            const postElement = $(this).closest('.post-item');
            const postId = postElement.data('post-id');
            const likeBtn = $(this);
            const likeImg = likeBtn.find('img');
            const likeCount = likeBtn.find('.like-count');

            if (postId) {
                $.post('/like', { postId: postId })
                    .done(function(response) {
                        if (response.liked) {
                            likeImg.attr('src', '${pageContext.request.contextPath}/static/images/heart-fill.png');
                            likeImg.attr('alt', 'Liked');
                        } else {
                            likeImg.attr('src', '${pageContext.request.contextPath}/static/images/heart.png');
                            likeImg.attr('alt', 'Like');
                        }

                        if (response.likeCount > 0) {
                            likeCount.text(response.likeCount).show();
                        } else {
                            likeCount.hide();
                        }
                    })
                    .fail(function() {
                        console.error('Error toggling like');
                    });
            }
        });

        const replyForm = document.getElementById('replyForm');
        const replyTextarea = document.getElementById('replyContent');
        const replyBtn = document.getElementById('replyBtn');

        function submitReplyForm() {
            const formData = new FormData(replyForm);

            replyBtn.disabled = true;
            replyBtn.textContent = 'Replying...';
            replyBtn.style.opacity = '0.6';

            $.ajax({
                url: '/post',
                method: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function(response) {
                    replyTextarea.value = '';
                    replyTextarea.style.height = 'auto';

                    App.loadView('timeline', { 
                        type: 'comments',
                        postId: postId 
                    }, '#comments-timeline');

                    replyBtn.disabled = false;
                    replyBtn.textContent = 'Reply';
                    replyBtn.style.opacity = '1';

                    showSuccessMessage('Reply posted successfully!');
                },
                error: function(xhr, status, error) {
                    console.error('Error posting reply:', error);
                    alert('Failed to post reply. Please try again.');

                    replyBtn.disabled = false;
                    replyBtn.textContent = 'Reply';
                    replyBtn.style.opacity = '1';
                }
            });
        }

        if (replyForm) {
            replyForm.addEventListener('submit', function(e) {
                e.preventDefault();
                submitReplyForm();
            });
        }

        if (replyTextarea) {
            replyTextarea.addEventListener('keydown', function(e) {
                if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
                    e.preventDefault();

                    if (replyTextarea.value.trim() === '') {
                        alert('Please write something before replying!');
                        return;
                    }

                    submitReplyForm();
                }
            });

            replyTextarea.addEventListener('input', function() {
                this.style.height = 'auto';
                this.style.height = this.scrollHeight + 'px';
            });
        }

        function showSuccessMessage(message) {
            const successDiv = $('<div></div>').css({
                position: 'fixed',
                top: '20px',
                right: '20px',
                background: '#28a745',
                color: 'white',
                padding: '12px 20px',
                borderRadius: '6px',
                zIndex: 1000,
                fontSize: '14px',
                boxShadow: '0 2px 8px rgba(0,0,0,0.2)'
            }).text(message);

            $('body').append(successDiv);

            setTimeout(() => {
                successDiv.remove();
            }, 3000);
        }
    });
</script>
