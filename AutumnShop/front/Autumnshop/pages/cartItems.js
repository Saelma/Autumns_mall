import React from "react";
import { useEffect, useState } from "react";
import { makeStyles } from "@mui/styles";
import axios from "axios";
import Payment from "./Payment";

// CSS 모음
const useStyles = makeStyles((theme) => ({
  cartContainer: {
    width: "800px",
    margin: "20px",
    padding: "20px",
    border: "1px solid #ccc",
    borderRadius: "8px",
    backgroundColor: "#f8f8f8",
  },

  cartTable: {
    width: "800px",
  },
  cartItem: {
    width: "500px",
    border: "1px solid #ddd",
    borderRadius: "8px",
    backgroundColor: "#fff",
    boxShadow: "0 4px 8px rgba(0, 0, 0, 0.2)", // 그림자 효과를 변경
    transition: "transform 0.3s ease-in-out", // 호버 효과를 위한 transition 추가
    "&:hover": {
      transform: "scale(1.05)", // 호버 시 약간 확대됨
      boxShadow: "0 8px 16px rgba(0, 0, 0, 0.3)", // 호버 시 그림자 효과 증가
    },
    flexDirection: "column",
    alignItems: "center",
    textAlign: "center",
  },
  productImage: {
    width: "100px",
    alignSelf: "center",
  },
  quantityInput: {
    width: "50px",
  },
  productDelete: {
    width: "100px",
    alginSelf: "center",
    cursor: "pointer",
  },
}));

// DB 접근 함수
async function getCartItem(loginInfo, setCartItem, setCartMemberId, setUpdatedQuantity) {
  let cartId = 0;

  // 1. 현재 로그인한 아이디에 따라 맞는 카트 가져옴
  const cartIdresponse = await axios
    .get(`http://localhost:8080/carts/${loginInfo.memberId}`, {
      headers: {
        Authorization: `Bearer ${loginInfo.accessToken}`,
      },
    })
    .then((cartIdresponse) => {
      cartId = cartIdresponse.id;
      setCartMemberId(cartIdresponse.data.id);
    });

  // 2. 카트 Id에 맞는 카트 아이템 목록들을 가져옴
  const cartItemsResponse = await axios.get("http://localhost:8080/cartItems", {
    params: { cartId: cartId },
    headers: {
      Authorization: `Bearer ${loginInfo.accessToken}`,
    },
  });

  setCartItem(cartItemsResponse.data);

      // cartItems가 비어 있을 경우 업데이트를 방지하거나 기본값 설정
      const initialQuantities = cartItemsResponse.data.length
      ? cartItemsResponse.data.map(item => item.quantity || 1) // 기본값 1
      : []; 

  setUpdatedQuantity(initialQuantities);

}

async function allDeleteCartItem(cartItemId) {
  const itemId = null;
  const confirm = window.confirm("장바구니를 초기화하겠습니까?");
  if (confirm) {
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
    const allDeleteCartItemResponse = await axios.delete(
      `http://localhost:8080/cartItems/${cartItemId}`,
      {
        headers: {
          Authorization: `Bearer ${loginInfo.accessToken}`,
        },
      }
    );
    window.location.href = "http://localhost:3000/cartItems";
  }
}

async function deleteCartItem(cartItemId, itemId) {
  const confirm = window.confirm("물건을 빼시겠습니까?");
  if (confirm) {
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
    const deleteCartItemResponse = await axios.delete(
      `http://localhost:8080/cartItems/${cartItemId}?id=${itemId}`,
      {
        headers: {
          Authorization: `Bearer ${loginInfo.accessToken}`,
        },
      }
    );
    window.location.href = "http://localhost:3000/cartItems";
  }
}

const CartItems = () => {
  const classes = useStyles();
  const [cartItems, setCartItems] = useState([]);
  const [cartMemberId, setCartMemberId] = useState();
  const [updatedQuantity, setUpdatedQuantity] = useState([]);
  let totalPrice = 0;

  useEffect(() => {
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
    getCartItem(loginInfo, setCartItems, setCartMemberId, setUpdatedQuantity);
  }, []);

  // 카트에 저장된 아이템들의 총 가격
  cartItems.forEach((item, index) => {
    totalPrice += item.productPrice * updatedQuantity[index];
  });

  const QuantityChange = (event, index) => {
    const newQuantity = [...updatedQuantity];
    newQuantity[index] = parseInt(event.target.value);
    setUpdatedQuantity(newQuantity);
    console.log(newQuantity);
  };

  return (
    <div>
      <div className={classes.cartContainer}>
        <h1>장바구니 목록</h1>
        <table className={classes.cartTable}>
          <thead>
            <tr>
              <th>번호</th>
              <th>상품 이름</th>
              <th>상품 가격</th>
              <th>상품 설명</th>
              <th>수량</th>
              <th>이미지</th>
            </tr>
          </thead>
          <tbody className="css">
            {cartItems.map((item, index) => (
              <tr key={item.id} className={classes.cartItem}>
                <td>{index + 1}</td>
                <td>{item.productTitle}</td>
                <td>{item.productPrice}</td>
                <td>{item.productDescription}</td>
                <td>
                  <select
                    className={classes.quantityInput}
                    value={updatedQuantity[index] || 0}
                    onChange={(event) => QuantityChange(event, index)}
                  >
                    {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((value) => (
                      <option key={value} value={value}>
                        {value}
                      </option>
                    ))}
                  </select>
                </td>

                <td>
                  {item.imageUrl && (
                    <img
                      src={item.imageUrl}
                      alt={`Product ${index + 1}`}
                      className={classes.productImage}
                    />
                  )}
                </td>
                <td>
                  <img
                    src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAxlBMVEU9mZkAAAD////Y6+s7hYXLy8sFAAA5iora2tpDfX2+vr4/n5/c7++ntbXe8vIMHx8ICgofFxdudnZ7hYVhaWm/z89NUlIWMDD4+Pjk5OQQAADGxsZ2dnaLi4tFlZXU5uZjY2MkJCSsrKyfn587cHAtQkI5Xl4aGRm4x8fL3NyXl5dLS0svLy8kR0ciMzMXEBA8PDw6aWk7dnYxVFSJk5OSnJwsPT2bp6dCQkIjTk44VVUwYmIpPT0cKCgfICAkMDAzREQRGRkEyR6+AAAJMUlEQVR4nO2de1+iTBTHEQ2zIZ/drqCGmmSSWnnZtrRse/9v6gFdizMzEAwMzOxnfn8Cwvk699s5mv6vSyvbAO5ShPJLEcqvxISWbR+KI9u2ciS02oNevyKe+r1BOwHnd4T23bhskliN7+wshNa12Hg7ja9jUzKG0Lr7UbbxCfXjLoYxmnBQtt2pNEhNeDAp2+aUmrTTEXbKNphBnRSEtmwJuNOEWq3SCNtlm8osWk6lEF6XbWcGXSchlBmQhkgQyg1IQcQJ5S2De+FlESO0I37Wnw5nbkMcubPhtB9hqx1LSP3Vk+toyJcmjrbmOO47NTXiCCkNvbFwhGILCyFn0SJN7kQTUgrhtC4q3k6oPiWNbkcRWkQebTXE5guEGg9EPrUiCO/wJ+8d8QF9ROceN/yOTmjhz92UbXti3eCmW1TCK2kBNQ0vjFc0QjwJ78u2OpXwjGpRCLEx/YNTttGp5GDVzYBC+AieMCSoRcNCDQPY/0gSHsL/YCEXoI+4gACHBCGsZ37IlUcDOXBm8IoghBMXQ9mS0E/EISCY4ITYoEK+JPQTESLYGOERuHsjXxL6iQjb/SOMEBbDednWMmlOLYh7wufwTUPGTOpnU9BgPGOEAP+pbFsZBTs2cYQvMhZDvyC+xBDCqnQmKeEMUNiAEPZoGnwM2InLu3dqAIpDQHjAmRAhpzafDVfDoTuvabw4IeFBDGEt3w/7eOt347OiMyqb1TrAzPcrvmrlECJt/lQhZby6uc+RlEOI5m8Uvq1ai1q+jGUQIoeYQgEJOc01HUsgRHPKhC1kdHNELIEQDmjousmvi1g8ITbsjtBbbohFE6JVIsBK5SMvxIIJUZIsutNtTojFEiI4WovXew4f1IomdG5TEOY0tVcoIaIsesUpl+nZQgnhx/5qM5lEpewm+yeLJUTk+vPF73Ov2/XOj0/P+hTEdQ6JyJcQjoiIJHwemaZZ9WWazWbXWxr4A3msk3AldNz1rOHsORHeG102t3h7+ZRLHDGHiQWOhM50lyb96WJed/zxILb39leziqs5workT5EJ62Bp6/ZyivVmTklAH/EcS8TspZ8f4aYSq0cKX4B4DB/LvlbCixCt4wErx36xM00K4gl47E1YQmKFGdft+Gw5Oq8SmKYHHsu+oseLEF9gjtDk+eT3qNsMczZ74QeMzAWRF2H9u3F8WLe95bHntxbb1tH8De5lXg/ilobfVDQUjU9+jTw/PWF1mnlCg1tNk7KTvdfk4vQXaDczt/ncahpqL5tBmZsLboQpRvOxmmWdBufX4qMh0ZFm0WY6bAR9PmZOjv1SVFukr26oMjaXK7ehsUFyHVsgzWnMVnmdtTEW4hFuKX0589nNW6opGro2LB2cgsb4AWbNXbz2syG+iUv4ieln20WGbMvQOhY/q7/Ltus/HyyHURmGGmWtkO6y7epyk6b/Wqk81GUh/OR06o3hdJIYsyUZ4U5BY153109JMBkm3wQg3GqbbRvu6u0jFvRemnIYoW3p9LPtexTmXHbCnYLzWTX3hZJtWTaAiki4VVA6ncZ6tfnac1N5ZXmRsIQ7bbPtfPj69tB6uGfIoloRhCgXOXXW84D8e97u03856NVlHSFyJkS1ZLOKCdRi3CvFmbCRyzj/r9iWhDkT5paCgdh2Z/Ad48P9uZnFNLPIl5B6rJpdT+IR5uyP6CcDoCJUhIpQESpCuQgnZwwCR61FJzxpMuhULkL6Fpo4KUJFqAh5Epq0LV+766Eb8hI2u+ej8y5l+6U3Oh55X/tOZSU0u6d9/8Jk2cX2tHVPtpMCZ54pN6F53v97aeKFEU1vvxJujEypCbtfszeTcBJ2Q1s3/qLLSQh2kC6/yiKgOWtKTFgNXwxtFe6C9f2uvITmCLz2fF8STbgn8diUlxDu5FaEilARKkJFqAgVoSJUhIpQESpCRagIFaEiVISKUBEqQkWoCBWhIlSEilARKkJFqAgVoSJUhIpQESpCRSg2YdS+NugSciQvYdUMX3z82kJrgvBSMu++BFaHXO0Cl5BLmQmr3f7ntWewC/riK2mrlH9DHkLT25/XGmNb2Xv4deEJgY+2s9Be/WXA+Pyrip25MI8vjMCN+ef15ln4DX3xCPvhFzyHUJpVz/Oq5HkL0/Svh46UNMEbJuIR/gFvAEcP8OSjCmtBpuKdx4cOodMffMIcQzO52efrFwNGBPtq3RMK6xywBUbj7PkDWnjrpUI0vT74eUs8zx8awkKP/Rg1EzOazRF2UPpVQO8tyK1gOtt6706g7ugE/y2bW2jOuZQSWGYyvkigMekgk9E9O28vSjn5Sw7E6BWatyesdNGB4vTA9H3+hKkiPMWKzVtbEd7M8NgrjPrD6u66AJ97ubi8ZvEgXBhhHkUxQxC2AghRLTPiQ53d9XwRniERQygIoI8sYTsL8n2ZLKxjhKZZvlyYx/IGczJ+sDYTxRJqSJsxMT7MsgaWLc5DK9Iaf1L6iTRu5owBEUohDCBRzV3d3F8m0P3Nyq1lx9Pi43LziK2exu9sLh+Mja1ug3s5hLErQ5j/TRsQWuDei6SEL4DCAoQ6uMfkaloAvQIKHRICZ2mtsk1lFPB7/ogRdjhUNUULVjRXGOE1uJtPeN6ChcUOu8YIYWVqlG0tk2Anw8YIdTjskbC9wNqKjY4TwoL4Uba9DPoABB2CEPbbcggKWrDwycwDgtCC87JG+igv5aoOS+HEIgj1K/gfZI97WqjQGzT/SicJYW3KuEZZlogokzaFUO9hD0lUFIkVhZ5OI8QTUR5EcsnEphJiDYY8GZUMhNrR6YQW/mDlPcPkZVFCdTJogRVBiHVOA7WGuUwv8BPS1mQkpWs9ilC/IB6ufDBHfSlACLmUab0LPZqQzKdBOi5qmoCUvkm1BTXglxVDiPfd9vo5HTZqdZEUBDT7STf2QI8j1Af0X21liKMYKwd6PCHeeZNOVzgQQSg5IgFIIZQakQSkEcaWRbGFl8EowqgaVXi1aTBUQt2iNP3Ca2xRWeiEtA6c6KLl0DhC3cKHi2KrZ0eBRBL640V5GHuH0RgxhD5jJ7etahx124lMv28J/bx6RAyLBVPniF7BJCUMdDjoiZmUt71BTO5MQbiVfdBuH4mjdvsgNmsyEMorRSi/FKH8+vcJ/wfHnmmQIvHBowAAAABJRU5ErkJggg=="
                    className={classes.productDelete}
                    onClick={() => deleteCartItem(cartMemberId, item.id)}
                  />
                </td>
              </tr>
            ))}
            <tr>
              <td>총 가격 : {totalPrice}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <Payment cartId={cartMemberId} quantity={updatedQuantity} />
      <button onClick={() => allDeleteCartItem(cartMemberId)}>삭제</button>
    </div>
  );
};

export default CartItems;
