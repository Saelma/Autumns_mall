import React, { useEffect, useState } from "react";
import axios from "axios";
import { Container, Typography, Box, Button } from "@mui/material";

const MyPage = () => {
  const [userInfo, setUserInfo] = useState(null);

  useEffect(() => {
    const fetchUserInfo = async () => {
      const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));

      if (!loginInfo || !loginInfo.accessToken) {
        window.location.href = "/login";
        return;
      }

      try {
        const response = await axios.get("http://localhost:8080/members/info", {
          headers: {
            Authorization: `Bearer ${loginInfo.accessToken}`,
          },
        });

        setUserInfo(response.data);
      } catch (error) {
        console.error(error);
        window.location.href = "/login";
      }
    };

    fetchUserInfo();
  }, []);

  const EditClick = () => {
    window.location.href = "http://localhost:3000/mypage/passCheck";
  };

  const passwordClick = () => {
    window.location.href= "http://localhost:3000/mypage/passwordEdit";
  }

  const mileageHistoryClick = () => {
    window.location.href = "http://localhost:3000/mypage/mileageHistory";
  }

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
          <Button
          variant="contained"
          color="primary"
          sx={{ marginTop: 2}}
          onClick={mileageHistoryClick}
          >
            마일리지 내역 확인
            </Button>
            </div>
        <div>
        <Button
        variant="contained"
        color="primary"
        sx={{ marginTop: 2}}
        onClick={passwordClick}
        >
          비밀번호 변경
          </Button>
          </div>
        <Button 
          variant="contained" 
          color="primary" 
          onClick={EditClick} 
          sx={{ marginTop: 2 }}
        >
          수정하기
        </Button>
      </Box>
    </Container>
  );
};

export default MyPage;
