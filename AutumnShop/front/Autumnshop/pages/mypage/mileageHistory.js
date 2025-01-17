import React from "react";
import { useEffect, useState } from "react";
import { makeStyles } from "@mui/styles";
import axios from "axios";

// CSS 적용
const useStyles = makeStyles((theme) => ({
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
      borderCollapse: "collapse", // 테이블 내부 간격 없애기
    },
    tableHeader: {
      backgroundColor: "#3f51b5", 
      color: "#fff", 
      textAlign: "center", 
      fontWeight: "bold", 
    },
    tableCell: {
      padding: "12px", // 셀 내부 패딩
      borderBottom: "1px solid #ddd", // 셀 경계선
      textAlign: "center",
    },
    tableRow: {
      "&:hover": {
        backgroundColor: "#f1f1f1", 
      },
    },
    totalContainer: {
        marginTop: "20px", // 테이블 아래 여백
        padding: "10px",
        backgroundColor: "#e1f5fe", // 배경색 추가
        borderRadius: "8px", // 테두리 둥글게
        fontSize: "18px", // 폰트 크기
        fontWeight: "bold", // 폰트 두껍게
        textAlign: "center", // 가운데 정렬
    },
  }));

  // 마일리지 내역 불러오기
async function getMileageHistory(setMileageHistory) {
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
    const getHistoryResponse = await axios
    .get(`http://localhost:8080/mileage/history`,
        {
            headers:{
                Authorization: `Bearer ${loginInfo.accessToken}`,
            },
        });
        setMileageHistory(getHistoryResponse.data);
}

async function memberInfo(setTotalMileage){
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
    const memberInfo = await axios.get(`http://localhost:8080/members/info`,
        {
            headers: {
                Authorization: `Bearer ${loginInfo.accessToken}`
            }
        }
    )
    setTotalMileage(memberInfo.data.totalMileage);
}


const mileageHistory = () => {
    const classes = useStyles();
    const [mileageHistory, setMileageHistory] = useState([]);
    const [totalMileage, setTotalMileage] = useState(0);


    useEffect(() => {
        getMileageHistory(setMileageHistory);
        memberInfo(setTotalMileage);
    }, [])

    return(
        <div className={classes.tableContainer}>
        <h3>마일리지 히스토리</h3>
        <table className={classes.table}>
          <thead>
            <tr className={classes.tableHeader}>
              <th className={classes.tableCell}>날짜</th>
              <th className={classes.tableCell}>상세내역</th>
              <th className={classes.tableCell}>마일리지</th>
            </tr>
          </thead>
          <tbody>
            {mileageHistory.map((mileage) => (
              <tr key={mileage.id} className={classes.tableRow}>
                <td className={classes.tableCell}>{mileage.date}</td>
                <td className={classes.tableCell}>
                  {mileage.type === "ADD" ? "마일리지 적립" : "마일리지 소모"}
                </td>
                <td className={classes.tableCell}>
                  {mileage.amount.toLocaleString()}원
                </td>
              </tr>
            ))}
          </tbody>
        </table>

            <div className={classes.totalContainer}>
                총 마일리지 : {totalMileage.toLocaleString()}원
            </div>
      </div>
    )
}

export default mileageHistory;