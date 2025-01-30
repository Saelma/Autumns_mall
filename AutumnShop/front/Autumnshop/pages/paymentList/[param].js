import React, { useEffect, useState } from "react";
import { makeStyles } from "@mui/styles";
import PaymentDate from "./paymentDate";

// CSS 적용
const useStyles = makeStyles(() => ({
  tableContainer: {
    width: "60%",
    margin: "20px auto",
    padding: "20px",
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
    fontSize: "14px",
  },
  tableHeader: {
    backgroundColor: "#f4f4f4",
    color: "#000",
    textAlign: "center",
    fontWeight: "bold",
    borderBottom: "3px solid #000",
    padding: "10px",
  },
  tableCell: {
    padding: "10px",
    borderBottom: "2px solid #000",
    textAlign: "center",
    fontSize: "20px",
  },
  tableRow: {
    "&:hover": {
      backgroundColor: "#f1f1f1",
    },
  },
  totalContainer: {
    marginTop: "20px",
    padding: "10px",
    backgroundColor: "#f4f4f4",
    borderRadius: "8px",
    fontSize: "18px",
    fontWeight: "bold",
    textAlign: "center",
    color: "#000",
    border: "2px solid #000",
  },
  paginationContainer: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    marginTop: "20px",
    gap: "10px",
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
    "&:hover": {
      backgroundColor: "#f1f1f1",
    },
  },
  currentPage: {
    fontWeight: "bold",
    color: "#000",
    fontSize: "16px",
  },
}));

async function getCartItem(loginInfo, setPaymentItems, page, year, month) {
  if (year != null && month != null) {
    try{
      let paymentPage = page || 0;
      if(paymentPage == -1 || paymentPage == NaN)
        paymentPage = 0;

      const paymentResponse = await fetch(`http://localhost:8080/payment/${year}/${month}?page=${paymentPage}`, {
        method: "GET",  
        headers: {
            Authorization: `Bearer ${loginInfo.accessToken}`,
          },
        });
        const data = await paymentResponse.json();
        setPaymentItems(data);
    } catch (error) {
      console.error("결제 항목을 불러오지 못했습니다!");
    }
  } else {
    try{
      const paymentPage = page || 0;
      // 현재 로그인한 아이디에 따라 맞는 카트 가져옴
      const paymentResponse = await fetch(`http://localhost:8080/payment?page=${paymentPage}`, {
        method: "GET",  
        headers: {
            Authorization: `Bearer ${loginInfo.accessToken}`,
          },
        });
      const data = await paymentResponse.json();
      setPaymentItems(data);
    } catch (error){
      console.error("결제 항목을 불러오지 못했습니다.");
    }
  }
}

const paymentList = () => {
  const classes = useStyles();
  const [paymentItems, setPaymentItems] = useState([]);
  const [totalPrice, setTotalPrice] = useState(0);
  const [pageNumber, setPageNumber] = useState(0);
  const [dateParams, setDateParams] = useState({ year: null, month: null });

  useEffect(() => {
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
    const urlSearchParams = new URLSearchParams(window.location.search);
    const page = urlSearchParams.get("pageNumber");
    const year = urlSearchParams.get("year");
    const month = urlSearchParams.get("month");
    setPageNumber(page != null ? page : 0);
    setDateParams({ year: year, month: month });
    getCartItem(loginInfo, setPaymentItems, page, year, month);
  }, []);

  // Page로 아이템을 불러오면서, 초기 렌더링 오류로 인해 && 연산자를 사용하여 문제 해결
  useEffect(() => {
    let itemTotalPrice = 0;
    if (paymentItems && paymentItems.content) {
      paymentItems.content.forEach((item, index) => {
        itemTotalPrice += item.productPrice * item.quantity;
      });
    }
    setTotalPrice(itemTotalPrice);
  }, [paymentItems]);

  const totalPages = paymentItems.totalPages;

  return (
    <div className={classes.tableContainer}>
      <PaymentDate classes={classes} />
      <h1>구매 목록</h1>
      <table className={classes.table}>
        <thead>
          <tr className={classes.tableHeader}>
            <th className={classes.tableCell}>번호</th>
            <th className={classes.tableCell}>상품 이름</th>
            <th className={classes.tableCell}>상품 가격</th>
            <th className={classes.tableCell}>평점</th>
            <th className={classes.tableCell}>수량</th>
            <th className={classes.tableCell}>이미지</th>
            <th className={classes.tableCell}>날짜</th>
          </tr>
        </thead>
        <tbody>
          {paymentItems.content &&
            paymentItems.content.map((item, index) => (
              <tr key={item.id} className={classes.tableRow}>
                <td className={classes.tableCell}>{index + 1}</td>
                <td className={classes.tableCell}>{item.productTitle}</td>
                <td className={classes.tableCell}>{item.productPrice}</td>
                <td className={classes.tableCell}>{item.productRate}</td>
                <td className={classes.tableCell}>{item.quantity}</td>
                <td className={classes.tableCell}>
                  {item.imageUrl && (
                    <img
                      src={item.imageUrl}
                      alt={`Product ${index + 1}`}
                      style={{ width: "100px", alignSelf: "center" }}
                    />
                  )}
                </td>
                <td className={classes.tableCell}>{item.date}</td>
              </tr>
            ))}
        </tbody>
      </table>

      <div className={classes.totalContainer}>
        총 가격 : {totalPrice}
      </div>

      <div className={classes.paginationContainer}>
        <button
          className={classes.paginationButton}
          onClick={() =>
            window.location.href = `${dateParams.year && dateParams.month ? `/paymentList?year=${dateParams.year}&month=${dateParams.month}&pageNumber=0` : `/paymentList?pageNumber=0`}`
          }
        >
          첫페이지
        </button>
        <button
          className={classes.paginationButton}
          onClick={() =>
            window.location.href = `${dateParams.year && dateParams.month ? `/paymentList?year=${dateParams.year}&month=${dateParams.month}&pageNumber=${Math.max(0, pageNumber - 1)}` : `/paymentList?pageNumber=${Math.max(0, pageNumber - 1)}`}`
          }
        >
          이전
        </button>
        {Array.from({ length: totalPages }, (_, i) => (
          <button
            key={i}
            className={classes.paginationButton}
            onClick={() =>
              window.location.href = `${dateParams.year && dateParams.month ? `/paymentList?year=${dateParams.year}&month=${dateParams.month}&pageNumber=${i}` : `/paymentList?pageNumber=${i}`}`
            }
          >
            {i + 1}
          </button>
        ))}
        <button
          className={classes.paginationButton}
          onClick={() =>
            window.location.href = `${dateParams.year && dateParams.month ? `/paymentList?year=${dateParams.year}&month=${dateParams.month}&pageNumber=${Math.min(totalPages - 1, pageNumber + 1)}` : `/paymentList?pageNumber=${Math.min(totalPages - 1, pageNumber + 1)}`}`
          }
        >
          다음
        </button>
        <button
          className={classes.paginationButton}
          onClick={() =>
            window.location.href = `${dateParams.year && dateParams.month ? `/paymentList?year=${dateParams.year}&month=${dateParams.month}&pageNumber=${totalPages - 1}` : `/paymentList?pageNumber=${totalPages - 1}`}`
          }
        >
          마지막페이지
        </button>
      </div>
    </div>
  );
};

export default paymentList;
