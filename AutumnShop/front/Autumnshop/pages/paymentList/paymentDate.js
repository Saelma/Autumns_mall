import React from "react";
import { useEffect, useState } from "react";

const paymentDate = (classes) => {
  const [selectedYear, setSelectedYear] = useState(2025);
  const [selectedMonth, setSelectedMonth] = useState(1);

  const handleChange = (event, setStateFunction) => {
    setStateFunction(parseInt(event.target.value));
  };

  const dateSearch = async () => {
    window.location.href = `http://localhost:3000/paymentList?year=${selectedYear}&month=${selectedMonth}`;
  };

  return (
    <div className={classes.cartContainer}>
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
      년
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
      월<button onClick={dateSearch}>검색</button>
    </div>
  );
};

export default paymentDate;
