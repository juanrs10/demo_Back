package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.QueryEntity;

import autovalue.shaded.com.google.common.base.Optional;
import java.util.List;


@Repository
public interface QueryRepository extends JpaRepository<QueryEntity,Long> {

    List<QueryEntity> findByStateTrue();

    
} 
   
