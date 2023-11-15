package com.example.demo.services;

import com.example.demo.entities.CommentEntity;
import com.example.demo.entities.QueryEntity;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.exceptions.ErrorMessage;
import com.example.demo.exceptions.IllegalOperationException;
import com.example.demo.repositories.CommentRepository;
import com.example.demo.repositories.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class QueryCommentService {

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Transactional
    public CommentEntity addCommentToQuery(Long queryId, Long commentId) throws EntityNotFoundException, IllegalOperationException {
        
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.COMMENT_NOT_FOUND));

        QueryEntity query = queryRepository.findById(queryId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.QUERY_NOT_FOUND));

        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new IllegalOperationException("El contenido del comentario no puede estar vacío.");
        }

        comment.setQuery(query);
        query.getComments().add(comment);

        queryRepository.save(query);
        
        return commentRepository.save(comment);
    }

    @Transactional
    public void removeCommentFromQuery(Long queryId, Long commentId) throws EntityNotFoundException, IllegalOperationException {
    QueryEntity query = queryRepository.findById(queryId)
            .orElseThrow(() -> new EntityNotFoundException("Query no encontrado."));
    
    CommentEntity comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("Comentario no encontrado."));

    if (!comment.getQuery().equals(query)) {
        throw new IllegalOperationException("El comentario no pertenece al query especificado.");
    }

    query.getComments().remove(comment); // Actualiza la lista de comentarios en memoria
    commentRepository.delete(comment); // Elimina el comentario de la base de datos
}

    @Transactional
    public List<CommentEntity> getCommentsByQuery(Long queryId) throws EntityNotFoundException {
        QueryEntity query = queryRepository.findById(queryId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.QUERY_NOT_FOUND));

        return query.getComments();
    }
}
