import React from "react";
import { makeStyles } from "@mui/styles";
import { useState, useEffect } from "react";

const useStyles = makeStyles((theme) => ({
    cartContainer: {
      width: "100%",
      maxWidth: "800px",
      margin: "20px auto",
      padding: "20px",
      border: "1px solid #ccc",
      borderRadius: "8px",
      backgroundColor: "#f8f8f8",
    },
    cartTable: {
      width: "800px",
    },
    cartItem: {
      width: "500px",
      border: "1px solid #ddd",
      borderRadius: "8px",
      backgroundColor: "#fff",
      boxShadow: "0 4px 8px rgba(0, 0, 0, 0.2)", 
      transition: "transform 0.3s ease-in-out", 
      "&:hover": {
        transform: "scale(1.05)", 
        boxShadow: "0 8px 16px rgba(0, 0, 0, 0.3)", 
      },
      flexDirection: "column",
      alignItems: "center",
      textAlign: "center",
    },
      productImage: {
      width: "100px",
      alignSelf: "center",
    },
      
    headerRow: {
        display: "flex",
        justifyContent: "space-between",
        padding: "10px",
        margin: "0 0 10px 0",
        border: "1px solid #007bff",
        borderRadius: "12px",
        backgroundColor: "#007bff",
        color: "white",
        fontWeight: "bold",
  },
    detailRow: {
        flexDirection: "column", 
        padding: "8px",
        margin: "5px 0",
        border: "1px solid #eee",
        borderRadius: "8px",
        backgroundColor: "#fafafa",
        boxShadow: "0 2px 4px rgba(0, 0, 0, 0.05)",
    },
    productDetailContainer: {
        display: "flex",
        justifyContent: "space-between",  
        alignItems: "center",
        marginBottom: "5px",
    },
    productCell: {
        padding: "5px 10px",
        flex: 1, 
    }
  }));

function OrderDetails({}){
    const classes = useStyles();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(' ');
    const [orderid, setOrderid] = useState([]);
    const [payment, setPayment] = useState([])

    useEffect(() => {
        const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
        
        async function fetchOrderDetails() {
            const orderresponse = await fetch(`http://localhost:8080/orders`,{
              method: "GET",
                headers : {
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

    if(loading) return <div>Loading...</div>;

    return (
        <div className={classes.cartContainer}>
        <div className={classes.headerRow}>
          <div>주문번호</div>
          <div>물품</div>
          <div>가격</div>
          <div>평점</div>
          <div>수량</div>
          <div>주문날짜</div>
          <div>이미지</div>
        </div>
        {payment && payment.map((paymentitem, index) => (
          <div key={index} className={classes.detailRow}>
            {paymentitem.map((item, idx) => (
              <div key={idx} className={classes.productDetailContainer}>
                <span className={classes.productCell}>{index + 1}</span>
                <span className={classes.productCell}>{item.productTitle}</span>
                <span className={classes.productCell}>{item.productPrice}</span>
                <span className={classes.productCell}>{item.productRate}</span>
                <span className={classes.productCell}>{item.quantity}</span>
                <span className={classes.productCell}>{item.date}</span>
                {item.imageUrl && (
                  <img
                    src={item.imageUrl}
                    alt={`Product ${idx + 1}`}
                    className={classes.productImage}
                  />
                )}
              </div>
            ))}
          </div>
        ))}
        </div>
      );
    }

export default OrderDetails;