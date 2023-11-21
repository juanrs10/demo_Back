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
import com.example.demo.entities.UserEntity;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.exceptions.IllegalOperationException;
import com.example.demo.repositories.CommentRepository;
import com.example.demo.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserCommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserCommentService userCommentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddCommentToUser() throws EntityNotFoundException, IllegalOperationException {
        // Arrange
        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setId(userId);

        CommentEntity comment = new CommentEntity();
        comment.setContent("This is a comment");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.save(comment)).thenReturn(comment);

        // Act
        CommentEntity addedComment = userCommentService.addCommentToUser(userId, comment);

        // Assert
        assertNotNull(addedComment);
        assertEquals(userId, addedComment.getUser().getId());
        assertEquals("This is a comment", addedComment.getContent());
    }

    @Test
    public void testAddCommentToUserWithEmptyContent() {
        // Arrange
        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setId(userId);

        CommentEntity comment = new CommentEntity();
        comment.setContent(""); // Empty content

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(IllegalOperationException.class, () -> userCommentService.addCommentToUser(userId, comment));
    }

    @Test
    public void testAddCommentToUserWithUserNotFound() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        CommentEntity comment = new CommentEntity();
        comment.setContent("This is a comment");

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userCommentService.addCommentToUser(userId, comment));
    }

    @Test
    public void testRemoveCommentFromUser() throws EntityNotFoundException, IllegalOperationException {
        // Arrange
        Long userId = 1L;
        Long commentId = 1L;

        UserEntity user = new UserEntity();
        user.setId(userId);

        CommentEntity comment = new CommentEntity();
        comment.setId(commentId);
        comment.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act
        userCommentService.removeCommentFromUser(userId, commentId);

        // Assert
        assertNull(comment.getUser());
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    public void testRemoveCommentFromUserWithUserNotFound() {
        // Arrange
        Long userId = 1L;
        Long commentId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userCommentService.removeCommentFromUser(userId, commentId));
    }

    @Test
    public void testRemoveCommentFromUserWithCommentNotFound() {
        // Arrange
        Long userId = 1L;
        Long commentId = 1L;

        UserEntity user = new UserEntity();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userCommentService.removeCommentFromUser(userId, commentId));
    }

    @Test
    public void testRemoveCommentFromUserWithMismatchedUser() {
        // Arrange
        Long userId = 1L;
        Long commentId = 1L;

        UserEntity user = new UserEntity();
        user.setId(userId);

        CommentEntity comment = new CommentEntity();
        comment.setId(commentId);
        comment.setUser(new UserEntity()); // Different user

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act & Assert
        assertThrows(IllegalOperationException.class, () -> userCommentService.removeCommentFromUser(userId, commentId));
    }

    @Test
    public void testGetCommentsByUser() throws EntityNotFoundException {
        // Arrange
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);

        List<CommentEntity> comments = new ArrayList<>();
        CommentEntity comment1 = new CommentEntity();
        comment1.setId(1L);
        comments.add(comment1);
        CommentEntity comment2 = new CommentEntity();
        comment2.setId(2L);
        comments.add(comment2);

        user.setComments(comments);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        List<CommentEntity> retrievedComments = userCommentService.getCommentsByUser(userId);

        // Assert
        assertNotNull(retrievedComments);
        assertEquals(2, retrievedComments.size());
        assertEquals(1L, retrievedComments.get(0).getId());
        assertEquals(2L, retrievedComments.get(1).getId());
    }

    @Test
    public void testGetCommentsByUserWithUserNotFound() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userCommentService.getCommentsByUser(userId));
    }
}

