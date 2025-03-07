import React, { useState } from "react";
import { TextField, Button, Container, Typography } from "@mui/material";
import { makeStyles } from "@mui/styles";

const useStyles = makeStyles((theme) => ({
  container: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    width: "100%",
    maxWidth: "500px",
    height: "100%",
    margin: "0 auto",
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
    marginTop: theme.spacing(2),
    backgroundColor: "#000000",
    color: "#ffffff",
    '&:hover': {
      backgroundColor: "#333",
    },
  },
  textField: {
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
  errorMessage: {
    color: "red",
    marginTop: "10px",
  },
}));

const PasswordReset = () => {
  const classes = useStyles();
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  const handlePasswordReset = async () => {
    if (newPassword !== confirmPassword) {
      setErrorMessage("새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.");
      return;
    }

    const email = localStorage.getItem("email");
    const inputCode = localStorage.getItem("verificationCode");

    try {
      const response = await fetch("http://localhost:8080/members/password/change", {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          email: email,
          newPassword: newPassword,
          inputCode: inputCode,
        }),
      });

      if (response.ok) {
        alert("비밀번호가 성공적으로 변경되었습니다.");
        setNewPassword("");
        setConfirmPassword("");
        setErrorMessage("");

        localStorage.removeItem("email");
        localStorage.removeItem("verificationCode");
        
        window.location.href = "/login"; // 로그인 페이지로 리디렉션
      } else {
        throw new Error("비밀번호 변경에 실패했습니다.");
      }
    } catch (error) {
      setErrorMessage(error.message);
    }
  };

  return (
    <Container className={classes.container}>
      <Typography variant="h6">비밀번호 재설정</Typography>
      <form className={classes.form} noValidate autoComplete="off">
        <TextField
          label="새 비밀번호"
          variant="outlined"
          type="password"
          value={newPassword}
          onChange={(e) => setNewPassword(e.target.value)}
          fullWidth
          required
          className={classes.textField}
        />
        <TextField
          label="새 비밀번호 확인"
          variant="outlined"
          type="password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          fullWidth
          required
          className={classes.textField}
        />
        {errorMessage && (
          <Typography className={classes.errorMessage} variant="body2">
            {errorMessage}
          </Typography>
        )}
        <Button
          variant="contained"
          className={classes.button}
          onClick={handlePasswordReset}
        >
          비밀번호 변경
        </Button>
      </form>
    </Container>
  );
};

export default PasswordReset;
