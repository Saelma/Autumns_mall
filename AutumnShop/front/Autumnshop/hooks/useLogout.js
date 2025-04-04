// hooks/useLogout.js
import { useState } from "react";
import axios from "axios";

const useLogout = () => {
  const [logoutDialogOpen, setLogoutDialogOpen] = useState(false);

  const handleLogoutDialogOpen = () => {
    setLogoutDialogOpen(true);
  };

  const handleLogoutDialogClose = () => {
    setLogoutDialogOpen(false);
  };

  const handleLogout = async () => {
    const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));

    if (loginInfo) {
      try {
        const response = await axios.delete(
          `${process.env.NEXT_PUBLIC_AUTUMNMALL_ADDRESS}members/logout`,
          {
            headers: {
              Authorization: `Bearer ${loginInfo.accessToken}`,
            },
            data: { refreshToken: loginInfo.refreshToken },
          }
        );

        if (response.status === 200) {
          localStorage.removeItem("loginInfo");
          window.location.href = "/welcome";
        }
      } catch (error) {
        console.error(error);
      }
    }

    setLogoutDialogOpen(false);
  };

  return {
    logoutDialogOpen,
    handleLogoutDialogOpen,
    handleLogoutDialogClose,
    handleLogout,
  };
};

export default useLogout;
