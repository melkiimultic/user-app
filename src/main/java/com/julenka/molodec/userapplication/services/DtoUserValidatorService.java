package com.julenka.molodec.userapplication.services;

import com.julenka.molodec.userapplication.dtoObjects.DtoUser;
import com.julenka.molodec.userapplication.dtoObjects.ResponseWithErrors;
import com.julenka.molodec.userapplication.dtoObjects.ResponseWithoutErrors;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class DtoUserValidatorService {

    private PasswordValidatorService passwordValidatorService;



    public Object validate(DtoUser dtoUser) {
        ResponseWithErrors errors = new ResponseWithErrors();

        if ((dtoUser == null)) {
            errors.setErrors(List.of("The query is empty"));
            return errors;
        }
        if (Strings.isEmpty(dtoUser.getName()) || Strings.isEmpty(dtoUser.getLogin()) || CollectionUtils.isEmpty(dtoUser.getRoles())) {
            errors.setErrors(List.of("At least one field is empty"));
            return errors;
        }
        if (!passwordValidatorService.passwordValidate(dtoUser.getPassword())) {
            errors.setErrors(List.of("wrong password format"));
            return errors;
        }
        return new ResponseWithoutErrors();
    }


}
