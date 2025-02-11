import React, { useState, useEffect } from "react";
import { makeStyles } from "@mui/styles";

// 스타일 정의
const useStyles = makeStyles(() => ({
  cartContainer: {
    width: "100%",
    maxWidth: "900px",
    margin: "20px auto",
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
  },
  headerRow: {
    display: "table-row",
    backgroundColor: "#000",
    color: "white",
    fontWeight: "bold",
    textAlign: "center",
  },
  headerCell: {
    padding: "12px 20px",
    border: "1px solid #ddd",
    textAlign: "center",
  },
  detailRow: {
    display: "table-row",
    backgroundColor: "#fff",
    borderBottom: "1px solid #ddd",
    marginBottom: "15px",
  },
  detailCell: {
    padding: "15px 20px",
    border: "1px solid #ddd",
    textAlign: "center",
    fontSize: "18px",
  },
  productImage: {
    width: "100px",
    marginTop: "10px",
    marginLeft: "auto",
    marginRight: "auto",
  },
}));

// OrderDetails 컴포넌트
function OrderDetails() {
  const classes = useStyles();
  const [loading, setLoading] = useState(true);
  const [orderid, setOrderid] = useState([]);
  const [payment, setPayment] = useState([]);

  // 주문 세부 정보 가져오기
  useEffect(() => {
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));

    async function fetchOrderDetails() {
      const orderresponse = await fetch(`http://localhost:8080/orders`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${loginInfo.accessToken}`,
        },
      });

      if (!orderresponse.ok) {
        throw new Error('주문 정보를 불러오는 데 실패했습니다.');
      }

      const orderDetails = await orderresponse.json();
      setLoading(false);
      setOrderid(orderDetails.map(order => order.id));
    }

    fetchOrderDetails();
  }, []);

  // 주문에 대한 결제 정보 가져오기
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
      {payment && payment.map((paymentitem, index) => (
        <div key={index}>
          <h2>주문번호 : {index + 1}</h2>
          <table className={classes.cartTable}>
            <thead>
              <tr className={classes.headerRow}>
                <th className={classes.headerCell}>물품</th>
                <th className={classes.headerCell}>가격</th>
                <th className={classes.headerCell}>평점</th>
                <th className={classes.headerCell}>수량</th>
                <th className={classes.headerCell}>주문날짜</th>
                <th className={classes.headerCell}>이미지</th>
              </tr>
            </thead>
            <tbody>
              {paymentitem.map((item, idx) => (
                <tr key={idx} className={classes.detailRow}>
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
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ))}
    </div>
  );
}

export default OrderDetails;
