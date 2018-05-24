var socket;
var index=1;
var ip;

var isDebug;
window.isLogin = false;
$(document).ready(function() {
    isDebug = false;
});

function load() {
    connect();

    var path = "/v1/dev/state?entityid=timer";
    doget(path, function (msg) {
        var ret = JSON.parse(msg);
        var timeId = document.getElementById('timer_id');
        if (ret.code == 0) {
            timeId.checked = true;
        } else {
            timeId.checked = false;
        }
    }, function (err) {
    });
}

function connect() {
    var host = "ws://" + ip +":8123/api/websocket"
    console.log("####websocket info " + host);
    socket = new WebSocket(host);
    try {

        socket.onopen = function (msg) {
            <!--alert("连接成功！");-->
            showStatus("连接成功！");
            showLog("连接成功：" + msg.toString());
            index++;
            var json = {"id": index,"type": "subscribe_events","event_type": "state_changed"};
            var listener = JSON.stringify(json);
            socket.send(listener);

            index++;
            var tt = {
                "id": index,
                "type": "get_states"
            }
            var listener1 = JSON.stringify(tt);
            socket.send(listener1);
        };

        socket.onmessage = function (msg) {
            if (typeof msg.data == "string") {
                displayContent(msg.data);
                //showLog(msg.data);
            } else {
                alert("非文本消息onmessage" + msg);
            }
        };

        socket.onerror = function (msg) {
            console.log('onerror received a message', msg);
            showLog(msg.toString());
            showStatus("连接错误：" + msg.toString());
        };

        socket.onclose = function (msg) {
            console.log('onclose received a message', msg);
            showStatus("连接关闭：" + msg.toString());
            showLog("连接关闭：" + msg);
        };
    } catch (ex) {
        console.log('catch received a message', msg);
        //log(ex);
        showStatus("连接异常：" + ex);
        showLog("连接异常：" + ex);
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

function ClearTextArea() {
    document.getElementById("txtContent").value = "";
}


function getText() {
    return document.getElementById("txtContent").value
}

function displayContent(msg) {
    var json = JSON.parse(msg);
    var div = document.getElementById('txtContent');
    div.value += "\r\n\r\n" + msg;
    div.scrollTop = div.scrollHeight;
    processEvent(json);
}


function processEvent(json) {
    if (json.type == 'event'){
        var entity_id = json.event.data.entity_id;
        if (entity_id == 'switch.mi_socket_plus'){
            processSocketPlus(json.event.data.new_state.state);
        }else if(entity_id == 'light.yeelight_ceiling_34ce00be19cc'){
            processYeelight(json.event.data.new_state.state);
        }
    }else if(json.type == 'result'){
        var list = json.result;
        for(item in list) {
            var entity_id = list[item].entity_id;
            if (entity_id == 'switch.mi_socket_plus'){
                processSocketPlus(list[item].state);
            }else if(entity_id == 'light.yeelight_ceiling_34ce00be19cc'){
                processYeelight(list[item].state);
            }
        }
    }
}

function processSocketPlus(state) {
    var socketplus = getId('socket_plus_id');
    if (state == 'on'){
        socketplus.checked = true;
    }else{
        socketplus.checked = false;
    }
}

function processYeelight(data) {
    var state = data.new_state.state;
    var socketplus = getId('yeelight_id');
    if (state == 'on'){
        socketplus.checked = true;
    }else{
        socketplus.checked = false;
    }
}




function SocketPlus() {
    index++;
    var socketplus = getId('socket_plus_id');
    var state = socketplus.checked;
    var sendjson;
    if (state){
        sendjson = {"id":index,"type":"call_service","domain":"switch","service":"turn_on","service_data":{"entity_id":"switch.mi_socket_plus"}};
    }else{
        sendjson = {"id":index,"type":"call_service","domain":"switch","service":"turn_off","service_data":{"entity_id":"switch.mi_socket_plus"}};
    }
    var data = JSON.stringify(sendjson);
    socket.send(data);
}


function Yeelight() {
    var yeelight = getId('yeelight_id');
    var state = yeelight.checked;
    index++;
    var sendjson;
    if (state){
        sendjson = {"id":index,"type":"call_service","domain":"light","service":"turn_on","service_data":{"entity_id":"light.yeelight_ceiling_34ce00be19cc"}};
    }else{
        sendjson = {"id":index,"type":"call_service","domain":"light","service":"turn_off","service_data":{"entity_id":"light.yeelight_ceiling_34ce00be19cc"}};
    }
    var data = JSON.stringify(sendjson);
    socket.send(data);
}

function TimerListen() {
    var timeId = document.getElementById('timer_id');
    var state = timeId.checked;
    var path = "/v1/pi/timer?state=" + (state ? "on" : "off");
    doget(path, function (result) {
        var json = JSON.parse(result);
        console.log("====TimerListen" + json);
        tips('TimerListen控制成功');
    }, function (err) {
        console.log("====TimerListen" + err);
        tips('TimerListen控制失败');
    });
}

function doget(path, suc, err) {
    jQuery.ajax({
        //提交的网址
        type: "GET",
        url: path,
        contentType: "application/x-www-form-urlencoded",
        dataType: 'text',
        success: suc,
        error: err
    });
}

function showLog(msg) {
    var div = document.getElementById('txtContent');
            div.value += "\r\n" + msg;
            div.scrollTop = div.scrollHeight;
}



function showStatus(msg) {
    getId("status").innerHTML = msg;
}

function onkey(event) {
    if (event.keyCode == 13) {
        send();
    }
}


//自定义弹框
function tips(msg) {
    duration = 1000;//isNaN(duration) ? 3000 : duration;
    var m = document.createElement('div');
    m.innerHTML = msg;
    m.style.cssText = "width: 20%;min-width: 150px;opacity: 0.7;height: 30px;color: rgb(255, 255, 255);line-height: 30px;text-align: center;border-radius: 5px;position: fixed;top: 40%;left: 20%;z-index: 999999;background: rgb(0, 0, 0);font-size: 12px;";
    document.body.appendChild(m);
    setTimeout(function () {
        var d = 0.5;
        m.style.webkitTransition = '-webkit-transform ' + d + 's ease-in, opacity ' + d + 's ease-in';
        m.style.opacity = '0';
        setTimeout(function () {
            document.body.removeChild(m)
        }, d * 1000);
    }, duration);
}