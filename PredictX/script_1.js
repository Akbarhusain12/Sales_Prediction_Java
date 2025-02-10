document.addEventListener("DOMContentLoaded", function () {
    const predictionForm = document.getElementById("predictionForm");
    const predictionResult = document.getElementById("predictionResult");

    predictionForm.addEventListener("submit", function (event) {
        event.preventDefault(); // Prevent page refresh

        // Collect input values into an array
        let features = [];
        for (let i = 0; i < 20; i++) {
            let value = document.getElementById(`feature${i}`).value;
            features.push(parseFloat(value));
        }

        // Validate input values
        if (features.includes(NaN)) {
            alert("Please fill in all fields with valid numbers.");
            return;
        }

        // Send data to Flask API
        fetch("http://127.0.0.1:5000/predict", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ features: features })
        })
        .then(response => response.json())
        .then(data => {
            if (data.prediction !== undefined) {
                predictionResult.textContent = `$${data.prediction.toFixed(2)}`;
            } else {
                predictionResult.textContent = "Error getting prediction";
            }
        })
        .catch(error => {
            console.error("Error:", error);
            predictionResult.textContent = "Prediction failed!";
        });
    });
});
