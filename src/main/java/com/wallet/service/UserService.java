package com.wallet.service;

import com.wallet.dto.user.CreateUserRequest;
import com.wallet.dto.user.UserResponse;
import com.wallet.model.User;
import com.wallet.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

public class UserService {
    private static final Pattern EMAIL_RE = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final UserRepository repo = new UserRepository();

    private static UserResponse toDto(User u) {
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail());
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    public List<User> listUsers() throws SQLException {
        return repo.findAll();
    }

    public List<UserResponse> listUsersDto() throws SQLException {
        return repo.findAll()
            .stream()
            .map(UserService::toDto)
            .collect(Collectors.toList());
    }

    public UserResponse createUser(CreateUserRequest req) throws SQLException {
        if (req == null) throw new IllegalArgumentException("body_required");

        String username = safe(req.getUsername());
        String email = safe(req.getEmail());
        String password = req.getPassword() == null ? "" : req.getPassword();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty())
            throw new IllegalArgumentException("username_email_password_required");

        if (!EMAIL_RE.matcher(email).matches())
            throw new IllegalArgumentException("invalid_email");

        if (password.length() < 8)
            throw new IllegalArgumentException("password_min_8");

        email = email.toLowerCase();

        String hash = BCrypt.hashpw(password, BCrypt.gensalt(12));

        User u = repo.insert(username, email, hash);
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail());
    }
}
