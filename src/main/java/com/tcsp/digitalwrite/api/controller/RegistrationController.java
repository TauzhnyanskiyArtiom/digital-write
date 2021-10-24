package com.tcsp.digitalwrite.api.controller;

import com.tcsp.digitalwrite.api.controller.helper.ControllerHelper;
import com.tcsp.digitalwrite.api.dto.AnswerDto;
import com.tcsp.digitalwrite.api.dto.RegistrationDto;
import com.tcsp.digitalwrite.api.exception.SystemException;
import com.tcsp.digitalwrite.shared.Constants;
import com.tcsp.digitalwrite.store.entity.RoleEntity;
import com.tcsp.digitalwrite.store.entity.SystemEntity;
import com.tcsp.digitalwrite.store.entity.UserEntity;
import com.tcsp.digitalwrite.store.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PersistenceException;
import java.util.*;
import java.util.stream.Collectors;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@RestController
public class RegistrationController {

    UserRepository userRepository;

    ControllerHelper controllerHelper;


    public static final String CREATE_USER = "/api/systems/registration/users";
    public static final String DELETE_USER = "/api/systems/registration/users/{token_user}";

    @PostMapping(CREATE_USER)
    public RegistrationDto registerUser(
            @RequestParam(value = "name") String nameUser,
            @RequestParam(value = "typing_speed") Double typingSpeed,
            @RequestParam Double accuracy,
            @RequestParam(value = "hold_time") Double holdTime,
            @RequestParam(value = "system_id") String systemId,
            @RequestParam(value = "user_roles") List<String> userRoles
    ){
        SystemEntity system = controllerHelper.getSystemOrThrowException(systemId);

        Set<RoleEntity> roles = userRoles.stream()
                .map(role -> controllerHelper.getRoleOrThrowException(role))
                .collect(Collectors.toSet());

        String token = UUID.randomUUID().toString();

        UserEntity user = UserEntity
                .builder()
                .token(token)
                .typingSpeed(typingSpeed)
                .name(nameUser)
                .accuracy(accuracy)
                .holdTime(holdTime)
                .system(system)
                .roles(roles)
                .build();

        try {
            UserEntity savedUser = userRepository.saveAndFlush(user);
            return RegistrationDto.makeDefault(savedUser);
        } catch (PersistenceException e) {
            throw new SystemException(Constants.ERROR_SERVICE);
        }

    }

    @DeleteMapping(DELETE_USER)
    public AnswerDto deleteUser(
            @PathVariable(value = "token_user") String tokenUser,
            @RequestParam(value = "system_id") String systemId
    ){
        controllerHelper.getSystemOrThrowException(systemId);

        UserEntity user = controllerHelper.getUserOrThrowException(tokenUser);

        try {
            userRepository.delete(user);
        } catch (PersistenceException e) {
            throw new SystemException(Constants.ERROR_SERVICE);
        }

        return AnswerDto.makeDefault(Constants.DELETE_USER);
    }
}
