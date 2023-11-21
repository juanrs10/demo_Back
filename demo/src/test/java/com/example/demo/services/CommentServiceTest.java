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
import com.example.demo.entities.UserEntity;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.exceptions.IllegalOperationException;
import com.example.demo.repositories.CommentRepository;
import com.example.demo.repositories.QueryRepository;
import com.example.demo.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QueryRepository queryRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateComment() throws IllegalOperationException, EntityNotFoundException {
        // Arrange
        Long userId = 1L;
        Long queryId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        CommentEntity newComment = new CommentEntity();
        newComment.setContent("This is a comment");

        QueryEntity query = new QueryEntity();
        query.setId(queryId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(queryRepository.findById(queryId)).thenReturn(Optional.of(query));
        when(commentRepository.save(newComment)).thenReturn(newComment);

        // Act
        CommentEntity createdComment = commentService.createComment(userId, queryId, newComment);

        // Assert
        assertNotNull(createdComment);
        assertEquals(userId, createdComment.getUser().getId());
        assertEquals(queryId, createdComment.getQuery().getId());
        assertEquals("This is a comment", createdComment.getContent());
    }

    @Test
    public void testCreateCommentWithEmptyContent() {
        // Arrange
        Long userId = 1L;
        Long queryId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        CommentEntity newComment = new CommentEntity();
        newComment.setContent(""); // Empty content

        QueryEntity query = new QueryEntity();
        query.setId(queryId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(queryRepository.findById(queryId)).thenReturn(Optional.of(query));

        // Act & Assert
        assertThrows(IllegalOperationException.class, () -> commentService.createComment(userId, queryId, newComment));
    }

    @Test
    public void testUpdateComment() throws IllegalOperationException, EntityNotFoundException {
        // Arrange
        Long commentId = 1L;
        CommentEntity existingComment = new CommentEntity();
        existingComment.setId(commentId);
        existingComment.setContent("Old content");

        CommentEntity updatedComment = new CommentEntity();
        updatedComment.setId(commentId);
        updatedComment.setContent("New content");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(existingComment)).thenReturn(existingComment);

        // Act
        CommentEntity modifiedComment = commentService.updateComment(commentId, updatedComment);

        // Assert
        assertNotNull(modifiedComment);
        assertEquals(commentId, modifiedComment.getId());
        assertEquals("New content", modifiedComment.getContent());
    }

    @Test
    public void testUpdateCommentWithEmptyContent() {
        // Arrange
        Long commentId = 1L;
        CommentEntity existingComment = new CommentEntity();
        existingComment.setId(commentId);
        existingComment.setContent("Old content");

        CommentEntity updatedComment = new CommentEntity();
        updatedComment.setId(commentId);
        updatedComment.setContent(""); // Empty content

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        // Act & Assert
        assertThrows(IllegalOperationException.class, () -> commentService.updateComment(commentId, updatedComment));
    }

    @Test
    public void testGetComment() throws EntityNotFoundException {
        // Arrange
        Long commentId = 1L;
        CommentEntity comment = new CommentEntity();
        comment.setId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act
        CommentEntity retrievedComment = commentService.getComment(commentId);

        // Assert
        assertNotNull(retrievedComment);
        assertEquals(commentId, retrievedComment.getId());
    }

    @Test
    public void testGetCommentNotFound() {
        // Arrange
        Long commentId = 1L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> commentService.getComment(commentId));
    }

    @Test
    public void testGetAllComments() {
        // Arrange
        List<CommentEntity> commentList = new ArrayList<>();
        CommentEntity comment1 = new CommentEntity();
        comment1.setId(1L);
        commentList.add(comment1);
        CommentEntity comment2 = new CommentEntity();
        comment2.setId(2L);
        commentList.add(comment2);

        when(commentRepository.findAll()).thenReturn(commentList);

        // Act
        List<CommentEntity> retrievedComments = commentService.getAllComments();

        // Assert
        assertNotNull(retrievedComments);
        assertEquals(2, retrievedComments.size());
        assertEquals(1L, retrievedComments.get(0).getId());
        assertEquals(2L, retrievedComments.get(1).getId());
    }

    @Test
    public void testDeleteComment() throws EntityNotFoundException {
        // Arrange
        Long commentId = 1L;
        CommentEntity comment = new CommentEntity();
        comment.setId(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act
        commentService.deleteComment(commentId);

        // Assert (verify that delete was called)
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    public void testDeleteCommentNotFound() {
        // Arrange
        Long commentId = 1L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(commentId));
    }
}
