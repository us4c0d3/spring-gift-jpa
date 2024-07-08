package gift.service;

import gift.dto.user.UserLoginRequest;
import gift.dto.user.UserRegisterRequest;
import gift.dto.user.UserResponse;
import gift.entity.User;
import gift.exception.user.UserAlreadyExistException;
import gift.exception.user.UserNotFoundException;
import gift.exception.user.UserUnauthorizedException;
import gift.util.mapper.UserMapper;
import gift.repository.UserRepository;
import java.util.Base64;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse registerUser(UserRegisterRequest request) {
        User user = UserMapper.toUser(request);

        Optional<User> existingUser = userRepository.findByEmail(user.email());

        if (existingUser.isPresent()) {
            throw new UserAlreadyExistException("이미 존재하는 Email입니다.");
        }

        Long id = userRepository.insert(user);
        return new UserResponse(
            id,
            request.email(),
            getToken(request.email(), request.password())
        );
    }

    public UserResponse loginUser(UserLoginRequest userRequest) {
        User user = userRepository.findByEmailAndPassword(userRequest.email(), userRequest.password())
            .orElseThrow(() -> new UserNotFoundException("로그인할 수 없습니다."));

        return new UserResponse(
            user.id(),
            user.email(),
            getToken(user.email(), user.password())
        );
    }

    public Long getUserIdByToken(String token) {
        HashMap<String, String> credentials = decodeToken(token);
        User user = userRepository.findByEmailAndPassword(
            credentials.get("email"),
            credentials.get("password"))
            .orElseThrow(() -> new UserUnauthorizedException("접근할 수 없습니다."));

        return user.id();
    }

    private HashMap<String, String> decodeToken(String token) {
        String decodedString = new String(Base64.getDecoder().decode(token));
        String[] parts = decodedString.split(":",2);
        HashMap<String,String> credentials = new HashMap<>();
        credentials.put("email", parts[0]);
        credentials.put("password", parts[1]);
        return credentials;
    }

    private String getToken(String email, String password) {
        return Base64.getEncoder()
            .encodeToString((email + ":" + password).getBytes());
    }

}
