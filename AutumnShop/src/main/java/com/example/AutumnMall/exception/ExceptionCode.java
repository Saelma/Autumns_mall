package com.example.AutumnMall.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404, "멤버 정보를 찾을 수 없습니다"),
    CARTITEM_NOT_FOUND(404, "장바구니 아이템을 찾을 수 없습니다"),
    CART_NOT_FOUND(404, "장바구니 정보를 찾을 수 없습니다"),
    PRODUCT_NOT_FOUND(404, "장바구니 정보를 찾을 수 없습니다"),
    RECENT_PRODUCT_NOT_FOUND(500, "최근 본 물품을 찾을 수 없습니다"),
    FAVORITES_NOT_FOUND(404, "즐겨찾기한 물품들을 찾을 수 없습니다"),
    ORDER_NOT_FOUND(500, "해당 주문 목록을 찾을 수 없습니다"),
    PAYMENT_NOT_FOUND(500, "해당 구매 목록을 찾을 수 없습니다"),
    REVIEW_NOT_FOUND(500, "해당 물품의 리뷰 목록을 찾을 수 없습니다"),
    INVALID_CARTITEM_STATUS(400, "구매 가능한 수량보다 더 구매할 수 없습니다! (최대 수량: 10)"),
    INVALID_PAYMENT_STATUS(400, "구매 가능한 수량보다 더 구매할 수 없습니다!"),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류");


    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}