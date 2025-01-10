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
import axios from "axios";
import AddressSearch from "../addressSearch";

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

const myWriteForm = () => {
  const classes = useStyles();

  async function getMember(){
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
    const getMemberResponse = await axios.get(
      "http://localhost:8080/members/info",
      {
        headers: {
          Authorization: `Bearer ${loginInfo.accessToken}`,
        },
      }
    );
    const data = getMemberResponse.data;
    console.log(data);
    setName(data.name);
    setEmail(data.email);
    setGender(data.gender);
    setPhone(data.phone);
    setZipCode(data.zipCode);
    setRoadAddress(data.roadAddress);
    setDetailAddress(data.detailAddress);

      // 생년월일 YYYY-MM-DD 형식으로 변환
    setBirthDate(`${data.birthYear}-${String(data.birthMonth).padStart(2, "0")}-${String(data.birthDay).padStart(2, "0")}`);
  }

  useEffect(() => {
    getMember();
  }, [])

  // 상태 설정 추가
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  const [gender, setGender] = useState("");
  const [phone, setPhone] = useState("");
  const [zipCode, setZipCode] = useState("");
  const [roadAddress, setRoadAddress] = useState("");
  const [detailAddress, setDetailAddress] = useState("");
  const [birthDate, setBirthDate] = useState("");

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
      console.log(memberSignupDto);
      const response = await axios.patch(
        "http://localhost:8080/members/write",
        memberSignupDto
      );
      if (response.status === 200 || response.status == 201) {
        console.log(memberSignupDto);
        window.location.href = "http://localhost:3000/mypage";
      }
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <Container maxWidth="sm" className={classes.container} component="main">
      <Typography variant="h4" component="h1" gutterBottom>
        내 정보
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
          InputProps={{ readOnly: true }}
        />
        <TextField
          label="이메일"
          type="email"
          variant="outlined"
          margin="normal"
          fullWidth
          required
          value={email}
          InputProps={{ readOnly: true }}
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
        setDetailAddress={setDetailAddress}/>


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

export default myWriteForm;
