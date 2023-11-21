package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.dto.BigQueryResultDTO;
import com.example.demo.entities.QueryEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.exceptions.IllegalOperationException;
import com.example.demo.repositories.QueryRepository;
import com.example.demo.repositories.UserRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.TableResult;

import lombok.extern.slf4j.Slf4j;

import com.google.cloud.bigquery.FieldValue;

@Slf4j
public class QueryServiceTest {

    @Mock
    private QueryRepository queryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoogleCredentials googleCredentials;

    @Mock
    private BigQuery bigQuery;

    @Mock
    private TableResult tableResult;

    @Mock
    private ResourceLoader resourceLoader;

    @InjectMocks
    private QueryService queryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateQuery() throws IllegalOperationException, EntityNotFoundException {
        // Arrange
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        QueryEntity newQuery = new QueryEntity();
        newQuery.setContent("SELECT * FROM table");
        newQuery.setState(true);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(queryRepository.save(newQuery)).thenReturn(newQuery);

        // Act
        QueryEntity createdQuery = queryService.createQuery(userId, newQuery);

        // Assert
        assertNotNull(createdQuery);
        assertEquals(userId, createdQuery.getUser().getId());
        assertEquals("SELECT * FROM table", createdQuery.getContent());
        assertTrue(createdQuery.getState());
    }

    @Test
    public void testCreateQueryWithEmptyContent() {
        // Arrange
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        QueryEntity newQuery = new QueryEntity();
        newQuery.setContent(""); // Empty content
        newQuery.setState(true);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        // Act & Assert
        assertThrows(IllegalOperationException.class, () -> queryService.createQuery(userId, newQuery));
    }

    @Test
    public void testCreateQueryWithNullState() {
        // Arrange
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        QueryEntity newQuery = new QueryEntity();
        newQuery.setContent("SELECT * FROM table");
        newQuery.setState(null); // Null state

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        // Act & Assert
        assertThrows(IllegalOperationException.class, () -> queryService.createQuery(userId, newQuery));
    }

    @Test
    public void testGetQuery() throws EntityNotFoundException {
        // Arrange
        Long queryId = 1L;
        QueryEntity query = new QueryEntity();
        query.setId(queryId);

        when(queryRepository.findById(queryId)).thenReturn(java.util.Optional.of(query));

        // Act
        QueryEntity retrievedQuery = queryService.getQuery(queryId);

        // Assert
        assertNotNull(retrievedQuery);
        assertEquals(queryId, retrievedQuery.getId());
    }

    @Test
    public void testGetQueryNotFound() {
        // Arrange
        Long queryId = 1L;

        when(queryRepository.findById(queryId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> queryService.getQuery(queryId));
    }

    @Test
    public void testGetAllQueries() {
        // Arrange
        List<QueryEntity> queryList = new ArrayList<>();
        QueryEntity query1 = new QueryEntity();
        query1.setId(1L);
        queryList.add(query1);
        QueryEntity query2 = new QueryEntity();
        query2.setId(2L);
        queryList.add(query2);

        when(queryRepository.findByStateTrue()).thenReturn(queryList);

        // Act
        List<QueryEntity> retrievedQueries = queryService.getAllQueries();

        // Assert
        assertNotNull(retrievedQueries);
        assertEquals(2, retrievedQueries.size());
        assertEquals(1L, retrievedQueries.get(0).getId());
        assertEquals(2L, retrievedQueries.get(1).getId());
    }

    @Test
    public void testUpdateQuery() throws EntityNotFoundException, IllegalOperationException {
        // Arrange
        Long queryId = 1L;
        QueryEntity existingQuery = new QueryEntity();
        existingQuery.setId(queryId);
        existingQuery.setContent("SELECT * FROM table");
        existingQuery.setState(true);

        QueryEntity updatedQuery = new QueryEntity();
        updatedQuery.setId(queryId);
        updatedQuery.setContent("SELECT * FROM new_table");
        updatedQuery.setState(false);

        when(queryRepository.findById(queryId)).thenReturn(java.util.Optional.of(existingQuery));
        when(queryRepository.save(existingQuery)).thenReturn(existingQuery);

        // Act
        QueryEntity modifiedQuery = queryService.updateQuery(queryId, updatedQuery);

        // Assert
        assertNotNull(modifiedQuery);
        assertEquals(queryId, modifiedQuery.getId());
        assertEquals("SELECT * FROM new_table", modifiedQuery.getContent());
        assertFalse(modifiedQuery.getState());
    }

    @Test
    public void testUpdateQueryWithEmptyContent() {
        // Arrange
        Long queryId = 1L;
        QueryEntity existingQuery = new QueryEntity();
        existingQuery.setId(queryId);
        existingQuery.setContent("SELECT * FROM table");
        existingQuery.setState(true);

        QueryEntity updatedQuery = new QueryEntity();
        updatedQuery.setId(queryId);
        updatedQuery.setContent(""); // Empty content
        updatedQuery.setState(false);

        when(queryRepository.findById(queryId)).thenReturn(java.util.Optional.of(existingQuery));

        // Act & Assert
        assertThrows(IllegalOperationException.class, () -> queryService.updateQuery(queryId, updatedQuery));
    }

    @Test
    public void testUpdateQueryWithNullState() {
        // Arrange
        Long queryId = 1L;
        QueryEntity existingQuery = new QueryEntity();
        existingQuery.setId(queryId);
        existingQuery.setContent("SELECT * FROM table");
        existingQuery.setState(true);

        QueryEntity updatedQuery = new QueryEntity();
        updatedQuery.setId(queryId);
        updatedQuery.setContent("SELECT * FROM new_table");
        updatedQuery.setState(null); // Null state

        when(queryRepository.findById(queryId)).thenReturn(java.util.Optional.of(existingQuery));

        // Act & Assert
        assertThrows(IllegalOperationException.class, () -> queryService.updateQuery(queryId, updatedQuery));
    }

    @Test
    public void testDeleteQuery() throws EntityNotFoundException {
        // Arrange
        Long queryId = 1L;
        QueryEntity query = new QueryEntity();
        query.setId(queryId);

        when(queryRepository.findById(queryId)).thenReturn(java.util.Optional.of(query));

        // Act
        queryService.deleteQuery(queryId);

        // Assert (verify that delete was called)
        verify(queryRepository, times(1)).delete(query);
    }

    @Test
    public void testDeleteQueryNotFound() {
        // Arrange
        Long queryId = 1L;

        when(queryRepository.findById(queryId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> queryService.deleteQuery(queryId));
    }

    @Test
    public void testConvertToDTOWithEmptyResult() throws IllegalOperationException {
        // Arrange
        when(tableResult.iterateAll().iterator().hasNext()).thenReturn(false);

        // Act
        BigQueryResultDTO dto = queryService.convertToDTO(tableResult);

        // Assert
        assertNotNull(dto);
        assertNotNull(dto.getRows());
        assertTrue(dto.getRows().isEmpty());
    }
}

