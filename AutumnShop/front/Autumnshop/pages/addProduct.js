import { useState, useEffect } from "react";
import { makeStyles } from "@mui/styles";
import axios from 'axios';

// 스타일 정의
const useStyles = makeStyles(() => ({
  container: {
    width: "600px",
    margin: "50px auto",
    padding: "30px",
    border: "4px solid #000",
    borderRadius: "15px",
    backgroundColor: "#fff",
    boxShadow: "5px 5px 15px rgba(0, 0, 0, 0.2)",
    textAlign: "center",
  },

  title: {
    fontSize: "28px",
    fontWeight: "bold",
    marginBottom: "20px",
  },

  errorText: {
    color: "red",
    fontSize: "16px",
    fontWeight: "bold",
    marginBottom: "12px",
  },

  form: {
    display: "flex",
    flexDirection: "column",
    gap: "15px",
  },

  input: {
    padding: "14px",
    fontSize: "18px",
    border: "3px solid #000",
    borderRadius: "8px",
    width: "95%",
  },

  textarea: {
    padding: "14px",
    fontSize: "18px",
    border: "3px solid #000",
    borderRadius: "8px",
    height: "120px",
    resize: "none",
    width: "95%",
  },

  select: {
    padding: "14px",
    fontSize: "18px",
    border: "3px solid #000",
    borderRadius: "8px",
    width: "100%",
  },

  fileInput: {
    padding: "12px",
    fontSize: "16px",
    border: "3px solid #000",
    borderRadius: "8px",
    backgroundColor: "#f9f9f9",
  },

  submitButton: {
    marginTop: "20px",
    padding: "15px",
    fontSize: "18px",
    fontWeight: "bold",
    color: "#fff",
    backgroundColor: "#000",
    border: "3px solid #000",
    borderRadius: "10px",
    cursor: "pointer",
    transition: "background-color 0.3s ease, transform 0.2s ease",

    "&:hover": {
      backgroundColor: "#444",
      transform: "scale(1.08)",
    },

    "&:active": {
      backgroundColor: "#222",
      transform: "scale(0.95)",
    },
  },
}));

export default function AddProduct() {
  const classes = useStyles();
  const [categories, setCategories] = useState([]);
  const [isAdmin, setIsAdmin] = useState(null);
  const [formData, setFormData] = useState({
    title: "",
    price: "",
    description: "",
    categoryId: "",
    count: "",
    image: null,
  });
  const [error, setError] = useState("");

  useEffect(() => {
    const getUserInfo = async () => {
      const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));

      if (!loginInfo || !loginInfo.accessToken) {
        window.location.href = "/login";
        return;
      }

      try {
        const response = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}members/info`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${loginInfo.accessToken}`,
          },
        });

        if(!response.ok){
          throw new error;
        }

        const data = await response.json();

        if (data.roles.some(role => role.name === "ROLE_ADMIN")) {
          setIsAdmin(true);
        } else {
          setIsAdmin(false);
        }

        if(data.roles[0].name != "ROLE_ADMIN"){
          throw error;
        }
      } catch (error) {
        window.location.href = "/welcome";
      }
    };

    getUserInfo();
  }, []);

  useEffect(() => {
    async function fetchCategories() {
      try {
        const response = await fetch(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}categories`);
        const data = await response.json();
        setCategories(data);
      } catch (error) {
        console.error("카테고리 불러오기 실패:", error);
      }
    }
    fetchCategories();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleImageChange = (e) => {
    setFormData({ ...formData, image: e.target.files[0] });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.title || !formData.price || !formData.description || !formData.categoryId || !formData.count || !formData.image) {
      setError("모든 항목을 입력하세요.");
      return;
    }

    const formDataToSend = new FormData();
    formDataToSend.append("addProductDto", JSON.stringify({
      title: formData.title,
      price: formData.price,
      description: formData.description,
      categoryId: formData.categoryId,
      count: formData.count,
    }));
    formDataToSend.append("image", formData.image);
    
    try {
      const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
      
      const response = await axios.post(`${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}products`, 
        formDataToSend, {
        headers: {
          "Authorization": `Bearer ${loginInfo.accessToken}`,
        }
      });

      if (!response.status == 200) {
        throw new Error("등록 실패");
      }

      alert("상품이 등록되었습니다.");
      window.location.href="http://localhost:3000/products"
    } catch (error) {
      console.error(error);
      setError("상품 등록 중 오류 발생");
    }
  };

  
  // 아직 관리자 여부 확인이 안 되었으면 로딩 표시
  if (isAdmin === null) {
    return <p>로딩 중...</p>;
  }

  // ROLE_ADMIN이 아니면 아무것도 렌더링하지 않음
  if (!isAdmin) {
    return null;
  }

  return (
    <div className={classes.container}>
      <h2 className={classes.title}>상품 등록</h2>
      {error && <p className={classes.errorText}>{error}</p>}
      <form className={classes.form} onSubmit={handleSubmit}>
        <input className={classes.input} type="text" name="title" placeholder="상품명" value={formData.title} onChange={handleChange} required />
        <input className={classes.input} type="number" name="price" placeholder="가격" value={formData.price} onChange={handleChange} required />
        <textarea className={classes.textarea} name="description" placeholder="설명" value={formData.description} onChange={handleChange} required />
        <input className={classes.input} type="number" name="count" placeholder="수량" value={formData.count} onChange={handleChange} required />
        
        <select className={classes.select} name="categoryId" value={formData.categoryId} onChange={handleChange} required>
          <option value="">카테고리 선택</option>
          {categories.map((category) => (
            <option key={category.id} value={category.id}>{category.name}</option>
          ))}
        </select>

        <input className={classes.fileInput} type="file" name="image" accept="image/*" onChange={handleImageChange} required />
        <button className={classes.submitButton} type="submit">등록</button>
      </form>
    </div>
  );
}