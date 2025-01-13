import React, { useEffect, useState } from "react";
import { TextField, Button } from "@mui/material";

const AddressSearch = ({ setRoadAddress, setZipCode, setDetailAddress, 
  roadAddress, zipCode, detailAdd
}) => {
      // 회원가입을 할 때의 경우와 내 정보를 수정하고자 할 때의 경우 두 가지를 구분 
      const [address, setAddress] = useState({
        roadAddress: roadAddress || "", 
        zipCode: zipCode || "",
      });
      const [detailAddress, setLocalDetailAddress] = useState(detailAdd || "");

  useEffect(() => {
    if (typeof window !== "undefined" && !window.Kakao) {
      // 카카오 자바스크립트 SDK 호출 
      const script = document.createElement("script");
      script.src = "https://t1.kakaocdn.net/kakao_js_sdk/v1/kakao.js";
      script.async = true;
      script.onload = () => {
        // 카카오 API 키 하드 코딩이 아니도록 관리
        const kakaoKey = process.env.NEXT_PUBLIC_KAKAO_KEY;

        if(kakaoKey){
          window.Kakao.init(kakaoKey);
          setKakaoLoaded(true);

          // 카카오 주소검색 API 호출 
          const postcodeScript = document.createElement("script");
          postcodeScript.src =
            "https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
          postcodeScript.async = true;
          postcodeScript.onload = () => {
            setPostcodeLoaded(true);
          };
          document.body.appendChild(postcodeScript);
      } else {
        console.error("카카오 키를 불러오지 못했습니다.");
      }
      };
      script.onerror = () => {
        console.error("카카오 SDK 로드 실패");
      };
      document.body.appendChild(script);
    }

    // 상태 초기화
    setAddress({
      roadAddress: roadAddress || "",
      zipCode: zipCode || "",
    });
    setLocalDetailAddress(detailAdd || "");
  }, [roadAddress, zipCode, detailAdd]);

  
    const [kakaoLoaded, setKakaoLoaded] = useState(false);
    const [postcodeLoaded, setPostcodeLoaded] = useState(false);

  const handleAddressSearch = () => {
    if (!kakaoLoaded || !postcodeLoaded) {
      return;
    }

    // 카카오 주소 검색 API 호출 확인 
    if (window.daum && window.daum.Postcode) {
      new window.daum.Postcode({
        oncomplete: function (data) {
          // 주소 검색 텍스트 이벤트 
          setAddress({
            roadAddress: data.roadAddress,
            zipCode: data.zonecode,
          });
          // DB 상위 props 전달 
          setRoadAddress(data.roadAddress);
          setZipCode(data.zonecode);
        },
      }).open();
    } else {
      console.error("카카오 주소 검색 API가 로드되지 않았습니다.");
    }
  };

  const handleDetailAddressChange = (e) => {
    setLocalDetailAddress(e.target.value);
    setDetailAddress(e.target.value); // 상위 props 전달
  };

  return (
    <div>
      <Button variant="contained" onClick={handleAddressSearch}>
        주소 검색
      </Button>
      <TextField
        label="우편번호"
        value={address.zipCode}
        fullWidth
        margin="normal"
        InputProps={{ readOnly: true }}
      />
      <TextField
        label="도로명 주소"
        value={address.roadAddress}
        fullWidth
        margin="normal"
        InputProps={{ readOnly: true }}
      />
      <TextField
        label="상세 주소"
        value={detailAddress}
        onChange={handleDetailAddressChange}
        fullWidth
        margin="normal"
        placeholder="상세 주소를 입력하세요"
      />
    </div>
  );
};

export default AddressSearch;
