package com.user.login.Mapper;          //Package declaration
import com.user.login.DTO.UserDTO;      //Importing UserDTO for mapping
import com.user.login.Entity.User;      //Importing User entity for mapping
import org.mapstruct.Mapper;            //Importing MapStruct Mapper annotation
import org.mapstruct.factory.Mappers;   //Importing Mappers factory for creating instances

@Mapper(componentModel = "spring")      //Marks this interface as a MapStruct mapper for Spring context
public interface UserMapper 
{
    //Provides a static instance of the mapper
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);  
    
    UserDTO toDTO(User user);       //Converts User entity to UserDTO
    User toEntity(UserDTO userDTO); //Converts UserDTO to User entity
}