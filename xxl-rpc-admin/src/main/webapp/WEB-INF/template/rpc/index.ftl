<!DOCTYPE html>
<html>
<head>
  	<title>服务治理中心</title>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
	<!-- DataTables -->
  	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
  	
</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && "off" == cookieMap["adminlte_settings"].value >sidebar-collapse</#if>">
<div class="wrapper">
	<!-- header -->
	<@netCommon.commonHeader />
	<!-- left -->
	<@netCommon.commonLeft />
	
	<!-- Content Wrapper. Contains page content -->
	<div class="content-wrapper">
		<!-- Content Header (Page header) -->
		<section class="content-header">
			<h1>服务管理<small></small></h1>
			<ol class="breadcrumb">
				<li><a><i class="fa fa-dashboard"></i>服务管理</a></li>
				<li class="active">服务中心</li>
			</ol>
		</section>
		
		<!-- Main content -->
	    <section class="content">
	    
	    	<div class="row">
	            <div class="col-xs-8">
	              	<div class="input-group">
	                	<span class="input-group-addon">服务接口</span>
	                	<input type="text" class="form-control" id="iface" value="${iface}" autocomplete="on" >
	              	</div>
	            </div>
	            <div class="col-xs-2">
	            	<button class="btn btn-block btn-info" id="searchBtn">搜索</button>
	            </div>
          	</div>
	    	
			<div class="row">
				<div class="col-xs-12">
					<div class="box">
			            <div class="box-header">
			            	<h3 class="box-title">调度列表</h3>
			            </div>
			            <div class="box-body">
			              	<table id="data_list" class="table table-bordered table-striped">
				                <thead>
					            	<tr>
					                	<th name="iface" >服务接口</th>
					                  	<th name="register_address" >注册机器</th>
					                  	<th>操作</th>
					                </tr>
				                </thead>
				                <tbody></tbody>
				                <tfoot></tfoot>
							</table>
						</div>
					</div>
				</div>
			</div>
			
	    </section>
	</div>
	
	<!-- footer -->
	<@netCommon.commonFooter />
</div>

<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<!-- daterangepicker -->
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.js"></script>
<script>var base_url = '${request.contextPath}';</script>
<script src="${request.contextPath}/static/js/rpc.index.1.js"></script>
</body>
</html>
