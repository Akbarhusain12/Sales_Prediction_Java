function uploadFile() {
    const fileInput = document.getElementById("fileInput");
    const uploadStatus = document.getElementById("uploadStatus");

    if (!fileInput.files.length) {
        uploadStatus.innerText = "❌ Please select a file!";
        return;
    }

    const file = fileInput.files[0];
    const formData = new FormData();
    formData.append("file", file);

    fetch("http://localhost:8080/upload", {
        method: "POST",
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        uploadStatus.innerText = `✅ Estimated Sales: ${data.predictedSales} units will be sold. 📦`;
    })
    .catch(error => {
        console.error("Error:", error);
        uploadStatus.innerText = "❌ Error uploading file.";
    });
}
