import React, { useState } from "react";
import { useEffect } from "react";
import { makeStyles } from "@mui/styles";
import PaymentMileage from "./paymentMileage";

const useStyles = makeStyles(() => ({
  container: {
    padding: "20px",
    border: "2px solid #333",
    borderRadius: "8px",
    backgroundColor: "#f8f8f8",
    textAlign: "center",
    maxWidth: "400px",
    margin: "0 auto",
  },
  buttonContainer: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    width: "100%",
  },
  button: {
    padding: "15px 30px",
    fontSize: "18px",
    fontWeight: "bold",
    color: "#fff",
    backgroundColor: "#000",
    border: "2px solid #000",
    borderRadius: "8px",
    cursor: "pointer",
    transition: "background-color 0.3s ease, transform 0.2s ease",
    marginTop: "20px",
    "&:hover": {
      backgroundColor: "#333",
      transform: "scale(1.05)",
    },
    "&:active": {
      backgroundColor: "#111",
      transform: "scale(0.95)",
    },
  },
}));

const Payment = ({ cartId, quantity, totalPrice }) => {
  const classes = useStyles();
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

  const paymentVerify = (pg_method, amount, redirect_url, memberData) => {
    return new Promise((resolve, reject) => {
      const { IMP } = window;
      IMP.init(process.env.NEXT_PUBLIC_IMP_MERCHANT_CODE); // 가맹점 식별코드
  
      IMP.request_pay(
        {
          pg: pg_method, // 결제사 (html5_inicis, kcp, tosspayments 등)
          pay_method: "card", // 결제 방식
          merchant_uid: `mid_${new Date().getTime()}`, // 주문번호 (고유한 값 필요)
          name: "AutumnMall 물건 결제",
          amount: amount,
          buyer_email: memberData.email,
          buyer_name: memberData.name,
          buyer_tel: memberData.phone,
          buyer_addr: memberData.roadAddress + ' ' + memberData.detailAddress,
          buyer_postcode: memberData.zipCode,
          m_redirect_url: redirect_url
        },
        function (rsp) {
          if (rsp.success) {
            alert("결제 성공");
            console.log(rsp); 
            resolve(rsp); 
          } else {
            alert(`결제 실패: ${rsp.error_msg}`);
            console.log(rsp);
            reject(new Error(rsp.error_msg));
          }
        }
      );
    });
  };

  const paymentSubmit = async (useMileage) => {
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
    try {
      const orderResponse = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}orders`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${loginInfo.accessToken}`,
        },
        body: JSON.stringify({
          memberId: loginInfo.memberId,
        }),
      });

      const orderData = await orderResponse.json();

      const memberResponse = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}members/info`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${loginInfo.accessToken}`,
        },
      });

      const memberData = await memberResponse.json();

      // 아임포트 결제 실행 후 성공해야 아래 코드 실행
      const rsp = await paymentVerify("kakaopay", remainPrice, `${process.env.NEXT_PUBLIC_AUTUMNMALL_VERCEL_ADDRESS}/welcome/redirect`, memberData);

      if(rsp.success){
        console.log(rsp.imp_uid);
        const paymentResponse = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}payment`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${loginInfo.accessToken}`,
          },
          body: JSON.stringify({
            cartId: cartId,
            quantity: quantity,
            orderId: orderData.id,
            impuid: rsp.imp_uid,
          }),
        });

        if (paymentResponse.status === 200) {
          window.alert("구매가 완료되었습니다!");
          window.location.href = `${process.env.NEXT_PUBLIC_AUTUMNMALL_VERCEL_ADDRESS}paymentList`;
        }

        // 사용한 마일리지가 없을 경우
        if (useMileage === 0) {
          const addMileageResponse = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}mileage/add`, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${loginInfo.accessToken}`,
            },
            body: JSON.stringify({
              amount: price / 100,
            }),
          });
        } // 사용한 마일리지가 있을 경우
        else if (useMileage > 0) {
          const minusMileageResponse = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}mileage/minus`, {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: `Bearer ${loginInfo.accessToken}`,
            },
            body: JSON.stringify({
              amount: useMileage,
            }),
          });
        }
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
      <PaymentMileage
        totalPrice={totalPrice}
        onMileageApply={mileageApply}
        remainPrice={remainPrice}
        setRemainPrice={setRemainPrice}
      />
      <div className={classes.buttonContainer}>
        <button type="submit" onClick={paymentSubmitClick} className={classes.button}>
          구매
        </button>
      </div>
    </div>
  );
};

export default Payment;
