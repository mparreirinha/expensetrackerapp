package com.parreirinha.expensetrackerapp.user.service;

import com.parreirinha.expensetrackerapp.auth.service.TokenService;
import com.parreirinha.expensetrackerapp.category.repository.CategoryRepository;
import com.parreirinha.expensetrackerapp.exceptions.ForbiddenException;
import com.parreirinha.expensetrackerapp.exceptions.ResourceNotFoundException;
import com.parreirinha.expensetrackerapp.transactions.repository.TransactionRepository;
import com.parreirinha.expensetrackerapp.user.domain.Role;
import com.parreirinha.expensetrackerapp.user.domain.User;
import com.parreirinha.expensetrackerapp.user.dto.UserAdminResponseDto;
import com.parreirinha.expensetrackerapp.user.mapper.UserMapper;
import com.parreirinha.expensetrackerapp.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserAdminService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final UserQueryService userQueryService;

    public UserAdminService(UserRepository userRepository,
                            UserService userService,
                            UserQueryService userQueryService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.userQueryService = userQueryService;
    }

    public List<UserAdminResponseDto> getUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.INSTANCE.toUserAdminResponseDtoList(users);
    }

    public UserAdminResponseDto getUser(UUID id) {
        return UserMapper.INSTANCE.toUserAdminResponseDto(userQueryService.getUserById(id));
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userQueryService.getUserById(id);
        if (user.getRole() == Role.ADMIN)
            throw new ForbiddenException("Delete Admins is not allowed");
        userService.deleteUser(user);
    }

}
