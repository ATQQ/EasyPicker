$(function () {
    var baseurl = "/reportsPicker/";
    var username = sessionStorage.getItem("username");
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
        this.on('aftercopy', function(event){
            // console.log("copy Event");
            alert("链接已经复制到剪贴板");
        });
    });



    /**
     * 下载指定任务中所有文件
     */
    $('#download').on('click', function () {
        var rows = filesTable.rows();
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
    })

    /**
     * 删除指定实验报告
     */
    $('#filesTable').on('click', '.delete', function () {
        var cells = filesTable.row($(this).parents('tr')).data();
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


    $('#taskPanel').on('click','button.checkChildren',function () {
        $('#taskActive').html($(this).html());
    })
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
    })

    /**
     * 复制指定内容到剪贴板
     * @param str
     */
    function copStr(str) {

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
                    '<button type="button"  class="checkChildren am-btn am-btn-secondary am-round">' + value + '</button>' +
                    '<button type = "button" class="delete am-btn am-btn-secondary am-round am-icon-trash" ></button > </div > </li >';
                break;
            case "course":
                $li =
                    '<li class="am-margin-top-sm"text="' + value + '"value="' + id + '">' +
                    '<div class="am-btn-group">' +
                    '<button type="button"  class="checkChildren am-btn am-btn-success am-round">' + value + '</button>' +
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

        //test
        // for (var i = 0; i < 10; i++) {
        //     addDataToFilesTable(i, "姓名" + i, "课程" + i, "任务" + i, "文件名" + i, new Date());
        // }

        //shareUrl
        var shareUrl=window.location.href;
        shareUrl=shareUrl.substring(0,shareUrl.lastIndexOf("/"))+"/home/"+username;
        $('#tempCopy').html(shareUrl);
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
        var $btns = '<div class="tpl-table-black-operation"><a class="download" href = "javascript:;">' +
            '<i class="am-icon-pencil"></i> 下载</a >' +
            '<a href="javascript:;" class="delete tpl-table-black-operation-del">' +
            '<i class="am-icon-trash" ></i> 删除</a></div> ';

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
})