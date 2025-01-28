import React, { useState, useEffect } from "react";
import {
  Container,
  Box,
  Typography,
  TextField,
  Button,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from "@mui/material";
import { makeStyles } from "@mui/styles";
import AddressSearch from "./addressSearch";

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
}));

const JoinForm = () => {
  const classes = useStyles();

  // 상태 설정 추가
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  const [birthDate, setBirthDate] = useState("");
  const [gender, setGender] = useState("");
  const [phone, setPhone] = useState("");
  const [zipCode, setZipCode] = useState("");
  const [roadAddress, setRoadAddress] = useState("");
  const [detailAddress, setDetailAddress] = useState("");

  // 성별 선택
  const handleChange = (event) => {
    setGender(event.target.value);
  };

  // 회원가입 제출
  const handleSubmit = async (event) => {
    event.preventDefault();

    // 전화번호 형식 확인
    const phonePattern = /^(010|031|032)-\d{3,4}-\d{4}$/;
    if(!phonePattern.test(phone)){
      alert("전화번호 형식이 올바르지 않습니다!");
      return;
    }
    if (!detailAddress || detailAddress.trim() === "") {
      alert("상세 주소를 입력해주세요!");
      return;
    }
    // 생년/월/일별로 분리 
    const [birthYear, birthMonth, birthDay] = birthDate.split("-");

    const memberSignupDto = {
      email,
      password,
      name,
      birthYear,
      birthMonth,
      birthDay,
      gender,
      phone,
      roadAddress,
      zipCode,
      detailAddress,
    };

    try {
      const response = await fetch("http://localhost:8080/members/signup", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(memberSignupDto),
      });
    
      if (response.ok) { // 200 또는 201 상태 코드가 반환되면
        window.location.href = "http://localhost:3000/welcome";
      } else {
        throw new Error("회원가입에 실패했습니다.");
      }
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <Container maxWidth="sm" className={classes.container} component="main">
      <Typography variant="h4" component="h1" gutterBottom>
        회원가입
      </Typography>
      <Box component="form" className={classes.form} onSubmit={handleSubmit}>
        <TextField
          label="회원이름"
          type="text"
          variant="outlined"
          margin="normal"
          fullWidth
          required
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
        <TextField
          label="이메일"
          type="email"
          variant="outlined"
          margin="normal"
          fullWidth
          required
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <TextField
          label="암호"
          type="password"
          variant="outlined"
          margin="normal"
          fullWidth
          required
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <TextField
          label="생년월일"
          type="date"
          variant="outlined"
          margin="normal"
          fullWidth
          InputLabelProps={{
            shrink: true,
          }}
          required
          value={birthDate}
          onChange={(e) => setBirthDate(e.target.value)}
        />

        <FormControl fullWidth variant="outlined" margin="normal" required>
          <InputLabel>성별</InputLabel>
          <Select value={gender} onChange={handleChange} label="성별">
            <MenuItem value="M">남성</MenuItem>
            <MenuItem value="F">여성</MenuItem>
          </Select>
        </FormControl>
        <TextField
        label="전화번호"
        type="text"
        variant="outlined"
        margin="normal"
        fullWidth
        required
        placeholder="010-0000-0000"
        value={phone}
        onChange={(e) => setPhone(e.target.value)}
        inputProps={{
          pattern: "^(010|031|032)-\\d{3,4}-\\d{4}$",
        }}
        helperText="형식: 010-0000-0000"
      />

        <AddressSearch 
        setRoadAddress={setRoadAddress}
        setZipCode={setZipCode}
        setDetailAddress={setDetailAddress}
        roadAddress={roadAddress}
        zipCode={zipCode}
        detailAdd={detailAddress}/>


        <Button
          type="submit"
          variant="contained"
          color="primary"
          size="large"
          fullWidth
        >
          회원가입
        </Button>
      </Box>
    </Container>
  );
};

export default JoinForm;
