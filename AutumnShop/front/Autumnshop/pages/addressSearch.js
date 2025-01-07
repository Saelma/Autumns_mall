import { useEffect, useState } from 'react';

const AddressSearch = ({ setRoadAddress, setZipCode }) => {
    const [address, setAddress] = useState({
      roadAddress: '',
      jibunAddress: '',
      zipCode: '',
    });
  
    const [kakaoLoaded, setKakaoLoaded] = useState(false);
    const [postcodeLoaded, setPostcodeLoaded] = useState(false);
  
    useEffect(() => {
      if (typeof window !== "undefined" && !window.Kakao) {
        const script = document.createElement('script');
        script.src = "https://t1.kakaocdn.net/kakao_js_sdk/v1/kakao.js"; 
        script.async = true;
        script.onload = () => {
          window.Kakao.init('c2734b30e79501566d57ac4816f299fb'); 
          setKakaoLoaded(true);
  
          const postcodeScript = document.createElement('script');
          postcodeScript.src = 'https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
          postcodeScript.async = true;
          postcodeScript.onload = () => {
            setPostcodeLoaded(true); 
          };
          document.body.appendChild(postcodeScript);
        };
        script.onerror = () => {
          console.error('카카오 SDK 로드 실패');
        };
        document.body.appendChild(script);
      }
    }, []);
  
    const handleAddressSearch = () => {
      if (!kakaoLoaded || !postcodeLoaded) {
        return;
      }
  
      if (window.daum && window.daum.Postcode) {
        new window.daum.Postcode({
          oncomplete: function (data) {
            setAddress({
              roadAddress: data.roadAddress, 
              jibunAddress: data.jibunAddress, 
              zipCode: data.zonecode,  
            });
            setRoadAddress(data.roadAddress);
            setZipCode(data.zonecode);
          },
        }).open();
      } else {
        console.error('카카오 주소 검색 API가 로드되지 않았습니다.');
      }
    };
  
    return (
      <div>
        <button onClick={handleAddressSearch}>주소 검색</button>
        {address.zipCode && (
          <div>
            <p><strong>우편번호:</strong> {address.zipCode}</p>
            <p><strong>도로명 주소:</strong> {address.roadAddress}</p>
            <p><strong>지번 주소:</strong> {address.jibunAddress}</p>
          </div>
        )}
      </div>
    );
  };

export default AddressSearch;
