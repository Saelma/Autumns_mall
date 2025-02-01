import React, { useState } from "react";
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

    const phonePattern = /^(010|031|032)-\d{3,4}-\d{4}$/;
    if (!phonePattern.test(phone)) {
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
        alert("회원가입에 성공했습니다!");
      } else {
        throw new error;
      }
    } catch (error) {
      alert("회원가입에 실패했습니다. 다시 정보를 확인해주세요.");
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
          variant="outlined"
          fullWidth
          required
          value={name}
          onChange={(e) => setName(e.target.value)}
          className={classes.textField}
        />
        <TextField
          label="이메일"
          type="email"
          variant="outlined"
          fullWidth
          required
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className={classes.textField}
        />
        <TextField
          label="암호"
          type="password"
          variant="outlined"
          fullWidth
          required
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className={classes.textField}
        />
        <TextField
          label="생년월일"
          type="date"
          variant="outlined"
          fullWidth
          InputLabelProps={{ shrink: true }}
          required
          value={birthDate}
          onChange={(e) => setBirthDate(e.target.value)}
          className={classes.textField}
        />

        <FormControl fullWidth required margin="normal">
          <InputLabel>성별</InputLabel>
          <Select value={gender} onChange={handleChange} label="성별">
            <MenuItem value="M">남성</MenuItem>
            <MenuItem value="F">여성</MenuItem>
          </Select>
        </FormControl>

        <TextField
          label="전화번호"
          placeholder="010-0000-0000"
          variant="outlined"
          fullWidth
          required
          value={phone}
          onChange={(e) => setPhone(e.target.value)}
          helperText="형식: 010-0000-0000"
          className={classes.textField}
        />

        <AddressSearch
          setRoadAddress={setRoadAddress}
          setZipCode={setZipCode}
          setDetailAddress={setDetailAddress}
          roadAddress={roadAddress}
          zipCode={zipCode}
          detailAdd={detailAddress}
        />

        <Button type="submit" variant="contained" className={classes.button} fullWidth>
          회원가입
        </Button>
      </Box>
    </Container>
  );
};

export default JoinForm;
