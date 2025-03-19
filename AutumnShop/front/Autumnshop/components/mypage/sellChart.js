import React, { useEffect, useState } from "react";
import { makeStyles } from "@mui/styles";
import { Typography, TableContainer, Table, TableHead, TableRow, TableCell, TableBody, Paper, CircularProgress, Select, MenuItem, FormControl, InputLabel, Dialog, DialogActions, DialogContent, DialogTitle, Button } from "@mui/material";
import { ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip } from "recharts";

const useStyles = makeStyles({
    chartContainer: {
        height: 300,
        marginBottom: 30,
        width: 700,
    },
    tableContainer: {
        marginTop: 20,
    },
    title: {
        marginTop: 60,
    },
    loaderContainer: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh', // 화면 전체를 채우게 하여 중앙에 표시
    },
    filterContainer: {
        display: 'flex',
        justifyContent: 'center',
        marginTop: 20,
    },
});

// 결제 정보 조회
async function getPaymentList(loginInfo, setPaymentList, setLoading) {
    setLoading(true);
    try {
        const paymentResponse = await fetch("http://localhost:8080/payment/findAll", {
            method: "GET",
            headers: { Authorization: `Bearer ${loginInfo.accessToken}` }
        });
        const paymentData = await paymentResponse.json();
        setPaymentList(paymentData);
    } catch (error) {
        console.log(error);
    } finally {
        setLoading(false); // 데이터 로딩 후 로딩 상태 false
    }
}

const SellChart = () => {
    const classes = useStyles();
    const [paymentList, setPaymentList] = useState([]);
    const [isAdmin, setIsAdmin] = useState(false);
    const [loading, setLoading] = useState(false); // 로딩 상태 관리
    const [filter, setFilter] = useState("yearly"); // 필터 상태 관리 (월별, 연도별)
    const [openModal, setOpenModal] = useState(false);  // 모달 열림 여부
    const [selectedProduct, setSelectedProduct] = useState(null);  // 선택된 제품 정보
    const currentYear = new Date().getFullYear(); // 현재 년도

    useEffect(() => {
        const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
        const getUserInfo = async () => {
            if (!loginInfo || !loginInfo.accessToken) {
                window.location.href = "/login";
                return;
            }

            // 로그인 한 사용자의 권한이 관리자(ADMIN)인지 확인
            try {
                const response = await fetch("http://localhost:8080/members/info", {
                    method: "GET",
                    headers: {
                        Authorization: `Bearer ${loginInfo.accessToken}`,
                    },
                });

                if (!response.ok) {
                    throw new error;
                }

                const data = await response.json();

                if (data.roles.some(role => role.name === "ROLE_ADMIN")) {
                    setIsAdmin(true);
                } else {
                    setIsAdmin(false);
                }

                if (data.roles[0].name !== "ROLE_ADMIN") {
                    throw error;
                }
            } catch (error) {
                window.location.href = "/welcome";
            }
        };

        getUserInfo();
        getPaymentList(loginInfo, setPaymentList, setLoading);
    }, []);

    const productSales = {}; // 각 차트의 물건 판매량
    const yearlySales = {}; // 연도 판매량
    const monthlySales = {}; // 월간 판매량
    const currentYearSales = {}; // 이번 년도 판매량

    paymentList.forEach((payment) => {
        const productId = payment.product.id;
        const productName = payment.product.title;
        const quantity = payment.quantity;
        const price = payment.product.price;
        const [year, month] = payment.date;

        // 이번 년도 판매량만 필터링
        if (year === currentYear) {
            if (!productSales[productId]) {
                productSales[productId] = { name: productName, totalSales: 0, totalRevenue: 0 };
            }
            productSales[productId].totalSales += quantity;
            productSales[productId].totalRevenue += quantity * price;

            if (!currentYearSales[productName]) currentYearSales[productName] = 0;
            currentYearSales[productName] += quantity * price;
        }

        // 연도별 매출
        if (!yearlySales[year]) yearlySales[year] = 0;
        yearlySales[year] += quantity * price;

        const monthKey = `${year}-${month.toString().padStart(2, "0")}`;
        if (!monthlySales[monthKey]) monthlySales[monthKey] = 0;
        monthlySales[monthKey] += quantity * price;
    });

    // Y축 값에 대해 "억" 또는 "만" 단위로 변환하는 함수
    const formatYAxisTicks = (value) => {
        // 1억 이상일 경우 "억" 단위로 변환
        if (value >= 100000000) {
            return `${(value / 100000000).toFixed(1)}억`;
        } // 1만 이상일 경우 "만" 단위로 변환
        else if (value >= 10000) {
            return `${(value / 10000).toFixed(1)}만`;
        }
        return value;
    };

    // 숫자 값을 천 단위로 구분하여 형식화
    const formatRevenue = (value) => {
        return value.toLocaleString();
    };
    

    // 이번 년도 판매량의 각 차트 데이터
    const chartData = Object.values(productSales).map((product) => ({
        name: product.name,
        sales: product.totalSales,
    }));

    const tableData = Object.values(productSales);
    const totalRevenue = tableData.reduce((sum, product) => sum + product.totalRevenue, 0);

    // 연간 판매량의 차트 데이터
    const yearlySalesData = Object.keys(yearlySales).map((year) => ({
        year,
        revenue: yearlySales[year],
    }));


    // 매출 목표 설정
    const targetRevenue = 14260000; // 목표 매출 : 7백만

    // 목표 달성 여부 체크
    const isTargetAchieved = totalRevenue >= targetRevenue;

    // 목표 달성 시 파란색, 미달성 시 빨간색
    const chartBarColor = isTargetAchieved ? "#82ca9d" : "#ff6666";


    // 날짜 형식 MMMM - DD -> 월간 포맷팅
    const formatMonth = (month) => {
        const [year, monthNum] = month.split("-");
        const monthNames = [
            "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"
        ];
        return `${year}년 ${monthNames[parseInt(monthNum, 10) - 1]}`;
    };

    // 월간 판매량의 차트 데이터
    const monthlySalesData = Object.keys(monthlySales)
        .map((month) => ({
            month: formatMonth(month),  // 포맷팅된 월을 사용
            revenue: monthlySales[month],
        }))
        .sort((a, b) => {
            const [yearA, monthA] = a.month.split("년 ");
            const [yearB, monthB] = b.month.split("년 ");
            return new Date(yearA, monthA.replace('월', '') - 1) - new Date(yearB, monthB.replace('월', '') - 1);  // 날짜를 비교하여 정렬
        });

    // 월간, 연도별로 필터링
    const handleFilterChange = (event) => {
        setFilter(event.target.value);
    };


    // 이번 년도 판매량 차트의 그래프 클릭 시 모달 창에 보여주기 위함
    const handleItemClick = (name) => {
        const productData = chartData.find(item => item.name === name);
        const paymentData = paymentList.find(payment => payment.product.title === name);
        if (productData) {
            setSelectedProduct({
                name: productData.name,
                sales: productData.sales,
                price: paymentData.product.price,
            });
            setOpenModal(true);
        }
    };
    // 모달 닫기
    const handleCloseModal = () => {
        setOpenModal(false);
    };

    if (loading) {
        return (
            <div className={classes.loaderContainer}>
                <CircularProgress />
            </div>
        );
    }

    if (!isAdmin) {
        return null;
    }

    return (
        <div className={classes.sellContainer}>
            <Typography variant="h5" align="center" gutterBottom>
                이번 년도 판매량
            </Typography>
            <Typography 
                variant="h6" 
                align="center" 
                gutterBottom
                style={{ color: isTargetAchieved ? 'blue' : 'red' }} // 목표 달성 여부에 따라 색상 변경
            >
                매출 목표: {formatRevenue(targetRevenue)}원 | 현재 매출 : {formatRevenue(totalRevenue)}원 | {isTargetAchieved ? '목표 달성' : '목표 미달성'}
            </Typography>
            <div className={classes.chartContainer}>
                <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={chartData} margin={{ left: 60, right: 20, top: 20, bottom: 20 }}>
                        <XAxis dataKey="name" />
                        <YAxis tickFormatter={formatYAxisTicks} />
                        <Tooltip formatter={(value) => formatRevenue(value)} />
                        <Bar dataKey="sales" fill={chartBarColor}
                        onClick={(data) => handleItemClick(data.name)}/>
                    </BarChart>
                </ResponsiveContainer>
            </div>

            {/* 모달 창 */}
            <Dialog open={openModal} onClose={handleCloseModal}>
                <DialogTitle>제품 상세 정보</DialogTitle>
                <DialogContent>
                    {selectedProduct && (
                        <>
                            <Typography variant="h5">{selectedProduct.name}</Typography> {/* 제품 이름 */}
                            <Typography>판매량: {selectedProduct.sales}</Typography> {/* 판매량 */}
                            <Typography>가격: {formatRevenue(selectedProduct.price)}원</Typography> {/* 가격 */}
                            <Typography>
                                총 매출: {formatRevenue(selectedProduct.sales * selectedProduct.price)}원
                            </Typography> {/* 판매량 * 가격으로 총 매출 계산 */}
                        </>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseModal} color="primary">닫기</Button>
                </DialogActions>
            </Dialog>

            <TableContainer component={Paper} className={classes.tableContainer}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>제품명</TableCell>
                            <TableCell align="right">판매량</TableCell>
                            <TableCell align="right">총 매출 (원)</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {tableData.map((product, index) => (
                            <TableRow key={index}>
                                <TableCell>{product.name}</TableCell>
                                <TableCell align="right">{product.totalSales}</TableCell>
                                <TableCell align="right">{formatRevenue(product.totalRevenue)}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            <div className={classes.filterContainer}>
                <FormControl variant="outlined">
                    <InputLabel>필터</InputLabel>
                    <Select
                        value={filter}
                        onChange={handleFilterChange}
                        label="필터"
                    >
                        <MenuItem value="monthly">월별</MenuItem>
                        <MenuItem value="yearly">연도별</MenuItem>
                    </Select>
                </FormControl>
            </div>

            {filter === "yearly" && (
                <>
                    <div className={classes.chartContainer}>
                        <ResponsiveContainer width="100%" height="100%">
                            <BarChart data={yearlySalesData} margin={{ left: 60, right: 20, top: 20, bottom: 20 }}>
                                <XAxis dataKey="year" />
                                <YAxis tickFormatter={formatYAxisTicks} />
                                <Tooltip formatter={(value) => formatRevenue(value)} />
                                <Bar dataKey="revenue" fill="#82ca9d" />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>

                    <TableContainer component={Paper} className={classes.tableContainer}>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>연도</TableCell>
                                    <TableCell align="right">매출 (원)</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {yearlySalesData.map((data, index) => (
                                    <TableRow key={index}>
                                        <TableCell>{data.year}</TableCell>
                                        <TableCell align="right">{formatRevenue(data.revenue)}</TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </>
            )}

            {filter === "monthly" && (
                <>
                    <div className={classes.chartContainer}>
                        <ResponsiveContainer width="100%" height="100%">
                            <BarChart data={monthlySalesData} margin={{ left: 60, right: 20, top: 20, bottom: 20 }}>
                                <XAxis dataKey="month" />
                                <YAxis tickFormatter={formatYAxisTicks} />
                                <Tooltip formatter={(value) => formatRevenue(value)} />
                                <Bar dataKey="revenue" fill="#ffc658" />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>

                    <TableContainer component={Paper} className={classes.tableContainer}>
                        <Table>
                            <TableHead>
                                <TableRow>
                                    <TableCell>월</TableCell>
                                    <TableCell align="right">매출 (원)</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {monthlySalesData.map((data, index) => (
                                    <TableRow key={index}>
                                        <TableCell>{data.month}</TableCell>
                                        <TableCell align="right">{formatRevenue(data.revenue)}</TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </>
            )}
        </div>
    );
};

export default SellChart;
