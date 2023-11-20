package com.example.demo.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.BigQueryResultDTO;
import com.example.demo.entities.QueryEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.exceptions.ErrorMessage;
import com.example.demo.exceptions.IllegalOperationException;
import com.example.demo.repositories.QueryRepository;
import com.example.demo.repositories.UserRepository;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import java.util.stream.Collectors;
import com.google.cloud.bigquery.Field;
import com.google.common.collect.Lists;

import autovalue.shaded.com.google.common.base.Optional;



@Slf4j
@Service
public class QueryService {

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public QueryEntity createQuery(Long userId, QueryEntity queryEntity) throws IllegalOperationException, EntityNotFoundException {
        if (queryEntity.getContent() == null || queryEntity.getContent().trim().isEmpty()) {
            throw new IllegalOperationException("El contenido del query no puede estar vacío o ser nulo.");
        }

        if (queryEntity.getState() == null) {
            throw new IllegalOperationException("El estado del query no puede ser nulo.");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));
        queryEntity.setUser(user);
        user.getQueries().add(queryEntity); //relación bidireccional
        return queryRepository.save(queryEntity);
    }

    @Transactional
    public QueryEntity getQuery(Long queryId) throws EntityNotFoundException {
        return queryRepository.findById(queryId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.QUERY_NOT_FOUND));
    }

    @Transactional
    public List<QueryEntity> getAllQueries() {
        // GET ONLY PUBLIC QUERIES
        return queryRepository.findByStateTrue();
    }

    @Transactional
    public QueryEntity updateQuery(Long queryId, QueryEntity updatedQuery) throws EntityNotFoundException, IllegalOperationException {
        QueryEntity existingQuery = queryRepository.findById(queryId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.QUERY_NOT_FOUND));

        if (updatedQuery.getContent() != null && !updatedQuery.getContent().trim().isEmpty()) {
            existingQuery.setContent(updatedQuery.getContent());
        } else {
            throw new IllegalOperationException("El contenido del query no puede estar vacío.");
        }

        if (updatedQuery.getState() != null) {
            existingQuery.setState(updatedQuery.getState());
        } else {
            throw new IllegalOperationException("El estado del query no puede ser nulo.");
        }

        return queryRepository.save(existingQuery);
    }

    @Transactional
    public void deleteQuery(Long queryId) throws EntityNotFoundException {
        QueryEntity query = queryRepository.findById(queryId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.QUERY_NOT_FOUND));
        queryRepository.delete(query);
    }

    public TableResult executeQuery(String query) throws InterruptedException, IOException, IllegalOperationException
     {  
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalOperationException("El contenido del query no puede estar vacío o ser solo espacios en blanco.");
        }
    
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream("demo/src/main/java/com/example/demo/keys/ethqueries-405417-b0b95b72e49c.json"))
            .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
        BigQuery bigquery = BigQueryOptions.newBuilder().setCredentials(credentials).build().getService();
    
    
        try {
            
            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();

            return bigquery.query(queryConfig);
        } catch (Exception e) {
            // Aquí capturas la excepción y extraes el mensaje de error
            throw new IllegalOperationException(e.getMessage());
        }
    }

    public BigQueryResultDTO convertToDTO(TableResult tableResult) throws IllegalOperationException {
        BigQueryResultDTO dto = new BigQueryResultDTO();
        List<Map<String, Object>> rows = new ArrayList<>();

         // Comprobar si hay filas en el resultado
        if (!tableResult.iterateAll().iterator().hasNext()) {
            // throw new IllegalOperationException("empty response");
            return dto;
        }

        // Obtenemos los nombres de las columnas del esquema del resultado
        List<String> columnNames = tableResult.getSchema().getFields().stream()
            .map(Field::getName)
            .collect(Collectors.toList());

        // Iteramos sobre cada fila del resultado
        tableResult.iterateAll().forEach(row -> {
            Map<String, Object> rowData = new HashMap<>();
            
            // Iteramos sobre cada campo en la fila
            for (int i = 0; i < row.size(); i++) {
                // Obtener el valor como String
                String valueAsString = row.get(i).getValue().toString();
                rowData.put(columnNames.get(i), valueAsString);
            }

            rows.add(rowData);
        });

        dto.setRows(rows);
        return dto;
    }



}

