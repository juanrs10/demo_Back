package com.example.demo.dto;

import com.example.demo.entities.QueryEntity;
import com.example.demo.entities.UserEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDetailDTO {

    private UserDTO user;

    private QueryDTO query;
    
}
