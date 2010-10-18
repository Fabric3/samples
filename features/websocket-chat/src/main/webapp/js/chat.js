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
            if (response.transport != 'polling' && response.state != 'connected' && response.state != 'closed') {
                if (response.status == 200) {
                    var data = response.responseBody;
                    var nd = document.createElement('div');
                    nd.setAttribute('class', 'messbody');
                    var message = JSON.parse(data);
                    nd.innerHTML = message.name +": " + message.message;
                    document.getElementById('board').insertBefore(nd, document.getElementById('board').firstChild);
                    response.responseBody = "";
                }
            }
        }

        var channelAddress = getElementByIdValue('address');
        var callbackFunction = !callbackAdded ? callback : null;
        var request = $.atmosphere.request = {transport: getElementByIdValue('transport')};
        $.atmosphere.subscribe(channelAddress, callbackFunction, request);
        connectedEndpoint = $.atmosphere.response;
        callbackAdded = true;
    }

    function connect() {
        getElementById('message').value = '';
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

    function createRequest() {
        return $.atmosphere.request = {headers: ['Content-Type','application/json'], data: '{ "name":"' + getElementByIdValue('name') + '", '
                + ' "message":"' + getElementByIdValue('message') + '"}' };
    }

    getElementById('connect').onclick = function(event) {
        if (getElementById('address').value == '') {
            alert("Please enter a channel to subscribe to");
            return;
        }
        connect();
    }

    getElementById('address').onkeyup = function(event) {
        getElementById('sendMessage').className = 'hidden';
        var keyc = getKeyCode(event);
        if (keyc == 13 || keyc == 10) {
            connect();
            return false;
        }
    }

    getElementById('message').setAttribute('autocomplete', 'OFF');

    getElementById('message').onkeyup = function(event) {
        var keyc = getKeyCode(event);
        if (keyc == 13 || keyc == 10) {
            var address = getElementByIdValue('address');
            connectedEndpoint.push(address, null, createRequest());
            getElementById('message').value = '';
            return false;
        }
        return true;
    };

    getElementById('send').onclick = function(event) {
        if (getElementById('address').value == '') {
            alert("Please enter a message to publish");
            return;
        }
        var address = getElementById('address').value;
        connectedEndpoint.push(address, null, createRequest());
        getElementById('message').value = '';
        return false;
    };
});






