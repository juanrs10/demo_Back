package com.example.demo.controllers;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.QueryDTO;
import com.example.demo.entities.CommentEntity;
import com.example.demo.entities.QueryEntity;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.exceptions.IllegalOperationException;
import com.example.demo.services.QueryCommentService;
import java.util.List;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = {"http://localhost:4200","http://localhost:8080"})

@RestController
@RequestMapping("/queries")
public class QueryCommentController {

    @Autowired
    private QueryCommentService queryCommentService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping(value = "/{queryId}/comments/{commentId}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CommentDTO addCommentToQuery(@PathVariable("queryId") Long queryId, @PathVariable("commentId") Long commentId) 
            throws EntityNotFoundException, IllegalOperationException {
        CommentEntity commentEntity = queryCommentService.addCommentToQuery(queryId, commentId);
        return modelMapper.map(commentEntity, CommentDTO.class);
    }

    @GetMapping(value = "/{queryId}/comments")
    @ResponseStatus(code = HttpStatus.OK)
    public List<CommentDTO> getCommentsByQuery(@PathVariable("queryId") Long queryId) throws EntityNotFoundException {
        List<CommentEntity> comments = queryCommentService.getCommentsByQuery(queryId);
        return modelMapper.map(comments, new TypeToken<List<CommentDTO>>(){}.getType());
    }

    @DeleteMapping(value = "/{queryId}/comments/{commentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void removeCommentFromQuery(@PathVariable("queryId") Long queryId, @PathVariable("commentId") Long commentId) 
            throws EntityNotFoundException, IllegalOperationException {
        queryCommentService.removeCommentFromQuery(queryId, commentId);
    }
}
