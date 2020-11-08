package com.julenka.molodec.userapplication.services;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class PasswordValidatorService {
    public boolean passwordValidate(String password) {

        if (Strings.isEmpty(password)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^(?=.*?[A-Z])(?=.*?[0-9]).{2,}$");
        return pattern.matcher(password).matches();
    }

}
