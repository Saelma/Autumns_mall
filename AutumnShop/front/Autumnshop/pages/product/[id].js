import React, { useState, useEffect } from "react";
import { Container, Typography, Card, CardMedia, CardContent, Box, Button, TextField } from "@mui/material";
import { makeStyles } from "@mui/styles";
import axios from "axios";
import { useRouter } from "next/router";
import Carts from "../Carts";
import Rating from "@mui/material/Rating"; // 별점 컴포넌트

const useStyles = makeStyles((theme) => ({
  container: {
    marginTop: theme.spacing(4),
    padding: theme.spacing(2),
    backgroundColor: "#f8f8f8",
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
    width: 400,
    height: 400,
    objectFit: 'cover',
    marginRight: theme.spacing(3),
  },
  content: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'flex-start',
    padding: theme.spacing(2),
    width: '60%',
  },
  title: {
    fontWeight: 700,
    fontSize: '1.8rem',
    marginBottom: theme.spacing(1),
    color: '#333',
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
  },
  reviewContainer: {
    marginTop: theme.spacing(4),
    padding: theme.spacing(2),
    backgroundColor: "#ffffff",
    borderRadius: "8px",
    boxShadow: "0px 2px 8px rgba(0,0,0,0.1)",
  },
  reviewBox: {
    marginBottom: theme.spacing(2),
  },
  reviewList: {
    marginTop: theme.spacing(4),
  },
  reviewItem: {
    marginBottom: theme.spacing(2),
    padding: theme.spacing(2),
    border: "1px solid #ddd",
    borderRadius: "8px",
  },
}));

async function getProduct(setProduct, id, setIsFavorite){
  try{
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
    const getProductResponse = await axios.get(`http://localhost:8080/products/${id}`, {
      headers: {
        Authorization: `Bearer ${loginInfo.accessToken}`,
      },
    });
    setProduct(getProductResponse.data); 
    
    // 즐겨찾기 상태 확인
    const checkFavoriteResponse = await axios.get(`http://localhost:8080/favorites/${id}`, {
      headers: {
        Authorization: `Bearer ${loginInfo.accessToken}`,
      },
    });
    setIsFavorite(checkFavoriteResponse.data); // 즐겨찾기 되어있다면 true
  } catch (error){
    console.error("물건을 불러오지 못했습니다.");
  }
}

// addRecentProduct 함수 한 번만 불러오도록 함
let isRequestingRecentProduct = false;

async function addRecentProduct(id) {
  if(isRequestingRecentProduct) return;
  isRequestingRecentProduct = true;
  try{
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
    const addRecentProductResponse = await axios.post(`http://localhost:8080/recentProducts/${id}`, 
      {},
      {
      headers: {
        Authorization: `Bearer ${loginInfo.accessToken}`,
      }
    });
  } catch (error) {
    console.error("최근 본 상품에 추가되지 못했습니다.");
  } finally {
    isRequestingRecentProduct = false;
  }
}

async function toggleFavorite(setIsFavorite, isFavorite, id) {
  try{
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
    const headers = {
      Authorization: `Bearer ${loginInfo.accessToken}`,
    };

    if(!isFavorite){
      const postFavoriteResponse = await axios.post(
        `http://localhost:8080/favorites/${id}`,
        {},
        { headers }
      );
      alert("즐겨찾기에 추가되었습니다!");
    }else{
      const deleteFavoriteResponse = await axios.delete(
        `http://localhost:8080/favorites/${id}`,
        { headers }
      );
      alert("즐겨찾기에서 제거되었습니다!");
    }

    setIsFavorite(!isFavorite);
  } catch (error) {
    console.error(error);
  }
}

async function getReviews(id, setReviews) {
  try{
    const getReviewsResponse = await axios.get(
      `http://localhost:8080/products/${id}/reviews`
    );
    setReviews(getReviewsResponse.data);
  } catch (error) {
    console.error("상품평을 불러오지 못했습니다.");
  }
}

const ProductDetail = () => {
  const router = useRouter();
  const { id } = router.query;
  const [product, setProduct] = useState(null);
  const [isFavorite, setIsFavorite] = useState(false);
  const [reviews, setReviews] = useState([]);
  const [newReview, setNewReview] = useState(""); 
  const [rating, setRating] = useState(0);
  const classes = useStyles();

  useEffect(() => {
    if (!id) return; // id가 없으면 로딩하지 않음

    getProduct(setProduct, id, setIsFavorite);
    addRecentProduct(id);
    getReviews(id, setReviews);
  }, [id]);

  const handleToggleFavorite = async () => {
    toggleFavorite(setIsFavorite, isFavorite, id);
  }

  const handleAddReview = async () => {
    if(rating == 0 || newReview.trim() === ""){
      alert("별점과 상품평을 입력하세요.");
      return;
    }

    try{
      const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
      const addReviewResponse = await axios.post(
        `http://localhost:8080/products/${id}/reviews`,
        {
          content: newReview,
          rating: rating,
        },
        {
          headers: {
            Authorization: `Bearer ${loginInfo.accessToken}`,
          }
        }
      );

      setReviews([...reviews, addReviewResponse.data]);
      setNewReview("");
      setRating(0);

    } catch (error){
      console.error("상품평 등록에 실패했습니다.", error);
    }
  }

  // 현재 사용자의 리뷰가 있을 경우
  const WrittenReview = reviews.some(
    (review) => review.memberId === JSON.parse(localStorage.getItem("loginInfo")).memberId
  );

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
        <Button 
        className={classes.addToCartButton}
        onClick={handleToggleFavorite}>
          {isFavorite ? '즐겨찾기 제거' : '즐겨찾기 추가'}
        </Button>
        <Button
          className={classes.backButton}
          onClick={() => router.push('/products')}
        >
          돌아가기
        </Button>
      </Box>
      {/* 이미 작성한 리뷰가 있다면 숨김*/ }
      {!WrittenReview && (
          <Box className={classes.reviewBox}>
            <Rating
              name="new-review-rating"
              value={rating}
              onChange={(event, newValue) => setRating(newValue)}
            />
            <TextField
              label="상품평을 입력하세요"
              fullWidth
              multiline
              rows={3}
              variant="outlined"
              value={newReview}
              onChange={(e) => setNewReview(e.target.value)}
            />
            <Button
              variant="contained"
              color="primary"
              onClick={handleAddReview}
              style={{ marginTop: "16px" }}
            >
              작성 완료
            </Button>
          </Box>
        )}
        {/* 리뷰 리스트 */}
        <Box className={classes.reviewList}>
          {reviews.length > 0 ? (
            reviews.map((review) => (
              <Box key={review.id} className={classes.reviewItem}>
                <Typography variant="subtitle1">
                  작성자: {review.authorName}
                </Typography>
                <Rating value={review.rating} readOnly />
                <Typography variant="body1">{review.content}</Typography>
              </Box>
            ))
          ) : (
            <Typography variant="body1">상품평이 없습니다.</Typography>
          )}
        </Box>
    </Container>
  );
};

export default ProductDetail;
