package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.entities.CommentEntity;
import com.example.demo.entities.QueryEntity;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.exceptions.IllegalOperationException;
import com.example.demo.repositories.CommentRepository;
import com.example.demo.repositories.QueryRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryCommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private QueryRepository queryRepository;

    @InjectMocks
    private QueryCommentService queryCommentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddCommentToQuery() throws EntityNotFoundException, IllegalOperationException {
        // Arrange
        Long queryId = 1L;
        Long commentId = 1L;

        QueryEntity query = new QueryEntity();
        query.setId(queryId);

        CommentEntity comment = new CommentEntity();
        comment.setId(commentId);
        comment.setContent("This is a comment");

        when(queryRepository.findById(queryId)).thenReturn(Optional.of(query));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(queryRepository.save(query)).thenReturn(query);

        // Act
        CommentEntity addedComment = queryCommentService.addCommentToQuery(queryId, commentId);

        // Assert
        assertNotNull(addedComment);
        assertEquals(queryId, addedComment.getQuery().getId());
        assertEquals(commentId, addedComment.getId());
        assertEquals("This is a comment", addedComment.getContent());
    }

    @Test
    public void testAddCommentToQueryWithEmptyContent() {
        // Arrange
        Long queryId = 1L;
        Long commentId = 1L;

        QueryEntity query = new QueryEntity();
        query.setId(queryId);

        CommentEntity comment = new CommentEntity();
        comment.setId(commentId);
        comment.setContent(""); // Empty content

        when(queryRepository.findById(queryId)).thenReturn(Optional.of(query));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act & Assert
        assertThrows(IllegalOperationException.class, () -> queryCommentService.addCommentToQuery(queryId, commentId));
    }

    @Test
    public void testAddCommentToQueryWithQueryNotFound() {
        // Arrange
        Long queryId = 1L;
        Long commentId = 1L;

        when(queryRepository.findById(queryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> queryCommentService.addCommentToQuery(queryId, commentId));
    }

    @Test
    public void testAddCommentToQueryWithCommentNotFound() {
        // Arrange
        Long queryId = 1L;
        Long commentId = 1L;

        QueryEntity query = new QueryEntity();
        query.setId(queryId);

        when(queryRepository.findById(queryId)).thenReturn(Optional.of(query));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> queryCommentService.addCommentToQuery(queryId, commentId));
    }

    @Test
    public void testRemoveCommentFromQuery() throws EntityNotFoundException, IllegalOperationException {
        // Arrange
        Long queryId = 1L;
        Long commentId = 1L;

        QueryEntity query = new QueryEntity();
        query.setId(queryId);

        CommentEntity comment = new CommentEntity();
        comment.setId(commentId);
        comment.setQuery(query);

        when(queryRepository.findById(queryId)).thenReturn(Optional.of(query));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act
        queryCommentService.removeCommentFromQuery(queryId, commentId);

        // Assert
        assertTrue(query.getComments().isEmpty());
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    public void testRemoveCommentFromQueryWithQueryNotFound() {
        // Arrange
        Long queryId = 1L;
        Long commentId = 1L;

        when(queryRepository.findById(queryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> queryCommentService.removeCommentFromQuery(queryId, commentId));
    }

    @Test
    public void testRemoveCommentFromQueryWithCommentNotFound() {
        // Arrange
        Long queryId = 1L;
        Long commentId = 1L;

        QueryEntity query = new QueryEntity();
        query.setId(queryId);

        when(queryRepository.findById(queryId)).thenReturn(Optional.of(query));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> queryCommentService.removeCommentFromQuery(queryId, commentId));
    }

    @Test
    public void testRemoveCommentFromQueryWithMismatchedQuery() {
        // Arrange
        Long queryId = 1L;
        Long commentId = 1L;

        QueryEntity query = new QueryEntity();
        query.setId(queryId);

        CommentEntity comment = new CommentEntity();
        comment.setId(commentId);
        comment.setQuery(new QueryEntity()); // Different query

        when(queryRepository.findById(queryId)).thenReturn(Optional.of(query));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act & Assert
        assertThrows(IllegalOperationException.class, () -> queryCommentService.removeCommentFromQuery(queryId, commentId));
    }

    @Test
    public void testGetCommentsByQuery() throws EntityNotFoundException {
        // Arrange
        Long queryId = 1L;
        QueryEntity query = new QueryEntity();
        query.setId(queryId);

        List<CommentEntity> comments = new ArrayList<>();
        CommentEntity comment1 = new CommentEntity();
        comment1.setId(1L);
        comments.add(comment1);
        CommentEntity comment2 = new CommentEntity();
        comment2.setId(2L);
        comments.add(comment2);

        query.setComments(comments);

        when(queryRepository.findById(queryId)).thenReturn(Optional.of(query));

        // Act
        List<CommentEntity> retrievedComments = queryCommentService.getCommentsByQuery(queryId);

        // Assert
        assertNotNull(retrievedComments);
        assertEquals(2, retrievedComments.size());
        assertEquals(1L, retrievedComments.get(0).getId());
        assertEquals(2L, retrievedComments.get(1).getId());
    }

    @Test
    public void testGetCommentsByQueryWithQueryNotFound() {
        // Arrange
        Long queryId = 1L;

        when(queryRepository.findById(queryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> queryCommentService.getCommentsByQuery(queryId));
    }
}

