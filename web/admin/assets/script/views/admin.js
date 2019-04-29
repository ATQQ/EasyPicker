$(function () {
    var baseurl = "/EasyPicker/";
    var username = sessionStorage.getItem("username");
    var reports=null;//存放所有文件信息
    var nodes=null;//存放所有类别信息(子类/父类)
    var isSupportClip=true;
    $('.username').html(username);

    //初始化ZeroClipboard对象
    var clip=new ZeroClipboard($('#createLink'));

    // 初始化DataTable组件
    var filesTable = $('#filesTable').DataTable({
        responsive: true,//是否是响应式？
        "pageLength": 10,//每页条数
        "dom": 'rt<"bottom"p><"clear">',//添加分页控件12004
        "order": [[0, 'asc']]//初始化排序是以那一列进行排序，并且，是通过什么方式来排序的，下标从0开始，‘’asc表示的是升序，desc是降序
    });

    //页面初始化
    Init();


    //为剪贴板绑定事件
    clip.on('ready', function(){
        console.log("Clip ready");
        $("#tempCopy").hide();
        this.on('aftercopy', function(event){
            // console.log("copy Event");
            alert("链接已经复制到剪贴板");
        });
    });

    clip.on('error',function (e) {
        isSupportClip=false;
        $('#createLink').hide();
        $("#tempCopy").show();
    });



    /**
     * 下载指定任务中所有文件
     */
    $('#download').on('click', function () {
        var parent=$("#courseList").val();
        var child=$("#taskList").val();
        if(parent==-1||child==-1){
            alert("请选择要下载的子类");
            return 0;
        }
        //取得子类与父类的名称
        nodes.forEach(function (key) {
            if(key.id==parent){
                parent=key.name;
            }
            if(key.id==child){
                child=key.name;
            }
        });
        // console.log(parent);
        // console.log(child);
        var count=0;
        reports.forEach(function (key) {
            if(key.course==parent&&key.tasks==child){
                count++;
            }
        });
        if(count==0){
            alert("没有可下载的文件");
        }else{
        //    开始下载文件
            var jsonArray=new Array();
            jsonArray.push({"key":"course","value":parent});
            jsonArray.push({"key":"tasks","value":child});
            jsonArray.push({"key":"username","value":username});
            // downloadFile(baseurl+"file/downloadZip",jsonArray);
            downloadFile(baseurl+"file/downZip",jsonArray);

        }
    })
    /**
     * 搜索table中的内容
     */
    $('#searchVal').on('click', function () {
        filesTable.search($(this).parent().prev().val()).draw();
    });


    /**
     * 切换面板
     */
    $('#navMenu').on('click', 'li', function () {
        var key = $(this).attr('key');
        // 面板切换
        $('.tpl-content-wrapper').hide();
        $('#panel-' + key).show();

        //侧边导航栏样式切换
        $('#navMenu').find('a').removeClass('active');
        $(this).find('a').addClass('active');
    });

    /**
     * 下载指定实验报告
     */
    $('#filesTable').on('click', '.download', function () {
        var cells = filesTable.row($(this).parents('tr')).data();
        var jsonArray=new Array();
        jsonArray.push({"key":"course","value":cells[2]});
        jsonArray.push({"key":"tasks","value":cells[3]});
        jsonArray.push({"key":"filename","value":cells[4]});
        jsonArray.push({"key":"username","value":username});
        // downloadFile(baseurl+"file/download",jsonArray);
        downloadFile(baseurl+"file/down",jsonArray);
    })

    /**
     * 删除指定实验报告
     */
    $('#filesTable').on('click', '.delete', function () {
        if(confirm("确认删除此文件,删除后将无法复原,请谨慎操作?")){
        var cells = filesTable.row($(this).parents('tr')).data();
        var that=this;
        $.ajax({
            url:baseurl+"report/report",
            type:"DELETE",
            headers:{
              "Content-Type": "application/json;charset=utf-8"
            },
            data:JSON.stringify({
                "id":cells[0]
            }),
            success:function (res) {
                if(res){
                    filesTable.row($(that).parents("tr")).remove().draw();

                    //异步获取最新的repors数据
                    $.ajax({
                        url: baseurl + 'report/report',
                        type: 'GET',
                        data: {
                            "username": username
                        },
                        success: function (res) {
                            if(res.status){
                                reports=res.data;
                            }
                        },
                        error: function () {
                            alert("网络错误");
                        }
                    })
                }

            }
        })
        }
    });


    //华丽的分割线--------------------------------------
    //类目区域

    /**
     * 删除课程
     */
    $("#coursePanel").on('click', '.delete', function (event) {
        var id = $(this).parents('li').val();
        if (confirm("确认删除此课程吗?")) {
            delCourseOrTask(1, id);
            $(this).parents('li').remove();
            clearpanel('#taskPanel');
            $('#addTask').unbind('click');
        }
        event.stopPropagation();
    });

    /**
     * 删除任务
     */
    $("#taskPanel").on('click', '.delete', function (event) {
        var id = $(this).parents('li').val();
        if (confirm("确认删除此任务吗?")) {
            delCourseOrTask(0, id);
            $(this).parents("li").remove();
        }
        event.stopPropagation();
    });


    /**
     * 生成任务/子类分享链接
     */
    $('#taskPanel').on('click','button.share',function () {
        var parent=$('#courceActive').html();
        var child=$(this).next().html();
        var shareUrl=window.location.href;
        shareUrl=shareUrl.substring(0,shareUrl.lastIndexOf("/"))+"/home/"+username;
        shareUrl+=('?parent='+parent+'&child='+child);
        // $('#tempCopy').val(shareUrl);
        $('#tempCopy').attr('href',shareUrl);
        $('#tempCopy').html("浏览器不支持一键复制,请长按我复制链接");
        openModel("#copy-panel");
    });

    /**
     * 生成课程/父类分享链接
     */
    $('#coursePanel').on('click','button.share',function () {
        var parent=$(this).next().html();
        var shareUrl=window.location.href;
        shareUrl=shareUrl.substring(0,shareUrl.lastIndexOf("/"))+"/home/"+username;
        shareUrl+=('?parent='+parent);
        // $('#tempCopy').val(shareUrl);
        $('#tempCopy').attr('href',shareUrl);
        $('#tempCopy').html("浏览器不支持一键复制,请长按我复制链接");
        openModel("#copy-panel");
    });

    /**
     * 生成全部类目链接
     */
    $('#shareAll').on('click',function () {
        //shareUrl
        var shareUrl=window.location.href;
        shareUrl=shareUrl.substring(0,shareUrl.lastIndexOf("/"))+"/home/"+username;
        // $('#tempCopy').val(shareUrl);
        $('#tempCopy').attr('href',shareUrl);
        $('#tempCopy').html("浏览器不支持一键复制,请长按我复制链接");
        openModel("#copy-panel");
    });
    /**
     * 显示当前点击了的子类
     */
    $('#taskPanel').on('click','button.checkChildren',function () {
        $('#taskActive').html($(this).html());
    });
    /**
     * 查看子类/选择课程
     */
    $('#coursePanel').on('click', '.checkChildren', function () {
        $('#courceActive').html($(this).html());
        var parentsId = $(this).parents('li').attr('value');
        setdataPanel('children', parentsId, username);

        //增加任务
        $('#addTask').unbind('click');
        $('#addTask').on('click', function () {
            var $input = $(this).parent().prev();
            var value = $input.val();
            if (value == null || value.trim() == '') {
                alert('内容不能为空');
                return;
            }
            var $lis = $('#taskPanel').children('li');
            for (var i = 0; i < $lis.length; i++) {
                if ($lis.eq(i).attr('text') == value) {
                    alert("内容已存在");
                    $input.val('');
                    return;
                }
            }
            addCourseOrTask(value, 0, parentsId, username);
        })
    });

    /**
     * 添加课程
     */
    $('#addCourse').on('click', function () {
        var $input = $(this).parent().prev();
        var value = $input.val();
        if (value == null || value.trim() == '') {
            alert('内容不能为空');
            return;
        }
        var $lis = $('#coursePanel').children('li');
        for (var i = 0; i < $lis.length; i++) {
            if ($lis.eq(i).attr('text') == value) {
                alert("内容已存在");
                $input.val('');
                return;
            }
        }
        addCourseOrTask(value, 1, null, username);
    });

    /**
     * 退出登录
     */
    $('#logout').on('click',function () {
        logout();
    });


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

    /**
     * 退出登录
     */
    function logout(){
        sessionStorage.removeItem("username");
        redirectHome();
    }
    /**
     * 添加课程或者任务
     * @param name 名称
     * @param type  1 课程  0 任务
     * @param parent -1表示添加课程
     */
    function addCourseOrTask(name, type, parent, username) {
        $.ajax({
            url: baseurl + 'course/add',
            contentType: "application/json",
            type: 'PUT',
            data: JSON.stringify({
                "name": name,
                "type": type,
                "parent": parent,
                "username": username
            }),
            success: function (res) {
                if (res.status == 0 || res.status == '0') {
                    alert('添加失败');
                    return;
                } else if (parent == null) {
                    insertToPanel("#coursePanel", name, res.id, 'course');
                } else {
                    insertToPanel("#taskPanel", name, res.id, 'task');
                }
            },
            error: function () {
                alert("网络错误");
            }
        })
    }

    /**
     * 删除课程/任务
     * @param type 课程/任务
     * @param id 待删除的id
     */
    function delCourseOrTask(type, id) {
        $.ajax({
            url: baseurl + 'course/del',
            contentType: "application/json",
            type: 'DELETE',
            data: JSON.stringify({
                "id": id,
                "type": type
            }),
            success: function (res) {
                if (res.status == 0 || res.status == '0') {
                    alert('删除失败');
                    return;
                }
            },
            error: function () {
                alert("网络错误");
            }
        })
    }

    /**
     * 设置管理面板数据
     * @param range
     * @param parentid
     */
    function setdataPanel(range, parentid, username) {
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
                        clearpanel('#coursePanel');
                    } else {
                        clearpanel("#taskPanel");
                    }
                    return;
                }
                if (range == 'parents') {
                    clearpanel('#coursePanel');
                    for (var i = 0; i < res.data.length; i++) {
                        insertToPanel("#coursePanel", res.data[i].name, res.data[i].id, 'course');
                    }
                } else if (range == 'children') {
                    clearpanel("#taskPanel");
                    for (var i = 0; i < res.data.length; i++) {
                        insertToPanel("#taskPanel", res.data[i].name, res.data[i].id, 'task');
                    }
                }
            },
            error: function () {
                alert("网络错误");
            }
        })
    }

    /**
     * 向管理面板插入数据
     * @param panelid
     * @param value
     * @param id
     * @param type 判断是任务还是课程 task/course
     */
    function insertToPanel(panelid, value, id, type) {
        var $li = '';
        switch (type) {
            case "task":
                $li =
                    '<li class="am-margin-top-sm"text="' + value + '"value="' + id + '">' +
                    '<div class="am-btn-group">' +
                    '<button title="生成子类文件收取链接" type="button"  class="share am-btn am-btn-secondary am-round am-icon-share-alt"></button>' +
                    '<button  type="button"  class="checkChildren am-btn am-btn-secondary am-round">' + value + '</button>' +
                    '<button type = "button" class="delete am-btn am-btn-secondary am-round am-icon-trash" ></button > </div > </li >';
                break;
            case "course":
                $li =
                    '<li class="am-margin-top-sm"text="' + value + '"value="' + id + '">' +
                    '<div class="am-btn-group">' +
                    '<button title="生成父类文件收取链接" type="button"  class="share am-btn am-btn-success am-round am-icon-share-alt"></button>' +
                    '<button title="查看子类任务" type="button"  class="checkChildren am-btn am-btn-success am-round">' + value + '</button>' +
                    '<button type = "button" class="delete am-btn am-btn-success am-round am-icon-trash" ></button > </div > </li >';
                break;
            default:
                break;
        }
        $(panelid).append($li);
    }


    /**
     * 重定向到首页
     */
    function redirectHome() {
        window.location.href = baseurl + "home";
    }

    /**
     * 清空管理面板数据
     * @param selectid
     */
    function clearpanel(panelid) {
        $(panelid).empty();
    }

    /**
     * 页面初始化填充数据
     */
    function Init() {
        //判断登录是否失效
        var token = sessionStorage.getItem("token");
        if (token == null || token == '') {
            alert("登录已经失效,请重新登录");
            redirectHome();
            return;
        }
        $('#coursePanel').empty();
        $('#taskPanel').empty();
        setdataPanel("parents", -1, username);

        //加载文件面板数据
        getReportsData(username);

        //加载文件面板下拉选框数据
        initSelectData();
        //test
        // for (var i = 0; i < 10; i++) {
        //     addDataToFilesTable(i, "姓名" + i, "课程" + i, "任务" + i, "文件名" + i, new Date());
        // }

    }

    /**
     * 通过用户名查询所有提交的任务的信息
     * @param username
     */
    function getReportsData(username) {
        $.ajax({
            url: baseurl + 'report/report',
            type: 'GET',
            data: {
                "username": username
            },
            success: function (res) {
               if(res.status){
                   reports=res.data;
                   reports.forEach(function (key) {
                       addDataToFilesTable(key.id,key.name,key.course,key.tasks,key.filename,key.date);
                   })
               }
            },
            error: function () {
                alert("网络错误");
            }
        })
    }
    /**
     * 向文件列表中添加数据
     * @param {Number} id
     * @param {String} name
     * @param {String} course
     * @param {String} task
     * @param {String} filename
     * @param {String} date
     */
    function addDataToFilesTable(id, name, course, task, filename, date) {
        var $btns = '<div class="tpl-table-black-operation"><a class="download btn-theme-green am-margin-sm" href = "javascript:;">' +
            '<i class="am-icon-pencil"></i> 下载</a >' +
            '<a href="javascript:;" class="delete tpl-table-black-operation-del am-margin-sm">' +
            '<i class="am-icon-trash" ></i> 删除</a></div> ';

        date=new Date(date).Format("yyyy-MM-dd hh:mm:ss");
        var rowNode = filesTable.row.add([
            id,
            name,
            course,
            task,
            filename,
            date,
            $btns
        ])
            .draw()
            .node();

        $(rowNode)
            .css('class', 'gradeX');
    }

    /**
     * 初始化文件面板下拉选框内容
     */
    function initSelectData() {
        $.ajax({
            url: baseurl + 'course/node',
            type: 'GET',
            data: {
                "username": username
            },
            success: function (res) {
                if(res.status){
                    nodes=res.data;
                    clearselect("#courseList");
                    insertToSelect("#courseList","全部","-1");
                    nodes.forEach(function (key) {
                        if(key.type==1)
                        insertToSelect("#courseList",key.name,key.id);
                    });
                    resetselect("#courseList","success");

                    //父类下拉框绑定事件
                    $('#courseList').on('change',function (e) {
                        let id=$(this).val();
                        // console.log(id);
                        clearselect("#taskList");
                        insertToSelect("#taskList","全部",-1);
                        if(id!=-1){//如果选择的不是全部
                            nodes.forEach(function (key) {
                                if(key.id==id){//加载选择的内容
                                    filesTable.rows().remove().draw();
                                    reports.forEach(function (v) {
                                        if(v.course==key.name){
                                            addDataToFilesTable(v.id,v.name,v.course,v.tasks,v.filename,v.date);
                                        }
                                    })
                                }
                                if(key.type==0&&id==key.parent)
                                    insertToSelect("#taskList",key.name,key.id);
                            });
                        }else{
                            filesTable.rows().remove().draw();
                            reports.forEach(function (v) {
                                    addDataToFilesTable(v.id,v.name,v.course,v.tasks,v.filename,v.date);
                            })
                            // serchTableVal("");
                        }
                        resetselect("#taskList");
                    })

                    //子类下拉框绑定事件
                    $('#taskList').on('change',function (e) {
                        let id=$(this).val();
                        // console.log(id);
                        if(id!=-1){
                            nodes.forEach(function (key) {
                                if(key.id==id){
                                    serchTableVal(key.name);
                                }
                            });
                        }else{
                            serchTableVal("");
                        }
                    })
                }
            },
            error: function () {
                alert("网络错误");
            }
        })
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
     * 搜索文件表中指定内容
     * @param content 待查找的内容
     */
    function serchTableVal(content) {
        filesTable.search(content).draw();
    }
    /**
     * 重置下拉选择框
     * @param selectid
     */
    function resetselect(selectid,style='secondary') {
        $(selectid).selected({
            btnStyle: style
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
})

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