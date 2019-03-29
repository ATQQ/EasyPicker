$(document).ready(function () {
        //页面初始化
        init();
        
        openModel("#rewrite-panel");

        $('#heart').on('click', function () {
            openModel("#admin-login");
            console.log("success");
        })

        $('#rewrite').on('click', function () {
            openModel("#rewrite-panel");
            console.log("success");
        })

        $("#course-list").on('click', '.del', function () {
            console.log("success");
        })


    }
)

var baseurl = "http://localhost:8080/reportsPicker/";

/**
 * 初始化数据
 */
function init() {
    $('#course').empty();
    $('#task').empty();
    getdata('parents',-1);
}

/**
 * 获取课程/任务数据
 * @param range
 * @param parentid
 */
function getdata(range, parentid) {
    $.ajax({
        url: baseurl + 'course/check',
        async: false,
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
            if (range=='parents'){
                for (var i = 0; i < res.data.length; i++) {
                    $('#course').append('<option value="' + res.data[i].id + '">' + res.data[i].name + '</option>');
                    console.log("success")
                }
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
