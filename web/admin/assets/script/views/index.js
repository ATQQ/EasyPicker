$(document).ready(function () {
    var baseurl = "/EasyPicker/";
    var uname = null;//提交者姓名
    var ucourse = null;//父类目名称
    var utask = null;//子类目名称
    var account = null;//管理员账号
    /**
     * 上传文件
     */

    //设置进度条
    $.AMUI.progress.configure({
        minimum: 0.1,//设置最小百分比
        easing: 'ease',//动画欢动函数
        positionUsing: '',
        speed: 600,//速度
        trickle: true,
        trickleRate: 0.02,
        trickleSpeed: 800,
        showSpinner: true,
        barSelector: '[role="nprogress-bar"]',
        spinnerSelector: '[role="nprogress-spinner"]',
        parent: '#thelist',//进度条父容器
        template: '<div class="nprogress-bar" role="nprogress-bar">' +
            '<div class="nprogress-peg"></div></div>' +
            '<div class="nprogress-spinner" role="nprogress-spinner">' +
            '<div class="nprogress-spinner-icon"></div></div>'
    })
    var progress = $.AMUI.progress;

    var uploader = WebUploader.create({
        // sendAsBinary:true,
        //选择完文件或是否自动上传
        auto: false,
        //swf文件路径
        swf: '../plunge/Uploader.swf',
        //是否要分片处理大文件上传。
        chunked: false,
        // 如果要分片，分多大一片？ 默认大小为5M.
        chunkSize: 5 * 1024 * 1024,
        // 上传并发数。允许同时最大上传进程数[默认值：3]   即上传文件数
        threads: 3,
        //文件接收服务端
        server: baseurl + "file/save",
        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: '#picker',
        method: "POST",
        // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
        resize: false,
        formData: {
            course: ucourse,
            task: utask
        }
        // accept:{
        //     title: 'Excell',
        //     extensions: 'xls,xlsx',
        //     mimeTypes: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel'
        // }
    });
    // 当有文件被添加进队列的时候
    uploader.on('fileQueued', function (file) {
        var $list = $('#thelist');
        $list.append('<div id="' + file.id + '" class="item">' +
            '<h4 class="info am-margin-bottom-sm">' + file.name + '</h4>' +
            '<p class="state fw-text-c">等待上传...</p>' +
            '</div>');
    });
    // 文件上传过程中创建进度条实时显示。
    uploader.on('uploadProgress', function (file, percentage) {
        var $li = $('#' + file.id),
            $percent = $li.find('.progress .progress-bar');

        // 避免重复创建
        if (!$percent.length) {
            $percent = $('<div class="progress progress-striped active">' +
                '<div class="progress-bar" role="progressbar" style="width: 0%">' +
                '</div>' +
                '</div>').appendTo($li).find('.progress-bar');
        }

        $li.find('p.state').text('上传中');
    });


    uploader.on('fileQueued', function (file) {
        uploader.md5File(file)

        // 及时显示进度
            .progress(function (percentage) {
                console.log('Percentage:', percentage);
                progress.set(percentage);
            })

            // 完成
            .then(function (val) {
                console.log('md5 result:', val);
            });

    });

    // 文件上传成功处理。
    uploader.on('uploadSuccess', function (file, response) {
        $('#' + file.id).find('p.state').text('已上传');
        console.log(response);
        addReport(uname, ucourse, utask, response.filename, account);
        $("#name").val("");
    });

    //上传出错
    uploader.on('uploadError', function (file) {
        $('#' + file.id).find('p.state').text('上传出错');
    });

    //上传动作结束
    uploader.on('uploadComplete', function (file) {

    });
    // 开始上传
    $('#uploadBtn').on('click', function (e) {
        ucourse = $('option[value="' + $("#course").val() + '"]').html();
        utask = $('option[value="' + $("#task").val() + '"]').html();
        // console.log(ucourse);
        // console.log(utask);
        uname = $('#name').val();
        if (uname.trim() == null || uname.trim() == "") {
            alert('姓名不能为空');
            return;
        }
        uploader.options.formData.course = ucourse;
        uploader.options.formData.task = utask;
        uploader.options.formData.username = account;
        // console.log(uploader.options.formData);
        uploader.upload();
    });
    //上传之前
    uploader.on('uploadBeforeSend', function (block, data) {
        var file = block.file;
        console.log(block);
    });
    //页面初始化
    init();
    // /**
    //  * 打开管理员登录界面
    //  */
    // $('#heart').on('click', function () {
    //     openModel("#admin-login");
    //     console.log("success");
    // })

    /**
     * 父类发生改变
     */
    $("#course").on('change', function () {
        setdata('children', $(this).val(), account);
    });

    /**
     * 子类发生改变
     */
    $("#task").on('change', function () {
        $.ajax({
            url: baseurl + "childContent/childContent",
            type: "GET",
            data: {
                "taskid": $(this).val()
            },
            success: function (res) {
                $('#uploadBtn').attr("disabled",false);
                //如果有数据
                if (res.status) {
                    $("#attributePanel").show();
                    // console.log(res);
                    if (res.ddl) {
                        let $ddl = $("#attributePanel").children('div[target="ddl"]');
                        //显示时间面板
                        $ddl.show();

                        //显示截止日期
                        $ddl.children().eq(0).html("截止日期:" + new Date(res.ddl).Format("yyyy-MM-dd,hh:mm:ss"));
                        //显示日期间隔
                        $ddl.children().eq(1).html(calculateDateDiffer(res.ddl, (new Date().getTime())) ? "还剩:" + calculateDateDiffer(res.ddl, (new Date().getTime())) : "已经截止!!!");
                    }else{
                        //隐藏截止时间面板
                        $("#attributePanel").children('div[target="ddl"]').hide();
                    }
                    if(res.template){
                        $("#attributePanel").children('div[target="template"]').show();
                        // $("#downlloadTemplate").attr("filename",res.template);
                        $("#downlloadTemplate").unbind('click');
                        $("#downlloadTemplate").on('click',function () {
                            var parent=$("#course").next().children().eq(0).find(".am-selected-status").html();
                            var child=$("#task").next().children().eq(0).find(".am-selected-status").html();
                            var jsonArray=new Array();
                            jsonArray.push({"key":"course","value":parent});
                            jsonArray.push({"key":"tasks","value":child+"_template"});
                            jsonArray.push({"key":"filename","value":res.template});
                            jsonArray.push({"key":"username","value":account});
                            downloadFile(baseurl+"file/down",jsonArray);
                        });
                    }else{
                        $("#attributePanel").children('div[target="template"]').hide();
                    }

                } else {
                    //    如果没有数据
                    $("#attributePanel").hide();
                }
            },
            error: function (e) {
                alert("网络错误");
            }
        });
    });

    /**
     * 管理员登录
     */
    $('#login-btn').on('click', function (e) {
        var username = $('#username').val();
        var pwd = $('#password').val();
        if (isEmpty(username)) {
            alert('账号为空')
            return;
        }
        if (isEmpty(pwd)) {
            alert("密码为空");
            return;
        }
        login(username, pwd);
        e.stopPropagation();
    })


    /**
     * 返回日期间隔时间
     * @param old  带比较的时间
     * @param now 当前的时间
     */
    function calculateDateDiffer(old, now) {
        var day = 0;
        var hour = 0;
        var minute = 0;
        var seconds = 0;
        if (now > old) {
            $('#uploadBtn').attr("disabled",true);
            return false;
        }
        var differ = Math.floor(Number((old - now) / 1000));
        day = Math.floor(differ / (24 * 60 * 60));//天
        differ -= day * (24 * 60 * 60);
        // console.log(differ);

        hour = Math.floor(differ / (60 * 60));//时
        differ -= hour * (60 * 60);
        // console.log(differ);

        minute = Math.floor(differ / 60);//分

        seconds = Math.floor(differ % 60);//秒

        return day + "天" + hour + "时" + minute + "分" + seconds + "秒";
    }

    /**
     * 用户登录
     * @param username
     * @param password
     */
    function login(username, password) {
        $.ajax({
            url: baseurl + 'user/login',
            type: "POST",
            contentType: 'application/json;charset=utf-8',
            data: JSON.stringify({
                "username": username,
                "password": password
            }),
            success: function (res) {
                console.log(res);
                var status = res.status;
                //登录失败
                if (status == -1 || status == 0) {
                    alert(res.errmsg);
                    return;
                }
                var data = res.data;
                //判断是否有权限
                if (data.power != 1) {
                    sessionStorage.setItem("token", data.token);
                    window.location.href = '/sugar/';
                } else {
                    alert("没有权限");
                }
            },
            error: function () {
                alert("网络错误");
            }
        })
    }

    /**
     * 判断字符串是否为空
     * @param str
     * @returns {boolean}
     */
    function isEmpty(str) {
        return (str == null || str.trim() == '');
    }

    /**
     * 增加报告
     * @param name
     * @param course
     * @param tasks
     * @param filename
     */
    function addReport(name, course, tasks, filename, username) {
        $.ajax({
            url: baseurl + 'report/save',
            async: true,
            contentType: "application/json",
            type: 'POST',
            data: JSON.stringify({
                "name": name,
                "course": course,
                "tasks": tasks,
                "filename": filename,
                "username": username
            }),
            success: function (res) {
                if (Number(res.status) == 1) {
                    alert("提交成功");
                } else {
                    alert("提交失败");
                }
            },
            error: function () {
                alert("网络错误");
            }
        })
    }

    /**
     * 初始化数据
     */
    function init() {
        $('#course').empty();
        $('#task').empty();
        //获取链接中 的管理员账号与附加参数

        var type = null;//三种情况
        //1 :获取全部父类
        //2 :获取指定父类
        //3: 获取指定子类

        var str = window.location.href;
        var username = null;
        var paramStr = null;
        if (str.lastIndexOf('?') !== -1) {
            username = str.substring(str.lastIndexOf("/") + 1, str.lastIndexOf('?'));
            paramStr = str.substring(str.lastIndexOf('?') + 1);
            //解码
            paramStr = decodeURI(decodeURI(paramStr));
            //获取parent/child
            var parent = getUrlParam(paramStr, 'parent');
            var child = getUrlParam(paramStr, 'child');
            if (parent) {
                type = 2;
                if (child) {
                    type = 3;
                }
            }
        } else {
            username = str.substring(str.lastIndexOf("/") + 1);
            type = 1;
        }

        // console.log(username);
        // console.log(parent);
        // console.log(child);
        // return;
        if (username === "" || username == null || type == null) {
            alert("链接失效!!!");
            redirectHome();
        }


        // console.log(type);
        account = username;
        //查询账号是否有效
        $.ajax({
            url: baseurl + 'user/check',
            async: false,
            contentType: "application/json",
            type: 'POST',
            data: JSON.stringify({
                "username": username
            }),
            success: function (res) {
                // console.log(res);
                if (res) {
                    switch (type) {
                        case 1:
                            setdata('parents', -1, username);
                            break;
                        case 2:
                            setDataByParent(type, parent, username);
                            break;
                        case 3:
                            setDataByChild(type, parent, child, username);
                            break;
                    }
                } else {
                    alert("链接失效!!!");
                    // redirectHome();
                }
            },
            error: function () {
                alert("网络错误");
                redirectHome();
            }
        })


    }

    /**
     * 获取Url中的参数
     * @param url 地址Url 或者 Url中参数部分
     * @param paramName
     */
    function getUrlParam(url, paramName) {
        var isExist = false;
        var res = null;
        isExist = url.lastIndexOf(paramName + '=') !== -1;
        if (isExist) {
            res = url.substring(url.indexOf(paramName + '=') + paramName.length + 1, (url.indexOf('&') > url.indexOf(paramName + '=') ? url.indexOf('&') : url.length));
        }
        return res;
    }

    /**
     * 重定向到首页
     */
    function redirectHome() {
        window.location.href = baseurl + "home";
    }

    /**
     * 查询用户是否存在
     * @param username
     */
    function checkUser(username) {
        $.ajax({
            url: baseurl + 'user/check',
            async: false,
            contentType: "application/json",
            type: 'POST',
            data: JSON.stringify({
                "username": username
            }),
            success: function (res) {
                return res;
            },
            error: function () {
                return false;
            }
        })
    }

    /**
     * 获取课程/任务数据
     * @param range
     * @param parentid
     * @param username
     */
    function setdata(range, parentid, username) {
        $.ajax({
            url: baseurl + 'course/check',
            async: true,
            contentType: "application/json",
            type: 'GET',
            data: {
                "range": range,
                "contentid": parentid,
                "username": username
            },
            success: function (res) {
                if (res.status == 0 || res.status == '0') {
                    // alert('无内容');
                    if (range == 'parents') {
                        clearselect('#course');
                        resetselect("#course");
                    } else {
                        clearselect("#task");
                        resetselect("#task");
                    }
                    return;
                }
                if (range == 'parents') {
                    clearselect('#course');
                    for (var i = 0; i < res.data.length; i++) {
                        insertToSelect("#course", res.data[i].name, res.data[i].id);
                    }
                    resetselect("#course");
                } else if (range == 'children') {
                    clearselect("#task");
                    for (var i = 0; i < res.data.length; i++) {
                        insertToSelect("#task", res.data[i].name, res.data[i].id);
                    }
                    resetselect("#task");
                }

            },
            error: function () {
                alert("网络错误");
            }
        })
    }

    /**
     * 设置提交指定父节点信息
     * @param type
     * @param parent
     * @param username
     */
    function setDataByParent(type, parent, username) {
        $.ajax({
            url: baseurl + 'course/course',
            contentType: "application/json",
            type: 'GET',
            data: {
                "type": type,
                "parent": parent,
                "username": username
            },
            success: function (res) {
                console.log(res);
                if (res.status) {
                    var node = res.data;
                    clearselect('#course');
                    insertToSelect("#course", node.name, node.id);
                    resetselect("#course");
                } else {
                    alert("链接失效");
                    redirectHome();
                }
            },
            error: function () {
                alert("网络错误");
            }
        });
    }

    /**
     * 设置提交指定子节点信息
     * @param type
     * @param parent
     * @param child
     * @param username
     */
    function setDataByChild(type, parent, child, username) {
        //查询父节点信息
        $.ajax({
            url: baseurl + 'course/course',
            contentType: "application/json",
            type: 'GET',
            data: {
                "type": 2,
                "parent": parent,
                "username": username
            },
            success: function (res) {
                // console.log(res);
                if (res.status) {
                    var node = res.data;
                    clearselect('#course');
                    insertToSelect("#course", node.name, node.id);
                    resetselect("#course");
                    //查询子节点信息
                    $.ajax({
                        url: baseurl + 'course/course',
                        contentType: "application/json",
                        type: 'GET',
                        data: {
                            "type": type,
                            "parent": parent,
                            "child": child,
                            "username": username
                        },
                        success: function (res) {
                            // console.log(res);
                            if (res.status) {
                                var node = res.data;
                                clearselect("#task");
                                insertToSelect("#task", node.name, node.id);
                                resetselect("#task");
                            } else {
                                alert("链接失效");
                                redirectHome();
                            }
                        },
                        error: function () {
                            alert("网络错误");
                        }
                    });
                } else {
                    alert("链接失效");
                    redirectHome();
                }
            },
            error: function () {
                alert("网络错误");
            }
        });
    }

    /**
     * 向下拉选择框插入数据
     * @param selectid
     * @param value
     * @param id
     */
    function insertToSelect(selectid, value, id) {
        $(selectid).append('<option value="' + id + '">' + value + '</option>');
    }

    /**
     * 清空下拉选择框
     * @param selectid
     */
    function clearselect(selectid) {
        $(selectid).empty();
        $(selectid).selected('destroy');
    }

    /**
     * 重置下拉选择框
     * @param selectid
     */
    function resetselect(selectid) {
        $(selectid).selected({
            btnStyle: 'secondary'
        });
    }

    /**
     * 关闭指定弹出层
     * @param {String} id 弹出层id
     */
    function closeModel(id) {
        $(id).modal('close');
    }

    /**
     * 下载指定的文件
     * @param path 请求的url
     * @param jsonArray 请求携带的参数
     */
    function downloadFile(path,jsonArray) {
        var form = $("<form>");
        form.attr("style","display:none");
        form.attr("target","");
        form.attr("method","get");
        form.attr("action",path);

        // var input1 = $("<input>");
        // input1.attr("type","hidden");
        // input1.attr("name","strZipPath");
        // form.append(input1);

        jsonArray.forEach(function (key) {
            let temp = $("<input>");
            temp.attr("type","hidden");
            temp.attr("name",key.key);
            temp.val(key.value);
            form.append(temp);
        });
        $("body").append(form);
        form.submit();
        form.remove();
        // //新窗口打开
        // var newTab = window.open('about:blank')
        // newTab.location.href = path;
        // //关闭新窗口
        // newTab.close();
    }

    /**
     * 打开指定弹出层
     * @param {String} id 弹出层id
     * @param {boolean} close 设置点击遮罩层是否可以关闭
     */
    function openModel(id, close) {
        $(id).modal({
            closeViaDimmer: close//设置点击遮罩层无法关闭
        });
        $(id).modal('open');
    }

});
//对Date进行扩展
Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1,                 //月份
        "d+": this.getDate(),                    //日
        "h+": this.getHours(),                   //小时
        "m+": this.getMinutes(),                 //分
        "s+": this.getSeconds(),                 //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds()             //毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
