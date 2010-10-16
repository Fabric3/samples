/*
 * Copyright (c) 2010 Metaform Systems
 *
 * See the NOTICE file distributed with this work for information
 * regarding copyright ownership.  This file is licensed
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
$(document).ready(function() {
    var baseUrl = window.location.protocol + "//" + window.location.host;

    var authenticated = false;
    var recordCache;
    var currentLoanId = null;
    var shown = false;

    var connectedEndpoint;
    var callbackAdded = false;

    // initialize the tabs
    var tabs = $("#tabs");
    tabs.tabs();
    tabs.hide();
    $("#details_div").hide();

    var requestAmountGraph = new GraphModel(1, 20);
    requestAmountGraph.init();

    var approvalAmountGraph = new GraphModel(1, 20);
    approvalAmountGraph.init();

    var timeToCompleteGraph = new GraphModel(1, 20);
    timeToCompleteGraph.init();

    function xAxisTickFormatter(x) {
        return '';
    }

    function yAxisTickFormatter(x) {
        return '';
    }

    function plot(statistics) {
        var options = {
            lines: { show: true },
            points: { show: false },
            yaxis: { min: 100000 , max: 1000000, tickDecimals:0},
            xaxis: { tickDecimals: 0, tickSize: 1, tickFormatter: xAxisTickFormatter},
            colors:['#2694e8']
        };
        var requestAmount = statistics.requestAmount;
        if (requestAmount > 0) {
            requestAmountGraph.add([requestAmount]);
            var placeholder = $("#average_request_amount_chart");
            $.plot(placeholder, requestAmountGraph.data, options);
        }
        var approvalAmount = statistics.approvalAmount;
        if (approvalAmount > 0) {
            approvalAmountGraph.add([approvalAmount]);
            placeholder = $("#average_approval_amount_chart");
            $.plot(placeholder, approvalAmountGraph.data, options);
        }

//        var automated = statistics.timeToAutomatedApproval;
        var manual = statistics.timeToManualApproval/60000;
        if (manual > 0) {
            options = {
                lines: { show: true },
                points: { show: false },
                yaxis: { min: 0 , max: 100, tickDecimals: 2},
                xaxis: { tickDecimals: 0, tickSize: 1, tickFormatter: xAxisTickFormatter},
                colors:['#2694e8']
            };
            timeToCompleteGraph.add([manual]);
            placeholder = $("#average_time_to_complete_chart");
            $.plot(placeholder, timeToCompleteGraph.data, options);
        }

    }

    function getElementById() {
        return document.getElementById(arguments[0]);
    }

    function getElementByIdValue() {
        return document.getElementById(arguments[0]).value;
    }

    function showDetail() {
        var record;
        for (i = 0; i < recordCache.length; i++) {
            if (recordCache[i].id == this.loanId) {
                record = recordCache[i];
                break;
            }
        }
        currentLoanId = record.id;
        var usernameLabel = getElementById("details_username");
        usernameLabel.innerHTML = record.username;
        var creditScoreLabel = getElementById("details_credit_score");
        creditScoreLabel.innerHTML = record.creditScore;
        var amountLabel = getElementById("details_amount");
        amountLabel.innerHTML = record.amount;
        var downPaymentLabel = getElementById("details_down_payment");
        downPaymentLabel.innerHTML = record.downPayment;
        $("#details_div").show();
    }

    function refreshTable() {
        $.ajax({
            type : "GET",
            contentType: "application/json",
            url: baseUrl + "/riskWorkQueue/records",
            dataType: "json",
            success: function(records) {
                currentLoanId = null;
                recordCache = records;
                var table = getElementById("applications_table");

                var rowCount = table.rows.length;
                // remove previous table contents
                for (var i = 0; i < rowCount; i++) {
                    table.deleteRow(i);
                    rowCount--;
                    i--;
                }

                for (i = 0; i < records.length; i++) {
                    var tr = document.createElement("tr");
                    tr.onclick = showDetail;
                    tr.loanId = records[i].id;  // set the cell id to the unique loan id
                    var td1 = document.createElement("td");
                    td1.width = 100;
                    td1.innerHTML = records[i].id;
                    var td2 = document.createElement("td");
                    td2.width = 100;
                    td2.innerHTML = records[i].username;
                    getElementById("applications_table_body").insertBefore(tr, getElementById("applications_table_body").firstChild);
                    tr.insertBefore(td2, tr.firstChild);
                    tr.insertBefore(td1, tr.firstChild);
                }
                table.grid.addCellProp();
                table.grid.addRowProp();

            }
        });
    }

    function updateTable() {
        if (!shown) {
            $("#applications_table").flexigrid(
            {
                url: '/riskWorkQueue/records',
                title: 'Application Approval Queue',
                height:300,
                width: 330,
                colModel : [
                    {display: 'Loan Id', name : 'id', width : 100, sortable : true, align: 'left'},
                    {display: 'Name', name : 'username', width : 190, sortable : true, align: 'left'}
                ],
                striped: true,
                usepager: true,
                useRp: true,
                rp: 15,
                singleSelect: true,
                onSubmit: refreshTable
            });
        } else {
            $('#applications_table').flexOptions().flexReload();
        }
        shown = true;
    }

    function acceptCurrentApplication() {
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: baseUrl + "/riskWorkQueue/assessment",
            dataType: "json",
            data: '{"loanId":"' + currentLoanId + '", "approved":"true"}',
            success: function () {
                updateTable();
            }
        });
    }

    function declineCurrentApplication() {
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: baseUrl + "/riskWorkQueue/assessment",
            dataType: "json",
            data: '{"loanId":"' + currentLoanId + '", "approved":"false"}',
            success: function () {
                updateTable();
            }
        });
    }

    /**
     * Performs a stateful authentication.
     */
    function login() {
        getElementById("login_message").innerHTML = "Logging in...";
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url:  baseUrl + "/fabric/security/token",
            dataType: "json",
            data: '{"username":"' + getElementByIdValue("username") + '", "password":"' + getElementByIdValue("password") + '"}',
            success: function () {
                authenticated = true;
                $('#login').hide("slow");
                $("#tabs").show();
                updateTable();
            }
        });
    }

    /**
     * Logs out the current browser session.
     */
    function logout() {
        $.ajax({
            type: "DELETE",
            contentType: "application/json",
            url:  baseUrl + "/fabric/security/token",
            dataType: "json",
            data: ' ',
            success: function () {
                authenticated = false;
            }
        });
        $('#messagebox').hide("slow");
        $('#loginbox').show();
        authenticated = false;
    }

    function subscribe() {
        function callback(response) {
            if (response.transport != 'polling' && response.state != 'connected' && response.state != 'closed') {
                if (response.status == 200) {
                    var data = response.responseBody;
                    if (data.length == 0) {
                        // no data
                        return;
                    }
                    var statistics = JSON.parse(data);
                    plot(statistics);
                }
            }
        }

        var channelAddress = baseUrl + "/channels/StatisticsChannel";
        var callbackFunction = !callbackAdded ? callback : null;
        var request = $.atmosphere.request = {transport: 'websocket'};
        $.atmosphere.subscribe(channelAddress, callbackFunction, request);
        connectedEndpoint = $.atmosphere.response;
        callbackAdded = true;
    }

    getElementById('login_btn').onclick = function() {
        if (!authenticated) {
            login();
        }
        return false;
    };

    getElementById('accept_btn').onclick = function() {
        acceptCurrentApplication();
        $("#details_div").hide();
        return false;
    };

    getElementById('decline_btn').onclick = function() {
        declineCurrentApplication();
        $("#details_div").hide();
        return false;
    };

    tabs.bind('tabsshow', function(event, ui) {
        if (ui.panel.id == "analyst_tab") {
            subscribe();
            //            plot([]);
        }
    });


});








