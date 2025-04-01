import { AppBar, Toolbar, Typography, Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Popover, Link } from "@mui/material";
import React, { useState, useEffect } from "react";
import useLogout from "../hooks/useLogout";
import NotificationPopup from "./Notification/notificationPopup";
import { makeStyles } from "@mui/styles";

const useStyles = makeStyles((theme) => ({
  appBar: {
    backgroundColor: "#000",
    color: "#fff",
    zIndex: 1200, // AppBar의 z-index를 높여서 다른 UI 요소 위에 보이도록 설정
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
    alignItems: "center",
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
  popoverPaper: {
    backgroundColor: "#000",
    padding: "16px",
    minWidth: "100px",
    boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
    borderRadius: "8px",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    zIndex: 1300, // Popover의 z-index를 높여서 AppBar와 겹치지 않도록 설정
  },
  nestedButton: {
    padding: "6px 16px",
    fontSize: "16px",
    border: "2px solid #fff",
    color: "#000",
    backgroundColor: "#fff",
    textAlign: "center",
    marginBottom: "8px",
    width: "150px",
    borderRadius: "20px",
    "&:hover": {
      backgroundColor: "#f0f0f0",
    },
    fontWeight: "bold",
  },
}));

const DesktopAppBar = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);
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

    handleLoginStatusChange();
    window.addEventListener("loginStatusChanged", handleLoginStatusChange);

    return () => {
      window.removeEventListener("loginStatusChanged", handleLoginStatusChange);
    };
  }, []);

  const handlePopoverOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handlePopoverClose = () => {
    setAnchorEl(null);
  };

  const handleMyPageClick = () => {
    window.location.href =`${process.env.NEXT_PUBLIC_AUTUMNMALL_VERCEL_ADDRESS}mypage`;
  }

  const open = Boolean(anchorEl);

  return (
    <AppBar position="fixed" className={classes.appBar}>
      <Toolbar className={classes.toolbar}>
        <div className={classes.logoContainer}>
          <Link href="/welcome" className={classes.link}>
            <Typography variant="h6" className={classes.logo}>
              AutumnsMall
            </Typography>
          </Link>
        </div>

        <div className={classes.buttonContainer}>
          <Link href="/products" className={classes.link}>
            <Button className={classes.button}>상품목록</Button>
          </Link>
          {isLoggedIn && (
            <>
              <Button className={classes.button} onClick={handlePopoverOpen}>
                내역
              </Button>

              <Popover
                open={open}
                anchorEl={anchorEl}
                onClose={handlePopoverClose}
                anchorOrigin={{
                  vertical: "bottom",
                  horizontal: "center",
                }}
                transformOrigin={{
                  vertical: "top",
                  horizontal: "center",
                }}
                PaperProps={{
                  className: classes.popoverPaper,
                }}
              >
                <Link href="/order" className={classes.link}>
                  <Button className={classes.nestedButton}>주문내역</Button>
                </Link>
                <Link href="/cartItems" className={classes.link}>
                  <Button className={classes.nestedButton}>카트목록</Button>
                </Link>
                <Link href="/paymentList" className={classes.link}>
                  <Button className={classes.nestedButton}>구매목록</Button>
                </Link>
              </Popover>

                <Button className={classes.button}
                  onClick={() => handleMyPageClick()}
                >MyPage</Button>
              <Button className={classes.button} onClick={handleLogoutDialogOpen}>
                로그아웃
              </Button>
            </>
          )}

          {isLoggedIn && <NotificationPopup />}

          {!isLoggedIn && (
            <Link href="/login" className={classes.link}>
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
