package com.webdev.cheeper.service;

import java.util.List;
import java.util.Set;

import com.webdev.cheeper.model.User;
import com.webdev.cheeper.repository.FollowRepository;

public class FollowService {
    private final FollowRepository followRepository;

    public FollowService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }
    
    public List<User> getFollowers(int userId) {
    	return followRepository.getFollowers(userId);
    }
    
    public List<User> getFollowing(int userId) {
    	return followRepository.getFollowing(userId);
    }
    
    public Set<Integer> getFollowingIds(int userId) {
    	return followRepository.getFollowingIds(userId);
    }
    
    public boolean follow(int followerId, int followingId) {
    	return followRepository.followUser(followerId, followingId);
    }
    
    public boolean unfollow(int followerId, int followingId) {
    	return followRepository.unfollowUser(followerId, followingId);
    }
    
    public int countFollowers(int userId) {
    	return followRepository.countFollowers(userId);
    }
    public int countFollowing(int userId) {
    	return followRepository.countFollowing(userId);
    }
    
 }
