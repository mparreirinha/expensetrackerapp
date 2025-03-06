package com.parreirinha.expensetrackerapp.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import com.parreirinha.expensetrackerapp.user.domain.User;
import com.parreirinha.expensetrackerapp.user.dto.UserResponseDto;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "email", source = "email", qualifiedByName = "maskEmail")
    UserResponseDto toUserResponseDto(User user);

    @Named("maskEmail")
    static String maskEmail(String email) {
        if (email.indexOf('@') < 4) {
            return email.substring(0, 1) + "****" + email.substring(email.indexOf('@'));
        }
        return email.substring(0, 3) + "****" + email.substring(email.indexOf('@'));
    }
    
}
