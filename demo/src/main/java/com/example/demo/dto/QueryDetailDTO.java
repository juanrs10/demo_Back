package com.example.demo.dto;

import java.util.List;

import com.example.demo.entities.CommentEntity;
import com.example.demo.entities.UserEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryDetailDTO {

    private UserDTO user;

    private List<CommentDTO> comments;
    
}
