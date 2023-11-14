package com.example.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.QueryEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.exceptions.ErrorMessage;
import com.example.demo.exceptions.IllegalOperationException;
import com.example.demo.repositories.QueryRepository;
import com.example.demo.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

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

        return queryRepository.save(queryEntity);
    }

    @Transactional
    public QueryEntity getQuery(Long queryId) throws EntityNotFoundException {
        return queryRepository.findById(queryId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.QUERY_NOT_FOUND));
    }

    @Transactional
    public List<QueryEntity> getAllQueries() {
        return queryRepository.findAll();
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

}

