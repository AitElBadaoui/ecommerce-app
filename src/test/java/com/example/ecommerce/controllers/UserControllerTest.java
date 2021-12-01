package com.example.ecommerce.controllers;

import com.example.ecommerce.TestUtils;
import com.example.ecommerce.exception.PasswordValidationException;
import com.example.ecommerce.exception.UsernameNotFoundException;
import com.example.ecommerce.model.persistence.User;
import com.example.ecommerce.model.persistence.repositories.CartRepository;
import com.example.ecommerce.model.persistence.repositories.UserRepository;
import com.example.ecommerce.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);


    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);


    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository",userRepository);
        TestUtils.injectObjects(userController, "cartRepository",cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder",encoder);
    }

    @Test
    @DisplayName("User password is too short")
    public void create_user_short_password() {
        String username = "test";
        String password = "o@0Ps";
        String confirmPassword = "confirm";
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(password);
        Assertions.assertThrows(PasswordValidationException.class, () -> { userController.createUser(request); });
    }

    @Test
    @DisplayName("Confirm password is valid")
    public void create_user_confirm_password_passes_validity() {
        // Minimum 10 Character Password with lowercase, uppercase letters, digits, a minimum of 4 lowercase letters and minimum of 2 uppercase letters
        String username = "passtest";
        String password = "ValidEno*ugh123";
        String confirmPassword = "ValidEno*ugh123";
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        ResponseEntity<User> response = userController.createUser(request);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Confirm password is not valid")
    public void create_user_confirm_password_not_valid() {
        // Minimum 10 Character Password with lowercase, uppercase letters, digits, a minimum of 4 lowercase letters and minimum of 2 uppercase letters
        String username = "badpasstest";
        String password = "validenough3";
        String confirmPassword = "validenough3";
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        Assertions.assertThrows(PasswordValidationException.class, () -> { userController.createUser(request); });
    }

    @Test
    @DisplayName("Confirming password does not match the password")
    public void create_user_confirm_password_not_matching_password() {
        String username = "test";
        String password = "Long7$Enough";
        String confirmPassword = "oops!";
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        Assertions.assertThrows(PasswordValidationException.class, () -> { userController.createUser(request); });
    }

    @Test
    @DisplayName("Create user")
    public void create_user_happy_path() throws Exception{
        when(encoder.encode("testPassword1!")).thenReturn("thisIsHashed");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPassword1!");
        request.setConfirmPassword("testPassword1!");

        final ResponseEntity<User> response = userController.createUser(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();

        Assert.assertNotNull(user);
        Assert.assertEquals(0,user.getId());
        Assert.assertEquals("test",user.getUsername());
        Assert.assertEquals("thisIsHashed",user.getPassword());

    }

    @Test
    @DisplayName("Get all users")
    public void get_all_users() throws Exception {
        String username = "test";
        String password = "passwordIsLong";

        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(password);

        ResponseEntity<List<User>> response = userController.listUsers();
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Get user by username")
    public void get_user_by_username_happy_path() throws Exception {
        String username = "test";
        String password = "passwordIsLong";
        User user1 = new User();

        when(userRepository.findByUsername(username)).thenReturn(user1);

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(password);

        ResponseEntity<User> response = userController.findByUserName(username);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Username does not exist")
    public void username_does_not_exist() throws Exception {
        String username = "test";
        String password = "passwordIsLong";
        User user1 = new User();

        when(userRepository.findByUsername(username)).thenReturn(null);

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setConfirmPassword(password);

        Assertions.assertThrows(UsernameNotFoundException.class, () -> { userController.findByUserName(username); });
    }


}
