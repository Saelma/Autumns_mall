import React, { useEffect, useState } from "react";
import { useRouter } from 'next/router'; // useRouter 훅을 사용하여 URL 파라미터를 추출
import Link from "next/link";
import { makeStyles } from "@mui/styles";
import { Box, Typography, Button } from "@mui/material";

// 스타일 정의
const useStyles = makeStyles(() => ({
  container: {
    width: "600px",
    margin: "50px auto",
    padding: "30px",
    border: "4px solid #000",
    borderRadius: "15px",
    backgroundColor: "#fff",
    boxShadow: "5px 5px 15px rgba(0, 0, 0, 0.2)",
    textAlign: "center",
  },
  title: {
    fontSize: "28px",
    fontWeight: "bold",
    marginBottom: "20px",
  },
  text: {
    fontSize: "18px",
    marginBottom: "12px",
    textAlign: "left",
    lineHeight: "1.6",
  },
  button: {
    marginTop: "20px",
    padding: "12px 20px",
    fontSize: "18px",
    fontWeight: "bold",
    color: "#fff",
    backgroundColor: "#000",
    border: "3px solid #000",
    borderRadius: "8px",
    cursor: "pointer",
    transition: "background-color 0.3s ease, transform 0.2s ease",

    "&:hover": {
      backgroundColor: "#444",
      transform: "scale(1.05)",
    },

    "&:active": {
      backgroundColor: "#222",
      transform: "scale(0.95)",
    },
  },
  productImage: {
    width: "500px",
    height: "700px",
    objectFit: "cover",
    marginTop: "20px",
  },
}));

const ReportDetailPage = () => {
    const router = useRouter(); // useRouter 훅 사용
    const { id } = router.query; // URL 파라미터에서 'id' 추출
    const [isAdmin, setIsAdmin] = useState(false);
    const [report, setReport] = useState(null);
    const classes = useStyles();

    useEffect(() => {
        const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
        const getUserInfo = async () => {
            if (!loginInfo || !loginInfo.accessToken) {
                window.location.href = "/login";
                return;
            }

            // 로그인 한 사용자의 권한이 관리자(ADMIN)인지 확인
            try {
                const response = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}members/info`, {
                    method: "GET",
                    headers: {
                        Authorization: `Bearer ${loginInfo.accessToken}`,
                    },
                });

                if (!response.ok) {
                    throw new error;
                }

                const data = await response.json();

                if (data.roles.some(role => role.name === "ROLE_ADMIN")) {
                    setIsAdmin(true);
                } else {
                    setIsAdmin(false);
                }

                if (data.roles[0].name !== "ROLE_ADMIN") {
                    throw error;
                }
            } catch (error) {
                console.error(error);
            }
        };

        getUserInfo();

        if (!id) return; // id가 없는 경우는 요청하지 않음

        const fetchReport = async () => {
            try {
                const response = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}report/${id}`, {
                    method: "GET",
                    headers: {
                        Authorization: `Bearer ${loginInfo.accessToken}`,
                    },
                });
                const data = await response.json();
                setReport(data);
            } catch (error) {
                console.error("Error fetching report", error);
            }
        };

        fetchReport();
    }, [id]);

    if (!report) return <div>로딩중...</div>;

    return (
        <Box className={classes.container}>
            <Typography variant="h4" className={classes.title}>
                신고 내용
            </Typography>
            <div className={classes.text}>
                <strong>물품 ID:</strong> {report.product.id}
            </div>
            <div className={classes.text}>
                <strong>물품 이름: </strong> {report.product.title}
            </div>
            <div className={classes.text}>
                <img
                    src={report.product.imageUrl}
                    alt="상품 이미지"
                    className={classes.productImage}
                />
            </div>
            <div className={classes.text}>
                <strong>신고 이유:</strong> {report.reason}
            </div>
            <div className={classes.text}>
                <strong>신고자:</strong> {report.member.email}
            </div>
            <div className={classes.text}>
                <strong>상세 내용:</strong> {report.content}
            </div>
            <Link href="/mypage">
                <Button className={classes.button}>돌아가기</Button>
            </Link>
        </Box>
    );
};

export default ReportDetailPage;
