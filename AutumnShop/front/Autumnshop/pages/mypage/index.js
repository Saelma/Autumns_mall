import React, { useEffect, useState } from "react";
import dynamic from "next/dynamic";
import { Box, Button, Typography, Divider, TextField } from "@mui/material";
import { makeStyles } from "@mui/styles";

const MileageHistory = dynamic(() => import("../../components/mypage/mileageHistory"));
const PassCheck = dynamic(() => import("../../components/mypage/passCheck"));
const RecentProducts = dynamic(() => import("../../components/mypage/recentProducts"));
const GetFavorites = dynamic(() => import("../../components/mypage/getFavorites"));
const PasswordEdit = dynamic(() => import("../../components/mypage/passwordEdit"));
const SellChart = dynamic(() => import("../../components/mypage/sellChart"));
const GetReport = dynamic(() => import("../../components/mypage/getReport"));

const useStyles = makeStyles({
  sidebar: {
    marginTop: "30px",
    width: "250px",
    backgroundColor: "#ffffff",
    display: "flex",
    flexDirection: "column",
    padding: "16px",
    boxShadow: "2px 0 5px rgba(0, 0, 0, 0.1)",
    height: "auto",
  },
  content: {
    marginTop: "30px",
    flex: 1,
    padding: "24px",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "flex-start",
    backgroundColor: "#fff",
    borderRadius: "8px",
    boxShadow: "0 0 10px rgba(0, 0, 0, 0.1)",
    height: "auto",
  },
  title: {
    marginBottom: "16px",
    fontWeight: 700,
    textAlign: "center",
    color: "#333",
  },
  button: {
    marginBottom: "8px",
    justifyContent: "flex-start",
    fontWeight: 600,
    color: "#000",
    backgroundColor: "transparent",
    "&:hover": { backgroundColor: "#808080" },
  },
  activeButton: {
    color: "#fff",
    backgroundColor: "#000000",
    "&:hover": {
      backgroundColor: "#000000",
    },
  },
  section: {
    display: "flex",
    flexDirection: "column",
    alignItems: "flex-start",
    width: "100%",
    padding: "16px 0",
    gap: 2,
  },
  sectionText: {
    marginBottom: "16px",
  },
  textField: {
    width: "100%",
    marginBottom: "16px", 
    borderRadius: "4px",
    border: "1px solid #ddd",
    padding: "10px",
    backgroundColor: "#f9f9f9",
    "& .MuiInputBase-root": {
      padding: "0",
    },
    "& .MuiInputLabel-root": {
      fontWeight: "700",
      fontSize: "1rem", 
      color: "#333", 
    },
    "& .MuiInputBase-input": {
      color: "#333", 
      fontWeight: "700", 
    },
  },
  divider: {
    marginBottom: "24px", 
    width: "100%",
    borderColor: "#000", 
    borderWidth: "2px", 
  },
});

const MyPage = () => {
  const [userInfo, setUserInfo] = useState(null);
  const [activeSection, setActiveSection] = useState("profile");
  const [isAdmin, setIsAdmin] = useState(false);
  const classes = useStyles();

  useEffect(() => {
    const getUserInfo = async () => {
      const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));

      if (!loginInfo || !loginInfo.accessToken) {
        window.location.href = "/login";
        return;
      }

      try {
        const response = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}members/info`, {
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
        console.log("유저 정보를 불러오는 데 실패했습니다.", error);
      }
    };

    getUserInfo();
  }, []);

  const renderSection = () => {
    switch (activeSection) {
      case "profile":
        return (
          <Box className={classes.section}>
            <TextField
              label="이름"
              value={userInfo.name}
              className={classes.textField}
              variant="outlined"
              InputProps={{ readOnly: true }}
            />
            <TextField
              label="이메일"
              value={userInfo.email}
              className={classes.textField}
              variant="outlined"
              InputProps={{ readOnly: true }}
            />
            <TextField
              label="생년월일"
              value={`${userInfo.birthYear}년 ${userInfo.birthMonth}월 ${userInfo.birthDay}일`}
              className={classes.textField}
              variant="outlined"
              InputProps={{ readOnly: true }}
            />
            <TextField
              label="성별"
              value={userInfo.gender === "M" ? "남자" : "여자"}
              className={classes.textField}
              variant="outlined"
              InputProps={{ readOnly: true }}
            />
            <TextField
              label="전화번호"
              value={userInfo.phone}
              className={classes.textField}
              variant="outlined"
              InputProps={{ readOnly: true }}
            />
            <TextField
              label="주소"
              value={`${userInfo.roadAddress} (${userInfo.zipCode})`}
              className={classes.textField}
              variant="outlined"
              InputProps={{ readOnly: true }}
            />
            {/* 관리자인 경우에만 결제 관리 버튼 추가 */}
            {userInfo.roles[0].name === "ROLE_ADMIN" && (
              <Button
                variant="contained"
                sx={{
                  marginTop: "20px",
                  backgroundColor: "#000",
                  color: "#fff",
                  "&:hover": { backgroundColor: "#333" },
                }}
                onClick={() => (window.location.href = "/addProduct")}
              >
                물품 추가
              </Button>
            )}
          </Box>
        );
      case "mileage":
        return <MileageHistory />;
      case "favorites":
        return <GetFavorites />;
      case "recent":
        return <RecentProducts />
      case "check":
        return <PassCheck />
      case "edit" :
        return <PasswordEdit />
      case "sellChart" :
        return <SellChart />
        case "report" :
          return <GetReport />
      default:
        return null;
    }
  };

  if (!userInfo) {
    return <div>Loading...</div>;
  }

  return (
    <Box sx={{ display: "flex", width: "100%", minHeight: "100vh", flexDirection: "row" }}>
      {/* 사이드바 */}
      <Box className={classes.sidebar}>
        <Typography variant="h6" sx={{ fontWeight: 600, marginBottom: 3 }}>
          마이페이지
        </Typography>
        <Button
          variant={activeSection === "profile" ? "contained" : "text"}
          onClick={() => setActiveSection("profile")}
          className={`${classes.button} ${activeSection === "profile" ? classes.activeButton : ""}`}
        >
          내 정보
        </Button>
        <Button
          variant={activeSection === "mileage" ? "contained" : "text"}
          onClick={() => setActiveSection("mileage")}
          className={`${classes.button} ${activeSection === "mileage" ? classes.activeButton : ""}`}
        >
          마일리지 내역
        </Button>
        <Button
          variant={activeSection === "favorites" ? "contained" : "text"}
          onClick={() => setActiveSection("favorites")}
          className={`${classes.button} ${activeSection === "favorites" ? classes.activeButton : ""}`}
        >
          즐겨찾기
        </Button>
        <Button
          variant={activeSection === "recent" ? "contained" : "text"}
          onClick={() => setActiveSection("recent")}
          className={`${classes.button} ${activeSection === "recent" ? classes.activeButton : ""}`}
        >
          최근 본 상품
        </Button>
        <Button
          variant={activeSection === "edit" ? "contained" : "text"}
          onClick={() => setActiveSection("edit")}
          className={`${classes.button} ${activeSection === "edit" ? classes.activeButton : ""}`}
        >
          비밀번호 수정
        </Button>
        <Button
          variant={activeSection === "check" ? "contained" : "text"}
          onClick={() => setActiveSection("check")}
          className={`${classes.button} ${activeSection === "check" ? classes.activeButton : ""}`}
        >
          개인정보 수정
        </Button>
        {userInfo.roles[0].name === "ROLE_ADMIN" && (
          <div>
          <Button
            variant={activeSection === "sellChart" ? "contained" : "text"}
            onClick={() => setActiveSection("sellChart")}
            className={`${classes.button} ${activeSection === "sellChart" ? classes.activeButton : ""}`}
          >
            판매 지표 확인
          </Button>
          <div>
          <Button
            variant={activeSection === "report" ? "contained" : "text"}
            onClick={() => setActiveSection("report")}
            className={`${classes.button} ${activeSection === "report" ? classes.activeButton : ""}`}
          >
            신고 목록 확인
          </Button>
          </div>
        </div>
        )}
      </Box>

      {/* 내용 */}
      <Box className={classes.content}>
        <Typography variant="h4" className={classes.title}>
          {activeSection === "profile" && "내 정보"}
          {activeSection === "mileage" && "마일리지 내역"}
          {activeSection === "favorites" && "즐겨찾기 목록"}
          {activeSection === "recent" && "최근 본 상품"}
          {activeSection === "edit" && "비밀번호 수정"}
          {activeSection === "check" && "개인정보 수정"}
          {activeSection === "sellChart" && "판매 지표 확인"}
          {activeSection === "report" && "신고 목록 확인"}
        </Typography>
        <Divider className={classes.divider} />
        {renderSection()}
      </Box>
    </Box>
  );
};

export default MyPage;
