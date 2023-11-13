package com.example.demo.entities;
import lombok.Data;
import javax.persistence.Entity;
import 



@Data
@Entity
public class UserEntity {

    private String email;
    private String password;

    private List<CommentEntity> comments;
    private List<QueryEntity> queriees;



    
}
