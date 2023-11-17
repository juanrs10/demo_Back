package com.example.demo.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.demo.services.CommentService;
import com.example.demo.entities.CommentEntity;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.exceptions.IllegalOperationException;
import com.example.demo.dto.CommentDTO;
import com.example.demo.dto.CommentDetailDTO;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = {"http://localhost:4200","http://localhost:8080"})

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<CommentDetailDTO> findAll() {
        List<CommentEntity> commentEntities = commentService.getAllComments();
        return modelMapper.map(commentEntities, new TypeToken<List<CommentDetailDTO>>() {}.getType());
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public CommentDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        CommentEntity commentEntity = commentService.getComment(id);
        return modelMapper.map(commentEntity, CommentDetailDTO.class);
    }

    @PostMapping("/users/{userId}/queries/{queryId}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CommentDTO create(@PathVariable("userId") Long userId, @PathVariable("queryId") Long queryId, @RequestBody CommentDTO commentDTO) throws IllegalOperationException, EntityNotFoundException {
        CommentEntity commentEntity = commentService.createComment(userId,queryId, modelMapper.map(commentDTO, CommentEntity.class));
        return modelMapper.map(commentEntity, CommentDTO.class);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public CommentDTO update(@PathVariable("id") Long id, @RequestBody CommentDTO commentDTO)
            throws EntityNotFoundException, IllegalOperationException {
        CommentEntity commentEntity = commentService.updateComment(id, modelMapper.map(commentDTO, CommentEntity.class));
        return modelMapper.map(commentEntity, CommentDTO.class);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws EntityNotFoundException, IllegalOperationException {
        commentService.deleteComment(id);
    }
}
