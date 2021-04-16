/**
 * @author zhizuqiu <994490929@qq.com>
 * version: 1.1.0
 * https://github.com/zhizuqiu/danmu-comment
 */

function Danmu_comment() {
    this.server_ip = "";
    this.server_port = "";
    this.title = "";
    this.danmu_speed = "";
    this.keycode = "";
    this.danmu_style = "";
    this.alerts = [];
    this.socket_danmu = null;
    this.events = [];
}

Danmu_comment.prototype = {
    init: function (ip, port, title, speed, code, style) {
        this.server_ip = ip;
        this.server_port = port;
        this.title = title;
        this.danmu_speed = speed;
        this.keycode = code;
        this.danmu_style = style;
        this.start();
        this.addInput();
        this.bindWebsocket(this);
        this.setEvent(this);
    },
    setEvent: function (that) {
        document.onkeydown = function (event_e) {
            if (window.event) {
                event_e = window.event;
            }
            var int_keycode = event_e.charCode || event_e.keyCode;
            if (int_keycode == that.keycode) {
                if (document.getElementById('alert_danmu').style.display == 'block') {
                    document.getElementById('alert_danmu').style.display = 'none';
                    document.getElementById('danmu_shadow').style.display = 'none';
                    if (document.getElementById('insert_danmu').value && that.socket_danmu) {
                        that.socket_danmu.send(JSON.stringify({
                            'type': 'message',
                            'title': that.title,
                            'danmu': document.getElementById('insert_danmu').value
                        }));
                    }
                } else {
                    document.getElementById('alert_danmu').style.display = 'block';
                    document.getElementById('danmu_shadow').style.display = 'block';
                    document.getElementById('insert_danmu').focus();
                }
            }
        };

        // remove button
        if (that.danmu_style == 1) {
            document.getElementById("remove_danmu").onclick = function () {
                that.clear_danmu();
                document.getElementById('alert_danmu').style.display = 'none';
                document.getElementById('danmu_shadow').style.display = 'none';
            };
        }

        //submit button
        document.getElementById("submit_danmu").onclick = function () {
            if (that.socket_danmu) {
                that.socket_danmu.send(JSON.stringify({
                    'type': 'mess',
                    'title': that.title,
                    'danmu': document.getElementById('insert_danmu').value
                }));
            }
            document.getElementById('alert_danmu').style.display = 'none';
            document.getElementById('danmu_shadow').style.display = 'none';
        };

    },
    bindWebsocket: function (that) {
        this.socket_danmu = new WebSocket('ws://' + this.server_ip + ':' + this.server_port);
        this.socket_danmu.onopen = function (event) {
            that.fire('onopen', event);
            this.send(JSON.stringify({
                'type': 'login',
                'title': that.title,
                'danmu': ''
            }));
        };
        this.socket_danmu.onmessage = function (event) {
            if (that.danmu_style == 0) {
                that.addDanmu_style_0(JSON.parse(event.data)['message']);
            } else if (that.danmu_style == 1) {
                that.addDanmu_style_1(JSON.parse(event.data));
            }
            that.fire('onmessage', event);
        };
        this.socket_danmu.onclose = function (event) {
            that.fire('onclose', event);
        };
    },
    onEvent: function (type, fn) {
        if (typeof this.events[type] === 'undefined') {
            this.events[type] = [fn];
        } else {
            this.events[type].push(fn);
        }
    },
    fire: function (type, event) {
        if (!this.events[type]) {
            return;
        }
        var i = 0,
            len = this.events[type].length;
        for (; i < len; i++) {
            this.events[type][i].call(this, event);
        }
    },
    addInput: function () {
        var thisNode = document.getElementById('alert_danmu');
        if (thisNode) {
            thisNode.remove();
        }
        thisNode = document.getElementById('danmu_shadow');
        if (thisNode) {
            thisNode.remove();
        }

        var parent = document.getElementsByTagName("body")[0];
        var div = document.createElement("div");
        div.setAttribute("id", "alert_danmu");
        div.setAttribute("class", "alert_danmu");
        var div1_1 = document.createElement("div");
        div1_1.setAttribute("class", "comment");
        div1_1.innerHTML = 'comment';
        var div1_2 = document.createElement("div");
        div1_2.setAttribute("class", "div1_2");
        var input = document.createElement("input");
        input.setAttribute("id", "insert_danmu");
        input.setAttribute("type", "text");
        input.setAttribute("class", "insert_danmu");
        div1_2.appendChild(input);
        var div1_3 = document.createElement("div");
        div1_3.setAttribute("class", "div1_3");
        var button = document.createElement("button");
        button.setAttribute("class", "submit_danmu");
        button.setAttribute("id", "submit_danmu");
        button.innerHTML = "submit";
        div1_3.appendChild(button);
        if (this.danmu_style == 1) {
            var button_remove = document.createElement("button");
            button_remove.setAttribute("class", "remove_danmu");
            button_remove.setAttribute("id", "remove_danmu");
            button_remove.innerHTML = "remove";
            div1_3.appendChild(button_remove);
        }
        div.appendChild(div1_1);
        div.appendChild(div1_2);
        div.appendChild(div1_3);
        parent.appendChild(div);


        var div_shadow = document.createElement("div");
        div_shadow.setAttribute("id", "danmu_shadow");
        div_shadow.setAttribute("class", "danmu_shadow");
        parent.appendChild(div_shadow);

    },
    addDanmu_style_0: function (danmu) {
        var w = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
        var h = Math.floor(Math.random() * ((window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight) / 2));
        var parent = document.getElementsByTagName("body")[0];
        var div = document.createElement("div");
        div.setAttribute("name", "danmu");
        div.setAttribute("class", "danmu-base danmu-style-0");
        div.setAttribute("style", "left:" + w + "px; top:" + h + "px;");
        div.innerHTML = danmu;
        document.getElementById('insert_danmu').value = "";
        parent.appendChild(div);
    },
    addDanmu_style_1: function (danmu) {
        var w = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
        var h = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
        var w_ram = Math.floor(Math.random() * w);
        var h_ram = Math.floor(Math.random() * (h / 2));
        var parent = document.getElementsByTagName("body")[0];
        var div = document.createElement("div");
        div.setAttribute("id", 'alert_' + danmu['time']);
        div.setAttribute("class", "danmu-base danmu-style-1");
        div.setAttribute("style", " left:" + w_ram + "px; top:" + h_ram + "px;");
        div.innerHTML = danmu['message'];
        document.getElementById('insert_danmu').value = "";
        parent.appendChild(div);
        this.fadeIn(document.getElementById('alert_' + danmu['time']), 300);
        this.alerts.push({"id": 'alert_' + danmu['time']});
    },
    moveDanmu: function (that) {
        var danmus = document.getElementsByName("danmu");
        for (danmu_ in danmus) {
            if (danmus[danmu_].style) {
                var w = parseInt(danmus[danmu_].style.left);
                w = w - that.danmu_speed;
                danmus[danmu_].style.left = w + "px";
                if (parseInt(danmus[danmu_].style.left) < -danmus[danmu_].offsetWidth) {
                    var parent = document.getElementsByTagName("body")[0];
                    parent.removeChild(danmus[danmu_]);
                }
            }
        }
    },
    start: function () {
        setInterval(this.moveDanmu, 5, this);
    },
    fadeIn: function (el, time) {
        if (el.style.opacity === "") {
            el.style.opacity = 0;
        }
        if (el.style.display === "" || el.style.display === 'none') {
            el.style.display = 'block';
        }
        var t = setInterval(function () {
            if (el.style.opacity < 1) {
                el.style.opacity = parseFloat(el.style.opacity) + 0.01;
            } else {
                clearInterval(t);
            }
        }, time / 100);
    },
    clear_danmu: function () {
        var count = this.alerts.length;
        for (var i = 0; i < this.alerts.length; i++) {
            document.getElementById(this.alerts[i].id).setAttribute("name", "danmu");
        }
        this.alerts.splice(0, count);
    }
};

