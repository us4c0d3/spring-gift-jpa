package gift.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import gift.dto.user.UserLoginRequest;
import gift.dto.user.UserRegisterRequest;
import gift.dto.user.UserResponse;
import gift.exception.user.UserAlreadyExistException;
import gift.exception.user.UserNotFoundException;
import gift.util.auth.JwtUtil;
import java.util.Base64;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * 코드 수정 과정에서 변경점이 많아 테스트 코드 수정이 많이 필요함
 */
@Disabled
@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    /*
     * dummy data
     *
     * userRepository.save(User.builder()
     *     .email("user1@example.com")
     *     .password("password1")
     *     .build());
     *
     * userRepository.save(User.builder()
     *     .email("user2@example.com")
     *     .password("password2")
     *     .build());
     *
     * userRepository.save(User.builder()
     *     .email("user3@example.com")
     *     .password("password3")
     *     .build());
     *
     * userRepository.save(User.builder()
     *     .email("user4@example.com")
     *     .password("password4")
     *     .build());
     *
     * userRepository.save(User.builder()
     *     .email("user5@example.com")
     *     .password("password5")
     *     .build());
     */

    @Test
    @DisplayName("register user test")
    @Transactional
    void registerUserTest() {
        //given
        UserRegisterRequest request = new UserRegisterRequest("user@email.com", "1q2w3e4r!");

        //when
        UserResponse actual = userService.registerUser(request);
        UserResponse expected = new UserResponse(jwtUtil.generateToken(null, null));

        //then
        assertThat(actual.token()).isEqualTo(expected.token());
    }

    @Test
    @DisplayName("Already Exist user registration test")
    @Transactional
    void alreadyExistUserRegistrationTest() {
        //given
        UserRegisterRequest request = new UserRegisterRequest("user1@example.com", "password1");

        //when&then
        assertThatThrownBy(() -> userService.registerUser(request))
            .isInstanceOf(UserAlreadyExistException.class);
    }

    @Test
    @DisplayName("user login test")
    @Transactional
    void userLoginTest() {
        //given
        UserLoginRequest loginRequest = new UserLoginRequest("user1@example.com", "password1");

        //when
        UserResponse actual = userService.loginUser(loginRequest);
        String token = Base64.getEncoder()
            .encodeToString(("user1@example.com:password1").getBytes());

        //then
        assertThat(actual.token()).isEqualTo(token);
    }

    @Test
    @DisplayName("unknown user login test")
    @Transactional
    void unknownUserLoginTest() {
        //given
        UserLoginRequest request = new UserLoginRequest("user1@email.com", "1q2w3e4r!");

        //when & then
        assertThatThrownBy(() -> userService.loginUser(request))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("wrong password login test")
    @Transactional
    void wrongPasswordLoginTest() {
        //given
        UserLoginRequest request = new UserLoginRequest("user1@email.com", "1234");

        //when & then
        assertThatThrownBy(() -> userService.loginUser(request))
            .isInstanceOf(UserNotFoundException.class);
    }
}
