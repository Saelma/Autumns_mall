import React, { useEffect, useState, useRef } from "react";
import { AppBar, Tab, Tabs, Toolbar, Typography } from "@mui/material";
import { makeStyles } from "@mui/styles";
import ProductSection from "./productSection";

const useStyles = makeStyles({
  container: {
    marginTop: "50px",
    display: "flex",
    flexDirection: "column",
    justifyContent: "center",
    alignItems: "center",
    width: "100%",
    overflow: "hidden",
  },
  appBarWrapper: {
    position: "fixed",
    top: 0,
    left: 0,
    right: 0,
    zIndex: 1100,
    height: "64px",
  },
  headerSpacer: {
    height: "64px",
  },
  tabContainer: {
    marginTop: "64px",
  },
  appBar: {
    position: "static",
    top: 0,
    left: 0,
    right: 0,
    zIndex: 1100,
    height: "56px",
    backgroundColor: "#333",
  },
  tab: {
    fontSize: "20px",
    color: "white",
    textTransform: "none",
    "&.Mui-selected": {
      backgroundColor: "#d3d3d3",
      color: "black",
    },
    "&:not(.Mui-selected)": {
      color: "white",
    },
  },
  tabIndicator: {
    backgroundColor: "#d3d3d3",
  },
});

const MainPage = () => {
  const [mainProducts, setMainProducts] = useState([]);
  const [clothingProducts, setClothingProducts] = useState([]);
  const [shoesProducts, setShoesProducts] = useState([]);
  const [accessoryProducts, setAccessoryProducts] = useState([]);
  const [value, setValue] = useState(0);
  const classes = useStyles();

  const mainRef = useRef(null);
  const clothingRef = useRef(null);
  const shoesRef = useRef(null);
  const accessoryRef = useRef(null);

  const sectionRefs = [mainRef, clothingRef, shoesRef, accessoryRef];

  // 마일리지 만료 처리
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
    } catch (error) {
      console.error("마일리지 만료 처리 중 오류가 발생했습니다.");
    }
  };

  // 상품 데이터 가져오기
  const fetchProducts = async () => {
    try {
      const response = await fetch("http://localhost:8080/products");
      const data = await response.json();
      
      // 별점 순으로 내림차순 정렬
      const sortedProducts = data.content.sort((a, b) => b.rating.rate - a.rating.rate);

      // 각 카테고리별 상품 리스트 설정
      setMainProducts(sortedProducts.slice(0, 6));
      setClothingProducts(sortedProducts.filter(p => p.category.name === "상의").slice(0, 6)); 
      setShoesProducts(sortedProducts.filter(p => p.category.name === "신발").slice(0, 6));
      setAccessoryProducts(sortedProducts.filter(p => p.category.name === "악세서리").slice(0, 6));
    } catch (error) {
      console.error("상품 정보를 불러오지 못했습니다.");
    }
  };

  useEffect(() => {
    mileageExpire();
  }, []);

  useEffect(() => {
    fetchProducts();
  }, []);

  // 탭 클릭시 위치 조정
  const handleChange = (event, newValue) => {
    setValue(newValue);
  
    const targetElement = sectionRefs[newValue].current;
  
    if (targetElement) {
      const elementPosition = targetElement.getBoundingClientRect().top;
      const offsetPosition = elementPosition + window.pageYOffset - 130; // 앱바 고려하여 좌표 조정
  
      window.scrollTo({
        top: offsetPosition,
        behavior: "smooth",
      });
    }
  };
  
  return (
    <div className={classes.container}>
      <div className={classes.appBarWrapper}>
        <div className={classes.headerSpacer}></div>
        <AppBar position="fixed" className={classes.appBar}>
          <Toolbar>
            <Typography variant="h6" sx={{ flexGrow: 1 }}></Typography>
            
            {/*탭 설정 */}
            <Tabs 
              value={value} 
              onChange={handleChange} 
              aria-label="Product Categories"
              TabIndicatorProps={{
                style: { backgroundColor: "#d3d3d3" },
              }}
            >
              <Tab label="추천 목록" className={classes.tab} />
              <Tab label="의상 추천" className={classes.tab} />
              <Tab label="신발 추천" className={classes.tab} />
              <Tab label="악세서리 추천" className={classes.tab} />
            </Tabs>
          </Toolbar>
        </AppBar>
      </div>

      {/* 각 물건들 설정 */}
      <div className={classes.tabContainer}>
        <ProductSection title="추천 상품" subtitle="요즘 잘나가는 인기 상품" products={mainProducts} sectionRef={mainRef} />
        <ProductSection title="상의" subtitle="인기 상의" products={clothingProducts} sectionRef={clothingRef} />
        <ProductSection title="신발" subtitle="인기 신발" products={shoesProducts} sectionRef={shoesRef} />
        <ProductSection title="악세서리" subtitle="인기 악세서리" products={accessoryProducts} sectionRef={accessoryRef} />
      </div>
    </div>
  );
};

export default MainPage;
