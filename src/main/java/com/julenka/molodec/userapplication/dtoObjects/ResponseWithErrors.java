package com.julenka.molodec.userapplication.dtoObjects;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ResponseWithErrors {
    @Getter
    private final boolean success = false;
    @Getter
    @Setter
    private List<String> errors;
}
