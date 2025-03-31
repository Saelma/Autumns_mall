import React from "react";
import { useEffect, useState } from "react";
import { makeStyles } from "@mui/styles";
import Payment from "../components/cartItem/Payment";

// CSS 모음
const useStyles = makeStyles((theme) => ({
  cartContainer: {
    padding: '20px',
    backgroundColor: '#fff',
    borderRadius: '8px',
    boxShadow: '0px 0px 10px rgba(0, 0, 0, 0.1)',
    width: '90%',
    margin: '0 auto',
    textAlign: 'center',
  },
  cartTable: {
    width: '100%',
    border: '3px solid #000',
    borderRadius: '8px',
    borderCollapse: 'collapse',
    marginTop: '20px',
    marginLeft: 'auto',
    marginRight: 'auto',
  },
  cartTableHeader: {
    backgroundColor: '#f4f4f4',
    fontWeight: 'bold',
    textAlign: 'left',
    borderBottom: '3px solid #000',
    borderTopLeftRadius: '8px',
    borderTopRightRadius: '8px',
  },
  cartItem: {
    borderBottom: '2px solid #000',
    padding: '10px 0',
  },
  cartItemCell: {
    padding: '8px',
    textAlign: 'center',
  },
  quantityInput: {
    padding: '5px',
    fontSize: '16px',
    borderRadius: '4px',
    border: '1px solid #ddd',
    width: '80px',
  },
  productImage: {
    width: '50px',
    height: '50px',
    objectFit: 'cover',
  },
  productDelete: {
    cursor: 'pointer',
    width: '30px',
    height: '30px'
  },
  totalPriceRow: {
    fontWeight: 'bold',
    fontSize: '18px',
    padding: '10px 0',
    textAlign: 'left',
  },
  deleteButton: {
    marginTop: "20px",
    padding: "10px 20px",
    fontSize: "16px",
    fontWeight: "bold",
    color: "#fff",
    backgroundColor: "#000",
    border: "2px solid #000",
    borderRadius: "8px",
    cursor: "pointer",
    transition: "background-color 0.3s ease, transform 0.2s ease",
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

// DB 접근 함수
async function getCartItem(loginInfo, setCartItem, setCartMemberId, setUpdatedQuantity, setRemainQuantity) {
  let cartId = 0;

  // 1. 현재 로그인한 아이디에 따라 맞는 카트 가져옴
  const cartIdResponse = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}carts/${loginInfo.memberId}`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${loginInfo.accessToken}`,
    },
  });

  const cartData = await cartIdResponse.json();
  cartId = cartData.id;
  setCartMemberId(cartData.id);

  // 2. 카트 Id에 맞는 카트 아이템 목록들을 가져옴
  const cartItemsResponse = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}cartItems`, {
    method: "GET",
    headers: {
      Authorization: `Bearer ${loginInfo.accessToken}`,
    },
    params: { cartId: cartId },
  });

  const cartItemsData = await cartItemsResponse.json();
  setCartItem(cartItemsData);

  // 남은 수량 가져오기
  const remainQuantitities = await Promise.all(
    cartItemsData.map(async (item) => {
      const productResponse = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}products/${item.productId}`);
      const productData = await productResponse.json();
      return productData.rating.count;
    })
  );

  setRemainQuantity(remainQuantitities);

  // cartItems가 비어 있을 경우 업데이트를 방지하거나 기본값 설정
  const initialQuantities = cartItemsData.length
    ? cartItemsData.map(item => item.quantity || 1) // 기본값 1
    : []; 

  setUpdatedQuantity(initialQuantities);
}

async function allDeleteCartItem(cartItemId) {
  const itemId = null;
  const confirm = window.confirm("장바구니를 초기화하겠습니까?");
  if (confirm) {
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
    
    const allDeleteCartItemResponse = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}cartItems/${cartItemId}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${loginInfo.accessToken}`,
      },
    });

    if (allDeleteCartItemResponse.ok) {
      window.location.href = "http://localhost:3000/cartItems";
    } else {
      console.error("장바구니의 물건을 삭제하는 데 실패했습니다.");
    }
  }
}

async function deleteCartItem(cartItemId, itemId) {
  const confirm = window.confirm("물건을 빼시겠습니까?");
  if (confirm) {
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));

    const deleteCartItemResponse = await fetch(
      `${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}cartItems/${cartItemId}?id=${itemId}`,
      {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${loginInfo.accessToken}`,
        },
      }
    );

    if (deleteCartItemResponse.ok) {
      window.location.href = "http://localhost:3000/cartItems";
    } else {
      console.error("장바구니의 물건을 삭제하는 데 실패했습니다.");
    }
  }
}

const CartItems = () => {
  const classes = useStyles();
  const [cartItems, setCartItems] = useState([]);
  const [cartMemberId, setCartMemberId] = useState();
  const [updatedQuantity, setUpdatedQuantity] = useState([]);
  const [remainQuantity, setRemainQuantity] = useState([]);
  let totalPrice = 0;

  useEffect(() => {
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
    getCartItem(loginInfo, setCartItems, setCartMemberId, setUpdatedQuantity, setRemainQuantity);
  }, []);

  // 카트에 저장된 아이템들의 총 가격
  cartItems.forEach((item, index) => {
    totalPrice += item.price * updatedQuantity[index];
  });

  const QuantityChange = (event, index) => {
    const newQuantity = [...updatedQuantity];
    newQuantity[index] = parseInt(event.target.value);

    if (newQuantity[index] > remainQuantity[index]) {
      alert(`잔여 수량은 ${remainQuantity[index]}개입니다. 더 이상 구매할 수 없습니다.`);
      return;
    }
  
    const updatedQuantities = [...updatedQuantity];
    updatedQuantities[index] = newQuantity;
    setUpdatedQuantity(updatedQuantities[index]);
  };

  return (
    <div className={classes.cartContainer}>
      <h1>장바구니 목록</h1>
      <table className={classes.cartTable}>
        <thead>
          <tr className={classes.cartTableHeader}>
            <th className={classes.cartItemCell}>번호</th>
            <th className={classes.cartItemCell}>상품 이름</th>
            <th className={classes.cartItemCell}>상품 가격</th>
            <th className={classes.cartItemCell}>상품 설명</th>
            <th className={classes.cartItemCell}>수량</th>
            <th className={classes.cartItemCell}>잔여 수량</th>
            <th className={classes.cartItemCell}>이미지</th>
            <th className={classes.cartItemCell}>삭제</th>
          </tr>
        </thead>
        <tbody>
          {cartItems.map((item, index) => (
            <tr key={item.id} className={classes.cartItem}>
              <td className={classes.cartItemCell}>{index + 1}</td>
              <td className={classes.cartItemCell}>{item.title}</td>
              <td className={classes.cartItemCell}>{item.price}</td>
              <td className={classes.cartItemCell}>{item.description}</td>
              <td className={classes.cartItemCell}>
                <select
                  className={classes.quantityInput}
                  value={updatedQuantity[index] || 0}
                  onChange={(event) => QuantityChange(event, index)}
                >
                  {remainQuantity[index] &&
                    [...Array(Math.min(remainQuantity[index], 10)).keys()].map((value) => (
                      <option key={value + 1} value={value + 1}>
                        {value + 1}
                      </option>
                    ))}
                </select>
              </td>
              <td className={classes.cartItemCell}>{remainQuantity[index]}</td>
              <td className={classes.cartItemCell}>
                {item.imageUrl && (
                  <img
                    src={item.imageUrl}
                    alt={`Product ${index + 1}`}
                    className={classes.productImage}
                  />
                )}
              </td>
              <td className={classes.cartItemCell}>
              <img
                    src="data:image/png;base64,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"
                    className={classes.productDelete}
                    onClick={() => deleteCartItem(cartMemberId, item.id)}
                  />
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className={classes.totalPriceRow}>
        <span>총액: {totalPrice}</span>
      </div>

      <div className={classes.paymentContainer}>
        <Payment cartId={cartMemberId} quantity={updatedQuantity} totalPrice={totalPrice} />
      </div>
      <button onClick={() => allDeleteCartItem(cartMemberId)} className={classes.deleteButton}>
        장바구니 전체 삭제
      </button>
    </div>
  );
};

export default CartItems;
