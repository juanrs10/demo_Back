package com.example.demo.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.example.demo.entities.CommentEntity;
import com.example.demo.entities.UserEntity;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.exceptions.ErrorMessage;
import com.example.demo.exceptions.IllegalOperationException;
import com.example.demo.repositories.CommentRepository;
import com.example.demo.repositories.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.repositories.QueryRepository;
import java.util.List;
import java.util.Optional;

import com.example.demo.entities.QueryEntity;


@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserEntity createUser(UserEntity userEntity) throws IllegalOperationException {
        if (userEntity.getEmail() == null || userEntity.getEmail().trim().isEmpty()) {
            throw new IllegalOperationException("El email no puede estar vacío o ser nulo.");
        }
    
        if (userEntity.getPassword() == null || userEntity.getPassword().trim().isEmpty()) {
            throw new IllegalOperationException("La contraseña no puede estar vacía o ser nula.");
        }
    
        // Aquí podrías añadir más validaciones si lo necesitas, como formato del email, etc.
    
        return userRepository.save(userEntity);
    }

    @Transactional
    public UserEntity getUser(Long userId) throws EntityNotFoundException{
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));
    }

    @Transactional
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public UserEntity updateUser(Long userId, UserEntity updatedUser) throws EntityNotFoundException, IllegalOperationException {
        UserEntity existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));
    
        // Validar el email
        if (updatedUser.getEmail() == null || updatedUser.getEmail().trim().isEmpty()) {
            throw new IllegalOperationException("El email no puede estar vacío o ser nulo.");
        }
    
        // Validar el password
        if (updatedUser.getPassword() == null || updatedUser.getPassword().trim().isEmpty()) {
            throw new IllegalOperationException("La contraseña no puede estar vacía o ser nula.");
        }
    
        // Actualizar las propiedades que necesitas
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setComments(updatedUser.getComments());
        existingUser.setQueries(updatedUser.getQueries());
    
        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long userId) throws EntityNotFoundException {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND));
        userRepository.delete(user);
    }

}
