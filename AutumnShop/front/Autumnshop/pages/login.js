import React, { useState, useRef } from "react";
import { Container, Box, Typography, TextField, Button } from "@mui/material";
import { makeStyles } from "@mui/styles";
import Link from "next/link";
import { useRouter } from "next/router";
import useNaverInit from "@/hooks/useNaverInit"; // 네이버 로그인 훅
import { useReCaptcha } from "next-recaptcha-v3";
import { KeyboardReturnOutlined } from "@mui/icons-material";

const useStyles = makeStyles((theme) => ({
  container: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    width: "100%",
    maxWidth: "500px",
    height: "100vh",
    margin: "0 auto",
    padding: theme.spacing(4),
    backgroundColor: "#ffffff",
    border: "2px solid #000",
    borderRadius: "16px",
    boxShadow: "0px 4px 12px rgba(0, 0, 0, 0.1)",
  },
  form: {
    display: "flex",
    flexDirection: "column",
    width: "100%",
    gap: theme.spacing(2),
  },
  textField: {
    backgroundColor: "#f5f5f5",
    borderRadius: "8px",
    marginBottom: theme.spacing(2),
    '& .MuiOutlinedInput-root': {
      '& fieldset': {
        borderColor: "#ccc",
      },
      '&:hover fieldset': {
        borderColor: "#888",
      },
      '&.Mui-focused fieldset': {
        borderColor: "#888",
      },
    },
    '& .MuiInputLabel-root': {
      color: "#888",
    },
    '& .MuiInputLabel-root.Mui-focused': {
      color: "#888",
    },
  },
  loginButton: {
    backgroundColor: "#000",
    color: "#fff",
    padding: theme.spacing(1.5),
    fontWeight: 600,
    borderRadius: "8px",
    border: "2px solid #000",
    "&:hover": {
      backgroundColor: "#444",
    },
  },
  naverButton: {
    width: "100%",
    marginTop: "30px",
    backgroundColor: "#00c73c",
    color: "white",
    padding: theme.spacing(1.5),
    fontWeight: 600,
    borderRadius: "8px",
    border: "none",
    "&:hover": {
      backgroundColor: "#007c00",
    },
  },
  linkButton: {
    backgroundColor: "#fff",
    color: "#000",
    border: "2px solid #000",
    borderRadius: "8px",
    padding: theme.spacing(1),
    marginTop: theme.spacing(1),
    "&:hover": {
      backgroundColor: "#f0f0f0",
    },
    "&:focus": {
      backgroundColor: "#e0e0e0",
    },
  },
  errorMessage: {
    color: "red",
    fontWeight: 500,
    textAlign: "center",
  },
}));

const Login = () => {
  const classes = useStyles();
  const router = useRouter();
  useNaverInit(); // 네이버 초기화 훅 호출

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState(null);
  const {executeRecaptcha} = useReCaptcha();

  const naverRef = useRef(null); // useRef로 초기화

  const handleNaverLoginClick = () => {
    if (naverRef.current && naverRef.current.children.length > 0) {
      // 네이버 로그인 버튼 클릭
      naverRef.current.children[0].click();
    }
  };

  const handleLogin = async (event) => {
    event.preventDefault();

    try {
      const token = await executeRecaptcha('login')

      const response = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}members/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ token, email, password }),
      });

      if (!response.ok) {
        throw new Error("로그인 실패");
      }

      const loginInfo = await response.json();
      localStorage.setItem("loginInfo", JSON.stringify(loginInfo));
      router.push("/welcome");

      // 로그인 상태 변경 이벤트 발생
      const event = new Event("loginStatusChanged");
      window.dispatchEvent(event);
    } catch (error) {
      setErrorMessage("이메일이나 암호가 틀렸습니다.");
    }
  };

  return (
    <Container maxWidth="sm" className={classes.container} component="main">
      <Typography variant="h4" component="h1" gutterBottom>
        로그인
      </Typography>
      <Box component="form" className={classes.form} onSubmit={handleLogin}>
        <TextField
          className={classes.textField}
          label="이메일"
          type="email"
          variant="outlined"
          fullWidth
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <TextField
          className={classes.textField}
          label="비밀번호"
          type="password"
          variant="outlined"
          fullWidth
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        {errorMessage && (
          <Typography variant="body1" className={classes.errorMessage}>
            {errorMessage}
          </Typography>
        )}
        <Button type="submit" className={classes.loginButton} fullWidth>
          로그인
        </Button>

        <Link href="/joinform" passHref>
          <Button className={classes.linkButton} fullWidth>
            회원가입
          </Button>
        </Link>
        <Link href="/resetPassword/emailAuth" passHref>
          <Button className={classes.linkButton} fullWidth>
            암호를 잊었어요
          </Button>
        </Link>
      </Box>
      {/* 네이버 로그인 버튼 */}
      <button ref={naverRef} id="naverIdLogin" style={{ display: "none" }} />
        <button
          className={classes.naverButton}
          onClick={handleNaverLoginClick}
        >
          네이버 아이디로 회원가입
      </button>
    </Container>
  );
};

export default Login;
