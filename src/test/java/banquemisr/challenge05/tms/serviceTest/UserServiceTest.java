package banquemisr.challenge05.tms.serviceTest;

import banquemisr.challenge05.tms.exception.InvalidCredentialsException;
import banquemisr.challenge05.tms.exception.PasswordException;
import banquemisr.challenge05.tms.exception.UserAlreadyExistException;
import banquemisr.challenge05.tms.exception.UserNotFoundException;
import banquemisr.challenge05.tms.model.LoginRequest;
import banquemisr.challenge05.tms.model.User;
import banquemisr.challenge05.tms.repository.UserRepository;
import banquemisr.challenge05.tms.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        User user = new User();
        user.setUserName("User");
        user.setUserPassword("Userpassword");
        when(userRepository.findByUserName("User")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Userpassword")).thenReturn("encryptedPassword");
        userService.registerUser(user);
        assertEquals("encryptedPassword", user.getUserPassword());
        assertEquals("user", user.getRole());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testRegisterUser_UsernameAlreadyExists() {
        User user = new User();
        user.setUserName("existingUser");
        when(userRepository.findByUserName("existingUser")).thenReturn(Optional.of(new User()));
        UserAlreadyExistException exception = assertThrows(UserAlreadyExistException.class, () -> userService.registerUser(user));
        assertEquals("Username is already taken", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterUser_PasswordIsNullOrEmpty() {
        User user = new User();
        user.setUserName("testUser");
        user.setUserPassword("");
        PasswordException exception = assertThrows(PasswordException.class, () -> userService.registerUser(user));
        assertEquals("Password cannot be null or empty", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testAuthenticateUser_Success() {
        User user = new User();
        user.setUserName("testUser");
        user.setUserPassword("encryptedPassword");
        LoginRequest loginRequest = new LoginRequest("testUser", "password");
        when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encryptedPassword")).thenReturn(true);
        User authenticatedUser = userService.authenticateUser(loginRequest);
        assertEquals(user, authenticatedUser);
        verify(userRepository, times(1)).findByUserName("testUser");
    }

    @Test
    void testAuthenticateUser_UserNotFound() {
        LoginRequest loginRequest = new LoginRequest("nonExistentUser", "password");
        when(userRepository.findByUserName("nonExistentUser")).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.authenticateUser(loginRequest));
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUserName("nonExistentUser");
    }

    @Test
    void testAuthenticateUser_InvalidCredentials() {
        User user = new User();
        user.setUserName("testUser");
        user.setUserPassword("encryptedPassword");
        LoginRequest loginRequest = new LoginRequest("testUser", "wrongPassword");
        when(userRepository.findByUserName("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encryptedPassword")).thenReturn(false);
        InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> userService.authenticateUser(loginRequest));
        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository, times(1)).findByUserName("testUser");
    }

}