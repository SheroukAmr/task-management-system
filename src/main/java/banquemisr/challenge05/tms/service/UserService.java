package banquemisr.challenge05.tms.service;

import banquemisr.challenge05.tms.exception.InvalidCredentialsException;
import banquemisr.challenge05.tms.exception.PasswordException;
import banquemisr.challenge05.tms.exception.UserAlreadyExistException;
import banquemisr.challenge05.tms.exception.UserNotFoundException;
import banquemisr.challenge05.tms.model.LoginRequest;
import banquemisr.challenge05.tms.repository.UserRepository;
import banquemisr.challenge05.tms.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public void registerUser(User user) {
        if (userRepository.findByUserName(user.getUserName()).isPresent()) {
            throw new UserAlreadyExistException("Username is already taken");
        }
        if (user.getUserPassword() == null || user.getUserPassword().isEmpty()) {
            throw new PasswordException("Password cannot be null or empty");
        }
        String encryptedPassword = passwordEncoder.encode(user.getUserPassword());
        user.setUserPassword(encryptedPassword);
        if (user.getRole() == null) {
            user.setRole("user");
        }
        userRepository.save(user);
    }
    public User authenticateUser(LoginRequest loginRequest) {
        // Fetch user by username
        User user = userRepository.findByUserName(loginRequest.getUserName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getUserPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        return user;
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}