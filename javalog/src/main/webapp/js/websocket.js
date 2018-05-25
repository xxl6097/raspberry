function load() {
    connect();
}

function connect() {
    var host = "ws://207.246.96.42:8125"
    console.log("####websocket info " + host);
    socket = new WebSocket(host);
    try {

        socket.onopen = function (msg) {
            showLog("连接成功：" + msg.toString());
        };

        socket.onmessage = function (msg) {
            if (typeof msg.data == "string") {
                showLog(msg.data);
            } else {
                alert("非文本消息onmessage" + msg);
            }
        };

        socket.onerror = function (msg) {
            console.log('onerror received a message', msg);
        };

        socket.onclose = function (msg) {
            console.log('onclose received a message', msg);
        };
    } catch (ex) {
        console.log('catch received a message', msg);
        //log(ex);
    }
}

function send() {
    var msg = getId("sendText").value
    socket.send(msg);
}

function disconnect() {
    try {
        socket.close();
        socket = null;
    } catch (ex) {
    }
}

window.onbeforeunload = function () {
    try {
        socket.close();
        socket = null;
    } catch (ex) {
    }
};

function getId(id) {
    return document.getElementById(id);
}


function getText() {
    return document.getElementById("log-container").value
}




function showLog(msg) {
    var div = document.getElementById('log-container');
            div.value += "\r\n" + msg;
            div.scrollTop = div.scrollHeight;
}



function onkey(event) {
    if (event.keyCode == 13) {
        send();
    }
}
