import { makeStyles } from "@material-ui/core/styles";
import axios from "axios";
import React, { useState, useEffect } from "react";

const useStyles = makeStyles(() => ({
  container: {
    padding: "20px",
    border: "1px solid #ccc",
    borderRadius: "8px",
    backgroundColor: "#f8f8f8",
    textAlign: "center",
    maxWidth: "400px",
    margin: "0 auto",
  },
  input: {
    padding: "10px",
    margin: "10px 0",
    borderRadius: "8px",
    border: "1px solid #ddd",
    width: "80%",
    fontSize: "16px",
  },
  button: {
    padding: "10px 20px",
    fontSize: "16px",
    fontWeight: "bold",
    color: "#fff",
    backgroundColor: "#007BFF",
    border: "none",
    borderRadius: "8px",
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
  title: {
    fontSize: "18px",
    fontWeight: "bold",
    marginBottom: "10px",
  },
  totalPrice: {
    fontSize: "16px",
    marginBottom: "10px",
  },
  remainingPrice: {
    fontSize: "16px",
    marginTop: "10px",
    color: "#28a745",
  },
  currentMileage: {
    fontSize: "16px",
    marginTop: "10px",
    color: "#17a2b8",
  },
}));

const paymentMileage = ({ totalPrice, onMileageApply, remainPrice, setRemainPrice }) => {
  const classes = useStyles();
  const [inMileage, setInMileage] = useState(0);
  const [userMileage, setUserMileage] = useState(0);
  const [remainingMileage, setRemainingMileage] = useState(0);

  useEffect(() => {
    const userInfo = async () => {
      const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
      try {
        const memberInfoResponse = await axios.get("http://localhost:8080/members/info", {
          headers: {
            Authorization: `Bearer ${loginInfo.accessToken}`,
          },
        });
        const { totalMileage } = memberInfoResponse.data;
        setUserMileage(totalMileage);
        setRemainingMileage(totalMileage);
      } catch (error) {
        console.error(error);
      }
    };

    userInfo();
  }, []);

  const mileageChange = (e) => {
    setInMileage(Number(e.target.value));
  };

  const applyMileage = () => {
    if (inMileage <= 0) {
      alert("0보다 큰 값을 입력하세요.");
      return;
    }
    if (inMileage > totalPrice) {
      alert("사용할 마일리지가 총 금액보다 많습니다.");
      return;
    }
    if (inMileage > remainingMileage) {
      alert("사용할 마일리지가 보유 마일리지보다 많습니다.");
      return;
    }

    const newPrice = totalPrice - inMileage;
    const newRemainingMileage = remainingMileage - inMileage;

    setRemainPrice(newPrice);
    setRemainingMileage(newRemainingMileage);
    onMileageApply(newPrice, inMileage);
  };

  return (
    <div className={classes.container}>
      <h3 className={classes.title}>마일리지 사용</h3>
      <p className={classes.totalPrice}>총 금액: {totalPrice.toLocaleString()}원</p>
      <p className={classes.currentMileage}>현재 마일리지: {userMileage.toLocaleString()}원</p>
      <input
        type="number"
        placeholder="사용할 마일리지 입력"
        value={inMileage}
        onChange={mileageChange}
        className={classes.input}
      />
      <button onClick={applyMileage} className={classes.button}>
        사용
      </button>
      <p className={classes.remainingPrice}>적용 후 금액: {remainPrice.toLocaleString()}원</p>
      <p className={classes.remainingPrice}>적용 후 마일리지: {remainingMileage.toLocaleString()}원</p>
    </div>
  );
};

export default paymentMileage;
