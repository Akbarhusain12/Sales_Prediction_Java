// Function to predict sales
function predictSales() {
    const storeCode = document.getElementById("storeCode").value.trim();
    const itemCode = document.getElementById("itemCode").value.trim();
    const resultElement = document.getElementById("result");

    if (!storeCode || !itemCode) {
        alert("Please enter both Store Code and Item Code!");
        return;
    }

    resultElement.innerHTML = "<b>Predicting...</b>"; // Show loading state

    fetch("http://localhost:8080/predict", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ storeCode, itemCode })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Server returned an error!");
        }
        return response.json();
    })
    .then(data => {
        if (data.predictedSales !== undefined) {
            resultElement.innerHTML = `<b>Predicted Sales:</b> ${parseFloat(data.predictedSales).toFixed(2)} units 📊`;
        } else {
            throw new Error("Invalid response from server");
        }
    })
    .catch(error => {
        console.error("Error:", error);
        alert("Failed to fetch prediction. Is the server running?");
        resultElement.innerHTML = "<b>Error fetching prediction.</b>";
    });
}

// Function to toggle table visibility
function toggleTable() {
    let tableContainer = document.getElementById("tableContainer");

    if (tableContainer.style.display === "none" || tableContainer.style.display === "") {
        tableContainer.style.display = "block";
    } else {
        tableContainer.style.display = "none";
    }
}
