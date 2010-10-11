$(document).ready(function() {
    var baseUrl = window.location.protocol + "//" + window.location.host + "/loans";

    var authenticated = false;
    var currentLoanId = null;

    // initialize the tabs
    var tabs = $("#tabs");
    tabs.tabs();
    tabs.hide();
    tabs.tabs("disable", "loans_tab");

    tabs.bind('tabsselect', function(event, ui) {
        if (ui.panel.id == "loans_tab") {
            getWaitingApplications();
        }
    });

    var loanAppDialog = $('<div></div>').html('Thank you for your loan application. We will be in touch shortly.').dialog({
        buttons: { "OK": function() {
            $(this).dialog("close");
        }},
        autoOpen: false,
        draggable: false,
        position: 'center',
        resizable: false,
        title: 'Loan Application Submitted',
        modal: true
    });

    var selectionDialog = $('<div></div>').html('Your loan option selection has been made.').dialog({
        buttons: { "OK": function() {
            $(this).dialog("close");
        }},
        autoOpen: false,
        draggable: false,
        position: 'center',
        resizable: false,
        title: 'Loan Option Selection',
        modal: true
    });

    function getElementById() {
        return document.getElementById(arguments[0]);
    }

    function getElementByIdValue() {
        return document.getElementById(arguments[0]).value;
    }

    /**
     * Creates a loan application
     */
    function createLoanApplication() {
        // bind the form data to an object and serialize it using JSON
        var objectJson = new Object();
        var binder = Binder.FormBinder.bind(document.getElementById("request_form"), objectJson);
        objectJson = binder.serialize();
        var formData = JSON.stringify(objectJson);
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: baseUrl + "/application",
            dataType: "json",
            data: formData,
            success: function () {
                loanAppDialog.dialog('open');
                var tabWidget = $("#tabs");
                tabWidget.tabs("enable", "loans_tab");
                tabWidget.tabs("select", "inbox_tab");
            }
        });
    }

    /**
     * Returns all approved applications waiting for the applicant to select a loan option
     */
    function getWaitingApplications() {
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: baseUrl + "/applications/4",
            dataType: "json",
            success: function (applications) {
                var content = $('#loans_tab_options');
                if (applications == null || applications.length == 0) {
                    content.html("An approved loan application was not found. If you submitted an application, it may still be in process.");
                    return;
                }
                var application = applications[0];
                currentLoanId = application.id;
                content.html("");
                $.each(application.options, function(key, option) {
                    content.append("<input type='radio' id='option_radio' value='" + option.type + "'>   " + option.type + "</input><br>");
                });
                content.append("</form>");
            }
        });
    }

    /**
     * Selects a loan option for an approved loan.
     */
    function selectLoanOption() {
        // bind the form data to an object and serialize it using JSON
        var type = $('input:radio[id=option_radio]:checked').val();
        var data = '{"id":"' + currentLoanId + '", "type":"' + type + '"}';

        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: baseUrl + "/application/selection",
            dataType: "json",
            data: data,
            success: function () {
                currentLoanId = null;
                var content = $('#loans_tab_options');
                selectionDialog.dialog('open');
                var tabWidget = $("#tabs");
                tabWidget.tabs("disable", "loans_tab");
                tabWidget.tabs("select", "inbox_tab");
                content.html("");
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
            url: "http://localhost:8181/fabric/security/token",
            dataType: "json",
            data: '{"username":"' + getElementByIdValue("username") + '", "password":"' + getElementByIdValue("password") + '"}',
            success: function () {
                authenticated = true;
                $('#login').hide("slow");
                $("#tabs").show();

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
            url:  window.location.protocol + "//" + window.location.host + "/fabric/security/token",
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

    getElementById('login_btn').onclick = function() {
        if (!authenticated) {
            login();
        }
        return false;
    };

    getElementById('request_btn').onclick = function() {
        createLoanApplication();
        return false;
    };

    getElementById('options_btn').onclick = function() {
        selectLoanOption();
        return false;
    };

});








