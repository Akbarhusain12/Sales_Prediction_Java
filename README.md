# PredictX - Real-Time Sales Forecasting System 🚀

PredictX is a Java-based real-time sales forecasting system that helps businesses predict future sales and make informed decisions. It features a clean, interactive dashboard, real-time prediction updates, and a smart recommendation engine to optimize inventory and suggest discounts.

## 📌 Features

- 📊 **Real-Time Sales Prediction**: Live forecasting without the need to upload files.
- 🧠 **Machine Learning Model in Java**: Predict future sales trends.
- 📈 **Recommendation System**: Suggests stock levels and discount strategies based on predicted sales.
- 💻 **User Interface**: Built with HTML, CSS, and JavaScript for a responsive and intuitive experience.
- 🖙 **Backend**: Developed using Java (no Spring Boot).
- 💃 **Database**: MySQL for storing and fetching sales data.

---

## 📂 Project Structure

```
PredictX/
├── frontend/
│   ├── index.html
│   ├── styles.css
│   └── script.js
├── backend/
│   ├── Main.java
│   ├── ForecastModel.java
│   └── RecommendationEngine.java
├── database/
│   └── sales_db.sql
└── README.md
```

---

## ⚖️ Tech Stack

- **Frontend**: HTML, CSS, JavaScript
- **Backend**: Java (Standard Edition)
- **Database**: MySQL
- **IDE**: IntelliJ IDEA
- **Version Control**: Git & GitHub

---

## ⚙️ How to Run

### 💪 Prerequisites
- Java 17 or above
- MySQL Server
- IntelliJ IDEA
- A modern web browser

### 💡 Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/predictx.git
   cd predictx
   ```

2. **Import Project into IntelliJ IDEA**
   - Open IntelliJ IDEA and import the `backend/` directory as a Java project.

3. **Configure Database**
   - Create a MySQL database named `sales_db`.
   - Run the script from `database/sales_db.sql` to create tables and insert sample data.
   - Update database credentials in the Java backend if required.

4. **Run Java Application**
   - Run `Main.java` from IntelliJ IDEA to start the backend server.

5. **Launch Frontend**
   - Open `frontend/index.html` in your browser.
   - The dashboard will fetch real-time predictions and recommendations from the backend.

---

## 📈 Project Impact

- Automated sales forecasting improved operational efficiency.
- Real-time updates eliminated the need for manual data uploads.
- Intelligent recommendations helped manage stock and discounts proactively.

---

## 🤝 Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

---
