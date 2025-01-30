import React, { useState } from "react";
import { makeStyles } from "@mui/styles";

const useStyles = makeStyles(() => ({
  tableContainer: {
    width: "80%",
    margin: "0 auto",
    padding: "20px",
    border: "3px solid #000",
    borderRadius: "10px",
    backgroundColor: "#f0f0f0",
    textAlign: "center",
    boxShadow: "0px 0px 10px rgba(0, 0, 0, 0.1)",
  },

  heading: {
    fontSize: "24px",
    marginBottom: "20px",
    color: "#333",
  },

  dateContainer: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    gap: "30px",
    backgroundColor: "#f0f0f0",
    padding: "20px",
    borderRadius: "10px",
  },

  dateInput: {
    padding: "15px 25px",
    fontSize: "24px",
    border: "2px solid #ccc",
    borderRadius: "10px",
    width: "150px",
    backgroundColor: "#fff",
    color: "#333",
    cursor: "pointer",
    "&:hover": {
      borderColor: "#000",
    },
    "&:focus": {
      outline: "none",
      borderColor: "#000",
    },
  },

  textStyle: {
    fontSize: "24px",
    fontWeight: "bold",
    color: "#333",
  },

  searchButton: {
    padding: "15px 35px",
    border: "2px solid #000",
    borderRadius: "10px",
    backgroundColor: "#000",
    cursor: "pointer",
    fontWeight: "bold",
    fontSize: "20px",
    color: "#fff",
    "&:hover": {
      backgroundColor: "#333",
    },
  },
}));

const paymentDate = () => {
  const classes = useStyles();
  const [selectedYear, setSelectedYear] = useState(2025);
  const [selectedMonth, setSelectedMonth] = useState(1);

  const handleChange = (event, setStateFunction) => {
    setStateFunction(parseInt(event.target.value));
  };

  const dateSearch = async () => {
    window.location.href = `http://localhost:3000/paymentList?year=${selectedYear}&month=${selectedMonth}`;
  };

  return (
    <div className={classes.tableContainer}>
      <h1 className={classes.heading}>결제 검색</h1>
      <div className={classes.dateContainer}>
        <select
          className={classes.dateInput}
          value={selectedYear}
          name="paymentYear"
          onChange={(event) => handleChange(event, setSelectedYear)}
        >
          {[2025, 2024, 2023, 2022, 2021, 2020].map((value) => (
            <option key={value} value={value}>
              {value}
            </option>
          ))}
        </select>
        <span className={classes.textStyle}>년</span>
        <select
          className={classes.dateInput}
          value={selectedMonth}
          name="paymentMonth"
          onChange={(event) => handleChange(event, setSelectedMonth)}
        >
          {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12].map((value) => (
            <option key={value} value={value}>
              {value}
            </option>
          ))}
        </select>
        <span className={classes.textStyle}>월</span>
        <button onClick={dateSearch} className={classes.searchButton}>
          검색
        </button>
      </div>
    </div>
  );
};

export default paymentDate;
