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
import AddressSearch from "../components/addressSearch/addressSearch";

const useStyles = makeStyles((theme) => ({
  container: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    marginTop: "40px",
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
    marginBottom: theme.spacing(2),
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
  const [verificationCode, setVerificationCode] = useState(""); // 인증 코드 상태
  const [message, setMessage] = useState(""); // 상태 메시지
  const [isEmailSent, setIsEmailSent] = useState(false); // 이메일 인증 코드 전송 여부
  const [isEmailVerified, setIsEmailVerified] = useState(false); // 이메일 인증 상태
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  const [birthDate, setBirthDate] = useState("");
  const [gender, setGender] = useState("");
  const [phone, setPhone] = useState("");
  const [zipCode, setZipCode] = useState("");
  const [roadAddress, setRoadAddress] = useState("");
  const [detailAddress, setDetailAddress] = useState("");
  const [isAdmin, setIsAdmin] = useState(false);

  // 네이버 로그인으로 회원가입 시
  useEffect(() => {
    console.log(process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS)
    const hashParams = new URLSearchParams(window.location.hash.replace('#', '?'));
    const accessToken = hashParams.get('access_token');
    
    if (accessToken) {
      fetchNaverUserData(accessToken);
    }
  }, []);

  const fetchNaverUserData = async (accessToken) => {
    try {
      const response = await fetch(`/api/naver/user?accessToken=${accessToken}`);
      const data = await response.json();
      console.log(data.response);
      setBirthDate(data.response.birthyear+ "-" + data.response.birthday);
      setGender(data.response.gender);
      setEmail(data.response.email);
      setName(data.response.name);
      setPhone(data.response.mobile);
    } catch (error) {
      console.error("네이버 유저 정보를 불러오는 데 실패했습니다!", error);
    }
  };


  // 성별 선택
  const handleChange = (event) => {
    setGender(event.target.value);
  };

  // 이메일 입력값 처리
  const handleEmailChange = (event) => {
    setEmail(event.target.value);
  };

    // 인증 코드 입력값 처리
    const handleVerificationCodeChange = (event) => {
      setVerificationCode(event.target.value);
    };

    // 이메일 인증 코드 요청 함수
    const handleSendVerificationEmail = async () => {
      if (!email) {
        setMessage("이메일을 입력해주세요.");
        return;
      }
      
      if (!email.includes("@naver.com")) {
        setMessage("네이버 이메일 및 '@'를 포함해야 합니다.");
        return;
      }
    
  
      try {
        const response = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}email/sendEmail`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ email: email }), 
        });
        console.log(response);
  
        if (response.ok) {
          setMessage("인증 코드가 이메일로 전송되었습니다.");
          setIsEmailSent(true); // 인증 코드 전송 완료 표시
        } else {
          const data = await response.text();
          setMessage(`Error: 이메일이 중복되거나 비어있지 않아야 합니다.`);
        }
      } catch (error) {
        console.error("Error:", error);
        setMessage("이메일 인증 코드 전송에 실패했습니다.");
      }
    };
  
    // 이메일 인증 코드 검증 함수
    const handleVerifyEmailCode = async () => {
      
      try {
        const response = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}email/verify`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
           email: email,
            code: verificationCode,
          }),
        });
  
        if (response.ok) {
          setMessage("인증 코드가 확인되었습니다. 회원가입을 진행하세요.");
          setIsEmailVerified(true); // 인증 완료 시 true로 설정
        } else {
          const data = await response.text();
          setMessage(`Error: 인증 코드가 비어있지 않아야 합니다.`);
          setIsEmailVerified(false); // 인증 실패 시 false로 설정
        }
      } catch (error) {
        console.error("Error:", error);
        setMessage("인증 코드 검증에 실패했습니다.");
        setIsEmailVerified(false); // 인증 실패 시 false로 설정
      }
    };

    const handleAdminToggle = (event) => {
      setIsAdmin(prev => {
        const newAdminStatus = !prev;
        alert(`관리자 권한이 ${newAdminStatus ? "활성화" : "비활성화"} 되었습니다. `);
        return newAdminStatus;
      })
    }
  

  // 회원가입 제출
  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!isEmailVerified) {
      alert("이메일 인증을 먼저 완료해주세요.");
      return;
    }

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
      isAdmin,
    };

    try {
      const response = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}members/signup`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(memberSignupDto),
      });

      if (response.ok) { // 200 또는 201 상태 코드가 반환되면
        window.location.href = `${process.env.NEXT_PUBLIC_AUTUMNMALL_VERCEL_ADDRESS}welcome`;
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
      {/* 이메일 입력 */}
      <TextField
        label="이메일 (네이버 메일)"
        variant="outlined"
        fullWidth
        required
        value={email}
        onChange={handleEmailChange}
        helperText="'네이버' 이메일을 입력하세요 (@을 포함해야 합니다)"
      />

      {/* 인증 코드 전송 버튼 */}
      <Button
        variant="contained"
        color="primary"
        onClick={handleSendVerificationEmail}
        disabled={isEmailSent}
        className={classes.button}
      >
        인증 코드 전송
      </Button>

      {/* 인증 코드 입력 */}
      {isEmailSent && (
        <div>
          <TextField
            label="인증 코드"
            variant="outlined"
            fullWidth
            required
            value={verificationCode}
            onChange={handleVerificationCodeChange}
            helperText="이메일로 받은 인증 코드를 입력하세요."
          />
          <Button
            variant="contained"
            color="primary"
            onClick={handleVerifyEmailCode}
            className={classes.button}
          >
            인증 코드 확인
          </Button>
        </div>
      )}

      {/* 메시지 출력 */}
      {message && <Typography color="error">{message}</Typography>}
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
        
        <Button
            variant="contained"
            color="primary"
            onClick={handleAdminToggle}
            className={classes.button}
          >
          관리자로 가입    
        </Button>

        <Button type="submit" variant="contained" className={classes.button} fullWidth>
          회원가입
        </Button>
      </Box>
    </Container>
  );
};

export default JoinForm;
