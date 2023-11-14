package com.example.demo.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QueryDTO {

    private Long id;
    private String content;
    private Boolean state;
    
}
