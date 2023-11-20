package com.example.demo.dto;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BigQueryResultDTO {
    private List<Map<String, Object>> rows;

    // Constructor, getters y setters
}

