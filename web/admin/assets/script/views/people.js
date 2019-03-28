
var table_people=null; /*用户表格*/
var baseUrl = 'http://localhost:8080/SWPU_Mail/';
$(document).ready(function () {

    //初始化表格
    table_people = $('#table_people').DataTable({
        responsive: true,
        "pageLength": 10,
        "dom": 'rt<"bottom"p><"clear">',
        "order": [
            [2, 'asc']
        ]
    });

    /**
     * 删除一条记录
     */
    $("#table_people tbody").on('click','button.row-del',function (res) {
        var rowdata=table_people.row($(this).parents('tr')).data();
        openModel("#confirm-delete",true);
        $("#sure-del").unbind('click');

        $("#sure-del").on('click',function () {
            console.log(rowdata[0]);
            closeModel("#confirm-delete");
        //    AJAX 提交删除
        })
    })
    //表格搜索
    $('#search-btn').on('click', function () {
        table_people.search($('#search-input').val()).draw();
    });
    //清空表格内容
    clearTable(table_people);
    //测试添加
    for (var i=0;i<40;i++){
        addContentTotable(i,"xsda@"+i+".com",
            "小花"+i,
            Math.floor(Math.random()*19999999),
            Math.floor(Math.random()*20000000),
            "2019-01-01 09:01:31");
    }

    $('#importFile').on('click',function () {
        console.log("success");
        openModel("#upload-modal",false);
    })

    $('#downloadFile').on('click',function (path) {
        downloadFile(baseUrl + "test/download");
    })
})

/**
 * 向人员管理表格中添加内容
 * @param id ID
 * @param email 邮箱
 * @param name  姓名
 * @param number    工号/学号
 * @param tel   电话
 * @param date  日期
 */
function addContentTotable(id,email,name,number,tel,date) {
    var $btns=' <div class="am-btn-toolbar">' +
        ' <div class="am-btn-group am-btn-group-xs">' +
        '  <button type="button" class="am-btn am-btn-default am-btn-xs am-text-danger am-round row-del"' +
        '  title="删除"><span' +
        ' class="am-icon-trash-o"></span></button>' +
        '  </div>' +
        '</div>';

    var rowNode = table_people
        .row.add([
            id,
            email,
            name,
            number,
            tel,
            date,
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
    form.attr("style","display:none");
    form.attr("target","");
    form.attr("method","post");
    form.attr("action",path);
    var input1 = $("<input>");
    input1.attr("type","hidden");
    input1.attr("name","strZipPath");
    $("body").append(form);
    form.append(input1);
    form.submit();
    form.remove();
}