$(document).ready(function() {
    var authenticated = false;
    var shown = false;

    function getElementById() {
        return document.getElementById(arguments[0]);
    }

    function getElementByIdValue() {
        return document.getElementById(arguments[0]).value;
    }

    /**
     * Executes a command represented by a grid button.
     * @param com  the command
     * @param grid the grid to execute the command against
     */
    function executeCommand(com, grid) {
        if (com == 'Delete') {
            var selected = $('.trSelected', grid);
            if (selected.length > 0) {
                //                confirm('Delete ' + selected.length + ' items?')
                for (var n = 0; n < selected.length; n++) {
                    deleteMessage(selected[n].firstChild.id);
                }
                updateTable();
            }
        } else if (com == 'Add') {
            alert('Add not supported');
        }
    }

    /**
     * Creates or reloads the table.
     */
    function updateTable() {
        if (!shown) {

            $("#messagetable").flexigrid(
            {
                url: 'messages',
                title: 'Current Messages',
                height:500,
                width: 535,
                colModel : [
                    {display: 'Sent By', name : 'name', width : 100, sortable : true, align: 'left'},
                    {display: 'Message', name : 'message', width : 180, sortable : true, align: 'left'}
                ],
                searchitems : [
                    {display: 'Sent By', name : 'name'},
                    {display: 'Message', name : 'message', isdefault: true}
                ],
                buttons : [
                    {name: 'Delete', bclass: 'delete', onpress : executeCommand},
                    {separator: true}
                ],
                striped: true,
                usepager: true,
                useRp: true,
                rp: 15,
                onSubmit: refreshTable
            });
            $('#messagebox').show("slow");
        } else {
            $('#messagetable').flexOptions().flexReload();
        }
        shown = true;
    }

    /**
     * Retries the current messages and populates the table.
     */
    function refreshTable() {
        $.ajax({
            type : "GET",
            contentType: "application/json",
            url:"http://localhost:8181/messages",
            dataType: "json",
            success: function(data) {
                var messages = data.messages;
                var table = getElementById("messagetable");

                var rowCount = table.rows.length;
                // remove previous table contents
                for (var i = 0; i < rowCount; i++) {
                    table.deleteRow(i);
                    rowCount--;
                    i--;
                }

                for (i = 0; i < messages.length; i++) {
                    var tr = document.createElement("tr");
                    var td1 = document.createElement("td");
                    td1.width = 110;
                    td1.id = messages[i].id;  // set the cell id to the unique message id
                    td1.innerHTML = "foo";
                    var td2 = document.createElement("td");
                    td2.width = 190;
                    td2.innerHTML = messages[i].text;
                    getElementById("messagetablebody").insertBefore(tr, getElementById("messagetablebody").firstChild);
                    tr.insertBefore(td2, tr.firstChild);
                    tr.insertBefore(td1, tr.firstChild);
                }
                table.grid.addCellProp();
                table.grid.addRowProp();

            }
        });
    }

    /**
     * Performs a stateful authentication.
     */
    function login() {
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "http://localhost:8181/fabric/security/token",
            dataType: "json",
            data: '{"username":"' + getElementByIdValue("username") + '", "password":"' + getElementByIdValue("password") + '"}',
            success: function () {
                authenticated = true;
                $('#loginbox').hide("slow");
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
            url: "http://localhost:8181/fabric/security/token",
            dataType: "json",
            data: ' ',
            success: function () {
                authenticated = false;
            }
        });
        $('#messagebox').hide("slow");
        $('#loginbox').show();
        shown = false;
        authenticated = false;
    }

    /**
     * Creates a message by posting a resource
     */
    function createMessage() {
        $.ajax({
            type: "POST",
            contentType: "application/json",
            url: "http://localhost:8181/messages/message",
            dataType: "json",
            data: '{"text":"' + getElementByIdValue("message") + '"}',
            success: updateTable
        });
    }

    /**
     * Deletes a message.
     * @param id the message id
     */
    function deleteMessage(id) {
        $.ajax({
            type: "DELETE",
            contentType: "application/json",
            url: "http://localhost:8181/messages/message/" + id,
            dataType: "json",
            data: ' ',
            success: function() {
            }
        });
    }


    $('#messagebox').hide();

    getElementById('message').setAttribute('autocomplete', 'OFF');

    getElementById('login').onclick = function() {
        if (!authenticated) {
            login();
        }
    };

    getElementById('logout').onclick = function() {
        logout();
    };

    getElementById('send').onclick = function() {
        if (!authenticated) {
            login();
        }
        createMessage();
        getElementById('message').value = '';
        return false;
    };

});








