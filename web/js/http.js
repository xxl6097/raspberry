var isDebug;
var webSocketServerInfo;
var logInfo;
var tmpJson = {"data":{"logBean":{"writeAll":false,"writeDebug":false,"writeError":false,"writeInfo":false,"writeVerbose":false,"writeWarm":false},"serverInfo":{"appName":"和而泰智能网关","port":8081,"serverIp":"192.168.31.96"}},"type":1};
window.isLogin = false;
$(document).ready(function() {
    isDebug = false;
});

function load() {
    console.log("###load#页面已加载！");
    getDeviceState();

}




function getDeviceState(){
getState("light.yeelight",{
                               onSuccess: function (msg) {
                                   var ret = JSON.parse(results);
                                   var yeelight = document.getElementById('yeelight_id');
                                       if(ret.state == 'on'){
                                       yeelight.checked = true;
                                       }else{
                                       yeelight.checked=false;
                                       }
                               },
                               onFailure: function (err) {
                                   console.log(JSON.stringify(err));
                               }
                           });


getState("switch.mi_socket_plus",{
                               onSuccess: function (msg) {
                                   var ret = JSON.parse(results);
                                               localLoad(ret);
                                               var socketplus = document.getElementById('socket_plus_id');
                                                   if(ret.state == 'on'){
                                                   socketplus.checked = true;
                                                   }else{
                                                   socketplus.checked=false;
                                                   }
                               },
                               onFailure: function (err) {
                                   console.log(JSON.stringify(err));
                               }
                           });
}

function getState(entityid,suc,err) {
    var dt = new Date();
    var json = { "type": 1,"data": {"age ": dt.getFullYear() + "-" + (dt.getMonth() + 1) + "-" + dt.getDate() + "-" + dt.getTime()}};
    var str = JSON.stringify(json);
    console.log("####getConfig ");
    jQuery.ajax({
        //提交的网址
        type: "GET",
        url: "/v1/pi/state?entityid="+entityid,
        contentType: "application/x-www-form-urlencoded",
        dataType: 'text',
        success: suc,
        error: err
    });
}


function getConfig1(entityid) {
    var dt = new Date();
    var json = { "type": 1,"data": {"age ": dt.getFullYear() + "-" + (dt.getMonth() + 1) + "-" + dt.getDate() + "-" + dt.getTime()}};
    var str = JSON.stringify(json);
    console.log("####getConfig ");
    jQuery.ajax({
        //提交的网址
        type: "GET",
        url: "/v1/pi/state?entityid="+entityid,
        contentType: "application/x-www-form-urlencoded",
        dataType: 'text',
        success: function(results) {
            console.log("####getConfig.results "+results);
            var ret = JSON.parse(results);
            //localLoad(ret);
            return true;
        },
        error: function(e) {
            //getId("btnConnect").disabled = false;
            return false;
        }
    });
}

function Yeelight(id) {
    var yeelight = document.getElementById(id);
    var state = yeelight.checked;
    console.log(id + "====" + state)
    jQuery.ajax({
        //提交的网址
        type: "GET",
        url: "/v1/pi/light?state="+(state?"on":"off"),
        contentType: "application/x-www-form-urlencoded",
        dataType: 'text',
        success: function(results) {
            console.log(id + "====" + results);
            return true;
        },
        error: function(e) {
            console.log(id + "====" + e);
            return false;
        }
    });
}


function SocketPlus() {
    var socketplus = document.getElementById('socket_plus_id');
    var state = socketplus.checked;
    console.log(id + "====" + state)
    jQuery.ajax({
        //提交的网址
        type: "GET",
        url: "/v1/pi/switch?state="+(state?"on":"off"),
        contentType: "application/x-www-form-urlencoded",
        dataType: 'text',
        success: function(results) {
            console.log(id + "====" + results);
            return true;
        },
        error: function(e) {
            console.log(id + "====" + e);
            return false;
        }
    });
}

function login() {
    var username = document.getElementById('username').value;
    var password = document.getElementById('password').value;
    console.log("####login username:" + username + " password:" + password);
    var json = { "type": 2, "data": { "username": username, "password": password } };
    var value = JSON.stringify(json);
    jQuery.ajax({
        //提交的网址
        type: "POST",
        url: "request",
        data: value,
        contentType: "application/x-www-form-urlencoded",
        dataType: 'text',
        success: function(results) {
            console.log("####login " + results);
            var ret = JSON.parse(results);
            Toast(ret.data, 3000);
            if (ret.type == 2) {
                window.isLogin = true;
                window.location.href = '../html/index.html'
            } else {
                window.isLogin = false;
            }
        }
    });
}

function handLogResults(results) {
    $("#db-data-div").empty();
    console.log("#### handLogResults " + results);
    results.forEach(function(item) {
        handItem(item);
    })

    $('#log-table').DataTable({
        "pageLength": 250,
    });

}

function handItem(item) {
    var color = "#000000";
    if (item.lev == 'VERBOSE') {
        color = '#717171';
    } else if (item.lev == 'DEBUG') {
        color = '#2980b9';
    } else if (item.lev == 'INFO') {
        color = '#27ae60';
    } else if (item.lev == 'WARN') {
        color = '#f39c12';
    } else if (item.lev == 'ERROR') {
        color = '#c0392b';
    }
    console.log("#### handItem " + item);
    item.msg = item.msg.replace(/\n\t\t/g, '<br>&nbsp;&nbsp;&nbsp;&nbsp&nbsp;&nbsp;&nbsp;&nbsp');

    var line = '<tr style="color : ' + color + '"><td>' + item.time + '</td><td>' + item.tag + '</td><td>' + item.msg + '</td></tr>';
    $("#db-data-div").append(line);
}

//自定义弹框
function Toast(msg, duration) {
    duration = isNaN(duration) ? 3000 : duration;
    var m = document.createElement('div');
    m.innerHTML = msg;
    m.style.cssText = "width: 60%;min-width: 150px;opacity: 0.7;height: 60px;color: rgb(255, 255, 255);line-height: 30px;text-align: center;border-radius: 5px;position: fixed;top: 40%;left: 20%;z-index: 999999;background: rgb(0, 0, 0);font-size: 12px;";
    document.body.appendChild(m);
    setTimeout(function() {
        var d = 0.5;
        m.style.webkitTransition = '-webkit-transform ' + d + 's ease-in, opacity ' + d + 's ease-in';
        m.style.opacity = '0';
        setTimeout(function() { document.body.removeChild(m) }, d * 1000);
    }, duration);
}

function IsPC() {
    var userAgentInfo = navigator.userAgent;
    var Agents = ["Android", "iPhone",
        "SymbianOS", "Windows Phone",
        "iPad", "iPod"
    ];
    var flag = true;
    for (var v = 0; v < Agents.length; v++) {
        if (userAgentInfo.indexOf(Agents[v]) > 0) {
            flag = false;
            break;
        }
    }
    return flag;
}

function getId(id) { return document.getElementById(id); }