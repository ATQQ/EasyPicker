
$(document).ready(function () {

    /**
     * 上传表格区域
     */
    //设置进度条
    $.AMUI.progress.configure({
        minimum: 0.1,//设置最小百分比
        easing: 'ease',//动画欢动函数
        positionUsing: '',
        speed: 600,//速度
        trickle: true,
        trickleRate: 0.02,
        trickleSpeed: 800,
        showSpinner: true,
        barSelector: '[role="nprogress-bar"]',
        spinnerSelector: '[role="nprogress-spinner"]',
        parent: '#thelist',//进度条父容器
        template: '<div class="nprogress-bar" role="nprogress-bar">' +
            '<div class="nprogress-peg"></div></div>' +
            '<div class="nprogress-spinner" role="nprogress-spinner">' +
            '<div class="nprogress-spinner-icon"></div></div>'
    })
    var progress = $.AMUI.progress;

    var baseUrl = 'http://localhost:8080/SWPU_Mail/';

    var uploader = WebUploader.create({
        // sendAsBinary:true,
        //选择完文件或是否自动上传
        auto: false,
        //swf文件路径
        swf: '../plunge/Uploader.swf',
        //是否要分片处理大文件上传。
        chunked: false,
        // 如果要分片，分多大一片？ 默认大小为5M.
        chunkSize: 5 * 1024 * 1024,
        // 上传并发数。允许同时最大上传进程数[默认值：3]   即上传文件数
        threads: 3,
        //文件接收服务端
        server: baseUrl+"file/save",
        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: '#picker',
        method:"POST",
        // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
        resize: false,
        accept:{
            title: 'Excell',
            extensions: 'xls,xlsx',
            mimeTypes: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel'
        }
    });
    // 当有文件被添加进队列的时候
    uploader.on('fileQueued', function (file) {
        var $list = $('#thelist');
        $list.append('<div id="' + file.id + '" class="item">' +
            '<h4 class="info">' + file.name + '</h4>' +
            '<p class="state">等待上传...</p>' +
            '</div>');
    });
    // 文件上传过程中创建进度条实时显示。
    uploader.on('uploadProgress', function (file, percentage) {
        var $li = $('#' + file.id),
            $percent = $li.find('.progress .progress-bar');

        // 避免重复创建
        if (!$percent.length) {
            $percent = $('<div class="progress progress-striped active">' +
                '<div class="progress-bar" role="progressbar" style="width: 0%">' +
                '</div>' +
                '</div>').appendTo($li).find('.progress-bar');
        }

        $li.find('p.state').text('上传中');
    });


    uploader.on('fileQueued', function (file) {
        uploader.md5File(file)

        // 及时显示进度
            .progress(function (percentage) {
                console.log('Percentage:', percentage);
                progress.set(percentage);
            })

            // 完成
            .then(function (val) {
                console.log('md5 result:', val);
            });

    });

    // 文件上传成功处理。
    uploader.on('uploadSuccess', function (file, response) {
        $('#' + file.id).find('p.state').text('已上传');
        console.log(response);
        var data=response;
        var filename=data.filename;
        switch (data.status) {
            case 0:
                alert("有"+data.count+"条数据导入失败");
                $("#downloadBtn").parent().show();
                $("#downloadBtn").unbind("click");
                $("#downloadBtn").on('click',function () {
                    $.download(baseUrl+"/file/download","GET",filename);
                })
                break;
            case 1:
                alert("所有数据导入成功");
                break;
        }
    });

    //上传出错
    uploader.on('uploadError', function (file) {
        $('#' + file.id).find('p.state').text('上传出错');
    });

    uploader.on('uploadComplete', function (file) {
        //$('#' + file.id).find('.progress').fadeOut();
        // $('#' + file.id).find('p.state').text('上传完成');
    });
    // 开始上传
    $('#uploadBtn').on('click', function (e) {
        uploader.upload();
    });
    //上传之前
    uploader.on('uploadBeforeSend', function (block, data) {
        var file=block.file;
        console.log(block);
    });



    /**
     * 上传图片区域
     */


    var imageup = WebUploader.create({

        // 开起分片上传。
        chunked: true,
        //选择完文件或是否自动上传
        auto: true,
        //swf文件路径
        swf: '../plunge/Uploader.swf',
        // 上传并发数。允许同时最大上传进程数[默认值：3]   即上传文件数
        threads: 3,
        //文件接收服务端
        server: baseUrl+"file/save",
        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: '#imgpicker',
        method:"POST",
        // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
        resize: false,
        accept:{
            title: 'Images',
            extensions: 'gif,jpg,jpeg,bmp,png',
            mimeTypes: 'image/*'
        }
    });
    // 当有文件被添加进队列的时候
    imageup.on('fileQueued', function (file) {
        var $list=$('#reply-img');
        var $li = $(
            '<li id="' + file.id + '" class="file-item thumbnail">' +
            '<img class="am-img-responsive am-img-thumbnail" alt="图片">' +
            '<div class="info">' + file.name + '</div>' +
            '</li>'
            ),
            $img = $li.find('img');
        $list.append($li);
        //创建图片预览
        imageup.makeThumb( file, function( error, src ) {
            if ( error ) {
                $img.replaceWith('<span>不能预览</span>');
                return;
            }
            $img.attr( 'src', src );
        });
    });
    // 文件上传过程中创建进度条实时显示。
    imageup.on('uploadProgress', function (file, percentage) {
        var $li = $( '#'+file.id ),
            $wait = $li.find('div.wait');
        // 避免重复创建
        if ( !$wait.length ) {
            $wait = $('<div class="wait"></div>').appendTo( $li );
        }

        $wait.text('上传中');
    });



    // 文件上传成功处理。
    imageup.on('uploadSuccess', function (file, response) {
        var $li = $( '#'+file.id ),
            $success = $li.find('div.success');
        // 避免重复创建
        if ( !$success.length ) {
            $success = $('<div class="success"></div>').appendTo( $li );
        }
        $li.children('div.wait').remove();
        $success.text('上传成功');
    });

    //上传出错
    imageup.on('uploadError', function (file) {
        var $li = $( '#'+file.id ),
            $error = $li.find('div.error');
        // 避免重复创建
        if ( !$error.length ) {
            $error = $('<div class="error"></div>').appendTo( $li );
        }
        $li.children('div.wait').remove();
        $error.text('上传失败');
    });

    imageup.on('uploadComplete', function (file) {
        //$('#' + file.id).find('.progress').fadeOut();
        // $('#' + file.id).find('p.state').text('上传完成');
    });


    /**
     * 文件下载
     * @param url 请求url
     * @param method 方法
     * @param filename 文件名
     */
    jQuery.download = function (url, method, filename) {
        jQuery('<form action="' + url + '" method="' + (method || 'post') + '">' +  // action请求路径及推送方法
            '<input type="text" name="filename" value="' + filename + '"/>' + // 文件路径
            '</form>')
            .appendTo('body').submit().remove();
            //新窗口打开
            var newTab = window.open('about:blank')
            newTab.location.href = url;
            //关闭新窗口
            newTab.close();
    }


    
});