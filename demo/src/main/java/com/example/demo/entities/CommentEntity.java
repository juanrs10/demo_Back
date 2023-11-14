package com.example.demo.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
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
