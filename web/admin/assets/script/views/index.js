$(document).ready(function () {
    openModel("#rewrite-panel");

    $('#heart').on('click', function () {
            openModel("#admin-login");
            console.log("success");
        })

        $('#rewrite').on('click', function () {
            openModel("#rewrite-panel");
            console.log("success");
        })

    $("#course-list").on('click','.del',function () {
        console.log("success");
    })

    }
)

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
