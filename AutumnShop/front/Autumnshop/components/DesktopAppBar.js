import { AppBar, Toolbar, Typography, Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material";
import Link from "next/link";
import React, { useState, useEffect } from "react";
import useLogout from "../hooks/useLogout";
import { makeStyles } from "@mui/styles";

const useStyles = makeStyles((theme) => ({
  appBar: {
    backgroundColor: "#000",
    color: "#fff",
  },
  toolbar: {
    display: "flex",
    justifyContent: "flex-start",
    alignItems: "center",
    width: "100%",
    position: "relative",
    paddingTop: "8px",
  },
  logo: {
    textDecoration: "none",
    color: "#fff",
    fontSize: "32px",
    fontWeight: "bold",
    transition: "transform 0.3s ease",
    position: "absolute",
    left: "50%",
    transform: "translateX(-50%)",
    top: "10px",
    "&:hover": {
      transform: "scale(1.2) translateX(-50%)",
    },
  },
  buttonContainer: {
    display: "flex",
    marginLeft: "auto",
    marginRight: "40px",
    marginBottom: "10px",
  },
  button: {
    fontSize: "18px",
    border: "2px solid #fff",
    borderRadius: "4px",
    padding: "6px 16px",
    color: "#fff",
    marginLeft: "16px",
    "&:hover": {
      border: "2px solid #fff",
      backgroundColor: "rgba(255, 255, 255, 0.1)",
    },
  },
  link: {
    textDecoration: "none",
    color: "inherit",
  },
}));

const DesktopAppBar = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const {
    logoutDialogOpen,
    handleLogoutDialogOpen,
    handleLogoutDialogClose,
    handleLogout,
  } = useLogout();

  const classes = useStyles();

  useEffect(() => {
    const handleLoginStatusChange = () => {
      const loginInfo = localStorage.getItem("loginInfo");
      setIsLoggedIn(!!loginInfo);
    };

    // 컴포넌트 마운트 시 초기 로그인 상태 확인
    handleLoginStatusChange();
    window.addEventListener("loginStatusChanged", handleLoginStatusChange);

    return () => {
      window.removeEventListener("loginStatusChanged", handleLoginStatusChange);
    };
  }, []);

  return (
    <AppBar position="static" className={classes.appBar}>
      <Toolbar className={classes.toolbar}>
        <div className={classes.logoContainer}>
          <Link href="/" passHref className={classes.link}>
            <Typography variant="h6" className={classes.logo}>
              AutumnsMall
            </Typography>
          </Link>
        </div>

        <div className={classes.buttonContainer}>
          {isLoggedIn && (
            <>
              <Link href="/order" passHref>
                <Button className={classes.button}>주문내역</Button>
              </Link>
              <Link href="/cartItems" passHref>
                <Button className={classes.button}>카트목록</Button>
              </Link>
              <Link href="/paymentList" passHref>
                <Button className={classes.button}>구매목록</Button>
              </Link>
              <Link href="/mypage" passHref>
                <Button className={classes.button}>MyPage</Button>
              </Link>
              <Button className={classes.button} onClick={handleLogoutDialogOpen}>
                로그아웃
              </Button>
            </>
          )}

          {!isLoggedIn && (
            <Link href="/login" passHref>
              <Button className={classes.button}>로그인</Button>
            </Link>
          )}
        </div>

        <Dialog
          open={logoutDialogOpen}
          onClose={handleLogoutDialogClose}
          aria-labelledby="logout-dialog-title"
          aria-describedby="logout-dialog-description"
        >
          <DialogTitle id="logout-dialog-title">로그아웃</DialogTitle>
          <DialogContent>
            <DialogContentText id="logout-dialog-description">
              로그아웃하시겠습니까?
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleLogoutDialogClose} color="primary">취소</Button>
            <Button onClick={handleLogout} color="primary" autoFocus>확인</Button>
          </DialogActions>
        </Dialog>
      </Toolbar>
    </AppBar>
  );
};

export default DesktopAppBar;
