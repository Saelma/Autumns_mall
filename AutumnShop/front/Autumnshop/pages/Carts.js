import React from "react";
import { CardActions, Button } from "@mui/material";
import axios from "axios";

const Carts = ({ title, price, id, description }) => {
  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
      const response = await axios.post("http://localhost:8080/carts", {
        memberId: loginInfo.memberId,
      });

      const itemsResponse = await axios.post(
        "http://localhost:8080/cartItems",
        {
          cartId: response.data.id,
          productId: id,
          productTitle: title,
          productPrice: price,
          productDescription: description,
          quantity: 1,
        },
        {
          headers: {
            Authorization: `Bearer ${loginInfo.accessToken}`,
          },
        }
      );
    } catch (error) {
      alert("로그인을 해야합니다!.");
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
