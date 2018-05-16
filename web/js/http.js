$(document).ready(function () {
});

function load() {
    console.log("###load#页面已加载！");
    getDeviceState();
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

function getId(id) {
    return document.getElementById(id);
}

//自定义弹框
function tips(msg) {
    duration = 1000;//isNaN(duration) ? 3000 : duration;
    var m = document.createElement('div');
    m.innerHTML = msg;
    m.style.cssText = "width: 60%;min-width: 150px;opacity: 0.7;height: 60px;color: rgb(255, 255, 255);line-height: 30px;text-align: center;border-radius: 5px;position: fixed;top: 40%;left: 20%;z-index: 999999;background: rgb(0, 0, 0);font-size: 12px;";
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

function getDeviceState() {
    var path = "/v1/pi/state?entityid=light.yeelight";
    doget(path, function (msg) {
            var ret = JSON.parse(msg);
            var yeelight = document.getElementById('yeelight_id');
            if (ret.state == 'on') {
                yeelight.checked = true;
            } else {
                yeelight.checked = false;
            }
        },
        function (err) {
            console.log(JSON.stringify(err));
        }
    );


    var path = "/v1/pi/state?entityid=switch.mi_socket_plus";
    doget(path, function (msg) {
            var ret = JSON.parse(msg);
            var socketplus = document.getElementById('socket_plus_id');
            if (ret.state == 'on') {
                socketplus.checked = true;
            } else if (ret.state == 'off') {
                socketplus.checked = false;
            } else {
                restartHomeassistant();
            }
        },
        function (err) {
            console.log(JSON.stringify(err));
        }
    );


    var path = "/v1/dev/state?entityid=timer";
    doget(path, function (ret) {
        var timeId = document.getElementById('timer_id');
        if (ret.code == 0) {
            timeId.checked = true;
        } else {
            timeId.checked = false;
        }
    }, function (err) {
    });
}


function restartHomeassistant() {
    var path = "/v1/pi/switch?entityid=homeassistant&state=restart";
    doget(path, function (result) {
        var json = JSON.parse(result);
        console.log("####getConfig.results " + json);
        tips('HomeAssistant is restart');
    }, function (err) {
        console.log("====Yeelight" + err);
        tips('HomeAssistant restart failed');
    });
}

function getState(entityid, suc, err) {
    var path = "/v1/pi/state?entityid=" + entityid;
    doget(path, suc, err);
}


function Yeelight(id) {
    var yeelight = getId(id);
    var state = yeelight.checked;
    console.log("===Yeelight=" + state)
    var path = "/v1/pi/light?state=" + (state ? "on" : "off");
    doget(path, function (result) {
        var json = JSON.parse(result);
        console.log("====Yeelight" + json);
        tips('Yeelight控制成功');
    }, function (err) {
        console.log("====Yeelight" + err);
        tips('Yeelight控制失败');
    });
}

function SocketPlus() {
    var socketplus = getId('socket_plus_id');
    var state = socketplus.checked;
    console.log(id + "====" + state)
    var path = "/v1/pi/switch?state=" + (state ? "on" : "off");
    doget(path, function (result) {
        var json = JSON.parse(result);
        console.log("====SocketPlus" + json);
        tips('SocketPlus控制成功');
    }, function (err) {
        console.log("====SocketPlus" + err);
        tips('SocketPlus控制失败');
    });
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




