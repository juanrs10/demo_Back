package com.example.demo.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class CommentEntity extends BaseEntity {

    private String content;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private UserEntity user;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private QueryEntity query;


    
}
