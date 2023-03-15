package com.bkravets.apartmentrentalapp.service.impl;

import com.bkravets.apartmentrentalapp.dto.LoginDto;
import com.bkravets.apartmentrentalapp.dto.UserDto;
import com.bkravets.apartmentrentalapp.entity.User;
import com.bkravets.apartmentrentalapp.exception.ResourceNotFoundException;
import com.bkravets.apartmentrentalapp.exception.UserAlreadyExistsException;
import com.bkravets.apartmentrentalapp.exception.UserAuthenticationException;
import com.bkravets.apartmentrentalapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User existingUser;
    private UserDto existingUserDto;
    private UserDto newUserDto;
    private User newUser;
    private LoginDto loginDTO;
    private String existingEmail;
    private String existingPassword;

    @BeforeEach
    void setUp() {
        newUser = new User(2L, "new@email.com", "+38", "ren", "dou", "encriptedPassword", null, null, null);

        existingEmail = "existing@example.com";
        existingPassword = "existingPassword";

        existingUser = new User();
        existingUser.setEmail(existingEmail);
        existingUser.setPassword(existingPassword);

        existingUserDto = new UserDto();
        existingUserDto.setEmail(existingEmail);
        existingUserDto.setPassword(existingPassword);

        newUserDto = new UserDto(2L,"new@email.com", "+38", "ren", "dou", "encriptedPassword");


        loginDTO = new LoginDto("john", "123456");
    }

    @Test
    void shouldGetCurrentUserWhenUserAuthenticated() {
        // Given
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(existingEmail);
        when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.of(existingUser));

        SecurityContextHolder.setContext(securityContext);

        // When
        UserDto userDTO = userService.getCurrentUser();

        // Then

        assertThat(userDTO.getEmail()).isEqualTo(existingEmail);

        verify(securityContext).getAuthentication();
        verify(authentication).isAuthenticated();
        verify(authentication).getName();
        verify(userRepository).findByEmail(existingEmail);
    }

    @Test
    void getCurrentUser_ShouldThrowExceptionWhenUserUnauthenticated() {
        // Given
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        SecurityContextHolder.setContext(securityContext);

        // When
        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(UserAuthenticationException.class)
                .hasMessage("User not authenticated");

        // Then
        verify(securityContext).getAuthentication();
        verify(authentication).isAuthenticated();
    }

    @Test
    void getCurrentUser_shouldThrowExceptionWhenUserUnknown() {
        // Given
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(existingEmail);
        when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.empty());

        SecurityContextHolder.setContext(securityContext);

        // When
        assertThatThrownBy(() -> userService.getCurrentUser())
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User with email " + existingEmail + " not found");

        // Then
        verify(securityContext).getAuthentication();
        verify(authentication).isAuthenticated();
        verify(userRepository).findByEmail(existingEmail);
    }

    @Test
    void shouldCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(userRepository.existsByEmail(newUserDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(newUserDto.getPassword())).thenReturn("encriptedPassword");

        UserDto createdUserDto = userService.create(newUserDto);

        assertThat(createdUserDto).isNotNull();
        assertThat(createdUserDto.getEmail()).isEqualTo(newUserDto.getEmail());
        assertThat(createdUserDto.getFirstName()).isEqualTo(newUserDto.getFirstName());
        assertThat(createdUserDto.getLastName()).isEqualTo(newUserDto.getLastName());
        assertThat(createdUserDto.getPhone()).isEqualTo(newUserDto.getPhone());

        verify(userRepository).existsByEmail(newUserDto.getEmail());
        verify(passwordEncoder).encode(newUserDto.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowExceptionWhenUserAlreadyExists() {
        when(userRepository.existsByEmail(existingEmail)).thenReturn(true);

        assertThatThrownBy(() -> userService.create(existingUserDto))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User with email" + existingEmail + " already exists");

        verify(userRepository).existsByEmail(existingEmail);
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void shouldUpdateCurrentUser() {
        // Given
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(existingEmail);
        when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(newUser);


        SecurityContextHolder.setContext(securityContext);

        // When
        UserDto userDTO = userService.update(newUserDto);


        // Then
        assertThat(userDTO.getPhone()).isEqualTo(newUserDto.getPhone());

        verify(securityContext).getAuthentication();
        verify(authentication).isAuthenticated();
        verify(authentication).getName();
        verify(userRepository).findByEmail(existingEmail);
        verify(userRepository).save(any(User.class));
    }

}
