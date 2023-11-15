package com.example.demo.entities;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import java.util.List;

@Data
@Entity
public class QueryEntity extends BaseEntity{

    private String content;
    private Boolean state;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private UserEntity user;

    @OneToMany(cascade = CascadeType.REMOVE)
    private List<CommentEntity> comments;
    
}
