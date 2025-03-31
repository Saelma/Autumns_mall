import React, { useEffect, useState } from "react";
import { makeStyles } from "@mui/styles";
import Link from "next/link";

const useStyles = makeStyles(() => ({
  container: {
    padding: "20px",
    border: "2px solid #333",
    borderRadius: "8px",
    backgroundColor: "#ffffff",
    textAlign: "center",
    maxWidth: "800px",
    margin: "20px auto",
    boxShadow: "0 4px 12px rgba(0, 0, 0, 0.1)",
  },
  table: {
    width: "100%",
    borderCollapse: "collapse",
  },
  tableHeader: {
    backgroundColor: "#000000",
    color: "#ffffff",
    fontWeight: "bold",
    textAlign: "center",
    padding: "14px",
  },
  tableCell: {
    border: "2px solid #ddd",
    padding: "14px",
    textAlign: "center",
    color: "#333",
  },
  tableRow: {
    "&:hover": {
      backgroundColor: "#f9f9f9",
    },
  },
  button: {
    padding: "10px 20px",
    fontSize: "16px",
    fontWeight: "bold",
    color: "#ffffff",
    backgroundColor: "#000000",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer",
    transition: "background-color 0.3s ease, transform 0.2s ease",
    "&:hover": {
      backgroundColor: "#333",
      transform: "scale(1.05)",
    },
    "&:active": {
      backgroundColor: "#222",
      transform: "scale(0.95)",
    },
  },
}));

let isRequestingFavorites = false;

async function getFavorites(setFavorites) {
  if (isRequestingFavorites) return;
  isRequestingFavorites = true;
  try {
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));

    if (!loginInfo || !loginInfo.accessToken) {
      window.location.href = "/login";
      return;
    }

    const getFavoritesResponse = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}favorites`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${loginInfo.accessToken}`,
      },
    });

    if (!getFavoritesResponse.ok) {
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
        <h2>즐겨찾기 목록</h2>
        <p>즐겨찾기 목록이 비어있습니다.</p>
      </div>
    );
  }

  return (
    <div className={classes.container}>
      <h2>즐겨찾기 목록</h2>
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
                  style={{
                    width: "100px",
                    height: "100px",
                    objectFit: "cover",
                    borderRadius: "8px",
                  }}
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
