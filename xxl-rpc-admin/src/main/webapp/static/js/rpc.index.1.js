$(function() {
	// init date tables
	var dataTable = $("#data_list").dataTable({
		"deferRender": true,
		"processing" : true, 
	    "serverSide": true,
		"ajax": {
			url: base_url + "/rpc/pageList",
	        data : function ( d ) {
                d.iface = $('#iface').val();
            }
	    },
	    "searching": false,
	    "ordering": false,
	    //"scrollX": true,	// X轴滚动条，取消自适应
	    "columns": [
	                { "data": 'iface'},
	                { "data": 'providers',
	                	"render": function ( data, type, row ) {
	                		return function(){
	                			var html = '';
	                			if (data && data.length > 0) {
									for (var int = 0; int < data.length; int++) {
										html += '[' + data[int] + ']，';
									}
									html = html.substring(0, html.length - 1)
								}
	                			return html;
	                		}
	                	}
	                },
	                { "data": 'iface' ,
	                	"render": function ( data, type, row ) {
	                		return function(){
	                			var html = '<p iface="'+ row.iface + '">' +
								  		'<button class="btn btn-danger btn-xs job_operate" type="remove" type="button">移除服务</button>  '+
								  		'</p>';
	                			return html;
	                		};
	                	}
	                }
	            ],
		"language" : {
			"sProcessing" : "处理中...",
			"sLengthMenu" : "每页 _MENU_ 条记录",
			"sZeroRecords" : "没有匹配结果",
			"sInfo" : "第 _PAGE_ 页 ( 总共 _PAGES_ 页 )",
			"sInfoEmpty" : "无记录",
			"sInfoFiltered" : "(由 _MAX_ 项结果过滤)",
			"sInfoPostFix" : "",
			"sSearch" : "搜索:",
			"sUrl" : "",
			"sEmptyTable" : "表中数据为空",
			"sLoadingRecords" : "载入中...",
			"sInfoThousands" : ",",
			"oPaginate" : {
				"sFirst" : "首页",
				"sPrevious" : "上页",
				"sNext" : "下页",
				"sLast" : "末页"
			},
			"oAria" : {
				"sSortAscending" : ": 以升序排列此列",
				"sSortDescending" : ": 以降序排列此列"
			}
		}
	});
	
	// 搜索按钮
	$('#searchBtn').on('click', function(){
		dataTable.fnDraw();
	});
	
	// job operate
	$("#data_list").on('click', '.job_operate',function() {
		var typeName;
		var url;
		var type = $(this).attr("type");
		if ("remove" == type) {
			typeName = "移除";
			url = base_url + "/rpc/remove";
		} else {
			return;
		}
		
		var iface = $(this).parent('p').attr("iface");
		ComConfirm.show("确认" + typeName + "?", function(){
			$.ajax({
				type : 'POST',
				url : url,
				data : {
					"iface"  : iface
				},
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
						ComAlert.show(1, typeName + "成功", function(){
							//window.location.reload();
							dataTable.fnDraw();
						});
					} else {
						ComAlert.show(1, typeName + "失败");
					}
				},
			});
		});
	});
	
});
