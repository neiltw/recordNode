

$(document).ready(function() {
    $('#table_result').DataTable(
        {
            "order": [[ 0, "desc" ]]
        }
    );
});



function changeMonthPay(){

    var monthPay = $("#monthPay").val();
    var Amount = $("#month").text();
    var total = $("#total").text();
    var cash = $("#cash").text();
    var csrt = $("#csrfToken").val();
    var month_id = $("#month_id").val();
    alert(monthPay)
    $.ajax({
        url:"api/changeMonth",
        type: "post",
        headers: {"Csrf-Token" : csrt},
        data: {
            month_data : monthPay,
            amount : Amount,
            month_id : month_id
        },
        success: function(data){
            $('#modifyMonthMessage').html("success")
        },
        error:function(){
            return "";
        }
    });
    $("#changeMonthPay").html(" <input type='hidden' id='csrfToken' value="+csrt+">   " +
        "<th id='changeMonthPay'><span id='month'>"+Amount+"</span>月/ <input id='monthPay' value="+monthPay+" style='width: 70px'> </th> " +
        "<th><span id='total'>"+total+"</span></th> " +
        "<th><span id='cash'>"+cash+"</span> <button type='sumbit'  onclick='changeMonthPay()'>En</button> </th> " +
        "<input type='hidden' id='month_id' value="+month_id+">");
}
''



// set color
window.chartColors = {
    red: 'rgb(255, 99, 132)',
    orange: 'rgb(255, 159, 64)',
    yellow: 'rgb(255, 205, 86)',
    green: 'rgb(75, 192, 192)',
    blue: 'rgb(54, 162, 235)',
    purple: 'rgb(153, 102, 255)',
    grey: 'rgb(201, 203, 207)'
};


function getChartData(configRef, done) {
    $.ajax({
        url: "api/GetledgerChart",
        type: "GET",
        success: function(data) {

            var ledger_value = [];
            var ledger_key = [];
            for(var w=0;w< data.length;w++){
                var obj = data[w];
                ledger_key.push(obj[0]);
                ledger_value.push(obj[1]);

            }
            // alert(ledger_key +ledger_value);
            configRef.data.datasets[0].data = ledger_value;
            configRef.data.labels = ledger_key;

            done(configRef);
        },
        error:function(){
            return "";
        }
    });
}



window.onload = function () {
    var ctx = document.getElementById("chart-area").getContext("2d");
    // set chart values
    var config = {
        type: 'pie',
        data: {
            datasets: [{
                backgroundColor: [
                    window.chartColors.red,
                    window.chartColors.orange,
                    window.chartColors.yellow,
                    window.chartColors.green,
                    window.chartColors.blue
                ],
                label: 'pieChart'
            }]
        },
        options: {
            responsive: true,
            legend: {
                position: 'top'
            },
            title: {
                display: true,
                text: 'Chart '
            },
            pieceLabel: {
                render: 'label'
            }
        }
    };

    getChartData(config, function (updatedConfig) {
        window.myPie = new Chart(ctx, updatedConfig);
    });
};

