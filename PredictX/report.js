document.addEventListener("DOMContentLoaded", function () {
    // ApexCharts configuration for Historical Sales Data
    var historicalOptions = {
        series: [
            { name: 'Net Profit', data: [50, 65, 72, 80, 90, 85, 95, 100] },
            { name: 'Revenue', data: [100, 120, 140, 160, 180, 175, 190, 210] },
            { name: 'Free Cash Flow', data: [40, 50, 55, 60, 70, 75, 80, 85] }
        ],
        chart: { type: 'bar', height: 350 },
        plotOptions: { bar: { horizontal: false, columnWidth: '55%', borderRadius: 5, borderRadiusApplication: 'end' } },
        dataLabels: { enabled: false },
        stroke: { show: true, width: 2, colors: ['transparent'] },
        xaxis: {
            categories: ['Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep'],
            labels: { style: { fontSize: '16px', fontWeight: 'bold' } }
        },
        yaxis: {
            title: { text: '₹ (thousands)' },
            labels: { style: { fontSize: '16px', fontWeight: 'bold' } }
        },
        fill: { opacity: 1 },
        tooltip: { y: { formatter: function (val) { return "₹ " + val + "K"; } } }
    };

    var historicalChart = new ApexCharts(document.querySelector("#chart-historical"), historicalOptions);
    historicalChart.render();

    var hr1 = document.createElement("hr");
    hr1.style.margin = "30px 0";
    document.querySelector("#chart-historical").parentNode.appendChild(hr1);

    var budgetOptions = {
        series: [
            { name: "Actual Sales", data: [12, 45, 40, 60, 55, 75, 85, 100, 150] },
            { name: "Budgeted Sales", data: [15, 40, 45, 50, 60, 70, 90, 110, 140] }
        ],
        chart: {
            height: 350,
            type: 'line',
            zoom: { enabled: false }
        },
        dataLabels: { enabled: false },
        stroke: { curve: 'smooth', width: 3 },
        grid: { row: { colors: ['#f3f3f3', 'transparent'], opacity: 0.5 } },
        xaxis: {
            categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep'],
            labels: { style: { fontSize: '16px', fontWeight: 'bold' } }
        },
        yaxis: {
            title: { text: '₹ (thousands)' },
            labels: { style: { fontSize: '16px', fontWeight: 'bold' } }
        },
        colors: ['#FF4560', '#008FFB'],
        markers: { size: 5 }
    };

    var budgetChart = new ApexCharts(document.querySelector("#chart-budget"), budgetOptions);
    budgetChart.render();

    var hr2 = document.createElement("hr");
    hr2.style.margin = "30px 0";
    document.querySelector("#chart-budget").parentNode.appendChild(hr2);

    var scatterOptions = {
        series: [
            {
                name: "T-Shirts",
                data: [[20, 15], [25, 18], [28, 20], [30, 25], [35, 30], [40, 35], [45, 40], [50, 42]]
            },
            {
                name: "Jeans",
                data: [[15, 20], [18, 22], [22, 25], [25, 28], [30, 32], [35, 35], [38, 38], [42, 40]]
            },
            {
                name: "Shoes",
                data: [[10, 10], [12, 15], [15, 18], [18, 22], [22, 25], [25, 28], [28, 30], [30, 35]]
            }
        ],
        chart: {
            height: 350,
            type: 'scatter',
            zoom: {
                enabled: true,
                type: 'xy'
            }
        },
        xaxis: {
            tickAmount: 10,
            labels: {
                formatter: function (val) {
                    return parseFloat(val).toFixed(1)
                },
                style: { fontSize: '16px', fontWeight: 'bold' }
            }
        },
        yaxis: {
            tickAmount: 7,
            labels: { style: { fontSize: '16px', fontWeight: 'bold' } }
        }
    };

    var scatterChart = new ApexCharts(document.querySelector("#chart-trends"), scatterOptions);
    scatterChart.render();
});
