import { useState, useEffect } from "react";
import { Button, Popover } from "@mui/material";
import NotificationsIcon from "@mui/icons-material/Notifications";
import { useRouter } from "next/router";
import { makeStyles } from "@mui/styles";

const useStyles = makeStyles({
    popupContent: {
        width: "300px",
        padding: "10px",
        backgroundColor: "#f0f0f0",
        borderRadius: "8px",
    },
    reportItem: {
        padding: "10px",
        margin: "5px 0",
        backgroundColor: "#d9d9d9",
        border: "2px solid white",
        borderRadius: "5px",
        cursor: "pointer",
        transition: "background-color 0.2s ease-in-out",
        "&:hover": {
            backgroundColor: "#c4c4c4",
        },
    },
    notificationBadge: {
        backgroundColor: "red",
        color: "white",
        fontSize: "12px",
        fontWeight: "bold",
        padding: "3px 7px",
        borderRadius: "50%",
        position: "absolute",
        top: "-10px",
        right: "10px",
    },
});

function NotificationPopup() {
    const classes = useStyles();
    const [notifications, setNotifications] = useState([]);
    const [anchorEl, setAnchorEl] = useState(null);
    const [isAdmin, setIsAdmin] = useState(false);
    const router = useRouter();

    useEffect(() => {
        const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));

        const getUserInfo = async () => {
            if (!loginInfo || !loginInfo.accessToken) {
                window.location.href = "/login";
                return;
            }

            // 로그인 한 사용자의 권한이 관리자(ADMIN)인지 확인
            const response = await fetch("http://localhost:8080/members/info", {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${loginInfo.accessToken}`,
                },
            });

            const data = await response.json();

            if (data.roles.some(role => role.name === "ROLE_ADMIN")) {
                setIsAdmin(true);
            } else {
                setIsAdmin(false);
            } 
        }

        const fetchNotifications = async () => {
            const res = await fetch("http://localhost:8080/report/notifications", {
                method: "GET",
                headers: { Authorization: `Bearer ${loginInfo?.accessToken}` },
            });
            if (res.ok) {
                const data = await res.json();
                setNotifications(data);
            }
        }
        
        getUserInfo();
        if(isAdmin){
            fetchNotifications();
        }
    }, [isAdmin]);

    const handlePopoverOpen = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handlePopoverClose = () => {
        setAnchorEl(null);
    };

    const handleReportClick = async (reportId) => {
        router.push(`/mypage/report/${reportId}`);
    };

    if(!isAdmin){
        return;
    }

    return (
        <div>
            <Button onClick={handlePopoverOpen}>
                <NotificationsIcon/>
                {notifications.length > 0 && (
                    <span className={classes.notificationBadge}>{notifications.length}</span>
                )}
            </Button>

            <Popover
                open={Boolean(anchorEl)}
                anchorEl={anchorEl}
                onClose={handlePopoverClose}
                anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
                transformOrigin={{ vertical: "top", horizontal: "center" }}
            >
                <div className={classes.popupContent}>
                    {notifications.length === 0 ? (
                        <p>새로운 신고가 없습니다.</p>
                    ) : (
                        notifications.map((report) => (
                            <div
                                key={report.id}
                                onClick={() => handleReportClick(report.id)}
                                className={classes.reportItem}
                            >
                                <h4>{report.reason}</h4>
                                <p>{report.content.slice(0, 100)}...</p>
                            </div>
                        ))
                    )}
                </div>
            </Popover>
        </div>
    );
}

export default NotificationPopup;
