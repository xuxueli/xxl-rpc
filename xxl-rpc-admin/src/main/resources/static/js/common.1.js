$(function(){

    // logout
    $("#logoutBtn").click(function(){
        layer.confirm( "确认注销登录?" , {
            icon: 3,
            title: "系统提示" ,
            btn: [ "确认", "取消" ]
        }, function(index){
            layer.close(index);

            $.post(base_url + "/logout", function(data, status) {
                if (data.code == "200") {
                    layer.msg( "注销成功" );
                    setTimeout(function(){
                        window.location.href = base_url + "/";
                    }, 500);
                } else {
                    layer.open({
                        title: I18n.system_tips ,
                        btn: [ I18n.system_ok ],
                        content: (data.msg || "注销失败" ),
                        icon: '2'
                    });
                }
            });
        });

    });


	// scrollup
	$.scrollUp({
		animation: 'fade',	// fade/slide/none
		scrollImg: true
	});


    // left menu status v: js + server + cookie
    $('.sidebar-toggle').click(function(){
        var xxlrpc_adminlte_settings = $.cookie('xxlrpc_adminlte_settings');	// on=open，off=close
        if ('off' == xxlrpc_adminlte_settings) {
            xxlrpc_adminlte_settings = 'on';
        } else {
            xxlrpc_adminlte_settings = 'off';
        }
        $.cookie('xxlrpc_adminlte_settings', xxlrpc_adminlte_settings, { expires: 7 });	//$.cookie('the_cookie', '', { expires: -1 });
    });

});
