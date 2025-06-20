package com.webdev.cheeper.service;

import com.webdev.cheeper.model.Like;
import com.webdev.cheeper.repository.LikeRepository;

import java.sql.Timestamp;
import java.util.List;

public class LikeService {
    private final LikeRepository likeRepository;

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public void toggleLike(int userId, int postId) {
        if (likeRepository.exists(postId, userId)) {
            likeRepository.delete(postId, userId);
        } else {
            Like like = new Like(userId, postId, new Timestamp(System.currentTimeMillis()));
            likeRepository.save(like);
        }
    }

    public List<Like> getLikesForPost(int postId) {
        return likeRepository.findByPostId(postId);
    }

    public boolean isLikedByUser(int postId, int userId) {
        return likeRepository.exists(postId, userId);
    }
}
