$(document).ready(function () {
    var baseurl = "/reportsPicker/";
    var uname=null;//提交者姓名
    var ucourse=null;//父类目名称
    var utask=null;//子类目名称
    var account=null;//管理员账号
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
        addReport(uname,ucourse,utask,response.filename,account);
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
        ucourse = $('option[value="'+$("#course").val()+'"]').html();
        utask = $('option[value="'+$("#task").val()+'"]').html();
        // console.log(ucourse);
        // console.log(utask);
        uname=$('#name').val();
        if(uname.trim()==null||uname.trim()==""){
            alert('姓名不能为空');
            return;
        }
        uploader.options.formData.course=ucourse;
        uploader.options.formData.task=utask;
        uploader.options.formData.username=account;
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
    /**
     * 打开管理员登录界面
     */
    $('#heart').on('click', function () {
        openModel("#admin-login");
        console.log("success");
    })

    /**
     * 课程信息发生改变
     */
    $("#course").on('change', function () {
        setdata('children', $(this).val(),account);
    });

    /**
     * 管理员登录
     */
    $('#login-btn').on('click',function (e) {
        var username=$('#username').val();
        var pwd=$('#password').val();
        if(isEmpty(username)){
            alert('账号为空')
            return;
        }
        if(isEmpty(pwd)){
            alert("密码为空");
            return;
        }
        login(username,pwd);
        e.stopPropagation();
    })


    /**
     * 用户登录
     * @param username
     * @param password
     */
    function login(username,password) {
        $.ajax({
            url:baseurl+'user/login',
            type:"POST",
            contentType:'application/json;charset=utf-8',
            data:JSON.stringify({
                "username":username,
                "password":password
            }),
            success:function (res) {
                console.log(res);
                var status=res.status;
                //登录失败
                if(status==-1||status==0){
                    alert(res.errmsg);
                    return;
                }
                var data=res.data;
                //判断是否有权限
                if(data.power!=1){
                    sessionStorage.setItem("token",data.token);
                    window.location.href='/sugar/';
                }else{
                    alert("没有权限");
                }
            },
            error:function () {
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
        return (str==null||str.trim()=='');
    }

    /**
     * 增加报告
     * @param name
     * @param course
     * @param tasks
     * @param filename
     */
    function addReport(name,course,tasks,filename,username) {
        $.ajax({
            url: baseurl + 'report/save',
            async: true,
            contentType: "application/json",
            type: 'POST',
            data: JSON.stringify({
                "name": name,
                "course": course,
                "tasks":tasks,
                "filename":filename,
                "username":username
            }),
            success: function (res) {
                if(Number(res.status)==1){
                    alert("提交成功");
                }else{
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
        //获取链接中 的管理员账号
        var str=window.location.href;
        var username=str.substring(str.lastIndexOf("/")+1);
        if(username==""||username==null){
            // redirectHome();
        }
        account=username;
        //查询账号是否有效
        $.ajax({
            url: baseurl + 'user/check',
            async: false,
            contentType: "application/json",
            type: 'POST',
            data: JSON.stringify({
                "username":username
            }),
            success: function (res) {
                if(res){
                    setdata('parents', -1,username);
                }else{
                    // alert("链接失效!!!");
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
     * 重定向到首页
     */
    function redirectHome() {
        window.location.href="/home/";
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
                "username":username
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
    function setdata(range, parentid,username) {
        $.ajax({
            url: baseurl + 'course/check',
            async: true,
            contentType: "application/json",
            type: 'GET',
            data: {
                "range": range,
                "contentid": parentid,
                "username":username
            },
            success: function (res) {
                if (res.status == 0 || res.status == '0') {
                    alert('无内容');
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

})
