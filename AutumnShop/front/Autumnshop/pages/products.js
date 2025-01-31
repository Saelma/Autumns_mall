import React, { useState, useEffect } from "react";
import {
  Container,
  Grid,
  Card,
  CardMedia,
  CardContent,
  Typography,
} from "@mui/material";
import { makeStyles } from "@mui/styles";
import Link from "next/link";

const useStyles = makeStyles((theme) => ({
  container: {
    marginTop: theme.spacing(3),
  },
  productCard: {
    marginBottom: theme.spacing(3),
    border: "2px solid #000",
    borderRadius: "8px",
  },
  media: {
    height: 0,
    paddingTop: "150%",
  },
  gridContainer: {
    justifyContent: "center",
  },
  categoryContainer: {
    backgroundColor: "#000000",
    padding: "10px 0",
    display: "flex",
    justifyContent: "center",
    gap: "15px",
  },
  categoryButton: {
    color: "#fff",
    fontSize: "16px",
    fontWeight: "bold",
    backgroundColor: "transparent",
    border: "2px solid #fff",
    borderRadius: "8px",
    padding: "10px 20px",
    cursor: "pointer",
    transition: "background-color 0.3s ease",
    "&:hover": {
      backgroundColor: "#333",
    },
  },
  title: {
    fontSize: "2rem",
    fontWeight: "700",
    marginTop: "20px",
    marginBottom: "20px",
    textAlign: "center",
  },
  paginationContainer: {
    display: "flex",
    justifyContent: "center",
    gap: "10px",
    marginTop: "20px",
  },
  paginationButton: {
    padding: "10px 20px",
    fontSize: "16px",
    fontWeight: "bold",
    backgroundColor: "#000000",
    color: "#fff",
    border: "2px solid #000000",
    borderRadius: "6px",
    cursor: "pointer",
    "&:hover": {
      backgroundColor: "#333",
    },
  },
}));

const ProductList = ({
  categories,
  products,
  pageNumber,
  totalPages,
  categoryId,
}) => {
  const classes = useStyles();

  return (
    <Container className={classes.container}>
      {/* 카테고리 버튼들 */}
      <div className={classes.categoryContainer}>
        <Link href={`/products`} passHref>
          <button className={classes.categoryButton}>모두</button>
        </Link>
        {categories.map((category) => (
          <Link href={`/products?categoryId=${category.id}`} passHref key={category.id}>
            <button className={classes.categoryButton}>{category.name}</button>
          </Link>
        ))}
      </div>
      <h1 className={classes.title}>물품 목록</h1>

      <Grid container spacing={3} className={classes.gridContainer}>
        {products.length > 0 ? (
          products.map((product, index) => (
            <Grid item xs={12} sm={12} md={4} lg={4} key={index}>
              <Card className={classes.productCard}>
                <Link href={`/product/${product.id}`} passHref>
                  { /* 한줄에 최대 3개 표시 */}
                  <CardMedia
                    className={classes.media}
                    image={product.imageUrl}
                    title={product.title}
                  />
                </Link>
                <CardContent>
                  <Typography gutterBottom variant="h5" component="div">
                    {product.title}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {product.price}원
                  </Typography>
                </CardContent>
              </Card>
            </Grid>
          ))
        ) : (
          <Grid
            item
            xs={12}
            style={{
              textAlign: "center",
              height: "500px",
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
            }}
          >
            <Typography variant="h4" component="div">
              해당 카테고리엔 상품이 없습니다.
            </Typography>
          </Grid>
        )}
      </Grid>

      {/* 페이지 네비게이션 버튼 */}
      <div className={classes.paginationContainer}>
        <Link
          href={`/products?page=0${
            categoryId ? `&categoryId=${categoryId}` : ""
          }`}
          passHref
        >
          <button className={classes.paginationButton}>첫 페이지</button>
        </Link>
        <Link
          href={`/products?page=${Math.max(0, pageNumber - 1)}${
            categoryId ? `&categoryId=${categoryId}` : ""
          }`}
          passHref
        >
          <button className={classes.paginationButton}>이전</button>
        </Link>
        {Array.from({ length: totalPages }, (_, i) => (
          <Link
            href={`/products?page=${i}${
              categoryId ? `&categoryId=${categoryId}` : ""
            }`}
            passHref
            key={i}
          >
            <button
              className={classes.paginationButton}
              style={{
                backgroundColor: i === pageNumber ? "#333" : "#000",
              }}
            >
              {i + 1}
            </button>
          </Link>
        ))}
        <Link
          href={`/products?page=${Math.min(totalPages - 1, pageNumber + 1)}${
            categoryId ? `&categoryId=${categoryId}` : ""
          }`}
          passHref
        >
          <button className={classes.paginationButton}>다음</button>
        </Link>
        <Link
          href={`/products?page=${totalPages - 1}${
            categoryId ? `&categoryId=${categoryId}` : ""
          }`}
          passHref
        >
          <button className={classes.paginationButton}>마지막 페이지</button>
        </Link>
      </div>
    </Container>
  );
};

// categories, products에서 api를 호출해 데이터를 가져온 후,
// 해당 js의 productList에 props 전달함 
export async function getServerSideProps(context) {
  const categoryId = context.query.categoryId || 0;
  const page = context.query.page || 0;

  let categories = [];
  let products = [];
  let pageNumber = 0;
  let totalPages = 0;

  try {
    const categoryResponse = await fetch("http://localhost:8080/categories");
    if(!categoryResponse.ok){
      throw new Error("물품별 카테고리를 불러오는 데 실패했습니다.");
    }
    categories = await categoryResponse.json();

    const productResponse = await fetch(
      `http://localhost:8080/products?categoryId=${categoryId}&page=${page}`);

      if (!productResponse.ok) {
        throw new Error("물품들을 불러오는 데 실패했습니다.");
      }

      const productData = await productResponse.json();
      products = productData.content;
      pageNumber = parseInt(productData.pageable.pageNumber);
      totalPages = parseInt(productData.totalPages);
  } catch (error) {
    console.error(error);
  }

  return {
    props: {
      categories,
      products,
      pageNumber,
      totalPages,
      categoryId,
    },
  };
}

export default ProductList;
