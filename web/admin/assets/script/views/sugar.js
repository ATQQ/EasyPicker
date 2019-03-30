$(document).ready(function () {
    var baseurl = "http://localhost:8080/reportsPicker/";
    //页面初始化
    init();

    /**
     * 打开管理面板
     */
    $('#rewrite').on('click', function () {
        openModel("#rewrite-panel");
        console.log("success");
    })

    /**
     * 课程下拉框发生改变
     */
    $("#course").on('change',function () {
        setdata('children',$(this).val());
    })

    /**
     * 添加课程
     */
    $('#addCourse').on('click',function () {
        var $input=$(this).parent().prev();
        var value=$input.val();
        if(value==null||value.trim()==''){
            alert('内容不能为空');
            return;
        }
        var $radios=$('input[type="radio"]');
        for (var i = 0; i <$radios.length ; i++) {
            if($radios.eq(i).attr('text')==value){
                alert("内容已存在");
                $input.val('');
                return;
            }
        }
        addCourseOrTask(value,1);
    });

    $("#coursePanel").on('click','.del',function (event) {
        var id=$(this).prev().val();
        if(confirm("确认删除此课程吗?")){
            delCourseOrTask(1,id);
            $(this).parent().remove();
        }
        event.stopPropagation();
    });

    $("#taskPanel").on('click','.del',function (event) {
        var id=$(this).parent().attr('key');
        console.log(id);
        if(confirm("确认删除此任务吗?")){
            delCourseOrTask(0,id);
            $(this).parent().remove();
        }
        event.stopPropagation();
    });
    /**
     * 初始化数据
     */
    function init() {
        $('#course').empty();
        $('#task').empty();
        setdata('parents', -1);
        setdataPanel("parents",-1);
    }

    /**
     * 设置下拉框课程/任务数据
     * @param range
     * @param parentid
     */
    function setdata(range, parentid) {
        $.ajax({
            url: baseurl + 'course/check',
            async: true,
            contentType: "application/json",
            type: 'GET',
            data: {
                "range": range,
                "contentid": parentid
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
     * 设置管理面板数据
     * @param range
     * @param parentid
     */
    function setdataPanel(range, parentid) {
        $.ajax({
            url: baseurl + 'course/check',
            async: true,
            contentType: "application/json",
            type: 'GET',
            data: {
                "range": range,
                "contentid": parentid
            },
            success: function (res) {
                if (res.status == 0 || res.status == '0') {
                    alert('无内容');
                    return;
                }
                if (range == 'parents') {
                    clearpanel('#coursePanel');
                    for (var i = 0; i < res.data.length; i++) {
                        insertToPanel("#coursePanel", res.data[i].name, res.data[i].id,'course');
                    }
                } else if (range == 'children') {
                    clearpanel("#taskPanel");
                    for (var i = 0; i < res.data.length; i++) {
                        insertToPanel("#taskPanel", res.data[i].name, res.data[i].id,'task');
                    }
                }
                $('input[type="radio"]').unbind('click');
                $('input[type="radio"]').on('click',function () {
                    var id=$(this).val();
                    setdataPanel('children',id);
                    $('#addTask').unbind('click');
                    $('#addTask').on('click',function () {
                        var $input=$(this).parent().prev();
                        var value=$input.val();
                        if(value==null||value.trim()==''){
                            alert('内容不能为空');
                            return;
                        }
                        var $spans=$('span.task');
                        for (var i = 0; i <$spans.length ; i++) {
                            if($spans.eq(i).attr('text')==value){
                                alert("内容已存在");
                                $input.val('');
                                return;
                            }
                        }
                        addCourseOrTask(value,0,id);
                    })
                })

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
     * 向管理面板插入数据
     * @param panelid
     * @param value
     * @param id
     * @param type 判断是任务还是课程 task/course
     */
    function insertToPanel(panelid, value, id,type) {
        var $li='';
        switch (type) {
            case "task":
                $li='<span class="task am-badge am-badge-success am-radius am-margin-top-sm am-text-default" text="'+value+'" key="'+id+'">'+value+'<i class="am-icon-trash-o del"></i></span>';
                break;
            case "course":
                $li='<label class="am-radio-inline">' +
                    '<input type="radio" name="radio10" text="'+value+'" value="'+id+'" data-am-ucheck>'+value +
                    '<i class="am-margin-right-sm am-icon-trash-o del"></i>' +
                    '</label>';
                break;
            default:
                break;
        }
        $(panelid).append($li);
    }

    /**
     * 清空管理面板数据
     * @param selectid
     */
    function clearpanel(panelid) {
        $(panelid).empty();
    }


    /**
     * 添加课程或者任务
     * @param name 名称
     * @param type  1 课程  0 任务
     * @param parent -1表示添加课程
     */
    function addCourseOrTask(name,type,parent) {
        $.ajax({
            url: baseurl + 'course/add',
            contentType: "application/json",
            type: 'PUT',
            data: JSON.stringify({
                "name": name,
                "type": type,
                "parent":parent
            }),
            success: function (res) {
                if (res.status == 0 || res.status == '0') {
                    alert('添加失败');
                    return;
                }else if (parent==null) {
                    insertToPanel("#coursePanel",name,res.id,'course');
                }else {
                    insertToPanel("#taskPanel",name,res.id,'task');
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
    function delCourseOrTask(type,id) {
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
