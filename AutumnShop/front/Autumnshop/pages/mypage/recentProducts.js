import React, { useEffect, useState } from "react";
import { makeStyles } from "@mui/styles";
import Link from "next/link";

const useStyles = makeStyles(() => ({
  container: {
    padding: "20px",
    border: "1px solid #ccc",
    borderRadius: "8px",
    backgroundColor: "#f8f8f8",
    textAlign: "center",
    maxWidth: "800px",
    margin: "20px auto",
  },
  table: {
    width: "100%",
    borderCollapse: "collapse",
  },
  tableHeader: {
    backgroundColor: "#3f51b5",
    color: "#fff",
    fontWeight: "bold",
    textAlign: "center",
    padding: "12px",
  },
  tableCell: {
    border: "1px solid #ddd",
    padding: "12px",
    textAlign: "center",
  },
  tableRow: {
    "&:hover": {
      backgroundColor: "#f1f1f1",
    },
  },
  button: {
    padding: "8px 16px",
    fontSize: "14px",
    fontWeight: "bold",
    color: "#fff",
    backgroundColor: "#007BFF",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer",
    transition: "background-color 0.3s ease, transform 0.2s ease",
    "&:hover": {
      backgroundColor: "#0056b3",
      transform: "scale(1.05)",
    },
    "&:active": {
      backgroundColor: "#003f7f",
      transform: "scale(0.95)",
    },
  },
}));

// getRecentProducts 함수 한 번만 불러올 수 있도록 함
let isRequestingRecentProduct = false;

async function getRecentProducts(setRecentProducts) {
    if(isRequestingRecentProduct) return;
    isRequestingRecentProduct = true;
  try {
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));

    if (!loginInfo || !loginInfo.accessToken) {
      alert("로그인을 해주세요");
      window.location.href = "/login"; // 로그인 페이지로 리다이렉트
      return;
    }

    const getRecentProductsResponse = await fetch("http://localhost:8080/recentProducts", {
      method: "GET",
      headers: {
        Authorization: `Bearer ${loginInfo.accessToken}`,
      },
    });

    if(!getRecentProductsResponse.ok){
      throw new error;
    }

    const data = await getRecentProductsResponse.json();

    if(!data.length)
        alert("최근 본 상품이 없습니다!");

    // 최근 본 상품 목록을 최신 순으로 정렬
    setRecentProducts(data.reverse()); // reverse()로 최신 순 정렬
  } catch (error) {
    console.error("최근 본 상품을 불러오지 못했습니다.");
  } finally {
    isRequestingRecentProduct = false;
  }
}

const RecentProducts = () => {
  const classes = useStyles();
  const [recentProducts, setRecentProducts] = useState([]);

  useEffect(() => {
    getRecentProducts(setRecentProducts);
  }, []);

  if (!recentProducts.length) {
    return (
      <div className={classes.container}>
        <h3>최근 본 상품</h3>
        <p>최근 본 상품이 없습니다.</p>
      </div>
    );
  }

  return (
    <div className={classes.container}>
      <h3>최근 본 상품</h3>
      <table className={classes.table}>
        <thead>
          <tr>
            <th className={classes.tableHeader}>이미지</th>
            <th className={classes.tableHeader}>제품명</th>
            <th className={classes.tableHeader}>가격</th>
            <th className={classes.tableHeader}>상세</th>
          </tr>
        </thead>
        <tbody>
          {recentProducts.map((product) => (
            <tr
              key={product.id}
              className={classes.tableRow}
            >
              <td className={classes.tableCell}>
                <img
                  src={product.imageUrl}
                  alt={product.title}
                  style={{ width: "100px", height: "100px", objectFit: "cover", borderRadius: "8px" }}
                />
              </td>
              <td className={classes.tableCell}>{product.title}</td>
              <td className={classes.tableCell}>{product.price.toLocaleString()}원</td>
              <td className={classes.tableCell}>
              <Link href={`/product/${product.id}`} passHref>
                    <button className={classes.button}>상세보기</button>
                </Link>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default RecentProducts;
