$(function() {

	// init date tables
	var dataTable = $("#data_list").dataTable({
		"deferRender": true,
		"processing" : true, 
	    "serverSide": true,
		"ajax": {
			url: base_url + "/registry/pageList",
	        data : function ( d ) {
				var obj = {};
				obj.start = d.start;
				obj.length = d.length;
				obj.biz = $('#biz').val();
				obj.env = $('#env').val();
                obj.key = $('#key').val();
				return obj;
            }
	    },
	    "searching": false,
	    "ordering": false,
	    //"scrollX": true,	// X轴滚动条，取消自适应
	    "columns": [
	                { data: 'id'},
					{ data: 'biz'},
            		{ data: 'env'},
            		{ data: 'key'},
                    {
                        data: 'data',
                        ordering: true,
                        render : function ( data, type, row ) {
                            if (data) {
                                return '<a href="javascript:;" class="showData" _id="'+ row.id +'">查看</spam></a>';
                            } else {
                                return '空';
                            }
                        }
                    },
					{ data: 'version'},
					{
						data: 'status',
                        ordering: true,
						render : function ( data, type, row ) {
						    if (data == 0) {
						        return '正常';
                            } else if (data == 1) {
                                return '锁定';
                            }
							return data;
						}
					},

	                { data: 'opt' ,
	                	"render": function ( data, type, row ) {
	                		return function(){

	                			// data
                                tableData['key'+row.id] = row;

                                // opt
	                			var html = '<p id="'+ row.id +'" >'+
										'<button class="btn btn-info btn-xs registry_update" type="button">编辑</button>  '+
										'<button class="btn btn-danger btn-xs registry_remove" type="button">删除</button>  '+
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
			"sInfo" : "第 _PAGE_ 页 ( 总共 _PAGES_ 页 ) 总记录数 _MAX_ ",
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

    // table data
    var tableData = {};

	// msg 弹框
    $("#data_list").on('click', '.showData',function() {
        var _id = $(this).attr('_id');
        var row = tableData['key' + _id ];
        ComAlertTec.show(row.data);
    });

    // search btn
	$('#searchBtn').on('click', function(){
		dataTable.fnDraw();
	});
	
	// registry_remove
	$("#data_list").on('click', '.registry_remove',function() {

		var id = $(this).parent('p').attr("id");

        layer.confirm( "确认删除该消息?", {
            icon: 3,
            title: "系统提示" ,
            btn: [ "确认", "取消" ]
        }, function(index){
            layer.close(index);

            $.ajax({
                type : 'POST',
                url : base_url + "/registry/delete",
                data : {
                    "id"  : id
                },
                dataType : "json",
                success : function(data){
                    if (data.code == 200) {

                        layer.open({
                            title: "系统提示",
                            btn: [ "确认" ],
                            content: "删除成功" ,
                            icon: '1',
                            end: function(layero, index){
                                dataTable.fnDraw(false);
                            }
                        });
                    } else {
                        layer.open({
                            title: "系统提示",
                            btn: [ "确认" ],
                            content: (data.msg || "删除失败" ),
                            icon: '2'
                        });
                    }
                }
            });
        });

	});

	// registry_add
	$('#registry_add').on('click', function(){
		$('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',
		errorClass : 'help-block',
		focusInvalid : true,
		rules : {
            biz : {
                required : true ,
                rangelength:[4,255]
            },
            env : {
                required : true ,
                rangelength:[4,255]
            },
            key : {
                required : true ,
                rangelength:[4,255]
            }
		},
		messages : {
            biz : {
                required :'请输入'  ,
                rangelength: '长度限制为[4~255]'
            },
            env : {
                required :'请输入'  ,
                rangelength: '长度限制为[4~255]'
            },
            key : {
                required :'请输入'  ,
                rangelength: '长度限制为[4~255]'
            }
		},
		highlight : function(element) {
			$(element).closest('.form-group').addClass('has-error');
		},
		success : function(label) {
			label.closest('.form-group').removeClass('has-error');
			label.remove();
		},
		errorPlacement : function(error, element) {
			element.parent('div').append(error);
		},
		submitHandler : function(form) {
			$.post(base_url + "/registry/add", $("#addModal .form").serialize(), function(data, status) {
				if (data.code == "200") {
					$('#addModal').modal('hide');

                    layer.open({
                        title: "系统提示",
                        btn: [ "确认" ],
                        content: "新增成功" ,
                        icon: '1',
                        end: function(layero, index){
                            dataTable.fnDraw(false);
                        }
                    });
				} else {
                    layer.open({
                        title: "系统提示",
                        btn: [ "确认" ],
                        content: (data.msg || "操作失败" ),
                        icon: '2'
                    });
				}
			});
		}
	});
	$("#addModal").on('hide.bs.modal', function () {
		$("#addModal .form")[0].reset();
		addModalValidate.resetForm();
		$("#addModal .form .form-group").removeClass("has-error");
	});

	// registry_update
	$("#data_list").on('click', '.registry_update',function() {
		var id = $(this).parent('p').attr("id");
        var row = tableData['key' + id ];

		$("#updateModal .form input[name='id']").val( id );
		$("#updateModal .form input[name='biz']").val( row.biz );
        $("#updateModal .form input[name='env']").val( row.env );
        $("#updateModal .form input[name='key']").val( row.key );
        $("#updateModal .form textarea[name='data']").val( row.data );
        $("#updateModal .form select[name='status']").find("option[value='" + row.status + "']").prop("selected",true);

		$('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var updateModalValidate = $("#updateModal .form").validate({
		errorElement : 'span',
		errorClass : 'help-block',
		focusInvalid : true,
        rules : {
            biz : {
                required : true ,
                rangelength:[4,255]
            },
            env : {
                required : true ,
                rangelength:[4,255]
            },
            key : {
                required : true ,
                rangelength:[4,255]
            }
        },
        messages : {
            biz : {
                required :'请输入'  ,
                rangelength: '长度限制为[4~255]'
            },
            env : {
                required :'请输入'  ,
                rangelength: '长度限制为[4~255]'
            },
            key : {
                required :'请输入'  ,
                rangelength: '长度限制为[4~255]'
            }
        },
		highlight : function(element) {
			$(element).closest('.form-group').addClass('has-error');
		},
		success : function(label) {
			label.closest('.form-group').removeClass('has-error');
			label.remove();
		},
		errorPlacement : function(error, element) {
			element.parent('div').append(error);
		},
		submitHandler : function(form) {
			$.post(base_url + "/registry/update", $("#updateModal .form").serialize(), function(data, status) {
				if (data.code == "200") {
					$('#updateModal').modal('hide');

                    layer.open({
                        title: "系统提示",
                        btn: [ "确认" ],
                        content: "更新成功" ,
                        icon: '1',
                        end: function(layero, index){
                            dataTable.fnDraw(false);
                        }
                    });
				} else {
                    layer.open({
                        title: "系统提示",
                        btn: [ "确认" ],
                        content: (data.msg || "更新失败" ),
                        icon: '2'
                    });
				}
			});
		}
	});
	$("#updateModal").on('hide.bs.modal', function () {
		$("#updateModal .form")[0].reset();
		updateModalValidate.resetForm();
		$("#updateModal .form .form-group").removeClass("has-error");
	});


});


// Com Alert by Tec theme
var ComAlertTec = {
    html:function(){
        var html =
            '<div class="modal fade" id="ComAlertTec" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">' +
            '<div class="modal-dialog">' +
            '<div class="modal-content-tec">' +
            '<div class="modal-body"><div class="alert" style="color:#fff;"></div></div>' +
            '<div class="modal-footer">' +
            '<div class="text-center" >' +
            '<button type="button" class="btn btn-info ok" data-dismiss="modal" >确认</button>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '</div>';
        return html;
    },
    show:function(msg, callback){
        // dom init
        if ($('#ComAlertTec').length == 0){
            $('body').append(ComAlertTec.html());
        }

        // init com alert
        $('#ComAlertTec .alert').html(msg);
        $('#ComAlertTec').modal('show');

        $('#ComAlertTec .ok').click(function(){
            $('#ComAlertTec').modal('hide');
            if(typeof callback == 'function') {
                callback();
            }
        });
    }
};
