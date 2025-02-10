// Sample sales data (Replace this with real data from backend)
const salesData = {
    labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun"], // Months
    datasets: [{
        label: "Sales Trend",
        data: [150, 200, 180, 220, 210, 260], // Example sales numbers
        borderColor: "#007bff",
        backgroundColor: "rgba(0, 123, 255, 0.2)",
        borderWidth: 2,
        fill: true,
    }]
};

// Function to render the sales chart
function renderSalesChart() {
    const ctx = document.getElementById("salesChart").getContext("2d");
    new Chart(ctx, {
        type: "line",
        data: salesData,
        options: {
            responsive: true,
            plugins: {
                legend: { position: "top" }
            },
            scales: {
                y: { beginAtZero: true }
            }
        }
    });
}

// Call function to display chart when page loads
document.addEventListener("DOMContentLoaded", renderSalesChart);
