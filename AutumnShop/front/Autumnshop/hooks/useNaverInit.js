import { useEffect, useCallback } from 'react';

const useNaverInit = () => {
  const handleNaverInit = useCallback(() => {
    // window.naver가 로드된 후 네이버 로그인 초기화
    if (window.naver) {
      const naverLogin = new window.naver.LoginWithNaverId({
        clientId: process.env.NEXT_PUBLIC_NAVER_CLIENT_ID,
        callbackUrl: `${process.env.NEXT_PUBLIC_AUTUMNMALL_VERCEL_ADDRESS}joinform`,
        callbackHandle: true,
        isPopup: false,
        loginButton: {
          color: 'green',
          type: 1,
          height: '60',
        },
      });

      naverLogin.init();
    }
  }, []);

  useEffect(() => {
    handleNaverInit();
  }, [handleNaverInit]);

  return null;
};

export default useNaverInit;