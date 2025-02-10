package com.example.AutumnMall.Member.mapper;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Member.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(source = "memberId", target = "memberId")
    @Mapping(source = "name", target = "nickname")
    @Mapping(source = "accessToken", target = "accessToken")
    @Mapping(source = "refreshToken", target = "refreshToken")
    MemberLoginResponseDto toLoginResponseDtoToMember(Long memberId, String name, String accessToken, String refreshToken);

    @Mapping(source = "memberId", target = "memberId")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "regdate", target = "regdate")
    MemberSignupResponseDto memberSignupResponseDtoToMember(Member member);

    @Mapping(target = "roles", ignore = true)
    @Mapping(source = "name", target= "name")
    @Mapping(source = "email", target= "email")
    // 비밀번호는 서비스에서 인코딩 후 설정
    @Mapping(source = "birthYear", target = "birthYear", qualifiedByName = "stringToInteger")
    @Mapping(source = "birthMonth", target = "birthMonth", qualifiedByName = "stringToInteger")
    @Mapping(source = "birthDay", target = "birthDay", qualifiedByName = "stringToInteger")
    // DB에서 자동 생성되므로 무시
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "phone", target = "phone")
    @Mapping(source = "roadAddress", target = "roadAddress")
    @Mapping(source = "zipCode", target = "zipCode")
    @Mapping(source = "detailAddress", target = "detailAddress")
    @Mapping(target = "memberId", ignore = true)
    @Mapping(target = "regdate", ignore = true)
    @Mapping(target = "mileages", ignore = true)
    @Mapping(target = "totalMileage", ignore = true)
    @Mapping(target = "favorites", ignore = true)
    @Mapping(target = "recentProducts", ignore = true)
    Member memberSignupDtoToMember(MemberSignupDto memberSignupDto);

    @Mapping(source = "name", target = "name", defaultValue = "null")
    @Mapping(source = "email", target = "email", defaultValue = "null")
    @Mapping(source = "birthYear", target = "birthYear", qualifiedByName = "stringToInteger")
    @Mapping(source = "birthMonth", target = "birthMonth", qualifiedByName = "stringToInteger")
    @Mapping(source = "birthDay", target = "birthDay", qualifiedByName = "stringToInteger")
    @Mapping(source = "gender", target = "gender", defaultValue = "null")
    @Mapping(source = "phone", target = "phone", defaultValue = "null")
    @Mapping(source = "roadAddress", target = "roadAddress", defaultValue = "null")
    @Mapping(source = "zipCode", target = "zipCode", defaultValue = "null")
    @Mapping(source = "detailAddress", target = "detailAddress", defaultValue = "null")
    @Mapping(target = "memberId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "regdate", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "mileages", ignore = true)
    @Mapping(target = "totalMileage", ignore = true)
    @Mapping(target = "favorites", ignore = true)
    @Mapping(target = "recentProducts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    Member updateMemberFromDto(MemberUpdateDto memberUpdateDto, @MappingTarget Member member);

    // String → Integer 변환 메서드
    @Named("stringToInteger")
    default Integer stringToInteger(String value) {
        return value != null ? Integer.parseInt(value) : null;
    }
}
