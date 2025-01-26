package com.example.AutumnMall.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResponseWrapper<T> {
    private boolean success;
    private T data;
    private String message;
}
