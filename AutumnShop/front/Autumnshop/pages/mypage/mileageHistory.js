import React, { useEffect, useState } from "react";
import { makeStyles } from "@mui/styles";

// CSS 적용
const useStyles = makeStyles(() => ({
    tableContainer: {
        width: "80%",
        margin: "20px auto",
        padding: "20px",
        border: "1px solid #ccc",
        borderRadius: "8px",
        backgroundColor: "#f8f8f8",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
    },
    table: {
        width: "100%",
        borderCollapse: "collapse",
    },
    tableHeader: {
        backgroundColor: "#3f51b5",
        color: "#fff",
        textAlign: "center",
        fontWeight: "bold",
    },
    tableCell: {
        padding: "12px",
        borderBottom: "1px solid #ddd",
        textAlign: "center",
    },
    tableRow: {
        "&:hover": {
            backgroundColor: "#f1f1f1",
        },
    },
    totalContainer: {
        marginTop: "20px",
        padding: "10px",
        backgroundColor: "#e1f5fe",
        borderRadius: "8px",
        fontSize: "18px",
        fontWeight: "bold",
        textAlign: "center",
    },
    paginationContainer: {
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        marginTop: "20px",
        gap: "10px",
    },
    paginationButton: {
        padding: "8px 16px",
        border: "1px solid #3f51b5",
        borderRadius: "4px",
        backgroundColor: "#fff",
        cursor: "pointer",
        color: "#3f51b5",
        fontWeight: "bold",
        "&:disabled": {
            backgroundColor: "#e0e0e0",
            cursor: "not-allowed",
        },
    },
    currentPage: {
        fontWeight: "bold",
    },
}));


// 현재 페이지에 따라서 마일리지 내역 출력 (size : 10)
async function getMileageHistory(setMileageHistory, page, setTotalPages) {
    try {
        const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
        const getHistoryResponse = await fetch(
            `http://localhost:8080/mileage/history?page=${page}&size=10`,
            {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${loginInfo.accessToken}`,
                },
            }
        );

        if(!getHistoryResponse.ok){
            throw new error;
        }


        const data = await getHistoryResponse.json();
        setMileageHistory(data.content);
        setTotalPages(data.totalPages);
    } catch (error) {
        console.error("마일리지 내역을 불러오지 못했습니다:", error);
    }
}

// 멤버 정보를 불러오는 함수 (총 마일리지를 불러오기 위함)
async function memberInfo(setTotalMileage) {
    try {
        const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
        const memberInfo = await fetch(`http://localhost:8080/members/info`, {
            method: "GET",
            headers: {
                Authorization: `Bearer ${loginInfo.accessToken}`,
            },
        });

        if(!memberInfo.ok){
            throw new error;
        }

        const data = await memberInfo.json();
        setTotalMileage(data.totalMileage);
    } catch (error) {
        console.error("멤버 정보를 불러오지 못했습니다:", error);
    }
}

const mileageHistory = () => {
    const classes = useStyles();
    const [mileageHistory, setMileageHistory] = useState([]);
    const [totalMileage, setTotalMileage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [currentPage, setCurrentPage] = useState(0);


    // 2. useEffect를 통해 마일리지 내역, 총 마일리지를 불러오며
    // 현재페이지가 바뀔 때 마다 새 마일리지 내역을 불러오는 형식
    // 아래의 PageChange 함수를 통해 page가 바뀔 때 마다 내역을 불러옴
    useEffect(() => {
        getMileageHistory(setMileageHistory, currentPage, setTotalPages);
        memberInfo(setTotalMileage);
    }, [currentPage]);

    // 1. 페이지가 0이상, 총 페이지 이하일 경우, 즉
    // 페이지 범위 내에서만 현재 페이지를 이동할 수 있음 
    const PageChange = (page) => {
        if (page >= 0 && page < totalPages) {
            setCurrentPage(page);
        }
    };

    return (
        <div className={classes.tableContainer}>
            <h3>마일리지 히스토리</h3>
            <table className={classes.table}>
                <thead>
                    <tr className={classes.tableHeader}>
                        <th className={classes.tableCell}>날짜</th>
                        <th className={classes.tableCell}>유형</th>
                        <th className={classes.tableCell}>상세내역</th>
                        <th className={classes.tableCell}>마일리지</th>
                    </tr>
                </thead>
                <tbody>
                    {mileageHistory.length > 0 ? (
                        mileageHistory.map((mileage) => (
                            <tr key={mileage.id} className={classes.tableRow}>
                                <td className={classes.tableCell}>{mileage.date}</td>
                                <td className={classes.tableCell}>
                                    {mileage.type === "ADD" && "적립"}
                                    {mileage.type === "MINUS" && "사용"}
                                    {mileage.type === "EXPIRATION" && "소멸"}
                                </td>
                                <td className={classes.tableCell}>{mileage.description}
                                </td>
                                <td className={classes.tableCell}>
                                    {mileage.amount.toLocaleString()}원
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan={4} className={classes.tableCell}>
                                마일리지 내역이 없습니다.
                            </td>
                        </tr>
                    )}
                </tbody>
            </table>
            <div className={classes.totalContainer}>
                총 마일리지 : {totalMileage.toLocaleString()}원
            </div>
            <div className={classes.paginationContainer}>
                <button
                    className={classes.paginationButton}
                    onClick={() => PageChange(0)}
                    // Page가 0일 경우 작동하지 않음
                    disabled={currentPage === 0}
                >
                    처음
                </button>
                <button
                    className={classes.paginationButton}
                    onClick={() => PageChange(currentPage - 1)}
                    // Page가 0일 경우 작동하지 않음
                    disabled={currentPage === 0}
                >
                    이전
                </button>
                <span className={classes.currentPage}>
                    {currentPage + 1} / {totalPages}
                </span>
                <button
                    className={classes.paginationButton}
                    onClick={() => PageChange(currentPage + 1)}
                    // Page가 끝 페이지일 경우 작동하지 않음
                    disabled={currentPage === totalPages - 1}
                >
                    다음
                </button>
                <button
                    className={classes.paginationButton}
                    onClick={() => PageChange(totalPages - 1)}
                    // Page가 끝 페이지일 경우 작동하지 않음
                    disabled={currentPage === totalPages - 1}
                >
                    마지막
                </button>
            </div>
        </div>
    );
};

export default mileageHistory;
