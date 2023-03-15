package com.bkravets.apartmentrentalapp.mapper;

import com.bkravets.apartmentrentalapp.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.bkravets.apartmentrentalapp.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toDTO(User user);
    User toEntity(UserDto userDTO);

}

