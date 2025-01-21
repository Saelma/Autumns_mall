import React, { useState, useEffect } from "react";
import { Container, Typography, Card, CardMedia, CardContent, Box, Button } from "@mui/material";
import { makeStyles } from "@mui/styles";
import myAxios from "../../utils/myaxios";
import { useRouter } from "next/router";
import Carts from "../Carts";
import Rating from "@mui/material/Rating"; // 별점 컴포넌트

const useStyles = makeStyles((theme) => ({
  container: {
    marginTop: theme.spacing(4),
    padding: theme.spacing(2),
    backgroundColor: "#f8f8f8", // 배경색
  },
  card: {
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'space-between',
    padding: theme.spacing(2),
    boxShadow: '0px 4px 12px rgba(0,0,0,0.1)',
    borderRadius: '8px',
    backgroundColor: 'white',
    overflow: 'hidden',
  },
  media: {
    width: 400, // 이미지의 고정된 너비
    height: 400,
    objectFit: 'cover',
    marginRight: theme.spacing(3), // 이미지와 텍스트 사이의 여백
  },
  content: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'flex-start',
    padding: theme.spacing(2),
    width: '60%', // 내용이 차지할 너비
  },
  title: {
    fontWeight: 700,
    fontSize: '1.8rem',
    marginBottom: theme.spacing(1),
    color: '#333', // 텍스트 색상
  },
  price: {
    fontSize: '1.5rem',
    fontWeight: 600,
    color: theme.palette.primary.main,
    marginBottom: theme.spacing(2),
  },
  description: {
    fontSize: '1rem',
    color: '#666',
    marginBottom: theme.spacing(2),
  },
  rating: {
    marginBottom: theme.spacing(2),
  },
  count: {
    fontSize: '1.2rem',
    color: '#333',
    marginBottom: theme.spacing(2),
  },
  buttonContainer: {
    display: 'flex',
    justifyContent: 'center',
    marginTop: theme.spacing(2),
    width: '100%',
  },
  addToCartButton: {
    backgroundColor: '#000',
    color: '#fff',
    padding: theme.spacing(1, 4),
    fontSize: '1rem',
    fontWeight: 600,
    '&:hover': {
      backgroundColor: '#444',
    },
  },
  backButton: {
    border: '1px solid #000',
    padding: theme.spacing(1, 4),
    fontSize: '1rem',
    fontWeight: 600,
    marginLeft: theme.spacing(2),
    '&:hover': {
      backgroundColor: '#f8f8f8',
    },
  }
}));

const ProductDetail = () => {
  const router = useRouter();
  const { id } = router.query;
  const [product, setProduct] = useState(null);
  const classes = useStyles();

  useEffect(() => {
    if (!id) return; // id가 없으면 로딩하지 않음

    const getProductResponse = async () => {
      try {
        const response = await myAxios.get(`/products/${id}`);
        setProduct(response.data);
      } catch (error) {
        console.error(error);
      }
    };

    getProductResponse();
  }, [id]);

  if (!product) return <div>Loading...</div>;

  return (
    <Container className={classes.container}>
      <Card className={classes.card}>
        <CardMedia
          className={classes.media}
          component="img"
          image={product.imageUrl}
          alt={product.title}
        />
        <CardContent className={classes.content}>
          <Typography variant="h4" className={classes.title}>
            {product.title}
          </Typography>
          {/* 별점 표시 */}
          <Box className={classes.rating}>
            <Rating
              name="product-rating"
              value={product.rating.rate}
              precision={0.5} // 소수점 별점
              readOnly
            />
          </Box>
          <Typography variant="h6" className={classes.price}>
            {product.price}원
          </Typography>
                    {/* 수량 표시 */}
          <Typography variant="body1" className={classes.count}>
            남은 수량: {product.rating.count}개
          </Typography>
          <Typography variant="body1" className={classes.description}>
            {product.description}
          </Typography>
        </CardContent>
      </Card>
      
      <Box className={classes.buttonContainer}>
        <Carts
          title={product.title}
          price={product.price}
          id={product.id}
          description={product.description}
        />
        {/* 돌아가기 버튼 */}
        <Button
          className={classes.backButton}
          onClick={() => router.push('/products')}
        >
          돌아가기
        </Button>
      </Box>
    </Container>
  );
};

export default ProductDetail;
