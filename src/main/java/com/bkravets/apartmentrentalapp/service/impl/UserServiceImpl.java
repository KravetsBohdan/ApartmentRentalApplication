package com.bkravets.apartmentrentalapp.service.impl;

import com.bkravets.apartmentrentalapp.dto.ApartmentDto;
import com.bkravets.apartmentrentalapp.dto.BookingDto;
import com.bkravets.apartmentrentalapp.dto.UserDto;
import com.bkravets.apartmentrentalapp.dto.LoginDto;
import com.bkravets.apartmentrentalapp.entity.User;
import com.bkravets.apartmentrentalapp.exception.ResourceNotFoundException;
import com.bkravets.apartmentrentalapp.exception.UserAlreadyExistsException;
import com.bkravets.apartmentrentalapp.exception.UserAuthenticationException;
import com.bkravets.apartmentrentalapp.mapper.ApartmentMapper;
import com.bkravets.apartmentrentalapp.mapper.BookingMapper;
import com.bkravets.apartmentrentalapp.mapper.UserMapper;
import com.bkravets.apartmentrentalapp.repository.UserRepository;
import com.bkravets.apartmentrentalapp.security.TokenProvider;
import com.bkravets.apartmentrentalapp.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @Override
    public UserDto getCurrentUser() {
        User currentUser = getLoggedUser();
        return UserMapper.INSTANCE.toDTO(currentUser);
    }

    public User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserAuthenticationException("User not authenticated");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + authentication.getName() + " not found"));
    }


    @Override
    public UserDto update(UserDto userDTO) {
        User currentUser = getLoggedUser();

        currentUser.setFirstName(userDTO.getFirstName());
        currentUser.setLastName(userDTO.getLastName());
        currentUser.setPhone(userDTO.getPhone());

        User savedUser = userRepository.save(currentUser);
        return UserMapper.INSTANCE.toDTO(savedUser);
    }

    @Override
    @Transactional
    public List<ApartmentDto> getUserApartments() {
        User currentUser = getLoggedUser();
        return currentUser
                .getApartments().stream()
                .map(ApartmentMapper.INSTANCE::toDTO).
                toList();

    }

    @Override
    @Transactional
    public List<BookingDto> getUserBookings() {
        User currentUser = getLoggedUser();
        return currentUser
                .getBooking().stream()
                .map(BookingMapper.INSTANCE::toDTO).
                toList();
    }

    @Override
    public UserDto create(UserDto userDTO) {
        String email = userDTO.getEmail();

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with email" + email + " already exists");
        }

        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User user = UserMapper.INSTANCE.toEntity(userDTO);
        User savedUser = userRepository.save(user);

        return UserMapper.INSTANCE.toDTO(savedUser);
    }


    @Override
    public String login(LoginDto loginDTO) {
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return tokenProvider.generateToken(authentication.getName());
    }

}

