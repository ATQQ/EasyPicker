$(document).ready(function () {
    var baseurl = "/EasyPicker/";
    var isGetCode=false;

    $('.am-alert-forgetPwd').alert();//激活模态弹窗

    /**
     * 页面初次完成渲染后
     */
    loadLocatAccount();

    /**
     * 输入框内容发生改变时候
     */
    $('input').on('change',function(){
        if($(this).attr('id')==='userMobile'){
            return;
        }
        if($(this).val()!=''){
            changeInputGroupColor($(this).parent(), 'secondary');
        }
    });

    /**
     * 手机号输入框内容改变
     */
    $('#userMobile').on('input',function (e) {
        var rMobile=/^0?(13|15|18|14|17)[0-9]{9}$/;
        if(rMobile.test(e.target.value)){
            if(yzTimes==90){
                $('#getCode').attr("disabled",false);
            }
            changeInputGroupColor($(this).parent(), 'secondary');
        }else{
            if($('#getCode').attr("disabled")){
                return;
            }
            $('#getCode').attr("disabled",true);
            changeInputGroupColor($(this).parent(), 'danger');
        }
    });

    var yzTimes2=90;//重置密码验证码等待时间
    /**
     * 忘记密码手机号输入框内容改变
     */
    $('#bindPhone').on('input',function (e) {
        var rMobile=/^0?(13|15|18|14|17)[0-9]{9}$/;
        if(rMobile.test(e.target.value)){
            if(yzTimes2===90){
                $('#getForgetCode').attr("disabled",false);
            }
            changeInputGroupColor($(this).parent(), 'secondary');
        }else{
            if($('#getForgetCode').attr("disabled")){
                return;
            }
            $('#getForgetCode').attr("disabled",true);
            changeInputGroupColor($(this).parent(), 'danger');
        }
    });

    /**
     * 忘记密码获取验证码
     */
    $('#getForgetCode').on('click',function (e) {
        var that=this;
        var fun=function(){
            yzTimes2--;
            $(that).html(yzTimes2+"(s)");
            if(yzTimes2===0){
                yzTimes2=90;
                $(that).attr('disabled',false);
                $(that).html("获取验证码");
                return;
            }
            setTimeout(fun,1000);
        };

        var mobile=$(this).parent().parent().prev().find('input').val();
        //ajax
        $.ajax({
            url: baseurl + "user/getCode" + `?time=${Date.now()}`,
            type:"GET",
            data:{
                "mobile":mobile
            },
            success:function (res) {
                if(res.code===200){
                    $(that).attr('disabled',true);
                    //开始执行
                    fun();
                }else{
                    alert(res.errMsg);
                }
            },
            error:function () {
                alert("网络错误");
            }
        });

    });

    /**
     * 确认重置密码
     */
    $('#sureReset').on('click',function () {
        var that=this;
        var $inputs = $('#forgetPanel').find('input');
        var phoneNumber = $inputs.eq(0).val();//手机号
        var code = $inputs.eq(1).val();//验证码
        var newPwd = $inputs.eq(2).val();//新密码

        //判断手机号是否合格
        if (phoneNumber.length!==11) {
            $inputs.eq(0).val('');
            resetPlaceHolder($inputs.eq(0), "手机号格式不正确");
            changeInputGroupColor($inputs.eq(0).parent(), 'danger');
            return;
        }
        if (code.length!==4) {
            $inputs.eq(1).val('');
            resetPlaceHolder($inputs.eq(1), "验证码格式不正确");
            changeInputGroupColor($inputs.eq(1).parent(), 'danger');
            return;
        }
        if (newPwd.length>16||newPwd.length<6) {
            $inputs.eq(2).val('');
            resetPlaceHolder($inputs.eq(2), "密码长度应为(6-16)");
            changeInputGroupColor($inputs.eq(2).parent(), 'danger');
            return;
        }
        const submitData={
            mobile:phoneNumber,
            password:newPwd
        }
        // console.log(submitData);
        $.ajax({
            url:baseurl+"user/update/"+code,
            type:"PUT",
            headers:{
              "content-Type":"application/json;charset=utf-8"
            },
            data:JSON.stringify(submitData),
            success:function (res) {
                switch (res.code) {
                    case 400:
                        $inputs.eq(1).val('');
                        resetPlaceHolder($inputs.eq(1), "验证码不不匹配");
                        changeInputGroupColor($inputs.eq(1).parent(), 'danger');
                        break;
                    case 200:
                        $(that).next().click();
                        alert("重置成功");
                        yzTimes2=1;
                        break;
                    case 401:
                        $inputs.eq(1).val('');
                        resetPlaceHolder($inputs.eq(1), "验证码不正确");
                        changeInputGroupColor($inputs.eq(1).parent(), 'danger');
                        break;
                    case 404:
                        $inputs.eq(0).val('');
                        resetPlaceHolder($inputs.eq(0), "手机号不存在");
                        changeInputGroupColor($inputs.eq(0).parent(), 'danger');
                        break;
                    case 405:
                        $inputs.eq(0).val('');
                        resetPlaceHolder($inputs.eq(0), "手机号已经存在");
                        changeInputGroupColor($inputs.eq(0).parent(), 'danger');
                        break;
                    default:
                        alert("未知异常,请联系管理员");
                        break;
                }

            },
            error:function () {
                alert("网路错误");
            }
        })
    });

    /**
     * 是否开启绑定手机号的面板
     */
    $('#isBindMobile').on('change',function (e) {
        if($(this).is(':checked')){
            $(this).parent().prev().attr('readonly',false).parent().next().show();
        }else{
            $(this).parent().prev().attr('readonly',true).parent().next().hide();
        }
    });

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


    var yzTimes=90;//验证码时间
    /**
     * 新用户注册获取验证码
     */
    $('#getCode').on('click',function (e) {

        var that=this;
        var fun=function(){
            yzTimes--;
            $(that).html(yzTimes+"(s)");
            if(yzTimes===0){
                yzTimes=90;
                $(that).attr('disabled',false);
                $(that).html("获取验证码");
                return;
            }
            setTimeout(fun,1000);
        };

        var mobile=$(this).parent().parent().prev().find('input').val();
        //ajax
        $.ajax({
            url: baseurl + "user/getCode" + `?time=${Date.now()}`,
            type:"GET",
            data:{
                "mobile":mobile
            },
            success:function (res) {
                if(res.code===200){
                    $(that).attr('disabled',true);
                    //开始执行
                    fun();
                    isGetCode=true;
                }else{
                    alert(res.errMsg);
                }

            },
            error:function () {
                alert("网络错误");
            }
        });

    });

    /**
     * 确认新用户注册
     */
    $('#register').on('click',function(){
        var that=this;
        var $inputs = $('#registerPanel').find('input');
        var username = $inputs.eq(0).val();
        var pwd1 = $inputs.eq(1).val();//第一次密码
        var pwd2 = $inputs.eq(2).val();//第二次密码
        var mobile=$inputs.eq(3).val();//手机号
        var code=$inputs.eq(5).val();//验证码
        //判断账号是否符合条件
        if (isEmpty(username)||username>12) {
            resetPlaceHolder($inputs.eq(0), "账号为空");
            changeInputGroupColor($inputs.eq(0).parent(), 'danger');
            return;
        }
        if (isEmpty(pwd1) || pwd1.length > 16||pwd1.length<6) {
            $inputs.eq(1).val('');
            resetPlaceHolder($inputs.eq(1), "密码不符合规范(6-16位)");
            changeInputGroupColor($inputs.eq(1).parent(), 'danger');
            return;
        }
        if (pwd1!=pwd2) {
            $inputs.eq(2).val('');
            resetPlaceHolder($inputs.eq(2), "两次密码不一致");
            changeInputGroupColor($inputs.eq(2).parent(), 'danger');
            return;
        }

        var submitData={
            "username": username,
            "password": pwd1
        };
        //判断是否需要绑定手机号
        if($('#isBindMobile').is(':checked')){
            //判断手机号
            var rMobile=/^0?(13|15|18|14|17)[0-9]{9}$/;
            if(!rMobile.test(mobile)){
                // console.log("success");
                changeInputGroupColor($inputs.eq(3).parent(), 'danger');
                return;
            }
            //判断验证码
            if(code.length!=4){
                $inputs.eq(5).val('');
                resetPlaceHolder($inputs.eq(5), "验证码格式不正确");
                changeInputGroupColor($inputs.eq(5).parent(), 'danger');
                return;
            }
            submitData={
                "username": username,
                "password": pwd1,
                "mobile":mobile,
                "code":code
            }
        }

        //提交客户端数据
        $.ajax({
            url: baseurl + 'user/user',
            type: "POST",
            data:submitData,
            success: function (res) {
                switch (res.code) {
                    case 200:
                        alert('注册成功');
                        $('input').val('');
                        $(that).next().click();
                        isGetCode=false;
                        break;
                    case 401:
                        $inputs.eq(0).val('');
                        resetPlaceHolder($inputs.eq(0), "账号已存在");
                        changeInputGroupColor($inputs.eq(0).parent(), 'danger');
                        break;
                    case 402:
                        $inputs.eq(3).val('');
                        resetPlaceHolder($inputs.eq(3), "手机号已存在");
                        changeInputGroupColor($inputs.eq(3).parent(), 'danger');
                        break;
                    case 403:
                        $inputs.eq(5).val('');
                        resetPlaceHolder($inputs.eq(5), "验证码不正确");
                        changeInputGroupColor($inputs.eq(5).parent(), 'danger');
                        break;
                    default:
                        break;
                }

            },
            error: function () {
                alert("网络错误");
            }
        })
    })

    /**
     * 初始化面板数据
     */
    function resetInputPlaceholder(){
        var $inputs = $('#registerPanel').find('input');
        resetPlaceHolder($inputs.eq(0),"请输入注册账号");
        resetPlaceHolder($inputs.eq(1),"请输入密码");
        resetPlaceHolder($inputs.eq(2),"请再次输入密码");
        resetPlaceHolder($inputs.eq(3),"(可选)绑定手机号");
        // resetPlaceHolder($inputs.e)
        resetPlaceHolder($inputs.eq(5),"输入验证码");
        isGetCode=false;
        // yzTimes=90;
    }
    /**
     * 切换登录/注册面板/忘记密码面板
     */
    $('.changePanel').on('click',function () {
        $('div.homePanel').hide();//隐藏全部
        const panelKey=$(this).attr('targetPanel');
        $('div.homePanel[panel="'+panelKey+'"]').addClass("flipInY").show();
        resetInputPlaceholder();
    });

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
        var $inputs=$('#loginPanel').find('input');

        if($('#rememberAccount').is(':checked')){
            storageAccount(username,password);
        }
            $.ajax({
            url:baseurl+'user/login',
            type:"POST",
            contentType:'application/json;charset=utf-8',
            data:JSON.stringify({
                "username":username,
                "password":password
            }),
            success:function (res) {
                // console.log(res);
                var status=res.status;
                //登录失败
                if(status==-1||status==0){
                    switch (status) {
                        case -1:
                            $inputs.eq(0).val('');
                            resetPlaceHolder($inputs.eq(0),"账号不存在");
                            changeInputGroupColor($inputs.eq(0).parent(),'danger');
                            break;
                        case 0:
                            $inputs.eq(1).val('');
                            resetPlaceHolder($inputs.eq(1),"密码错误");
                            changeInputGroupColor($inputs.eq(1).parent(),'danger');
                            break;
                    }
                    // alert(res.errmsg);
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
        return (str==null||str.trim()==='');
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

    /**
     * 本地存储账号信息
     */
    function storageAccount(username,password) {
        localStorage.setItem("user",JSON.stringify({"username":username,"password":password}));
    }

    /**
     * 加载最后一次存储的账号信息
     */
    function loadLocatAccount() {
      var nowUser= localStorage.getItem("user");
      if(nowUser==null){
          return;
      }
      nowUser=JSON.parse(nowUser);
      $('#login-username').val(nowUser.username);
      $('#login-password').val(nowUser.password);
    }

})
