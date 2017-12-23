/**
 * Created by Admin on 07.10.2017.
 */
$(document).ready(function() {

    $("#converterCurrency").change(function() {
        if ($("#converterCurrency").val() === 'UAH (Ukrainian Hryvnia)') {
            $("#converterDate").prop('disabled', true);
        } else {
            $("#converterDate").prop('disabled', false);
        }
    });

    //make chart card
    function createCard(block, url, text) {
        $(block).append('<div class="chip">' +
            '<img src="'+ url +'" alt="Person" width="96" height="96">' +
            '  ' + text +
            '</div>');
    }

    //make notification
    function makeNotification(text) {
        $('#notifyWrapper').append('<div id="snackbar"></div>');
        var x = document.getElementById("snackbar");
        x.className = "show";
        x.append(text);
        setTimeout(function() { x.className = x.className.replace("show", ""); }, 3000);
    }

    var value = $("#fru option:selected").text();
    var toValue = $("#fruFrom option:selected").text();

    var dataLiveSend = {};
    dataLiveSend['liveSend'] = value;
    dataLiveSend['toCurrency'] = toValue;

    //Hot Table Build
    $.ajax({
        url : '/webapi/resource/uahLive',
        method : 'POST',
        data : dataLiveSend,
        dataType : 'json'
    }).error(function(result) {
        makeNotification('Error code: ' + result.responseJSON.errorCode + '\nMessage: ' + result.responseJSON.errorMessage);
    }).success(function(result) {
        console.log(result);

        var dataSet = result.map(function (item) {
            return item;
        });


        var firstTableTemplate = _.template($('#currentCurrenciesScript').html());
        var firstCompiled = firstTableTemplate({
            data: dataSet
        });
        $('#startTable').html(firstCompiled);
    });

    var dataToSend = {};
    dataToSend['baseChartCurrency'] = value;
    dataToSend['toChartCurrency'] = toValue;

    //Hot Chart Build
    $.ajax({
        url : '/webapi/resource/hotChart',
        method : "POST",
        data : dataToSend,
        dataType : 'json'
    }).error(function(result) {
        makeNotification('Error code: ' + result.responseJSON.errorCode + '\nMessage: ' + result.responseJSON.errorMessage);
    }).success(function(result) {
        makeNotification('Success');

        var baseCurrency = result.map(function(item) {
            return item.baseCurrencyName;
        });
        var toCurrency = result.map(function(item) {
            return item.toCurrency;
        });
        var date = result.map(function(item) {
            return item.date;
        });
        var price = result.map(function(item) {
            return item.price;
        });


        var ctx = document.getElementById("hotMyChart");
        Chart.defaults.global.animation.duration = 2000;

        var myChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: date || [],
                datasets: [{
                    label: baseCurrency[0],
                    fill: true,
                    lineTension: 0.5,
                    backgroundColor: "#0074D9",
                    borderColor: "#0074D9",
                    borderCapStyle: 'butt',
                    borderDash: [],
                    borderDashOffset: 0.0,
                    borderJoinStyle: 'miter',
                    pointBorderColor: "#0074D9",
                    pointBackgroundColor: "#fff",
                    pointBorderWidth: 7,
                    pointHoverRadius: 10,
                    pointHoverBackgroundColor: "rgba(75,192,192,1)",
                    pointHoverBorderColor: "rgba(220,220,220,1)",
                    pointHoverBorderWidth: 10,
                    pointRadius: 1,
                    pointHitRadius: 10,
                    scaleFontColor: "#0074D9",
                    fontColor: "#FFFFFF",
                    data: price || []
                }]
            },
            options: {
                scales: {
                    yAxes: [{
                        ticks: {
                            beginAtZero: false,
                            fontColor: "#FFFFFF",
                            fontSize: 10,
                            fontFamily: "'Damion', cursive"
                        }
                    }],
                    xAxes: [{
                        ticks: {
                            fontColor: "#FFFFFF",
                            fontSize: 10,
                            fontFamily: "'Damion', cursive",
                        }
                    }]
                },
                responsive: true,
                maintainAspectRatio: true,
                title: {
                    display: true,
                    fontFamily: "'Damion', cursive",
                    fontColor: "white",
                    text: 'Tendency chart of ' + toCurrency[0] + ' to ' + baseCurrency[0] + ' for last week'
                }
            }
        });

    });

    //Submit form, send all form data to server and build table with received data from response
    function submitForm(formName, path, method, scriptName, divId) {
        $('#notifyWrapper').empty();

        if (formName === 'ratesByDateForm') {
            $('#byDate').hide();
            $('.wrapp').show();
            $('.searchByDate').hide();
        } else if (formName === 'converterForm') {
            $('#byConverter').hide();
            $('.spinnerConverter').show();
            $('.searchInConverter').hide();
        }

        var formData = $('form[name=' + formName + ']').serializeArray();
        var dataSendObj = {};

        formData.forEach(function(item) {
            dataSendObj[item.name] = item.value
        });

        return $.ajax({
            url : '/webapi/resource/' + path,
            method : method,
            data : dataSendObj,
            dataType : 'json',
            success:function(data) {
                console.log(data);
            }
        }).error(function(result) {
            $(".wrapp").hide();
            $('.spinnerConverter').hide();
            $('.searchByDate').hide();
            $('.searchInConverter').hide();
            makeNotification('Error code: ' + result.responseJSON.errorCode + '\nMessage: ' + result.responseJSON.errorMessage);
        }).success(function(result) {
            makeNotification('Success');
            var dataSet = result.map(function (item) {
                return item;
            });

            var firstTableTemplate = _.template($(scriptName).html());
            var firstCompiled = firstTableTemplate({
                data: dataSet
            });

            $(divId).html(firstCompiled);

            if (formName === 'ratesByDateForm') {
                $(".wrapp").hide();
                $("#byDate").show();
                $('.searchByDate').show();
            } else if (formName === 'converterForm') {
                $('.spinnerConverter').hide();
                $('#byConverter').show();
                $('.searchInConverter').show();
            }
        });
    }



    function submitPredictionForm(formName) {
        $('#notifyWrapper').empty();
        $('#predictionBlock').hide();
        $('.loaderSection').show();

        var formData = $('form[name=' + formName + ']').serializeArray();
        var dataSendObj = {};

        formData.forEach(function(item) {
            dataSendObj[item.name] = item.value
        });

        return $.ajax({
            url : '/webapi/resource/dynamics',
            method : 'POST',
            data : dataSendObj,
            dataType : 'json'
        }).error(function(result) {
            $('#predictionBlock').hide();
            $('.loaderSection').hide();
            makeNotification('Error code: ' + result.responseJSON.errorCode + '\nMessage: ' + result.responseJSON.errorMessage);
        }).success(function(result) {
            makeNotification('Success');
            var dataSet = result.map(function (item) {
                return item;
            });

            var firstTableTemplate = _.template($('#predictionScript').html());
            var firstCompiled = firstTableTemplate({
                data: dataSet
            });

            $('#predictionBlock').html(firstCompiled);

            $('.loaderSection').hide();
            $('#predictionBlock').show();
        });
    }

    //Chart builder
    function submitChart(formName) {
        $('#notifyWrapper').empty();
        $("#chartDiv").hide();
        $(".spinnerChart").show();
        $('.chipsBlock').empty();

        var formData = $('form[name=' + formName + ']').serializeArray();
        var dataSendObj = {};

        formData.forEach(function(item) {
            dataSendObj[item.name] = item.value
        });

        return $.ajax({
            url : '/webapi/resource/' + 'chart',
            method : "POST",
            data : dataSendObj,
            dataType : 'json'
        }).error(function(result) {
            $(".spinnerChart").hide();
            $("#chartDiv").show();
            makeNotification('Error code: ' + result.responseJSON.errorCode + '\nMessage: ' + result.responseJSON.errorMessage);
        }).success(function(result) {
            makeNotification('Success');

            var baseCurrency = result.map(function(item) {
                return item.baseCurrencyName;
            });
            var toCurrency = result.map(function(item) {
                return item.toCurrency;
            });
            var date = result.map(function(item) {
                return item.date;
            });
            var price = result.map(function(item) {
                return item.price;
            });


            var max = parseFloat(price[0]);
            var min = parseFloat(price[0]);
            var avg = parseFloat(price[0]);

            for (var i = 1; i < price.length; i++) {
                if (max < price[i]) max = parseFloat(price[i]);
                if (min > price[i]) min = parseFloat(price[i]);
                avg += parseFloat(price[i]);
            }
            avg /= price.length;

            $('#myChart').remove();
            $('#chartDiv').append('<canvas id="myChart" style="width: 100%; height: auto;"></canvas>');

            createCard('.chipsBlock', '/img/arrow-up.png', 'Maximum Value: ' + max);
            createCard('.chipsBlock', '/img/arrow-down.png', 'Minimum Value: ' + min);
            createCard('.chipsBlock', '/img/avg.png', 'Average Value: ' + avg);

            var ctx = document.getElementById("myChart");
            Chart.defaults.global.animation.duration = 2000;

            var myChart = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: date || [],
                    datasets: [{
                        label: toCurrency[0],
                        fill: false,
                        lineTension: 0.1,
                        backgroundColor: "#0074D9",
                        borderColor: "#0074D9",
                        borderCapStyle: 'butt',
                        borderDash: [],
                        borderDashOffset: 0.0,
                        borderJoinStyle: 'miter',
                        pointBorderColor: "#0074D9",
                        pointBackgroundColor: "#fff",
                        pointBorderWidth: 3,
                        pointHoverRadius: 5,
                        pointHoverBackgroundColor: "rgba(75,192,192,1)",
                        pointHoverBorderColor: "rgba(220,220,220,1)",
                        pointHoverBorderWidth: 2,
                        pointRadius: 1,
                        pointHitRadius: 10,
                        scaleFontColor: "#0074D9",
                        fontColor: "#0074D9",
                        data: price || []
                    }]
                },
                options: {
                    scales: {
                        yAxes: [{
                            ticks: {
                                beginAtZero: false,
                                fontColor: "#0074D9",
                                fontSize: 13,
                                fontFamily: "'Damion', cursive"
                            }
                        }],
                        xAxes: [{
                            ticks: {
                                fontColor: "#0074D9",
                                fontSize: 13,
                                fontFamily: "'Damion', cursive",
                            }
                        }]
                    },
                    responsive: true,
                    maintainAspectRatio: true,
                    title: {
                        display: true,
                        fontFamily: "'Damion', cursive",
                        fontColor: "white",
                        text: 'From: ' + baseCurrency[0]
                    }
                }
            });
            $(".spinnerChart").hide();
            $("#chartDiv").show();
        });
    }


    // Submit reload off
    $('#converterForm').submit(function(event){
        event.preventDefault();
    });
    $('#ratesByDateForm').submit(function(event){
        event.preventDefault();
    });
    $('#chartForm').submit(function(event){
        event.preventDefault();
    });
    $('#predictionForm').submit(function(event){
        event.preventDefault();
    });


    //Button action listeners
    $('#sendByDate').on('click', function () {
        submitForm("ratesByDateForm", "byDate", "POST", "#byDateCurrenciesScript", "#byDate");
    });

    $('#sendConverter').on('click', function () {
        submitForm("converterForm", "converter", "POST", "#converterScript", "#byConverter");
    });

    $('#sendChart').on('click', function () {
        submitChart("chartForm");
    });

    $('#sendPrediction').on('click', function () {
        submitPredictionForm("predictionForm");
    });

});