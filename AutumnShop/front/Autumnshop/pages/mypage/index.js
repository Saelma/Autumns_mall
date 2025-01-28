import React, { useEffect, useState } from "react";
import { Container, Typography, Box, Button } from "@mui/material";
import Link from "next/link";

const MyPage = () => {
  const [userInfo, setUserInfo] = useState(null);

  useEffect(() => {
    const getUserInfo = async () => {
      const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));

      if (!loginInfo || !loginInfo.accessToken) {
        window.location.href = "/login";
        return;
      }

      try {
        const response = await fetch("http://localhost:8080/members/info", {
          method: "GET",
          headers: {
            Authorization: `Bearer ${loginInfo.accessToken}`,
          },
        });

        if(!response.ok){
          throw new error;
        }

        const data = await response.json();
        setUserInfo(data);
      } catch (error) {
        console.error(error);
        window.location.href = "/login";
      }
    };

    getUserInfo();
  }, []);

  if (!userInfo) {
    return <div>Loading...</div>;
  }

  return (
    <Container
      sx={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        minHeight: "100vh",
      }}
    >
      <h1>MyPage</h1>
      <p>MyPage 페이지입니다.</p>
      <Box sx={{ textAlign: "center" }}>
        <Typography variant="h5">이름: {userInfo.name}</Typography>
        <Typography variant="h5">이메일: {userInfo.email}</Typography>
        <Typography variant="h5">
          생년월일: {userInfo.birthYear}년 {userInfo.birthMonth}월{" "}
          {userInfo.birthDay}일
        </Typography>
        <Typography variant="h5">
          성별: {userInfo.gender === "M" ? "남자" : "여자"}
        </Typography>
        <Typography variant="h5">마일리지: {userInfo.totalMileage}</Typography>
        <div>
          <Link href={`/mypage/mileageHistory`}>
          <Button
          variant="contained"
          color="primary"
          sx={{ marginTop: 2}}
          >
            마일리지 내역 확인
            </Button>
            </Link>
            </div>
        <div>
          <Link href={`/mypage/passwordEdit`} passHref>
        <Button
        variant="contained"
        color="primary"
        sx={{ marginTop: 2}}
        >
          비밀번호 변경
          </Button>
          </Link>
          </div>
        <div>
        <Link href={`/mypage/getFavorites`} passHref>
        <Button
            variant="contained"
            color="primary"
            sx={{ marginTop: 2 }}
          >
            즐겨찾기 목록 보기
          </Button>
          </Link>
        </div>
        <div>
          <Link href={`/mypage/recentProducts`} passHref>
          <Button
            variant="contained"
            color="primary"
            sx={{ marginTop : 2}}
            >
              최근 본 상품 보기
            </Button>
            </Link>
        </div>
        <div>
          <Link href={`/mypage/passCheck`} passHref>
        <Button 
          variant="contained" 
          color="primary" 
          sx={{ marginTop: 2 }}
        >
          수정하기
        </Button>
        </Link>
        </div>
      </Box>
    </Container>
  );
};

export default MyPage;
