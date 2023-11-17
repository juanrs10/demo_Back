package com.example.demo.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.demo.services.UserService;
import com.example.demo.entities.UserEntity;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.exceptions.IllegalOperationException;
import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserDetailDTO;

@CrossOrigin(origins = {"http://localhost:4200","http://localhost:8080"})
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<UserDetailDTO> findAll() {
        List<UserEntity> userEntities = userService.getAllUsers();
        return modelMapper.map(userEntities, new TypeToken<List<UserDetailDTO>>() {}.getType());
    }
    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public UserDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        UserEntity userEntity = userService.getUser(id);
        return modelMapper.map(userEntity, UserDetailDTO.class);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserDTO create(@RequestBody UserDTO userDTO) throws IllegalOperationException {
        UserEntity userEntity = userService.createUser(modelMapper.map(userDTO, UserEntity.class));
        return modelMapper.map(userEntity, UserDTO.class);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public UserDTO update(@PathVariable("id") Long id, @RequestBody UserDTO userDTO)
            throws EntityNotFoundException, IllegalOperationException {
        UserEntity userEntity = userService.updateUser(id, modelMapper.map(userDTO, UserEntity.class));
        return modelMapper.map(userEntity, UserDTO.class);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws EntityNotFoundException, IllegalOperationException {
        userService.deleteUser(id);
    }
}
