import React, { useEffect, useState } from "react";
import { TextField, Button } from "@mui/material";

const AddressSearch = ({ setRoadAddress, setZipCode, setDetailAddress }) => {
  const [address, setAddress] = useState({
    roadAddress: "",
    zipCode: "",
  });

  const [detailAddress, setLocalDetailAddress] = useState("");

  const [kakaoLoaded, setKakaoLoaded] = useState(false);
  const [postcodeLoaded, setPostcodeLoaded] = useState(false);

  useEffect(() => {
    if (typeof window !== "undefined" && !window.Kakao) {
      // 카카오 자바스크립트 SDK 호출 
      const script = document.createElement("script");
      script.src = "https://t1.kakaocdn.net/kakao_js_sdk/v1/kakao.js";
      script.async = true;
      script.onload = () => {
        window.Kakao.init("c2734b30e79501566d57ac4816f299fb");
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
      };
      script.onerror = () => {
        console.error("카카오 SDK 로드 실패");
      };
      document.body.appendChild(script);
    }

    // 상태 초기화
    setRoadAddress("");
    setZipCode("");
    setDetailAddress("");
  }, []);

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
