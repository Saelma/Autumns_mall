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
  },
  form: {
    display: "flex",
    flexDirection: "column",
    width: "100%",
  },
  button: {
    marginTop: theme.spacing(2),
  },
}));

const PasswordChange = () => {
  const classes = useStyles();
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  // 비밀번호 변경 처리 함수
  const handlePasswordChange = async () => {
    if (newPassword !== confirmPassword) {
      setErrorMessage("새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다.");
      return;
    }

    try {
      const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
      const response = await fetch(
        "http://localhost:8080/members/changePassword",
        {
          method: "PATCH",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${loginInfo.accessToken}`,
          },
          body: JSON.stringify({
            oldPassword,
            newPassword
          }),
        });

      if (response.ok) {
        alert("비밀번호가 성공적으로 변경되었습니다.");
        setOldPassword("");
        setNewPassword("");
        setConfirmPassword("");
        setErrorMessage("");
        window.location.href = "http://localhost:3000/mypage";
      } else {
        throw new error;
      }
    } catch (error) {
      setErrorMessage("비밀번호 변경에 실패했습니다. 다시 시도해주세요.");
    }
  };

  return (
    <Container className={classes.container}>
      <Typography variant="h6">비밀번호 변경</Typography>
      <form className={classes.form} noValidate autoComplete="off">
        <TextField
          label="현재 비밀번호"
          variant="outlined"
          type="password"
          value={oldPassword}
          onChange={(e) => setOldPassword(e.target.value)}
          fullWidth
          required
          margin="normal"
        />
        <TextField
          label="새 비밀번호"
          variant="outlined"
          type="password"
          value={newPassword}
          onChange={(e) => setNewPassword(e.target.value)}
          fullWidth
          required
          margin="normal"
        />
        <TextField
          label="새 비밀번호 확인"
          variant="outlined"
          type="password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          fullWidth
          required
          margin="normal"
        />
        {errorMessage && (
          <Typography color="error" variant="body2" style={{ marginTop: "10px" }}>
            {errorMessage}
          </Typography>
        )}
        <Button
          variant="contained"
          color="primary"
          className={classes.button}
          onClick={handlePasswordChange}
        >
          비밀번호 변경
        </Button>
      </form>
    </Container>
  );
};

export default PasswordChange;
