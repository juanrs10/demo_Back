package com.example.demo.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.demo.services.UserQueryService;
import com.example.demo.entities.QueryEntity;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.exceptions.IllegalOperationException;
import com.example.demo.dto.QueryDTO;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = {"http://localhost:4200","http://localhost:8080"})

@RestController
@RequestMapping("api/users")
public class UserQueryController {

    @Autowired
    private UserQueryService userQueryService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping(value = "/{userId}/queries")
    @ResponseStatus(code = HttpStatus.CREATED)
    public QueryDTO addQueryToUser(@PathVariable("userId") Long userId, @RequestBody QueryDTO queryDTO) 
            throws EntityNotFoundException, IllegalOperationException {
        QueryEntity queryEntity = userQueryService.addQueryToUser(userId, modelMapper.map(queryDTO, QueryEntity.class));
        return modelMapper.map(queryEntity, QueryDTO.class);
    }

    @GetMapping(value = "/{userId}/queries")
    @ResponseStatus(code = HttpStatus.OK)
    public List<QueryDTO> getQueriesByUser(@PathVariable("userId") Long userId) throws EntityNotFoundException {
        List<QueryEntity> queries = userQueryService.getQueriesByUser(userId);
        return modelMapper.map(queries, new TypeToken<List<QueryDTO>>(){}.getType());
    }

    @DeleteMapping(value = "/{userId}/queries/{queryId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void removeQueryFromUser(@PathVariable("userId") Long userId, @PathVariable("queryId") Long queryId) 
            throws EntityNotFoundException, IllegalOperationException {
        userQueryService.removeQueryFromUser(userId, queryId);
    }
}
