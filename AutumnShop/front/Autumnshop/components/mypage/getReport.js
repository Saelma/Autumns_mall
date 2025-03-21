import React, { useEffect, useState } from "react";
import Link from "next/link";
import { makeStyles } from "@mui/styles";

const useStyles = makeStyles({
    container: {
        display: "flex",
        width: "100%",
        height: "100%",
        flexDirection: "column",
        padding: "20px",
        backgroundColor: "#fff",
        color: "#000",
        margin: "0 auto"
    },
    reportBox: {
        display: "flex",
        width: "80%",
        minHeight: "150px",
        border: "2px solid black",
        borderRadius: "8px",
        padding: "10px",
        marginBottom: "15px",
    },
    leftSection: {
        width: "30%",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
    },
    productImage: {
        width: "150px",
        height: "300px",
        objectFit: "cover",
        borderRadius: "5px",
        marginBottom: "5px",
    },
    rightSection: {
        width: "70%",
        display: "flex",
        flexDirection: "column",
        paddingLeft: "15px",
    },
    reason: {
        fontWeight: "bold",
        fontSize: "25px",
        cursor: "pointer",
        textDecoration: "underline",
        marginBottom: "10px",
        transition: "all 0.3s ease",
        "&:hover": {
            fontSize: "30px",
        },
    },
    memberInfo: {
        fontSize: "14px",
        color: "#666",
        marginBottom: "10px",
    },
    reportContent: {
        fontSize: "16px",
    },
    button: {
        padding: "10px 15px",
        border: "2px solid black",
        backgroundColor: "white",
        cursor: "pointer",
        fontSize: "14px",
        '&:hover': {
            backgroundColor: "gray",
        },
        alignSelf: "flex-start",  // 버튼을 왼쪽에 정렬
        marginTop: "auto",  // 맨 아래로 배치
    }
});

const ReportPage = () => {
    const [reports, setReports] = useState([]);
    const [isAdmin, setIsAdmin] = useState(false);
    const classes = useStyles();

    useEffect(() => {
        const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
        if (!loginInfo || !loginInfo.accessToken) {
            window.location.href = "/login";
            return;
        }

        const getUserInfo = async () => {
            try {
                const response = await fetch("http://localhost:8080/members/info", {
                    method: "GET",
                    headers: { Authorization: `Bearer ${loginInfo.accessToken}` },
                });
                if (!response.ok) throw new Error();
                const data = await response.json();
                setIsAdmin(data.roles.some(role => role.name === "ROLE_ADMIN"));
            } catch (error) {
                window.location.href = "/welcome";
            }
        };

        const fetchReports = async () => {
            try {
                const response = await fetch("http://localhost:8080/report", {
                    method: "GET",
                    headers: { Authorization: `Bearer ${loginInfo.accessToken}` },
                });
                if (!response.ok) throw new Error();
                const data = await response.json();
                setReports(data);
            } catch (error) {
                console.error("Error fetching reports", error);
            }
        };

        getUserInfo();
        fetchReports();
    }, []);

    if (!isAdmin) return null;

    return (
        <div className={classes.container}>
            {reports.map((report) => (
                <div key={report.id} className={classes.reportBox}>
                    <div className={classes.leftSection}>
                        <img src={report.product.imageUrl} alt="상품 이미지" className={classes.productImage} />
                        <div>{report.product.title}</div>
                        <div>ID: {report.product.id}</div>
                    </div>
                    <div className={classes.rightSection}>
                        <Link 
                        href={`/mypage/report/${report.id}`}
                        style={{ textDecoration: "none", color: "black" }}
                        className={classes.reason}>
                            {report.reason}
                        </Link>
                        <div className={classes.memberInfo}>신고자: {report.member.email}</div>
                        <button 
                            className={classes.button}
                            onClick={() => window.location.href = `/product/${report.product.id}`}
                        >바로가기</button>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default ReportPage;
