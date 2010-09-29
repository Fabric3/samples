$(document).ready(function() {
    var connectedEndpoint;
    var callbackAdded = false;

    function getKeyCode(ev) {
        if (window.event) {
            return window.event.keyCode;
        }
        return ev.keyCode;
    }

    function getElementById() {
        return document.getElementById(arguments[0]);
    }

    function getTransport(t) {
        transport = t.options[t.selectedIndex].value;
        if (transport == 'autodetect') {
            transport = 'websocket';
        }
        return false;
    }

    function getElementByIdValue() {
        return document.getElementById(arguments[0]).value;
    }

    function subscribe() {
        function callback(response) {
            $.atmosphere.log('info', ["response.state: " + response.state]);
            $.atmosphere.log('info', ["response.transport: " + response.transport]);
            if (response.state != 'connected' && response.state != 'closed') {
                $.atmosphere.log('info', ["response.responseBody: " + response.responseBody]);
                if (response.status == 200) {
                    var data = response.responseBody;
                    var event = JSON.parse(data);
                    var nd = document.createElement('div');
                    if (event.monitorLevel == 'SEVERE') {
                        nd.setAttribute('class', 'messerror');
                        nd.innerHTML = "ERROR: " + event.source + " [" + event.runtime + "]  " + event.data[0].message;
                    } else {
                        nd.setAttribute('class', 'messbody');
                        nd.innerHTML = event.source + " [" + event.runtime + "]  " + event.message;
                    }
                    document.getElementById('board').insertBefore(nd, document.getElementById('board').firstChild);
                    response.responseBody = "";
                }
            }
        }

        var channelAddress = getElementByIdValue('address');
        var callbackFunction = !callbackAdded ? callback : null;
        var request = $.atmosphere.request = {transport: 'websocket'};
        $.atmosphere.subscribe(channelAddress, callbackFunction, request);
        connectedEndpoint = $.atmosphere.response;
        callbackAdded = true;
    }

    function connect() {
        subscribe();
        getElementById('connect').value = "Disconnect";
        getElementById('connect').onclick = function(event) {
            disconnect();
        };
    }

    function disconnect() {
        $.atmosphere.closeSuspendedConnection();
        getElementById('connect').value = "Connect";
        getElementById('connect').onclick = function(event) {
            if (getElementById('address').value == '') {
                alert("Please enter a channel to subscribe to");
                return;
            }
            connect();
        }
    }

    getElementById('connect').onclick = function(event) {
        if (getElementById('address').value == '') {
            alert("Please enter a channel to subscribe to");
            return;
        }
        connect();
    };

    getElementById('address').onkeyup = function(event) {
        getElementById('sendMessage').className = 'hidden';
        var keyc = getKeyCode(event);
        if (keyc == 13 || keyc == 10) {
            connect();
            return false;
        }
    };

    getElementById('message').setAttribute('autocomplete', 'OFF');
})
        ;






