package com.example.demo.services;

import com.example.demo.entities.CommentEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.exceptions.ErrorMessage;
import com.example.demo.exceptions.IllegalOperationException;
import com.example.demo.repositories.CommentRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class UserCommentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Transactional
    public CommentEntity addCommentToUser(Long userId, CommentEntity comment) throws EntityNotFoundException, IllegalOperationException {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.COMMENT_NOT_FOUND));

        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new IllegalOperationException("El contenido del comentario no puede estar vacío.");
        }

        comment.setUser(user);
        return commentRepository.save(comment);
    }

    @Transactional
    public void removeCommentFromUser(Long userId, Long commentId) throws EntityNotFoundException, IllegalOperationException{
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));
        
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.COMMENT_NOT_FOUND));

        if (!comment.getUser().equals(user)) {
            throw new IllegalOperationException("El comentario no pertenece al usuario especificado.");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public List<CommentEntity> getCommentsByUser(Long userId) throws EntityNotFoundException {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));

        return user.getComments();
    }

}
