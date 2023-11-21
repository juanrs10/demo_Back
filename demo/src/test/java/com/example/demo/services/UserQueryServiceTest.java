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

import com.example.demo.entities.QueryEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.exceptions.IllegalOperationException;
import com.example.demo.repositories.QueryRepository;
import com.example.demo.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserQueryServiceTest {

    @Mock
    private QueryRepository queryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserQueryService userQueryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddQueryToUser() throws EntityNotFoundException, IllegalOperationException {
        // Arrange
        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setId(userId);

        QueryEntity query = new QueryEntity();
        query.setContent("This is a query");
        query.setState(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(queryRepository.save(query)).thenReturn(query);

        // Act
        QueryEntity addedQuery = userQueryService.addQueryToUser(userId, query);

        // Assert
        assertNotNull(addedQuery);
        assertEquals(userId, addedQuery.getUser().getId());
        assertEquals("This is a query", addedQuery.getContent());
        assertEquals(true, addedQuery.getState());
    }

    @Test
    public void testAddQueryToUserWithEmptyContent() {
        // Arrange
        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setId(userId);

        QueryEntity query = new QueryEntity();
        query.setContent(""); // Empty content

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(IllegalOperationException.class, () -> userQueryService.addQueryToUser(userId, query));
    }

    @Test
    public void testAddQueryToUserWithNullState() {
        // Arrange
        Long userId = 1L;

        UserEntity user = new UserEntity();
        user.setId(userId);

        QueryEntity query = new QueryEntity();
        query.setContent("This is a query");
        query.setState(null); // Null state

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(IllegalOperationException.class, () -> userQueryService.addQueryToUser(userId, query));
    }

    @Test
    public void testAddQueryToUserWithUserNotFound() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        QueryEntity query = new QueryEntity();
        query.setContent("This is a query");
        query.setState(true);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userQueryService.addQueryToUser(userId, query));
    }

    @Test
    public void testRemoveQueryFromUser() throws EntityNotFoundException, IllegalOperationException {
        // Arrange
        Long userId = 1L;
        Long queryId = 1L;

        UserEntity user = new UserEntity();
        user.setId(userId);

        QueryEntity query = new QueryEntity();
        query.setId(queryId);
        query.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(queryRepository.findById(queryId)).thenReturn(Optional.of(query));

        // Act
        userQueryService.removeQueryFromUser(userId, queryId);

        // Assert
        assertNull(query.getUser());
        verify(queryRepository, times(1)).delete(query);
        assertTrue(user.getQueries().isEmpty());
    }

    @Test
    public void testRemoveQueryFromUserWithUserNotFound() {
        // Arrange
        Long userId = 1L;
        Long queryId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userQueryService.removeQueryFromUser(userId, queryId));
    }

    @Test
    public void testRemoveQueryFromUserWithQueryNotFound() {
        // Arrange
        Long userId = 1L;
        Long queryId = 1L;

        UserEntity user = new UserEntity();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(queryRepository.findById(queryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userQueryService.removeQueryFromUser(userId, queryId));
    }

    @Test
    public void testRemoveQueryFromUserWithMismatchedUser() {
        // Arrange
        Long userId = 1L;
        Long queryId = 1L;

        UserEntity user = new UserEntity();
        user.setId(userId);

        QueryEntity query = new QueryEntity();
        query.setId(queryId);
        query.setUser(new UserEntity()); // Different user

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(queryRepository.findById(queryId)).thenReturn(Optional.of(query));

        // Act & Assert
        assertThrows(IllegalOperationException.class, () -> userQueryService.removeQueryFromUser(userId, queryId));
    }

    @Test
    public void testGetQueriesByUser() throws EntityNotFoundException {
        // Arrange
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);

        List<QueryEntity> queries = new ArrayList<>();
        QueryEntity query1 = new QueryEntity();
        query1.setId(1L);
        queries.add(query1);
        QueryEntity query2 = new QueryEntity();
        query2.setId(2L);
        queries.add(query2);

        user.setQueries(queries);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        List<QueryEntity> retrievedQueries = userQueryService.getQueriesByUser(userId);

        // Assert
        assertNotNull(retrievedQueries);
        assertEquals(2, retrievedQueries.size());
        assertEquals(1L, retrievedQueries.get(0).getId());
        assertEquals(2L, retrievedQueries.get(1).getId());
    }

    @Test
    public void testGetQueriesByUserWithUserNotFound() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userQueryService.getQueriesByUser(userId));
    }
}

