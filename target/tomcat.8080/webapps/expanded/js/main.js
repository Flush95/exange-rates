/**
 * Created by Admin on 24.05.2017.
 */

$(document).ready(function() {

    $.ajax({
        url : '/webapi/resource/fixerLive',
        method : 'POST',
        dataType : 'json',
        success:function(data) {
            console.log(data);
        }
    }).error(function(result) {
        console.log("error: " + result);
    }).success(function(result) {
        console.log("success");
        console.log(result);


        var dataSet = result.map(function (item) {
            return item;
        });

        console.log("before");
        var firstTableTemplate = _.template($("#currentCurrenciesScript").html());
        var firstCompiled = firstTableTemplate({
            data: dataSet
        });
        $("#table1").html(firstCompiled);
    });



    //Visibility of blocks
    $('.firstPointLi').click(function() {
        $('.firstPoint').toggleClass('hidden');
        $('.secondPoint').addClass('hidden');
        $('.thirdPoint').addClass('hidden');
    });

    $('.secondPointLi').click(function() {
        $('.secondPoint').toggleClass('hidden');
        $('.firstPoint').addClass('hidden');
        $('.thirdPoint').addClass('hidden');
    });

    $('.thirdPointLi').click(function() {
        $('.thirdPoint').toggleClass('hidden');
        $('.firstPoint').addClass('hidden');
        $('.secondPoint').addClass('hidden');
    });


    //Submit form, send all form data to server and build table with received response data
    function submitForm(formName, path, method) {
        var formData = $('form[name=' + formName + ']').serializeArray();
        var dataSendObj = {};

        formData.forEach(function(item) {
            dataSendObj[item.name] = item.value
        });
        console.log(dataSendObj);

        return $.ajax({
            url : '/webapi/myresource/' + path,
            method : method,
            data : dataSendObj,
            dataType : 'json',
            success:function(data) {
                console.log(data);
            }
        }).error(function(result) {
            console.log("error: " + result);
        }).success(function(result) {
            console.log("success");
            console.log(result);


            var dataSet = result.map(function (item) {
                return item;
            });

            console.log("before");
            buildChart(result, formName);

            if (path === 'firstTableRequest') {
                var firstTableTemplate = _.template($("#templateRootTable").html());
                var firstCompiled = firstTableTemplate({
                    data: dataSet
                });
                $("#table1").html(firstCompiled);
            } else if (path === 'secondTableRequest') {
                console.log("here");
                var secondTableTemplate = _.template($("#tmpSecondTable").html());
                var secondCompiled = secondTableTemplate({
                    data: dataSet
                });
                $("#table2").html(secondCompiled);
            } else if (path === 'thirdTableRequest') {
                var thirdTableTemplate = _.template($("#tmpThirdTable").html());
                var thirdCompiled = thirdTableTemplate({
                    data: dataSet
                });
                $("#table3").html(thirdCompiled);
            }

        });
    }


    //Chart builder
    function buildChart(result, formName) {
        var income;
        var marginalIncome;
        var profit;
        var fixedCosts;
        var q;
        var variableCosts;

            if (formName === 'FirstTableForm') {
                q = result.map(function(item) {
                    return item.q;
                });
                fixedCosts = result.map(function(item) {
                    return item.fixedCosts;
                });
                var displacedCosts = result.map(function(item) {
                    return item.displacedCosts;
                });
                var generalExpenses = result.map(function(item) {
                    return item.generalExpenses;
                });
                income = result.map(function(item) {
                    return item.income;
                });
                marginalIncome = result.map(function(item) {
                    return item.marginalIncome;
                });
                profit = result.map(function(item) {
                    return item.profit;
                });
                var operatingLever = result.map(function(item) {
                    return item.operatingLever;
                });
                var safetyMargin = result.map(function(item) {
                    return item.safetyMargin;
                });

                var ctx = document.getElementById("myChart");

                Chart.defaults.global.animation.duration = 2000;

                var myChart = new Chart(ctx, {
                    type: "line",
                    data: {
                        labels: q || profit,
                        datasets: [{
                            label: 'Fixed Costs',
                            fill: false,
                            lineTension: 0.1,
                            backgroundColor: "rgba(75,192,192,0.4)",
                            borderColor: "rgba(75,192,192,1)",
                            borderCapStyle: 'butt',
                            borderDash: [],
                            borderDashOffset: 0.0,
                            borderJoinStyle: 'miter',
                            pointBorderColor: "rgba(75,192,192,1)",
                            pointBackgroundColor: "#fff",
                            pointBorderWidth: 1,
                            pointHoverRadius: 5,
                            pointHoverBackgroundColor: "rgba(75,192,192,1)",
                            pointHoverBorderColor: "rgba(220,220,220,1)",
                            pointHoverBorderWidth: 2,
                            pointRadius: 1,
                            pointHitRadius: 10,
                            scaleFontColor: "white",
                            data: fixedCosts || []
                        }, {
                            label: 'Displaced Costs',
                            borderColor: "red",
                            data: displacedCosts || []
                        }, {
                            label: 'General Expenses',
                            borderColor: "green",
                            data: generalExpenses || []
                        }, {
                            label: 'Income',
                            borderColor: "blue",
                            data: income || []
                        }, {
                            label: 'Marginal Income',
                            borderColor: "yellow",
                            data: marginalIncome || []
                        }, {
                            label: 'Operating Lever',
                            borderColor: "pink",
                            data: operatingLever || []
                        }, {
                            label: 'Safety Margin',
                            borderColor: "brown",
                            data: safetyMargin || []
                        }]

                    },
                    options: {
                        scales: {
                            yAxes: [{
                                ticks: {
                                    beginAtZero: false,
                                    fontColor: "black",
                                    fontSize: 13,
                                    fontFamily: "'Damion', cursive"
                                },
                            }],
                            xAxes: [{
                                ticks: {
                                    fontColor: "black",
                                    fontSize: 13,
                                    fontFamily: "'Damion', cursive",
                                }
                            }]
                        },
                        responsive: true,
                        maintainAspectRatio: true,
                        title: {
                            display: true,
                            fontColor: "Black",
                            text: "Chart"
                        },
                    }
                });

            } else if (formName === 'SecondTableForm') {
                q = result.map(function(item) {
                    return item.q;
                });
                fixedCosts = result.map(function(item) {
                    return item.fixedCosts;
                });
                var generalCosts = result.map(function(item) {
                    return item.generalCosts;
                });
                income = result.map(function(item) {
                    return item.income;
                });
                marginalIncome = result.map(function(item) {
                    return item.marginalIncome;
                });
                profit = result.map(function(item) {
                    return item.profit;
                });
                variableCosts = result.map(function(item) {
                    return item.variableCosts;
                });


                var ctxSecond = document.getElementById("secondChart");

                Chart.defaults.global.animation.duration = 2000;

                var secondChart = new Chart(ctxSecond, {
                    type: "line",
                    data: {
                        labels: q || profit,
                        datasets: [{
                            label: 'Fixed Costs',
                            fill: false,
                            lineTension: 0.1,
                            backgroundColor: "rgba(75,192,192,0.4)",
                            borderColor: "rgba(75,192,192,1)",
                            borderCapStyle: 'butt',
                            borderDash: [],
                            borderDashOffset: 0.0,
                            borderJoinStyle: 'miter',
                            pointBorderColor: "rgba(75,192,192,1)",
                            pointBackgroundColor: "#fff",
                            pointBorderWidth: 1,
                            pointHoverRadius: 5,
                            pointHoverBackgroundColor: "rgba(75,192,192,1)",
                            pointHoverBorderColor: "rgba(220,220,220,1)",
                            pointHoverBorderWidth: 2,
                            pointRadius: 1,
                            pointHitRadius: 10,
                            scaleFontColor: "white",
                            data: fixedCosts || []
                        }, {
                            label: 'General Costs',
                            borderColor: "red",
                            data: generalCosts || []
                        }, {
                            label: 'Income',
                            borderColor: "green",
                            data: income || []
                        }, {
                            label: 'Variable Costs',
                            borderColor: "blue",
                            data: variableCosts || []
                        }, {
                            label: 'Marginal Income',
                            borderColor: "yellow",
                            data: marginalIncome || []
                        }, {
                            label: 'Profit',
                            borderColor: "brown",
                            data: profit || []
                        }]

                    },
                    options: {
                        scales: {
                            yAxes: [{
                                ticks: {
                                    beginAtZero: false,
                                    fontColor: "black",
                                    fontSize: 13,
                                    fontFamily: "'Damion', cursive"
                                },
                            }],
                            xAxes: [{
                                ticks: {
                                    fontColor: "black",
                                    fontSize: 13,
                                    fontFamily: "'Damion', cursive",
                                }
                            }]
                        },
                        responsive: true,
                        maintainAspectRatio: true,
                        title: {
                            display: true,
                            fontColor: "Black",
                            text: "Chart"
                        },
                    }
                });

            } else if (formName === 'ThirdTableForm') {
                var p = result.map(function(item) {
                    return item.p;
                });
                fixedCosts = result.map(function(item) {
                    return item.fixedCosts;
                });
                generalCosts =  result.map(function(item) {
                    return item.generalCosts;
                });
                income = result.map(function(item) {
                    return item.income;
                });
                marginalIncome = result.map(function(item) {
                    return item.marginalIncome;
                });
                profit = result.map(function(item) {
                    return item.profit;
                });
                var summedVariableCosts = result.map(function(item) {
                    return item.summedVariableCosts;
                });

                var ctxThird = document.getElementById("thirdChart");

                Chart.defaults.global.animation.duration = 2000;

                var thirdChart = new Chart(ctxThird, {
                    type: "line",
                    data: {
                        labels: p || profit,
                        datasets: [{
                            label: 'Fixed Costs',
                            fill: false,
                            lineTension: 0.1,
                            backgroundColor: "rgba(75,192,192,0.4)",
                            borderColor: "rgba(75,192,192,1)",
                            borderCapStyle: 'butt',
                            borderDash: [],
                            borderDashOffset: 0.0,
                            borderJoinStyle: 'miter',
                            pointBorderColor: "rgba(75,192,192,1)",
                            pointBackgroundColor: "#fff",
                            pointBorderWidth: 1,
                            pointHoverRadius: 5,
                            pointHoverBackgroundColor: "rgba(75,192,192,1)",
                            pointHoverBorderColor: "rgba(220,220,220,1)",
                            pointHoverBorderWidth: 2,
                            pointRadius: 1,
                            pointHitRadius: 10,
                            scaleFontColor: "white",
                            data: fixedCosts || []
                        }, {
                            label: 'General Costs',
                            borderColor: "red",
                            data: generalCosts || []
                        }, {
                            label: 'Income',
                            borderColor: "green",
                            data: income || []
                        }, {
                            label: 'Summed Variable Costs',
                            borderColor: "blue",
                            data: summedVariableCosts || []
                        }, {
                            label: 'Marginal Income',
                            borderColor: "yellow",
                            data: marginalIncome || []
                        }]

                    },
                    options: {
                        scales: {
                            yAxes: [{
                                ticks: {
                                    beginAtZero: false,
                                    fontColor: "black",
                                    fontSize: 13,
                                    fontFamily: "'Damion', cursive"
                                },
                            }],
                            xAxes: [{
                                ticks: {
                                    fontColor: "black",
                                    fontSize: 13,
                                    fontFamily: "'Damion', cursive",
                                }
                            }]
                        },
                        responsive: true,
                        maintainAspectRatio: true,
                        title: {
                            display: true,
                            fontColor: "Black",
                            text: "Chart"
                        },
                    }
                });

            }


    }


    //Button action listeners
    $('.firstTableSubmit').on('click', function() {
        var $btn = $(this).button('loading');
        submitForm("FirstTableForm", "firstTableRequest", "POST").success(function() {
            $btn.button('reset');
        }).error(function() {
            $btn.button('reset');
        });
    });

    $('.secondTableSubmit').on('click', function() {
        var $btn = $(this).button('loading');
        submitForm("SecondTableForm", "secondTableRequest", "POST").success(function() {
            $btn.button('reset');
        }).error(function() {
            $btn.button('reset');
        });
    });

    $('.thirdTableSubmit').on('click', function() {
        var $btn = $(this).button('loading');
        submitForm("ThirdTableForm", "thirdTableRequest", "POST").success(function() {
            $btn.button('reset');
        }).error(function() {
            $btn.button('reset');
        });
    });
});