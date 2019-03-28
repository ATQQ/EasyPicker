var table_problem = null; /*用户表格*/
var baseUrl = 'http://localhost:8080/WebTest/';
$(document).ready(function () {
    //初始化表格
    table_problem = $('#table_problem').DataTable({
        responsive: true,
        "pageLength": 10,
        "dom": 'rt<"bottom"p><"clear">',
        "order": [
            [2, 'asc']
        ]
    });

    //过滤器配置（对于搜索框的配置，自定义筛选）
    //未配置成功

    /**
     * 初始化
     */
    init();
    /**
     * 删除一条记录
     */
    $("#table_problem tbody").on('click', 'button.row-del', function (res) {
        var rowdata = table_problem.row($(this).parents('tr')).data();
        openModel("#confirm-delete", true);
        $("#sure-del").unbind('click');

        $("#sure-del").on('click', function () {
            console.log(rowdata[0]);
            closeModel("#confirm-delete");
            //    AJAX 提交删除
        })
    })


    /**
     * 查看问题
     */
    $("#table_problem tbody").on('click', 'button.row-check', function (res) {
        var rowdata = table_problem.row($(this).parents('tr')).data();
        console.log(rowdata[0]);
        openModel("#modal-problem",false);
    })


    /**
     * 回复问题
     */
    $("#table_problem tbody").on('click', 'button.row-reply', function (res) {
        var rowdata = table_problem.row($(this).parents('tr')).data();
        console.log(rowdata[0]);
        openModel("#modal-reply",false);
    })
    /**
     * 表格搜索
     */
    $('#search-btn').on('click', function () {
        table_problem.search($('#search-input').val()).draw();
    });
    /**
     * 进行过滤
     */
    $("#status-fliter").on('change', function () {
     var key=$(this).val();
     if(key==1||key=='1'){
         key='待回复';
     }else if(key==0||key=='0'){
         key='已回复';
     }else{
         key='';
     }
        table_problem.search(key).draw();//表格的重新加载
    });
    /**
     * 清空表格内容
     */
    clearTable(table_problem);

    // 测试添加
    for (var i=0;i<40;i++){
        addContentTotable(i,"xsda@"+i+".com",
            "小花"+i,
            "2019-01-01 09:01:31",
            i%2);
    }
})
function init() {

}


/**
 *  向表格添加内容
 * @param id 反馈id
 * @param email 邮箱
 * @param problem   问题内容
 * @param date  日期
 * @param status    状态
 */
function addContentTotable(id, email, problem, date, status) {
    var $btns = '<button type="button"' +
        'class="am-btn am-btn-default am-btn-lg am-text-danger am-round row-del"' +
        'title="删除">' +
        '<span class="am-icon-trash-o"></span>' +
        '</button>' +
        '<button type="button"' +
        'class="am-btn am-btn-default am-btn-lg am-text-danger am-round row-check"' +
        'title="查看">' +
        '<span class="am-icon-smile-o"></span></button>' +
        '<button type="button"' +
        'class="am-btn am-btn-default am-btn-lg am-text-danger am-round row-reply"' +
        'title="回复"><span class="am-icon-heart-o"></span></button>';

    var $status = '';
    if (status == '1' || status == 1) {
        $status='<div state="1" style="color:#2eb72e;">已回复</div>';
    }
    else{
        $status='<div state="0" style="color:#e91e63;">待回复</div>';
    }
    var rowNode = table_problem
        .row.add([
            id,
            email,
            problem,
            date,
            $status,
            $btns
        ])
        .draw()
        .node();

    $(rowNode)
        .css('class', 'gradeX');
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
 * 关闭指定弹出层
 * @param {String} id 弹出层id
 */
function closeModel(id) {
    $(id).modal('close');
}

/**
 * 清空表格内容
 * @param {DataTable} $table DataTable对象
 */
function clearTable($table) {
    $table.rows()
        .remove()
        .draw();
}

/**
 * 下载文件
 * @param path 请求的url
 */
function downloadFile(path) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", path);
    var input1 = $("<input>");
    input1.attr("type", "hidden");
    input1.attr("name", "strZipPath");
    $("body").append(form);
    form.append(input1);
    form.submit();
    form.remove();
}