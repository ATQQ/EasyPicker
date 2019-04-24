$(document).ready(function () {
    var baseurl = "/reportsPicker/";

    /**
     * 打开管理员登录界面
     */
    // $('#heart').on('click', function () {
    //     openModel("#admin-login");
    //     console.log("success");
    // })


    /**
     * 输入框内容发生改变时候
     */
    $('input').on('change',function(){
        if($(this).val()!=''){
            changeInputGroupColor($(this).parent(), 'secondary');
        }
    })

    /**
     * 用户登录
     */
    $('#login').on('click',function (e) {
        var $inputs = $('#loginPanel').find('input');
        var username = $inputs.eq(0).val();
        var pwd = $inputs.eq(1).val();
        if(isEmpty(username)){
            resetPlaceHolder($inputs.eq(0),"账号为空");
            changeInputGroupColor($inputs.eq(0).parent(),'danger');
            return;
        }
        if(isEmpty(pwd)){
            resetPlaceHolder($inputs.eq(1), "密码为空");
            changeInputGroupColor($inputs.eq(1).parent(), 'danger');
            return;
        }
        login(username,pwd);
        e.stopPropagation();
    })


    /**
     * 新用户注册
     */
    $('#register').on('click',function(){
        var $inputs = $('#registerPanel').find('input');
        var username = $inputs.eq(0).val();
        var pwd1 = $inputs.eq(1).val();
        var pwd2 = $inputs.eq(2).val();
        if (isEmpty(username)&&username>12) {
            resetPlaceHolder($inputs.eq(0), "账号为空");
            changeInputGroupColor($inputs.eq(0).parent(), 'danger');
            return;
        }
        if (isEmpty(pwd1) || pwd1 > 16||pwd1<6) {
            resetPlaceHolder($inputs.eq(1), "密码不符合规范");
            changeInputGroupColor($inputs.eq(1).parent(), 'danger');
            return;
        }
        if (pwd1!=pwd2) {
            resetPlaceHolder($inputs.eq(2), "两次密码不一致");
            changeInputGroupColor($inputs.eq(2).parent(), 'danger');
            return;
        }
        //ajax
        $.ajax({
            url: baseurl + 'user/user',
            type: "POST",
            headers:{
                'Content-Type':'application/json;charset=utf-8'
            },
            contentType: 'application/json;charset=utf-8',
            data: JSON.stringify({
                "username": username,
                "password": pwd1
            }),
            success: function (res) {
               if(res.status){
                //    清空输入框
                    $('input').val('');
               }else{
                   resetPlaceHolder($inputs.eq(0), "账号已存在");
                   changeInputGroupColor($inputs.eq(0).parent(), 'danger');
               }
            },
            error: function () {
                alert("网络错误");
            }
        })
    })

    /**
     * 切换登录/注册面板
     */
    $('.changePanel').on('click',function () {
        $(this).parents('.homePanel').hide().siblings().show();
    })

    /**
     * 重置输入框placeHolder内容
     * @param {input} $input 
     * @param {String} placeholder 
     */
    function resetPlaceHolder($input,placeholder) {
        $input.attr('placeholder',placeholder);
    }

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
     * 切换Amazeui中输入框组的颜色
     * @param {Object} $group 输入框对象
     * @param {String} color 颜色/success/danger/secondary/default/primary
     */
    function changeInputGroupColor($group,color) {
        var colors=['success','danger','secondary','primary'];
        colors.forEach(key => {
            $group.removeClass('am-input-group-'+key);            
        });
        $group.addClass('am-input-group-'+color);
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
