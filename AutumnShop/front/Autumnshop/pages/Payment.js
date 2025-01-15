import axios from "axios";
import React, { useState } from "react";
import { useEffect } from "react";

const Payment = ({ cartId, quantity, totalPrice }) => {
  const[price, setPrice] = useState();

  useEffect(() => {
    setPrice(totalPrice);
  })

  const paymentSubmit = async () => {
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
    try {
      const orderResponse = await axios.post(
        "http://localhost:8080/orders",
        {
          memberId : loginInfo.memberId
        }
      );

      const paymentResponse = await axios.post(
        "http://localhost:8080/payment",
        {
          cartId: cartId,
          quantity: quantity,
          orderId : orderResponse.data.id,
        },
        {
          headers: {
            Authorization: `Bearer ${loginInfo.accessToken}`,
          },
        }
      );
      if (paymentResponse.status == 200) {
        window.alert("구매가 완료되었습니다!");
        window.location.href = "http://localhost:3000/paymentList";
      }

      const addMileageResponse = await axios.post(
        "http://localhost:8080/mileage/add",
        {
          amount : price
        },
        {
          headers: {
            Authorization: `Bearer ${loginInfo.accessToken}`,
          },
        }
      )
    } catch (error) {
      console.error(error);
    }
  };

  const paymentSubmitClick = () => {
    const confirm = window.confirm("결제를 하시겠습니까?");
    if (confirm) paymentSubmit();
  };

  return (
    <div>
      <button type="submit" onClick={paymentSubmitClick}>
        구매
      </button>
    </div>
  );
};

export default Payment;
