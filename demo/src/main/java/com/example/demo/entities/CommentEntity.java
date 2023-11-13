package com.example.demo.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class CommentEntity {

    private String content;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private UserEntity User;
    
}
