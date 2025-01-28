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

// getFavorites 함수 한 번만 불러올 수 있도록 함
let isRequestingFavorites = false;

async function getFavorites(setFavorites) {
  if(isRequestingFavorites) return;
  isRequestingFavorites = true;
  try {
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));

    if (!loginInfo || !loginInfo.accessToken) {
      window.location.href = "/login";
      return;
    }

    const getFavoritesResponse = await fetch("http://localhost:8080/favorites", {
      method: "GET",
      headers: {
        Authorization: `Bearer ${loginInfo.accessToken}`,
      },
    });
    console.log(getFavoritesResponse);

    if (!getFavoritesResponse.ok){
      throw new error;
    }

    const data = await getFavoritesResponse.json();
    setFavorites(data);
  } catch (error) {
    console.error("즐겨찾기 목록을 불러오지 못했습니다.");
  } finally {
    isRequestingFavorites = false;
  }
}

const GetFavorites = () => {
  const classes = useStyles();
  const [favorites, setFavorites] = useState([]);

  useEffect(() => {
    getFavorites(setFavorites);
  }, []);

  if (!favorites.length) {
    return (
      <div className={classes.container}>
        <h3>즐겨찾기 목록</h3>
        <p>즐겨찾기 목록이 비어있습니다.</p>
      </div>
    );
  }

  return (
    <div className={classes.container}>
      <h3>즐겨찾기 목록</h3>
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
          {favorites.map((product) => (
            <tr key={product.id} className={classes.tableRow}>
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

export default GetFavorites;
