import React, { useState } from "react";
import { Container, Typography, TextField, Button, Box } from "@mui/material";
import { makeStyles } from "@mui/styles";

const useStyles = makeStyles((theme) => ({
  container: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    marginTop: "40px",
    width: "100%",
    maxWidth: "400px",
    backgroundColor: "#ffffff",
    padding: "20px",
    borderRadius: "8px",
    boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
  },
  form: {
    display: "flex",
    flexDirection: "column",
    width: "100%",
  },
  button: {
    marginTop: "16px",
    backgroundColor: "#000000",
    color: "#ffffff",
    '&:hover': {
      backgroundColor: "#333",
    },
  },
  textField: {
    marginTop: "32px",
    marginBottom: "16px",
  },
}));

const EmailAuth = () => {
  const classes = useStyles();
  const [email, setEmail] = useState("");
  const [verificationCode, setVerificationCode] = useState("");
  const [message, setMessage] = useState("");
  const [isEmailSent, setIsEmailSent] = useState(false);
  
  const handleEmailChange = (event) => {
    setEmail(event.target.value);
  };

  const handleVerificationCodeChange = (event) => {
    setVerificationCode(event.target.value);
  };

  const handleSendVerificationEmail = async () => {
    if (!email.includes("@naver.com")) {
      setMessage("네이버 이메일만 입력 가능합니다.");
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/members/password/reset-request", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email }),
      });


      if (response.ok) {
        setMessage("인증 코드가 이메일로 전송되었습니다.");
        setIsEmailSent(true);
      } else {
        setMessage("이메일 전송 실패.");
      }
    } catch (error) {
      setMessage("서버 오류. 다시 시도해주세요.");
    }
  };

  const handleVerifyEmailCode = async () => {
    try {
      const response = await fetch("http://localhost:8080/members/password/verify", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, code: verificationCode }),
      });

      if (response.ok) {
        setMessage("인증 성공!");
        localStorage.setItem("email", email);
        localStorage.setItem("verificationCode", verificationCode);

        window.location.href = "./passwordReset"; // 로그인 페이지로 리디렉션
      } else {
        setMessage("인증 코드가 올바르지 않습니다.");
      }
    } catch (error) {
      setMessage("서버 오류. 다시 시도해주세요.");
    }
  };

  return (
    <Container className={classes.container}>
      <Typography variant="h5">이메일 인증</Typography>
      <Box className={classes.form}>
        <TextField
          label="이메일 (네이버 메일)"
          variant="outlined"
          fullWidth
          required
          value={email}
          onChange={handleEmailChange}
          className={classes.textField}
        />
        <Button
          variant="contained"
          className={classes.button}
          onClick={handleSendVerificationEmail}
          disabled={isEmailSent}
        >
          인증 코드 전송
        </Button>
        {isEmailSent && (
          <>
            <TextField
              label="인증 코드"
              variant="outlined"
              fullWidth
              required
              value={verificationCode}
              onChange={handleVerificationCodeChange}
              className={classes.textField}
            />
            <Button
              variant="contained"
              className={classes.button}
              onClick={handleVerifyEmailCode}
            >
              인증 코드 확인
            </Button>
          </>
        )}
        {message && <Typography color="error">{message}</Typography>}
      </Box>
    </Container>
  );
};

export default EmailAuth;
