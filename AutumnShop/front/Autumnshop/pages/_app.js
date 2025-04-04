import React, { useEffect } from "react";
import { ThemeProvider } from "@mui/material/styles";
import { Box } from "@mui/material"; // Container 대신 Box 사용
import AppBar from "../components/AppBar";
import { createTheme } from "@mui/material/styles";
import Router from "next/router";
import Script from 'next/script';
import { ReCaptchaProvider } from 'next-recaptcha-v3';

const theme = createTheme({
  typography: {
    fontFamily: "Roboto, sans-serif",
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: `
        html, body, #__next {
          height: 100%;
        }
      `,
    },
  },
});

function MyApp({ Component, pageProps }) {
  useEffect(() => {
    const refreshTokenInterval = setInterval(async () => {
      const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));

      console.log(loginInfo);
      if (loginInfo) {
        const { accessToken, refreshToken } = loginInfo;

        try {
          const response = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}members/refreshToken`, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${accessToken}`,
            },
            body: JSON.stringify({ refreshToken }), // refreshToken을 별도의 객체로 보냄
          });

          if (response.ok) {
            const data = await response.json();
            const newLoginInfo = data;
            localStorage.setItem("loginInfo", JSON.stringify(newLoginInfo));
          } else {
            throw new Error("리프레쉬 토큰을 재생성하는데 실패했습니다.");
          }
        } catch (error) {
          console.error(error);

          // 오류가 발생하면 local storage를 삭제하고 홈 페이지로 이동
          localStorage.removeItem("loginInfo");
          window.dispatchEvent(new CustomEvent("loginStatusChanged"));
          Router.push("/welcome");
        }
      }
    }, 10 * 1000); // 10분마다 실행

    // 컴포넌트가 언마운트 될 때 인터벌을 정리
    return () => {
      clearInterval(refreshTokenInterval);
    };
  }, []);

  return (
    <ThemeProvider theme={theme}>
      <ReCaptchaProvider reCaptchaKey={process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}
            scriptProps={{
              async: true,
              defer: false,
              appendTo: "head",
              nonce: undefined
            }}
          >
      <AppBar />
      <Box
        sx={{
          display: "flex",
          minHeight: "100vh",
        }}
      >

        <Script
        src="https://static.nid.naver.com/js/naveridlogin_js_sdk_2.0.2.js"
        strategy="beforeInteractive"
       />

      <Script 
        src="https://cdn.iamport.kr/v1/iamport.js" 
        strategy="beforeInteractive"
      />
        {/* Content Section */}
        <Box
          sx={{
            flex: 1, // 콘텐츠 영역이 남은 공간을 차지하도록 설정
            paddingTop: "64px", // AppBar 높이를 고려한 여백
            padding: 4,
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
            alignItems: "flex-start", // 상단에 정렬되도록 설정
          }}
        >
          <Component {...pageProps} />
        </Box>
      </Box>
      </ReCaptchaProvider>
    </ThemeProvider>
  );
}

export default MyApp;