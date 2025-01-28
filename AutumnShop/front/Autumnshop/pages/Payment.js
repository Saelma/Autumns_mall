import React, { useState } from "react";
import { useEffect } from "react";
import PaymentMileage from "./paymentMileage";

const Payment = ({ cartId, quantity, totalPrice }) => {
  const [price, setPrice] = useState();
  const [useMileage, setUseMileage] = useState(0);
  const [remainPrice, setRemainPrice] = useState(totalPrice);

  // 총 가격이 변경될 때마다 마일리지 관련 (적용 후 가격, 적용 할 마일리지) 리셋
  useEffect(() => {
    setPrice(totalPrice);
    setRemainPrice(totalPrice);
    setUseMileage(0);
  }, [totalPrice]);

  // 사용한 마일리지를 계산해 총 계산 업데이트
  const mileageApply = (newPrice, mileage) => {
    setPrice(newPrice);
    setUseMileage(mileage);
  };

  const paymentSubmit = async (useMileage) => {
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
    try {
      const orderResponse = await fetch(
        "http://localhost:8080/orders",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
          memberId : loginInfo.memberId
          }),
        });

        const orderData = await orderResponse.json();

      const paymentResponse = await fetch(
        "http://localhost:8080/payment",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${loginInfo.accessToken}`,
          },
          body: JSON.stringify({
            cartId: cartId,
            quantity: quantity,
            orderId : orderData.id,
          }),
      });

      if (paymentResponse.status == 200) {
        window.alert("구매가 완료되었습니다!");
        window.location.href = "http://localhost:3000/paymentList";
      }

      // 사용한 마일리지가 없을 경우
      if(useMileage == 0){
        const addMileageResponse = await fetch(
          "http://localhost:8080/mileage/add",
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${loginInfo.accessToken}`,
            },
            body: JSON.stringify({
              amount : price / 100
            })
          })

      } // 사용한 마일리지가 있을 경우
      else if(useMileage > 0){
        const minusMileageResponse = await fetch(
          "http://localhost:8080/mileage/minus",
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${loginInfo.accessToken}`,
            },
            body: JSON.stringify({
              amount: useMileage
            })
          })

      }
    } catch (error) {
      console.error(error);
    }
  };

  const paymentSubmitClick = () => {
    const confirm = window.confirm("결제를 하시겠습니까?");
    if (confirm) paymentSubmit(useMileage);
  };

  return (
    <div>
      <PaymentMileage totalPrice={totalPrice} onMileageApply={mileageApply}
       remainPrice={remainPrice} setRemainPrice={setRemainPrice} />
      <button type="submit" onClick={paymentSubmitClick}>
        구매
      </button>
    </div>
  );
};

export default Payment;
