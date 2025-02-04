// pages/index.js
import { useEffect } from "react";
import Router from "next/router";

const IndexPage = () => {
  useEffect(() => {
    Router.push("/welcome"); // 기본 페이지
  }, []);

  return null; // 이 페이지는 빈 화면
};

export default IndexPage;
