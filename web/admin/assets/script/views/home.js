$(document).ready(function () {
    var baseurl = "/reportsPicker/";

    /**
     * 打开管理员登录界面
     */
    $('#heart').on('click', function () {
        openModel("#admin-login");
        console.log("success");
    })


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
                    sessionStorage.setItem("username",username);
                    window.location.href=baseurl+'admin';
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
