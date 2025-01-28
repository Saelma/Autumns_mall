import React from "react";
import { CardActions, Button } from "@mui/material";

const Carts = ({ title, price, id, description }) => {
  const handleSubmit = async (event) => {
    event.preventDefault();
  
    try {
      const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
  
      const cartResponse = await fetch("http://localhost:8080/carts", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          memberId: loginInfo.memberId,
        }),
      });
  
      const cartData = await cartResponse.json();
  
      const itemsResponse = await fetch("http://localhost:8080/cartItems", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${loginInfo.accessToken}`,
        },
        body: JSON.stringify({
          cartId: cartData.id,
          productId: id,
          productTitle: title,
          productPrice: price,
          productDescription: description,
          quantity: 1,
        }),
      });
  
      if (itemsResponse.ok) {
        alert("해당 물건을 장바구니에 담았습니다!");
      } else {
        throw new Error("장바구니에 물건을 담는 데 실패했습니다.");
      }
    } catch (error) {
      alert("로그인을 해야합니다!");
    }
  };

  return (
    <CardActions>
      <form onSubmit={handleSubmit}>
        <Button
          type="submit"
          variant="contained"
          color="primary"
          fullWidth
          onSubmit={handleSubmit}
        >
          장바구니 담기
        </Button>
      </form>
    </CardActions>
  );
};

export default Carts;
