import React, { useEffect, useState } from "react";
import Link from "next/link";
import { makeStyles } from "@mui/styles";
import { Notifications } from '@mui/icons-material';

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
    },
    paginationContainer: {
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        marginTop: "20px",
    },
    paginationButton: {
        padding: "8px 15px",
        border: "2px solid #000",
        borderRadius: "4px",
        backgroundColor: "#fff",
        cursor: "pointer",
        color: "#000",
        fontWeight: "bold",
        fontSize: "14px",
        "&:disabled": {
            backgroundColor: "#e0e0e0",
            cursor: "not-allowed",
        },
        "&:hover:not(:disabled)": {
            backgroundColor: "#f1f1f1",
        },
    },
    currentPage: {
        fontWeight: "bold",
        fontSize: "16px",
        margin: "0 10px",
    },
    notificationIcon: {
        fontSize: "1.5rem",
        marginLeft: "10px",
        color: "red",
    },
});

const ReportPage = () => {
    const [reports, setReports] = useState([]);
    const [isAdmin, setIsAdmin] = useState(false);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
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
                const response = await fetch(`http://localhost:8080/report?page=${page}`, {
                    method: "GET",
                    headers: { Authorization: `Bearer ${loginInfo.accessToken}` },
                });
                if (!response.ok) throw new Error();
                const data = await response.json();
                setReports(data.content);
                setTotalPages(data.totalPages);
            } catch (error) {
                console.error("Error fetching reports", error);
            }
        };


        getUserInfo();
        fetchReports();
    }, [page]);

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
                        {report.seen === false && (
                            <Notifications className={classes.notificationIcon} />
                        )}
                        <button 
                            className={classes.button}
                            onClick={() => window.location.href = `/product/${report.product.id}`}
                        >바로가기</button>
                    </div>
                </div>
            ))}

            {/* 페이지네이션 UI */}
            <div className={classes.paginationContainer}>
                <button
                    className={classes.paginationButton}
                    onClick={() => setPage(0)}
                    disabled={page === 0}
                >
                    첫페이지
                </button>
                <button
                    className={classes.paginationButton}
                    onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
                    disabled={page === 0}
                >
                    이전
                </button>
                <span className={classes.currentPage}>
                    {page + 1} / {totalPages}
                </span>
                <button
                    className={classes.paginationButton}
                    onClick={() => setPage((prev) => Math.min(prev + 1, totalPages - 1))}
                    disabled={page === totalPages - 1}
                >
                    다음
                </button>
                <button
                    className={classes.paginationButton}
                    onClick={() => setPage(totalPages - 1)}
                    disabled={page === totalPages - 1}
                >
                    마지막페이지
                </button>
            </div>
        </div>
    );
};

export default ReportPage;
