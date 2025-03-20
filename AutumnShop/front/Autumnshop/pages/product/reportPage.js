import React, { useState } from "react";
import { TextField, Button, Box, Typography } from "@mui/material";
import { useRouter } from "next/router";
import { makeStyles } from "@mui/styles";

const useStyles = makeStyles((theme) => ({
  container: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    height: "100vh",
    padding: theme.spacing(3),
    margin: '0 auto',
  },
  formBox: {
    width: 400,
    textAlign: "center",
  },
  button: {
    width: "100%",
  },
}));

const ReportPage = () => {
  const [reason, setReason] = useState("");
  const [content, setContent] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const router = useRouter();
  const { productId } = router.query;
  const classes = useStyles();

  const handleReportSubmit = async () => {
    if (!reason || !content) {
      alert("신고 사유와 상세 내용을 입력해주세요.");
      return;
    }

    setIsSubmitting(true);

    try {
      const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
      if (!loginInfo) {
        alert("로그인이 필요합니다.");
        return;
      }

      const response = await fetch(`http://localhost:8080/report`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${loginInfo.accessToken}`,
        },
        body: JSON.stringify({
          productId: productId,
          reason: reason,
          content: content,
        }),
      });

      if (!response.ok) {
        throw new Error("신고를 처리할 수 없습니다.");
      }

      alert("신고가 접수되었습니다.");
      router.push(`/product/${productId}`);
    } catch (error) {
      console.error("신고 처리 중 오류가 발생했습니다.", error);
      alert("신고 처리에 실패했습니다.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Box className={classes.container}>
      <Box className={classes.formBox}>
        <Typography variant="h4" gutterBottom>
          상품평 신고
        </Typography>
        <TextField
          label="신고 사유"
          fullWidth
          variant="outlined"
          value={reason}
          onChange={(e) => setReason(e.target.value)}
          sx={{ marginBottom: 2 }}
        />
        <TextField
          label="상세 내용"
          fullWidth
          variant="outlined"
          multiline
          rows={4}
          value={content}
          onChange={(e) => setContent(e.target.value)}
          sx={{ marginBottom: 2 }}
        />
        <Button
          variant="contained"
          color="error"
          onClick={handleReportSubmit}
          disabled={isSubmitting}
          className={classes.button}
        >
          신고하기
        </Button>
      </Box>
    </Box>
  );
};

export default ReportPage;
