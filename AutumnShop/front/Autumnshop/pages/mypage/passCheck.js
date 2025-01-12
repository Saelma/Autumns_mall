import React, { useState } from "react";
import {
    Container,
    Typography,
    TextField,
    Button,
  } from "@mui/material";
import { makeStyles } from "@mui/styles";
import axios from "axios";

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

const passCheck = () => {
    const classes = useStyles();

    const [password, setPassword] = useState("");
    const [errorMessage, setErrorMessage] = useState("");

      const passwordCheck = async () => {
        try{
            const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
             const response = await axios.post(
                 "http://localhost:8080/members/checkPassword",
                 {
                     password
                 },
                 {
                    headers: {
                        Authorization: `Bearer ${loginInfo.accessToken}`,
                    },
                 }
             );
             if(response.data){
                window.location.href = "http://localhost:3000/mypage/myWrite";
             }else{
                setErrorMessage("비밀번호가 일치하지 않습니다.");
             }
        }catch (error){
            setErrorMessage("비밀번호가 일치하지 않습니다.");
        }
      }

    return (
    <Container className={classes.container}>
      <Typography variant="h6">비밀번호를 입력해주세요.</Typography>
      <form className={classes.form} noValidate autoComplete="off">
        <TextField
          label="현재 비밀번호"
          variant="outlined"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
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
          onClick={passwordCheck}
        >
          비밀번호 확인
        </Button>
      </form>
    </Container>
    )

}

export default passCheck;