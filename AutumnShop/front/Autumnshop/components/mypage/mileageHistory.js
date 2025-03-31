import React, { useEffect, useState } from "react";
import { makeStyles } from "@mui/styles";

// CSS 적용
const useStyles = makeStyles(() => ({
    tableContainer: {
        width: "80%",
        margin: "20px auto",
        padding: "30px",
        border: "3px solid #000",
        borderRadius: "8px",
        backgroundColor: "#fff",
        color: "#000",
        boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
        textAlign: "center",
    },
    table: {
        width: "100%",
        borderCollapse: "collapse",
        fontSize: "16px",
    },
    tableHeader: {
        backgroundColor: "#f4f4f4",
        color: "#000",
        textAlign: "center",
        fontWeight: "bold",
        borderBottom: "3px solid #000",
        padding: "15px",
    },
    tableCell: {
        padding: "16px",
        borderBottom: "2px solid #000",
        textAlign: "center",
        fontSize: "16px",
    },
    tableRow: {
        "&:hover": {
            backgroundColor: "#f1f1f1",
        },
    },
    totalContainer: {
        marginTop: "30px",
        padding: "15px",
        backgroundColor: "#f4f4f4",
        borderRadius: "8px",
        fontSize: "20px",
        fontWeight: "bold",
        textAlign: "center",
        color: "#000",
        border: "2px solid #000",
    },
    paginationContainer: {
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        marginTop: "30px",
        gap: "15px",
    },
    paginationButton: {
        padding: "10px 20px",
        border: "2px solid #000",
        borderRadius: "4px",
        backgroundColor: "#fff",
        cursor: "pointer",
        color: "#000",
        fontWeight: "bold",
        "&:disabled": {
            backgroundColor: "#e0e0e0",
            cursor: "not-allowed",
        },
        "&:hover": {
            backgroundColor: "#f1f1f1",
        },
    },
    currentPage: {
        fontWeight: "bold",
        color: "#000",
        fontSize: "18px",
    },
}));

// 현재 페이지에 따라서 마일리지 내역 출력 (size: 10)
async function getMileageHistory(setMileageHistory, page, setTotalPages) {
    try {
        const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
        const getHistoryResponse = await fetch(
            `${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}mileage/history?page=${page}&size=10`,
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
        const memberInfo = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}members/info`, {
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
            <h2>마일리지 히스토리</h2>
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
