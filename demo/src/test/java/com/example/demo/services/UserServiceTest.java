package com.example.demo.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.entities.UserEntity;
import com.example.demo.exceptions.EntityNotFoundException;
import com.example.demo.exceptions.IllegalOperationException;
import com.example.demo.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser() throws IllegalOperationException {
        // Arrange
        UserEntity newUser = new UserEntity();
        newUser.setEmail("test@example.com");
        newUser.setPassword("password");

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(newUser.getPassword())).thenReturn("hashed_password");
        when(userRepository.save(newUser)).thenReturn(newUser);

        // Act
        UserEntity createdUser = userService.createUser(newUser);

        // Assert
        assertNotNull(createdUser);
        assertEquals(newUser.getEmail(), createdUser.getEmail());
        assertEquals("hashed_password", createdUser.getPassword());
    }

    @Test
    public void testCreateUserWithExistingEmail() {
        // Arrange
        UserEntity existingUser = new UserEntity();
        existingUser.setEmail("existing@example.com");

        UserEntity newUser = new UserEntity();
        newUser.setEmail("existing@example.com"); // Existing email
        newUser.setPassword("password");

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThrows(IllegalOperationException.class, () -> userService.createUser(newUser));
    }

    @Test
    public void testGetUser() throws EntityNotFoundException {
        // Arrange
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserEntity retrievedUser = userService.getUser(userId);

        // Assert
        assertNotNull(retrievedUser);
        assertEquals(userId, retrievedUser.getId());
    }

    @Test
    public void testGetUserNotFound() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(userId));
    }

    @Test
    public void testGetAllUsers() {
        // Arrange
        List<UserEntity> userList = new ArrayList<>();
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        userList.add(user1);
        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        userList.add(user2);

        when(userRepository.findAll()).thenReturn(userList);

        // Act
        List<UserEntity> retrievedUsers = userService.getAllUsers();

        // Assert
        assertNotNull(retrievedUsers);
        assertEquals(2, retrievedUsers.size());
        assertEquals(1L, retrievedUsers.get(0).getId());
        assertEquals(2L, retrievedUsers.get(1).getId());
    }

    @Test
    public void testUpdateUser() throws EntityNotFoundException, IllegalOperationException {
        // Arrange
        Long userId = 1L;
        UserEntity existingUser = new UserEntity();
        existingUser.setId(userId);
        existingUser.setEmail("old@example.com");
        existingUser.setPassword("old_password");

        UserEntity updatedUser = new UserEntity();
        updatedUser.setId(userId);
        updatedUser.setEmail("new@example.com");
        updatedUser.setPassword("new_password");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(updatedUser.getPassword())).thenReturn("hashed_password");
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        // Act
        UserEntity result = userService.updateUser(userId, updatedUser);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("hashed_password", result.getPassword());
    }

    @Test
    public void testDeleteUser() throws EntityNotFoundException {
        // Arrange
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testDeleteUserNotFound() {
        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(userId));
    }

    @Test
    public void testAuthenticateUser() throws EntityNotFoundException, IllegalOperationException {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(password);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(password)).thenReturn("hashed_password");

        // Act
        UserEntity authenticatedUser = userService.authenticateUser(email, password);

        // Assert
        assertNotNull(authenticatedUser);
        assertEquals(email, authenticatedUser.getEmail());
        assertEquals("hashed_password", authenticatedUser.getPassword());
    }

    @Test
    public void testAuthenticateUserNotFound() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.authenticateUser(email, password));
    }

    @Test
    public void testAuthenticateUserIncorrectPassword() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword("incorrect_password");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(IllegalOperationException.class, () -> userService.authenticateUser(email, password));
    }
}
