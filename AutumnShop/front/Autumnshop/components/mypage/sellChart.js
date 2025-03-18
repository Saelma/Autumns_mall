import React, { useEffect, useState } from "react";
import { makeStyles } from "@mui/styles";
import { Typography, TableContainer, Table, TableHead, TableRow, TableCell, TableBody, Paper } from "@mui/material";
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
    }
});

async function getPaymentList(loginInfo, setPaymentList) {
    try {
        const paymentResponse = await fetch("http://localhost:8080/payment/findAll", {
            method: "GET",
            headers: { Authorization: `Bearer ${loginInfo.accessToken}` }
        });
        const paymentData = await paymentResponse.json();
        setPaymentList(paymentData);
    } catch (error) {
        console.log(error);
    }
}

const SellChart = () => {
    const classes = useStyles();
    const [paymentList, setPaymentList] = useState([]);
    const [isAdmin, setIsAdmin] = useState(false);
    const currentYear = new Date().getFullYear();  // 현재 년도

    useEffect(() => {
        const loginInfo = JSON.parse(localStorage.getItem("loginInfo"));
        const getUserInfo = async () => {
      
            if (!loginInfo || !loginInfo.accessToken) {
              window.location.href = "/login";
              return;
            }
      
            try {
              const response = await fetch("http://localhost:8080/members/info", {
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
        getPaymentList(loginInfo, setPaymentList);
    }, []);

    const productSales = {};
    const yearlySales = {};
    const monthlySales = {};
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

    const formatYAxisTicks = (value) => {
        if (value >= 100000000) {
            return `${(value / 100000000).toFixed(1)}억`;
        } else if (value >= 10000) {
            return `${(value / 10000).toFixed(1)}만`;
        }
        return value;
    };

    const formatRevenue = (value) => {
        return value.toLocaleString();
    };

    const chartData = Object.values(productSales).map((product) => ({
        name: product.name,
        sales: product.totalSales,
    }));

    const tableData = Object.values(productSales);
    const totalRevenue = tableData.reduce((sum, product) => sum + product.totalRevenue, 0);

    const yearlySalesData = Object.keys(yearlySales).map((year) => ({
        year,
        revenue: yearlySales[year],
    }));

    const monthlySalesData = Object.keys(monthlySales)
    .map((month) => ({
        month,
        revenue: monthlySales[month],
    }))
    .sort((a, b) => {
        const [yearA, monthA] = a.month.split("-");
        const [yearB, monthB] = b.month.split("-");
        return new Date(yearA, monthA - 1) - new Date(yearB, monthB - 1);  // 날짜를 비교하여 정렬
    });

    if(!isAdmin){
        return null;
    }

    return (
        <div className={classes.sellContainer}>
            <Typography variant="h5" align="center" gutterBottom>
                이번 년도 판매량
            </Typography>
            <div className={classes.chartContainer}>
                <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={chartData}>
                        <XAxis dataKey="name" />
                        <YAxis tickFormatter={formatYAxisTicks} />
                        <Tooltip formatter={(value) => formatRevenue(value)} />
                        <Bar dataKey="sales" fill="#8884d8" />
                    </BarChart>
                </ResponsiveContainer>
            </div>

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

            <Typography variant="h6" align="center" style={{ marginTop: 20 }}>
                이번 달 총 판매 금액: {formatRevenue(totalRevenue)} 원
            </Typography>

            <Typography variant="h5" align="center" gutterBottom className={classes.title}>
                연도별 매출
            </Typography>
            <div className={classes.chartContainer}>
                <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={yearlySalesData}>
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

            <Typography variant="h5" align="center" gutterBottom className={classes.title}>
                월별 매출
            </Typography>
            <div className={classes.chartContainer}>
                <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={monthlySalesData}>
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
        </div>
    );
};

export default SellChart;
