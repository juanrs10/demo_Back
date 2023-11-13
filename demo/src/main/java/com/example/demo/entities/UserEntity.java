package com.example.demo.entities;
import lombok.Data;
import javax.persistence.Entity;
import com.example.demo.entities.CommentEntity;
import com.example.demo.entities.QueryEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;

import java.util.List;



@Data
@Entity
public class UserEntity {

    private String email;
    private String password;

    @OneToMany(cascade=CascadeType.REMOVE)
    private List<CommentEntity> comments;

    @OneToMany(cascade=CascadeType.REMOVE)
    private List<QueryEntity> queries;



    
}
