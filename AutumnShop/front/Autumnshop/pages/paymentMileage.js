import { alertTitleClasses } from "@mui/material";
import axios from "axios";
import React, { useState, useEffect } from "react";

const paymentMileage = ({ totalPrice, onMileageApply, remainPrice, setRemainPrice }) => {
    const [inMileage, setInMileage] = useState(0);
    const [userMileage, setUseMileage] = useState(0);


    useEffect(() => {
        const userInfo = async () => {
            const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
            try {
                const memberInfoResponse = await axios.get("http://localhost:8080/members/info", {
                  headers: {
                    Authorization: `Bearer ${loginInfo.accessToken}`,
                  },
                });
                setUseMileage(memberInfoResponse.data.totalMileage);
              } catch (error) {
                console.error(error);
              }
        };

        userInfo();
    }, [])
    console.log(userMileage);

    const mileageChange = (e) => {
        setInMileage(Number(e.target.value));
    };

    const applyMileage = () => {
        if(inMileage <= 0){
            alert("0보다 큰 값을 입력하세요.");
            return;
        }
        if(inMileage > totalPrice){
            alert("사용할 마일리지가 총 금액보다 많습니다.")
            return;
        }
    
        const newPrice = totalPrice - inMileage;
        setRemainPrice(newPrice);
        onMileageApply(newPrice, inMileage);
    }
    return (
        <div>
          <h3>마일리지 사용</h3>
          <p>총 금액: {totalPrice.toLocaleString()}원</p>
          <input
            type="number"
            placeholder="사용할 마일리지 입력"
            value={inMileage}
            onChange={mileageChange}
          />
          <button onClick={applyMileage}>사용</button>
          <p>적용 후 금액: {remainPrice.toLocaleString()}원</p>
        </div>
      );
}

export default paymentMileage;