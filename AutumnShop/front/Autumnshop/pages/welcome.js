import React, { useEffect, useState } from "react";
import { Button } from "@mui/material";
import { makeStyles } from "@mui/styles";

const useStyles = makeStyles({
  container: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    height: "100vh",
    width: "100%",
    overflow: "hidden",
  },
  innerContainer: {
    position: "relative",
    width: "600px",
    height: "900px",
    display: "flex",
    flexDirection: "column",
    justifyContent: "center",
    alignItems: "center",
  },
  sectionTitle: {
    width: "100%",
    marginLeft: "20px",
    marginBottom: "20px",
    fontSize: "32px",
    fontWeight: "bold",
  },
  subTitle: {
    width: "100%",
    marginLeft: "20px",
    marginBottom: "40px",
    fontSize: "24px",
    fontWeight: "normal",
  },
  imageContainer: {
    position: "relative",
    width: "100%",
    height: "100%",
    overflow: "hidden",
    marginTop: "-30px",
  },
  productImage: {
    width: "100%",
    height: "100%",
    objectFit: "cover",
    cursor: "pointer",
  },
  buttonsContainer: {
    marginTop: "20px",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
  },
  button: {
    margin: "5px",
    width: "140px",
    height: "30px",
    backgroundColor: "white",
    border: "3px solid black",
    color: "black",
    textTransform: "none",
    borderRadius: "50px",
    transition: "background-color 0.3s, border-color 0.3s",
    outline: "none",
    "&:hover": {
      backgroundColor: "white",
      borderColor: "black",
      color: "black",
    },
    "&:focus": {
      backgroundColor: "white",
      borderColor: "black",
      color: "black",
      outline: "none",
    },
    "&:active": {
      backgroundColor: "white",
      borderColor: "black",
      color: "black",
    },
  },
});

const MainPage = () => {
  const [mainProducts, setMainProducts] = useState([]);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const classes = useStyles();

  // 상품 데이터 가져오기 및 마일리지 만료 처리
  useEffect(() => {
    const mileageExpire = async () => {
      try {
        const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
        if (!loginInfo || !loginInfo.accessToken) {
          console.error("로그인 정보가 없습니다.");
          return;
        }

        // 마일리지 만료 처리
        const getMileageExpireResponse = await fetch(
          "http://localhost:8080/mileage/expire",
          {
            method: "POST",
            headers: {
              Authorization: `Bearer ${loginInfo.accessToken}`,
            },
          }
        );

        if (!getMileageExpireResponse.ok) {
          console.error("마일리지 만료 처리 실패");
        }

        // 상품 데이터 가져오기
        const response = await fetch("http://localhost:8080/products");
        const data = await response.json();
        setMainProducts(data.content);
      } catch (error) {
        console.error("상품 정보를 불러오지 못했습니다.");
      }
    };

    mileageExpire();
  }, []);

  // 자동 이미지 전환 처리
  useEffect(() => {
    if (mainProducts.length === 0) return; // 상품이 없으면 자동 이미지 전환 시작하지 않음

    const intervalId = setInterval(() => {
      setCurrentImageIndex((prevIndex) =>
        prevIndex === mainProducts.length - 1 ? 0 : prevIndex + 1
      );
    }, 3000); // 이미지 전환 시간

    return () => clearInterval(intervalId); // 컴포넌트 언마운트 시 interval 해제
  }, [mainProducts]); // mainProducts가 변경될 때마다 자동 이미지 전환 시작

  const handleImageChange = (index) => {
    setCurrentImageIndex(index); // 이미지 인덱스를 수동으로 변경
  };

  const handleImageClick = (id) => {
    window.location.href = `http://localhost:3000/product/${id}`;
  };

  return (
    <div className={classes.container}>
      <div className={classes.innerContainer}>
        <div className={classes.sectionTitle}>추천 상품</div>
        <div className={classes.subTitle}>요즘 잘나가는 인기 상품</div>

        {mainProducts.length > 0 && (
          <div className={classes.imageContainer}>
            <h2 style={{ textAlign: "center" }}>
              {mainProducts[currentImageIndex]?.name}
            </h2>

            <div
              style={{
                display: "flex",
                transition: "transform 0.5s ease-in-out",
                transform: `translateX(-${currentImageIndex * 600}px)`,
                width: `${mainProducts.length * 600}px`,
              }}
            >
              {mainProducts.map((product, index) => (
                <div
                  key={index}
                  style={{
                    width: "600px",
                    height: "600px",
                    border: "5px solid black",
                    borderRadius: "10px",
                  }}
                >
                  <img
                    src={product.imageUrl}
                    alt={product.name}
                    className={classes.productImage}
                    onClick={() => handleImageClick(product.id)}
                  />
                </div>
              ))}
            </div>

            <div className={classes.buttonsContainer}>
              {mainProducts.map((_, index) => (
                <Button
                  key={index}
                  variant="outlined"
                  className={classes.button}
                  onClick={() => handleImageChange(index)}
                />
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default MainPage;
