package com.julenka.molodec.userapplication.services;

import com.julenka.molodec.userapplication.EnumRoles;
import com.julenka.molodec.userapplication.dtoObjects.DtoUser;
import com.julenka.molodec.userapplication.dtoObjects.ResponseWithErrors;
import com.julenka.molodec.userapplication.dtoObjects.ResponseWithoutErrors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class DtoUserValidatorServiceTest {

    @Mock
    private PasswordValidatorService validator;

    @InjectMocks
    private DtoUserValidatorService service;

    @Test
    @DisplayName("Wrong password -> ResponseWithErrors with appropriate msg")
    void errorsContainWrongPassMessage() {
        Mockito.when(validator.passwordValidate(ArgumentMatchers.anyString())).thenReturn(false);
        DtoUser dto = new DtoUser();
        dto.setName("name");
        dto.setLogin("login");
        dto.setPassword("wqe");
        dto.setRoles(List.of(EnumRoles.ADMIN));
        Object validate = service.validate(dto);
        assertTrue(validate instanceof ResponseWithErrors);
        ResponseWithErrors errors = (ResponseWithErrors) validate;
        assertFalse(errors.isSuccess());
        assertTrue(errors.getErrors().contains("wrong password format"));
        Mockito.verify(validator, times(1)).passwordValidate("wqe");
    }

    @Test
    @DisplayName("valid password->ResponseWithoutErrors")
    void successIsTrue() {
        Mockito.when(validator.passwordValidate(ArgumentMatchers.anyString())).thenReturn(true);
        DtoUser dto = new DtoUser();
        dto.setName("name");
        dto.setLogin("login");
        dto.setPassword("Pswd1");
        dto.setRoles(List.of(EnumRoles.ADMIN));
        Object validate = service.validate(dto);
        assertTrue(validate instanceof ResponseWithoutErrors);
        ResponseWithoutErrors errors = (ResponseWithoutErrors) validate;
        assertTrue(errors.isSuccess());
        Mockito.verify(validator, times(1)).passwordValidate("Pswd1");
    }
    @Test
    @DisplayName("empty name->ResponseWithErrors with appropriate msg")
    void emptyName() {
        DtoUser dto = new DtoUser();
        dto.setName("");
        dto.setLogin("login");
        dto.setPassword("Pswd1");
        dto.setRoles(List.of(EnumRoles.ADMIN));
        Object validate = service.validate(dto);
        assertTrue(validate instanceof ResponseWithErrors);
        ResponseWithErrors errors = (ResponseWithErrors) validate;
        assertFalse(errors.isSuccess());
        assertTrue(errors.getErrors().contains("At least one field is empty"));
    }
    @Test
    @DisplayName("null dtoUser->ResponseWithErrors with appropriate msg")
    void userIsNull(){
        DtoUser dto = null;
        Object validate = service.validate(dto);
        assertTrue(validate instanceof ResponseWithErrors);
        ResponseWithErrors errors = (ResponseWithErrors) validate;
        assertFalse(errors.isSuccess());
        assertTrue(errors.getErrors().contains("The query is empty"));
    }


}