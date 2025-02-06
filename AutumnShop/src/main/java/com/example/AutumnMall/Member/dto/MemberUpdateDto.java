package com.example.AutumnMall.Member.dto;

import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateDto {

    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$",
            message = "이메일 형식을 맞춰야합니다")
    private String email;

    @NotEmpty
    @Pattern(regexp = "^[a-zA-Z가-힣\\\\s]{2,15}",
            message = "이름은 영문자, 한글, 공백포함 2글자부터 15글자까지 가능합니다.")
    private String name;

    @NotNull
    @Pattern(regexp = "^\\d{4}$", message = "생년은 4자리 숫자로 입력해야 합니다")
    private String birthYear;

    @NotNull
    @Pattern(regexp = "^(0?[1-9]|1[012])$", message = "생월은 1부터 12까지의 숫자로 입력해야 합니다")
    private String birthMonth;

    @NotNull
    @Pattern(regexp = "^(0?[1-9]|[12][0-9]|3[01])$", message = "생일은 1부터 31까지의 숫자로 입력해야 합니다")
    private String birthDay;

    @NotEmpty
    @Pattern(regexp = "^[MF]{1}$", message = "성별은 'M' 또는 'F'로 입력해야 합니다")
    private String gender;

    @NotNull(message = "전화번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^(010|031|032)-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String phone;

    // 도로명 주소 패턴 검증 (예시: 서울특별시 강남구 삼성로 85)
    @NotNull(message = "도로명 주소는 필수 입력 사항입니다.")
    @Length(min = 5, max = 100, message = "도로명 주소는 최소 5자 이상 100자 이하이어야 합니다.")
    @Pattern(
            regexp = "^[가-힣A-Za-z·\\d~\\-\\.\\s]+(로|길)\\s\\d+(-\\d+)*\\s?\\d*$",
            message = "도로명 주소 형식이 올바르지 않습니다."
    )
    private String roadAddress;

    @NotNull(message = "우편번호는 필수 입력 사항입니다.")
    @Pattern(regexp = "^\\d{5}$", message = "우편번호 형식이 올바르지 않습니다. 형식: 12345")
    private String zipCode;

    @NotNull(message = "상세주소를 입력해주세요.")
    private String detailAddress;
}