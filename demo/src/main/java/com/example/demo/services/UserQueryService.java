package com.example.demo.services;

import com.example.demo.entities.QueryEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.exceptions.ErrorMessage;
import com.example.demo.exceptions.IllegalOperationException;
import com.example.demo.repositories.QueryRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserQueryService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QueryRepository queryRepository;

    @Transactional
    public QueryEntity addQueryToUser(Long userId, QueryEntity queryEntity) throws EntityNotFoundException, IllegalOperationException {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));

        if (queryEntity.getContent() == null || queryEntity.getContent().trim().isEmpty()) {
            throw new IllegalOperationException("El contenido del query no puede estar vacío.");
        }

        if (queryEntity.getState() == null) {
            throw new IllegalOperationException("El estado del query no puede ser nulo.");
        }

        queryEntity.setUser(user);
        user.getQueries().add(queryEntity);

        return queryRepository.save(queryEntity);
    }

    @Transactional
    public void removeQueryFromUser(Long userId, Long queryId) throws EntityNotFoundException, IllegalOperationException {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));
        
        QueryEntity query = queryRepository.findById(queryId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.QUERY_NOT_FOUND));

        if (!query.getUser().equals(user)) {
            throw new IllegalOperationException("El query no pertenece al usuario especificado.");
        }

        queryRepository.delete(query);
        user.getQueries().remove(query);
    }

    @Transactional
    public List<QueryEntity> getQueriesByUser(Long userId) throws EntityNotFoundException {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));

        return user.getQueries();
    }

}
