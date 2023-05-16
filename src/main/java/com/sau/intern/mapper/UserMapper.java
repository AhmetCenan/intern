package com.sau.intern.mapper;

import com.sau.intern.dto.SignupDto;
import com.sau.intern.dto.UserRequest;
import com.sau.intern.dto.UserResponse;
import com.sau.intern.model.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

    @Mapping(target = "role", source = "roleId")
    User toEntity(SignupDto signupDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "role", source = "roleId")
    User partialUpdate(UserRequest userRequest, @MappingTarget User user);

    @Mapping(target = "roleId", source = "role.id")
    UserResponse toDto(User user);

    List<UserResponse> toUserResponseList(List<User> userList);
}