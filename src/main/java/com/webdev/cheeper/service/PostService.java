package com.webdev.cheeper.service;

import com.webdev.cheeper.model.Post;
import com.webdev.cheeper.repository.PostRepository;

import java.sql.SQLException;

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
}
