package com.webdev.cheeper.service;

import com.webdev.cheeper.model.Post;
import com.webdev.cheeper.repository.PostRepository;

import java.sql.SQLException;
import java.util.Optional;

public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // Método para crear un nuevo post
    public void createPost(Post post) throws SQLException {
        // Validación: El contenido no puede estar vacío
        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }

        // Guardar el post en la base de datos
        postRepository.save(post);
    }

    // Método para obtener un post por ID
    public Post getPostById(int postId) throws SQLException {
        Optional<Post> postOpt = postRepository.findById(postId);
        return postOpt.orElse(null);
    }

    // Método para obtener todos los posts
    public java.util.List<Post> getAllPosts() throws SQLException {
        return postRepository.findAll();
    }

    public java.util.List<Post> getPostsByFollowedUsers(int userId) throws SQLException {
        return postRepository.findByFollowedUsers(userId);
    }

    // Método para obtener todos los posts
    public java.util.List<Post> getAllPostsButYours(int userId) throws SQLException {
        return postRepository.findAllButYou(userId);
    }

    // Método para obtener posts por usuario
    public java.util.List<Post> getCommentsForPost(int postId) throws SQLException {
        return postRepository.findBySourceId(postId);
    }

    public java.util.List<Post> getPostsByUserId(int userId) throws SQLException {
        return postRepository.findByUserId(userId);
    }


    public void deletePost(int postId) throws SQLException {
        postRepository.deleteById(postId);
    }

    public void updatePost(Post post) throws SQLException {
        // Validation 
        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
        
        // Update the post's timestamp
        post.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        
        postRepository.update(post);
    }
}