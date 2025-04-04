import React from "react";
import { Button } from "@mui/material";

const Carts = ({ title, price, id, description, classes }) => {
  const isBatchRequest = false;

  const handleSubmit = async (event) => {
    event.preventDefault();
  
    try {
      const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
  
      const cartResponse = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}carts`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${loginInfo.accessToken}`,
        },
        body: JSON.stringify({
          memberId: loginInfo.memberId,
        }),
      });
  
      const cartData = await cartResponse.json();
  
      const itemsResponse = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}cartItems`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${loginInfo.accessToken}`,
          "Batch-Request": isBatchRequest ? "true" : "false", // 배치 요청인지 아닌지 판단
        },
        body: JSON.stringify([{
          cartId: cartData.id,
          productId: id,
          productTitle: title,
          productPrice: price,
          productDescription: description,
          quantity: 1,
        }]),
      });
      
      if (itemsResponse.ok) {
        alert("해당 물건을 장바구니에 담았습니다!");
      }else if(itemsResponse.status === 400){
        alert("잔여수량이 추가하고자 하는 물건보다 작습니다!");
      }else {
        throw new Error("장바구니에 물건을 담는 데 실패했습니다.");
      }
    } catch (error) {
      alert("로그인을 해야합니다!");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <Button
        type="submit"
        className={classes.addToCartButton}
        variant="contained"
        color="primary"
        fullWidth
      >
        장바구니 담기
      </Button>
    </form>
  );
};

export default Carts;
