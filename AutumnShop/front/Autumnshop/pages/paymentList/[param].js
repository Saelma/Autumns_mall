import React from "react";
import { useEffect, useState } from "react";
import { makeStyles } from "@mui/styles";
import axios from "axios";
import PaymentDate from "./paymentDate";
import { Button, Box } from "@mui/material";
import Link from "next/link";

// CSS 모음
const useStyles = makeStyles((theme) => ({
  cartContainer: {
    width: "800px",
    margin: "20px",
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
    boxShadow: "0 4px 8px rgba(0, 0, 0, 0.2)", // 그림자 효과를 변경
    transition: "transform 0.3s ease-in-out", // 호버 효과를 위한 transition 추가
    "&:hover": {
      transform: "scale(1.05)", // 호버 시 약간 확대됨
      boxShadow: "0 8px 16px rgba(0, 0, 0, 0.3)", // 호버 시 그림자 효과 증가
    },
    flexDirection: "column",
    alignItems: "center",
    textAlign: "center",
  },
  productImage: {
    width: "100px",
    alignSelf: "center",
  },
}));

async function getCartItem(loginInfo, setPaymentItems, page, year, month) {
  if (year != null && month != null) {
    const paymentResponse = await axios
      .get(`http://localhost:8080/payment/${year}/${month}`, {
        headers: {
          Authorization: `Bearer ${loginInfo.accessToken}`,
        },
        params: {
          page: page,
        },
      })
      .then((paymentResponse) => {
        setPaymentItems(paymentResponse.data);
      });
  } else {
    // 1. 현재 로그인한 아이디에 따라 맞는 카트 가져옴
    const paymentResponse = await axios
      .get(`http://localhost:8080/payment`, {
        headers: {
          Authorization: `Bearer ${loginInfo.accessToken}`,
        },
        params: {
          page: page,
        },
      })
      .then((paymentResponse) => {
        setPaymentItems(paymentResponse.data);
      });
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
    <div>
      <PaymentDate classes={classes} />
      <div className={classes.cartContainer}>
        <h1>구매 목록</h1>
        <table className={classes.cartTable}>
          <thead>
            <tr>
              <th>번호</th>
              <th>상품 이름</th>
              <th>상품 가격</th>
              <th>평점</th>
              <th>수량</th>
              <th>이미지</th>
              <th>날짜</th>
            </tr>
          </thead>
          <tbody className="css">
            {paymentItems.content &&
              paymentItems.content.map((item, index) => (
                <tr key={item.id} className={classes.cartItem}>
                  <td>{index + 1}</td>
                  <td>{item.productTitle}</td>
                  <td>{item.productPrice}</td>
                  <td>{item.productRate}</td>
                  <td>{item.quantity}</td>
                  <td>
                    {item.imageUrl && (
                      <img
                        src={item.imageUrl}
                        alt={`Product ${index + 1}`}
                        className={classes.productImage}
                      />
                    )}
                  </td>
                  <td>{item.date}</td>
                </tr>
              ))}
            <tr>
              <td>총 가격 : {totalPrice}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <Box display="flex" justifyContent="center" marginBottom={3}>
        {/* 페이지 네비게이터 버튼 */}
        <a
          href={
            dateParams.year && dateParams.month
              ? `/paymentList?year=${dateParams.year}&month=${dateParams.month}&pageNumber=0`
              : `/paymentList?pageNumber=0`
          }
        >
          <Button variant="outlined">첫페이지</Button>
        </a>
        <a
          href={
            dateParams.year && dateParams.month
              ? `/paymentList?year=${dateParams.year}&month=${
                  dateParams.month
                }&pageNumber=${Math.max(0, pageNumber - 1)}`
              : `/paymentList?pageNumber=${Math.max(0, pageNumber - 1)}`
          }
        >
          <Button variant="outlined">이전</Button>
        </a>
        {Array.from({ length: totalPages }, (_, i) => (
          <a
            href={
              dateParams.year && dateParams.month
                ? `/paymentList?year=${dateParams.year}&month=${dateParams.month}&pageNumber=${i}`
                : `/paymentList?pageNumber=${i}`
            }
            key={i}
          >
            <Button variant="outlined" selected={i === pageNumber}>
              {i + 1}
            </Button>
          </a>
        ))}
        <a
          href={
            dateParams.year && dateParams.month
              ? `/paymentList?year=${dateParams.year}&month=${
                  dateParams.month
                }&pageNumber=${Math.min(totalPages - 1, pageNumber + 1)}`
              : `/paymentList?pageNumber=${Math.min(
                  totalPages - 1,
                  pageNumber + 1
                )}`
          }
        >
          <Button variant="outlined">다음</Button>
        </a>
        <a
          href={
            dateParams.year && dateParams.month
              ? `/paymentList?year=${dateParams.year}&month=${
                  dateParams.month
                }&pageNumber=${totalPages - 1}`
              : `/paymentList?pageNumber=${totalPages - 1}`
          }
        >
          <Button variant="outlined">마지막페이지</Button>
        </a>
      </Box>
    </div>
  );
};

export default paymentList;
