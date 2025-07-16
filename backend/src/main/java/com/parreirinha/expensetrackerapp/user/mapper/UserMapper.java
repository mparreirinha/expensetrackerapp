package com.parreirinha.expensetrackerapp.user.mapper;

import com.parreirinha.expensetrackerapp.user.dto.UserAdminResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.parreirinha.expensetrackerapp.user.domain.User;
import com.parreirinha.expensetrackerapp.user.dto.UserResponseDto;

import java.util.List;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "email", source = "email", qualifiedByName = "maskEmail")
    UserResponseDto toUserResponseDto(User user);

    UserAdminResponseDto toUserAdminResponseDto(User user);

    List<UserAdminResponseDto> toUserAdminResponseDtoList(List<User> users);

    @Named("maskEmail")
    static String maskEmail(String email) {
        if (email == null || !email.contains("@"))
            return "****@****";
        if (email.indexOf('@') < 4)
            return email.substring(0, 1) + "****" + email.substring(email.indexOf('@'));
        return email.substring(0, 3) + "****" + email.substring(email.indexOf('@'));
    }
    
}
