import React, { useState, useEffect } from "react";
import { Button } from "@mui/material";
import { makeStyles } from "@mui/styles";

const useStyles = makeStyles({
  innerContainer: {
    position: "relative",
    width: "600px",
    display: "flex",
    flexDirection: "column",
    justifyContent: "center",
    alignItems: "center",
    marginBottom: "40px",
    height: "auto",
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
    marginBottom: "20px",
    fontSize: "24px",
    fontWeight: "normal",
  },
  imageContainer: {
    position: "relative",
    width: "100%",
    overflow: "hidden",
    height: "auto",
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
  activeButton: {
    backgroundColor: "darkgray !important",
  },
  imageSlide: {
    display: "flex",
    transition: "transform 0.5s ease-in-out",
  },
  imageItem: {
    width: "600px",
    height: "600px",
    border: "5px solid black",
    borderRadius: "10px",
  },
});

const ProductSection = ({ title, subtitle, products, sectionRef }) => {
  const classes = useStyles();
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  const handleImageChange = (index) => {
    setCurrentImageIndex(index);
  };

  const handleImageClick = (id) => {
    window.location.href = `http://localhost:3000/product/${id}`;
  };

  useEffect(() => {
    if (products.length === 0) return;

    const intervalId = setInterval(() => {
      setCurrentImageIndex((prevIndex) =>
        prevIndex === products.length - 1 ? 0 : prevIndex + 1
      );
    }, 3000);

    return () => clearInterval(intervalId);
  }, [products]);

  return (
    <div ref={sectionRef} className={classes.innerContainer}>
      <div className={classes.sectionTitle}>{title}</div>
      <div className={classes.subTitle}>{subtitle}</div>
      {products.length > 0 && (
        <div className={classes.imageContainer}>
          <h2 style={{ textAlign: "center" }}>
            {products[currentImageIndex]?.name}
          </h2>

          {/* 이미지 슬라이드 구현 부분 */}
          <div
            className={classes.imageSlide}
            style={{
              transform: `translateX(-${currentImageIndex * 600}px)`,
              width: `${products.length * 600}px`,
            }}
          >
            {products.map((product, index) => (
              <div key={index} className={classes.imageItem}>
                <img
                  src={product.imageUrl}
                  alt={product.name}
                  className={classes.productImage}
                  onClick={() => handleImageClick(product.id)}
                />
              </div>
            ))}
          </div>

          {/* 버튼 누를 시 다른 이미지로 슬라이드함 */}
          <div className={classes.buttonsContainer}>
            {products.map((_, index) => (
              <Button
                key={index}
                variant="outlined"
                className={`${classes.button} ${
                  currentImageIndex === index ? classes.activeButton : ""
                }`}
                onClick={() => handleImageChange(index)}
              />
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default ProductSection;
