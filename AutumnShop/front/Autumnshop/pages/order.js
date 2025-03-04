import React, { useState, useEffect } from "react";
import { makeStyles } from "@mui/styles";

// 스타일 정의
const useStyles = makeStyles(() => ({
  cartContainer: {
    width: "100%",
    maxWidth: "900px",
    margin: "50px auto",
    padding: "20px",
    border: "3px solid #000",
    borderRadius: "8px",
    backgroundColor: "#fff",
    boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
    textAlign: "center",
  },
  heading: {
    fontSize: "32px",
    fontWeight: "bold",
    color: "#333",
    marginBottom: "20px",
  },
  cartTable: {
    width: "100%",
    borderCollapse: "collapse",
    marginBottom: "10px",
  },
  headerRow: {
    backgroundColor: "#000",
    color: "white",
    fontWeight: "bold",
  },
  headerCell: {
    padding: "12px 20px",
    border: "1px solid #ddd",
    textAlign: "center",
  },
  detailRow: {
    backgroundColor: "#fff",
    borderBottom: "1px solid #ddd",
    marginBottom: "10px",  // tr 사이에 여백 추가
  },
  detailCell: {
    padding: "15px 20px",
    border: "1px solid #ddd",
    textAlign: "center",
    fontSize: "18px",
  },
  productImage: {
    width: "100px",
    margin: "10px auto",
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
    "&:hover:not(:disabled)": {
      backgroundColor: "#f1f1f1",
    },
  },
  currentPage: {
    fontWeight: "bold",
    fontSize: "16px",
    margin: "0 10px",
  },
}));

function OrderDetails() {
  const classes = useStyles();
  const [loading, setLoading] = useState(true);
  const [orderid, setOrderid] = useState([]);
  const [payment, setPayment] = useState([]);
  const [shippingStatus, setShippingStatus] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const size = 10;

  useEffect(() => {
    async function fetchOrderDetails() {
      const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));

      const orderresponse = await fetch(
        `http://localhost:8080/orders?page=${page}&size=${size}`, 
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${loginInfo.accessToken}`,
          },
        }
      );

      if (!orderresponse.ok) {
        throw new Error('주문 정보를 불러오는 데 실패했습니다.');
      }

      const orderDetails = await orderresponse.json();
      setLoading(false);
      setOrderid(orderDetails.content.map(order => order.id));
      setTotalPages(orderDetails.totalPages); // 전체 페이지 수 설정
    }

    fetchOrderDetails();
  }, [page]); // 페이지 변경될 때마다 실행

  useEffect(() => {
    async function fetchOrderFollow() {
      try {
        const paymentPromises = orderid.map(async (orderid) => {
          const response = await fetch(`http://localhost:8080/payment/order?orderId=${orderid}`, {
            method: "GET",
            headers: {
              Authorization: `Bearer ${JSON.parse(localStorage.getItem("loginInfo")).accessToken}`,
            },
          });
          
          if (!response.ok) {
            throw new Error(`주문 ID의 정보를 불러오는 데 실패했습니다.: ${orderid}`);
          }

          return response.json();
        });

        const payments = await Promise.all(paymentPromises);
        setPayment(payments);
      } catch (error) {
        console.error('구매 정보를 불러오는 데 실패했습니다.:', error);
      }
    }

    if (orderid.length > 0) {
      fetchOrderFollow();
    }
  }, [orderid]);

  if (loading) return <div>Loading...</div>;

  return (
    <div className={classes.cartContainer}>
      <h1>주문목록</h1>
      {payment && payment.map((paymentitem, index) => (
        <div key={index}>
          <table className={classes.cartTable}>
            <thead>
              <tr className={classes.headerRow}>
                <th className={classes.headerCell}>주문번호</th>
                <th className={classes.headerCell}>물품</th>
                <th className={classes.headerCell}>가격</th>
                <th className={classes.headerCell}>평점</th>
                <th className={classes.headerCell}>수량</th>
                <th className={classes.headerCell}>주문날짜</th>
                <th className={classes.headerCell}>이미지</th>
                <th className={classes.headerCell}>배송 상태</th>
              </tr>
            </thead>
            <tbody className={classes.tbody}>
              {paymentitem.map((item, idx) => (
                <tr key={idx} className={classes.detailRow}>
                  <td className={classes.detailCell}>{item.order.id}</td>
                  <td className={classes.detailCell}>{item.title}</td>
                  <td className={classes.detailCell}>{item.price}</td>
                  <td className={classes.detailCell}>{item.productRate}</td>
                  <td className={classes.detailCell}>{item.quantity}</td>
                  <td className={classes.detailCell}>{item.date}</td>
                  <td className={classes.detailCell}>
                    {item.imageUrl && (
                      <img
                        src={item.imageUrl}
                        alt={`Product ${idx + 1}`}
                        className={classes.productImage}
                      />
                    )}
                  </td>
                  <td className={classes.detailCell}>
                    {/* 배송 상태 표시 */}
                    {item.order.delivery ? item.order.delivery.status : '정보 없음'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
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
}

export default OrderDetails;
