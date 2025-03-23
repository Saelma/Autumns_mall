import { Notifications } from '@mui/icons-material';
import { makeStyles } from '@mui/styles';

const useStyles = makeStyles((theme) => ({
    notificationIcon: {
        position: 'relative',
        cursor: 'pointer',
        fontSize: '2rem',
    },
    notificationCount: {
        position: 'absolute',
        top: -5,
        right: -5,
        backgroundColor: 'red',
        color: 'white',
        borderRadius: '50%',
        width: '100px',
        height: '20px',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        fontSize: '0.8rem',
    },
}));

function NotificationIcon() {
    const classes = useStyles();
    const [unseenCount, setUnseenCount] = useState(0);

    useEffect(() => {
        async function fetchNotifications() {
            const res = await fetch("/api/reports/notifications");
            const data = await res.json();
            setUnseenCount(data.length);
        }

        fetchNotifications();
    }, []);

    return (
        <div className={classes.notificationIcon}>
            <Notifications sx={{ fontSize: '3rem' }}/>
            {unseenCount > 0 && <span className={classes.notificationCount}>{unseenCount}</span>}
        </div>
    );
}

export default NotificationIcon;
