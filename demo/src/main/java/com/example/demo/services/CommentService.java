package com.example.demo.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.example.demo.entities.CommentEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.exceptions.ErrorMessage;
import com.example.demo.exceptions.IllegalOperationException;
import com.example.demo.repositories.CommentRepository;
import com.example.demo.repositories.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.repositories.QueryRepository;
import java.util.List;
import java.util.Optional;

import com.example.demo.entities.QueryEntity;


@Slf4j
@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QueryRepository queryRepository;  // Asumiendo que tienes un QueryRepository

    @Transactional
    public CommentEntity createComment(Long userId, Long queryId, CommentEntity commentEntity) throws IllegalOperationException, EntityNotFoundException{
        if (commentEntity.getContent() == null || commentEntity.getContent().trim().isEmpty()) {
            throw new IllegalOperationException("El comentario no puede estar vacío.");
        }

        Optional<UserEntity> user = userRepository.findById(userId);

        if (user.isEmpty()){

            throw new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND);
        }
        QueryEntity query = queryRepository.findById(queryId).orElseThrow(() -> new EntityNotFoundException(ErrorMessage.QUERY_NOT_FOUND));

        commentEntity.setUser(user.get());
        commentEntity.setQuery(query);

        return commentRepository.save(commentEntity);
    }

    @Transactional
    public CommentEntity updateComment(Long commentId, CommentEntity updatedComment) throws IllegalOperationException, EntityNotFoundException{

        CommentEntity existingComment = commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException(ErrorMessage.COMMENT_NOT_FOUND));

        if (updatedComment.getContent() != null && !updatedComment.getContent().trim().isEmpty()) {
            existingComment.setContent(updatedComment.getContent());
        } else {
            throw new IllegalOperationException("El contenido del comentario no puede estar vacío.");
        }

        return commentRepository.save(existingComment);
    }

    @Transactional()
    public CommentEntity getComment(Long commentId) throws EntityNotFoundException {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.COMMENT_NOT_FOUND));
    }

    @Transactional()
    public List<CommentEntity> getAllComments() {
        return commentRepository.findAll();
    }

    @Transactional
    public void deleteComment(Long commentId) throws EntityNotFoundException {
        CommentEntity comment = commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("Comentario no encontrado."));
        commentRepository.delete(comment);
    }
}

