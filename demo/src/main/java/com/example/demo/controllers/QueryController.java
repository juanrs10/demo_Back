package com.example.demo.controllers;

import java.io.IOException;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.services.QueryService;
import com.google.cloud.bigquery.TableResult;
import com.example.demo.entities.QueryEntity;
import com.example.demo.entities.QueryEntity;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.exceptions.IllegalOperationException;
import com.example.demo.dto.BigQueryResultDTO;
import com.example.demo.dto.QueryDTO;
import com.example.demo.dto.QueryDetailDTO;
import com.example.demo.dto.QueryDTO;
import org.slf4j.Logger;

@CrossOrigin(origins = {"http://localhost:4200","http://localhost:8080"})
@RestController
@RequestMapping("/api/queries")
public class QueryController {

    private static final Logger logger = LoggerFactory.getLogger(QueryController.class);

    @Autowired
    private QueryService queryService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<QueryDetailDTO> findAll() {
        List<QueryEntity> queryEntities = queryService.getAllQueries();
        return modelMapper.map(queryEntities, new TypeToken<List<QueryDTO>>() {}.getType());
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public QueryDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        QueryEntity queryEntity = queryService.getQuery(id);
        return modelMapper.map(queryEntity, QueryDetailDTO.class);
    }

    @PostMapping("/users/{userId}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public QueryDTO create(@PathVariable("userId") Long userId, @RequestBody QueryDTO queryDTO) throws IllegalOperationException, EntityNotFoundException {
        QueryEntity queryEntity = queryService.createQuery(userId, modelMapper.map(queryDTO, QueryEntity.class));
        
        return modelMapper.map(queryEntity, QueryDTO.class);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public QueryDTO update(@PathVariable("id") Long id, @RequestBody QueryDTO queryDTO)
            throws EntityNotFoundException, IllegalOperationException {
        QueryEntity queryEntity = queryService.updateQuery(id, modelMapper.map(queryDTO, QueryEntity.class));
        return modelMapper.map(queryEntity, QueryDTO.class);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws EntityNotFoundException, IllegalOperationException {
        queryService.deleteQuery(id);
    }

    @PostMapping("/execute")
    public ResponseEntity<BigQueryResultDTO> executeQuery(@RequestBody QueryDTO queryDTO) throws IllegalOperationException, InterruptedException, IOException  {
        TableResult tableResult = queryService.executeQuery(queryDTO.getContent());
        logger.debug("PERRO HPTA", tableResult);
        BigQueryResultDTO resultDTO = queryService.convertToDTO(tableResult);
        return ResponseEntity.ok(resultDTO);
}
}
