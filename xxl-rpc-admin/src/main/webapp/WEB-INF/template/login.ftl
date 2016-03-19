<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<title>服务中心</title> 
	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />

</head>
<body class="hold-transition login-page">
	<div class="login-box">
		<div class="login-logo"><a><b>xxl</b>RPC</a></div>
		<div class="login-box-body">
			<p class="login-box-msg">分布式配置管理平台</p>
			<form id="loginForm">
				<div class="form-group has-feedback">
					<input type="text" class="form-control" name="userName" placeholder="请输入账号" minlength="6" maxlength="18" value="admin" >
					<span class="glyphicon glyphicon-envelope form-control-feedback"></span>
				</div>
				<div class="form-group has-feedback">
					<input type="password" class="form-control" name="password" placeholder="请输入密码" minlength="6" maxlength="18" value="123456" >
		            <span class="glyphicon glyphicon-lock form-control-feedback"></span>
				</div>
				<div class="row">
					<!--
					<div class="col-xs-8">
						<div class="checkbox icheck">
		                	<label><input type="checkbox"> Remember Me</label>
		              	</div>
		            </div>
		            -->
		            <div class="col-xs-4">
		              	<button type="submit" class="btn btn-primary btn-block btn-flat">登陆</button>
		            </div><!-- /.col -->
				</div>
			</form>
	        <!-- <a href="javascript:alert('Bingo...');">忘记密码</a><br> -->

		</div><!-- /.login-box-body -->
	</div><!-- /.login-box -->
	
<@netCommon.commonScript />
<@netCommon.comAlert />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<!-- daterangepicker -->
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/daterangepicker.js"></script>
<script>var base_url = '${request.contextPath}';</script>
<script src="${request.contextPath}/static/js/login.1.js"></script>
</body>
</html>
