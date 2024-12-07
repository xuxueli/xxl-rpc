$(function() {

	// select2
	$("#addModal .form select[name='appname']").select2()
	$("#updateModal .form select[name='appname']").select2()

	// ---------- ---------- ---------- main table  ---------- ---------- ----------
	// init date tables
	$.dataTableSelect.init();
	var mainDataTable = $("#data_list").dataTable({
		"deferRender": true,
		"processing" : true, 
	    "serverSide": true,
		"ajax": {
			url: base_url + "/instance/pageList",
			type:"post",
			// request data
	        data : function ( d ) {
	        	var obj = {};
                obj.appname = $('#data_filter .appname').val();
                obj.env = $('#data_filter .env').val();
	        	obj.start = d.start;
	        	obj.length = d.length;
                return obj;
            },
			// response data filter
			dataFilter: function (originData) {
				var originJson = $.parseJSON(originData);
				return JSON.stringify({
					recordsTotal: originJson.data.totalCount,
					recordsFiltered: originJson.data.totalCount,
					data: originJson.data.pageData
				});
			}
	    },
	    "searching": false,
	    "ordering": false,
	    //"scrollX": true,																		// scroll x，close self-adaption
		//"dom": '<"top" t><"bottom" <"col-sm-3" i><"col-sm-3 right" l><"col-sm-6" p> >',		// dataTable "DOM layout"：https://datatables.club/example/diy.html
		"drawCallback": function( settings ) {
			$.dataTableSelect.selectStatusInit();
		},
	    "columns": [
			{
				"title": '<input align="center" type="checkbox" id="checkAll" >',
				"data": 'id',
				"visible" : true,
				"width":'5%',
				"render": function ( data, type, row ) {
					tableData['key'+row.id] = row;
					return '<input align="center" type="checkbox" class="checkItem" data-id="'+ row.id +'"  >';
				}
			},
			{
				"title": 'Env',
				"data": 'env',
				"width":'5%'
			},
			{
				"title": 'AppName',
				"data": 'appname',
				"width":'15%'
			},
			{
				"title": '分组',
				"data": 'group',
				"width":'10%'
			},
			{
				"title": 'IP:PORT',
				"data": 'ip',
				"width":'15%',
				"render": function ( data, type, row ) {
					return row.ip + ":" + row.port
				}
			},
			{
				"title": '注册模式',
				"data": 'registerModel',
				"width":'10%',
				"render": function ( data, type, row ) {
					var ret = data;
					$("#addModal .form select[name='registerModel']").children("option").each(function() {
						if ($(this).val() === row.registerModel+"") {
							ret = $(this).html();
						}
					});
					return ret;
				}
			},
			{
				"title": '最后注册心跳时间',
				"data": 'registerHeartbeat',
				"width":'12%'
			},
		],
		"language" : {
			"sProcessing" : I18n.dataTable_sProcessing ,
			"sLengthMenu" : I18n.dataTable_sLengthMenu ,
			"sZeroRecords" : I18n.dataTable_sZeroRecords ,
			"sInfo" : I18n.dataTable_sInfo ,
			"sInfoEmpty" : I18n.dataTable_sInfoEmpty ,
			"sInfoFiltered" : I18n.dataTable_sInfoFiltered ,
			"sInfoPostFix" : "",
			"sSearch" : I18n.dataTable_sSearch ,
			"sUrl" : "",
			"sEmptyTable" : I18n.dataTable_sEmptyTable ,
			"sLoadingRecords" : I18n.dataTable_sLoadingRecords ,
			"sInfoThousands" : ",",
			"oPaginate" : {
				"sFirst" : I18n.dataTable_sFirst ,
				"sPrevious" : I18n.dataTable_sPrevious ,
				"sNext" : I18n.dataTable_sNext ,
				"sLast" : I18n.dataTable_sLast
			},
			"oAria" : {
				"sSortAscending" : I18n.dataTable_sSortAscending ,
				"sSortDescending" : I18n.dataTable_sSortDescending
			}
		}
	});

    // table data
    var tableData = {};

	// search btn
	$('#data_filter .searchBtn').on('click', function(){
        mainDataTable.fnDraw();
	});

	// ---------- ---------- ---------- delete operation ---------- ---------- ----------
	// delete
	$("#data_operation").on('click', '.delete',function() {

		// find select ids
		var selectIds = $.dataTableSelect.selectIdsFind();
		if (selectIds.length <= 0) {
			layer.msg(I18n.system_please_choose + I18n.system_data);
			return;
		}

		// do delete
		layer.confirm( I18n.system_ok + I18n.system_opt_del + '?', {
			icon: 3,
			title: I18n.system_tips ,
            btn: [ I18n.system_ok, I18n.system_cancel ]
		}, function(index){
			layer.close(index);

			$.ajax({
				type : 'POST',
				url : base_url + "/instance/delete",
				data : {
					"ids" : selectIds
				},
				dataType : "json",
				success : function(data){
					if (data.code == 200) {
                        layer.msg( I18n.system_opt_del + I18n.system_success );
						mainDataTable.fnDraw(false);	// false，refresh current page；true，all refresh
					} else {
                        layer.msg( data.msg || I18n.system_opt_del + I18n.system_fail );
					}
				},
				error: function(xhr, status, error) {
					// Handle error
					console.log("Error: " + error);
					layer.open({
						icon: '2',
						content: (I18n.system_opt_del + I18n.system_fail)
					});
				}
			});
		});
	});

	// ---------- ---------- ---------- add operation ---------- ---------- ----------
	// add validator method
	jQuery.validator.addMethod("groupValid", function(value, element) {
		var valid = /^[a-z][a-z0-9]*$/;
		return this.optional(element) || valid.test(value);
	}, '限制小写字母开头，由小写字母、数字组成' );
	// add
	$("#data_operation .add").click(function(){
		$('#addModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var addModalValidate = $("#addModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,  
        rules : {
			group : {
				required : false,
                rangelength:[4, 30],
				groupValid: true
			},
			ip : {
                required : true,
				rangelength:[9, 46]
            },
			port : {
				required : true,
				range:[1000, 65535]
			}
        }, 
        messages : {
			group : {
            	required : I18n.system_please_input,
                rangelength: I18n.system_lengh_limit + "[4-30]"
            },
			ip : {
                required : I18n.system_please_input,
                rangelength: I18n.system_lengh_limit + "[9-46]"
            },
			port : {
				required : I18n.system_please_input,
				rangelength: I18n.system_num_range + "[1000-65535]"
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

			// request
			var paramData = $("#addModal .form").serializeArray();

			// post
        	$.post(base_url + "/instance/insert", paramData, function(data, status) {
    			if (data.code == "200") {
					$('#addModal').modal('hide');

                    layer.msg( I18n.system_opt_add + I18n.system_success );
                    mainDataTable.fnDraw();
    			} else {
					layer.open({
						title: I18n.system_tips ,
                        btn: [ I18n.system_ok ],
						content: (data.msg || I18n.system_opt_add + I18n.system_fail ),
						icon: '2'
					});
    			}
    		});
		}
	});
	$("#addModal").on('hide.bs.modal', function () {
		addModalValidate.resetForm();

		$("#addModal .form")[0].reset();
		$("#addModal .form .form-group").removeClass("has-error");
	});

	// ---------- ---------- ---------- update operation ---------- ---------- ----------
	$("#data_operation .update").click(function(){

		// find select ids
		var selectIds = $.dataTableSelect.selectIdsFind();
		if (selectIds.length != 1) {
			layer.msg(I18n.system_please_choose + I18n.system_one + I18n.system_data);
			return;
		}
		var row = tableData[ 'key' + selectIds[0] ];

		// base data
		$("#updateModal .form input[name='id']").val( row.id );
		$("#updateModal .form select[name='env']").val( row.env );
		$("#updateModal .form select[name='appname']").val( row.appname );
		$("#updateModal .form input[name='group']").val( row.group );
		$("#updateModal .form input[name='ip']").val( row.ip );
		$("#updateModal .form input[name='port']").val( row.port );
		$("#updateModal .form select[name='registerModel']").val( row.registerModel );

		// show
		$('#updateModal').modal({backdrop: false, keyboard: false}).modal('show');
	});
	var updateModalValidate = $("#updateModal .form").validate({
		errorElement : 'span',  
        errorClass : 'help-block',
        focusInvalid : true,
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
		rules : {
			name : {
				required : true,
				rangelength:[4, 20]
			},
			desc : {
				required : true,
				rangelength:[4, 100]
			},
			accessToken : {
				required : true,
				rangelength:[4, 50],
				accessTokenValid: true
			}
		},
		messages : {
			name : {
				required : I18n.system_please_input,
				rangelength: I18n.system_lengh_limit + "[4-20]"
			},
			desc : {
				required : I18n.system_please_input,
				rangelength: I18n.system_lengh_limit + "[4-100]"
			},
			accessToken : {
				required : I18n.system_please_input,
				rangelength: I18n.system_lengh_limit + "[4-50]"
			}
		},
        submitHandler : function(form) {

			// request
			var paramData = $("#updateModal .form").serializeArray();

            $.post(base_url + "/instance/update", paramData, function(data, status) {
                if (data.code == "200") {
                    $('#updateModal').modal('hide');

                    layer.msg( I18n.system_opt_edit + I18n.system_success );
					mainDataTable.fnDraw(false);
                } else {
                    layer.open({
                        title: I18n.system_tips ,
                        btn: [ I18n.system_ok ],
                        content: (data.msg || I18n.system_opt_edit + I18n.system_fail ),
                        icon: '2'
                    });
                }
            });
		}
	});
	$("#updateModal").on('hide.bs.modal', function () {

		// reset
		updateModalValidate.resetForm();

		$("#updateModal .form")[0].reset();
        $("#updateModal .form .form-group").removeClass("has-error");
	});

});
