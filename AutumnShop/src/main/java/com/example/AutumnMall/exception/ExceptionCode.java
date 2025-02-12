package com.example.AutumnMall.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404, "멤버 정보를 찾을 수 없습니다"),
    INVALID_CARTITEM_STATUS(400, "구매 가능한 수량보다 더 구매할 수 없습니다! (최대 수량: 10)");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}