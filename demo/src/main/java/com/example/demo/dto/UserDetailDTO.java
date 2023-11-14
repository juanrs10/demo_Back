package com.example.demo.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailDTO {

    
    private List<CommentDTO> comments;

    private List<QueryDTO> queries;


    
}
