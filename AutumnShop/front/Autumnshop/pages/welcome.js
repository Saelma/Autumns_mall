import React, { useEffect, useState } from "react";
import axios from "axios";


const WelcomePage = () => {
  useEffect(() => {
    const mileageExpire = async () => {
      try {
        const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
        const getMileageExpireResponse = await axios.post(
          "http://localhost:8080/mileage/expire",
          {},
          {
            headers: {
              Authorization: `Bearer ${loginInfo.accessToken}`,
            },
          }
        );
    
      } catch (error) {
        console.error("멤버 정보를 불러오지 못했습니다.");
      }
    };

    mileageExpire();
  }, []);

  return (
    <div>
      <h1>환영합니다.</h1>
    </div>
  );
};

export default WelcomePage;