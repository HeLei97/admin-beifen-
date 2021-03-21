// 邮箱正则
var emaileg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
/**
 * 敏感操作验证密码
 * @param Func 验证成功执行代码
 * @constructor
 */
function Passwordverification(Func) {
    var $=layui.$;
    layer.prompt({
        formType: 1
        ,title: '敏感操作，请验证口令'
    }, function(value, index){
        var index1;
        $.ajax({
            url: "/user/ConfirmPassword",
            type: "post",
            data: {"operation_pwd":value},
            dataType: "json",
            beforeSend: function () {
                index1 = layer.load(0, {shade: false});
            }, complete: function () {
                layer.close(index1);
            }, success: function (data) {
                if (data.code_type == 0) {
                    layer.close(index);
                    Func();
                }else if (data.code == 20011){
                    layer.msg("密码校验失败:"+data.msg, {icon: 2});
                }else {
                    layer.msg('密码错误!', {icon: 2});
                }
            },
            error: function (data) {
                top.layer.msg('网络请求失败，请重试！', {icon: 5});
            }
        });
    });
}

/**
 * 获取连接参数
 * @param name key
 * @returns {string|null} values
 */
function getQueryString(name) {
    var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
    var r = window.location.search.substr(1).match(reg);
    if (r != null) {
        return unescape(r[2]);
    }
    return null;
}

//判断字符是否为空的方法
function isEmpty(obj){
    if(typeof obj == "undefined" || obj == null || obj == ""){
        return true;
    }else{
        return false;
    }
}