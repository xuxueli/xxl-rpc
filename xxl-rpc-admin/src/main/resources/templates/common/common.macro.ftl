<#macro commonStyle>

	<#-- favicon -->
	<link rel="icon" href="${request.contextPath}/static/favicon.ico" />

	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<!-- Tell the browser to be responsive to screen width -->
	<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
	<!-- Bootstrap -->
	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/bootstrap/css/bootstrap.min.css">
	<!-- Font Awesome -->
	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/font-awesome/css/font-awesome.min.css">
	<!-- Ionicons -->
	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/Ionicons/css/ionicons.min.css">
	<!-- Theme style -->
	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/dist/css/AdminLTE.min.css">
	<!-- AdminLTE Skins. Choose a skin from the css/skins folder instead of downloading all of them to reduce the load. -->
	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/dist/css/skins/_all-skins.min.css">

	<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
	<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
	<!--[if lt IE 9]>
	<script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
	<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
	<![endif]-->

	<!-- pace -->
	<link rel="stylesheet" href="${request.contextPath}/static/adminlte/bower_components/PACE/themes/blue/pace-theme-flash.css">

	<!-- scrollup -->
	<link rel="stylesheet" href="${request.contextPath}/static/plugins/scrollup/image.css">

</#macro>

<#macro commonScript>

	<!-- jQuery 2.1.4 -->
	<script src="${request.contextPath}/static/adminlte/bower_components/jquery/jquery.min.js"></script>
	<!-- Bootstrap 3.3.5 -->
	<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap/js/bootstrap.min.js"></script>
	<!-- FastClick -->
	<script src="${request.contextPath}/static/adminlte/bower_components/fastclick/fastclick.js"></script>
	<!-- AdminLTE App -->
	<script src="${request.contextPath}/static/adminlte/dist/js/adminlte.min.js"></script>
	<!-- jquery.slimscroll -->
	<script src="${request.contextPath}/static/adminlte/bower_components/jquery-slimscroll/jquery.slimscroll.min.js"></script>

	<!-- pace -->
	<script src="${request.contextPath}/static/adminlte/bower_components/PACE/pace.min.js"></script>
	<#-- jquery cookie -->
	<script src="${request.contextPath}/static/plugins/jquery/jquery.cookie.js"></script>

	<#-- layer -->
	<script src="${request.contextPath}/static/plugins/layer/layer.js"></script>

	<!-- scrollup -->
	<script src="${request.contextPath}/static/plugins/scrollup/jquery.scrollUp.min.js"></script>

	<#-- common -->
	<script src="${request.contextPath}/static/js/common.1.js"></script>
	<script>
		var base_url = '${request.contextPath}';
	</script>

</#macro>

<#macro commonHeader>
	<header class="main-header">
        <a href="${request.contextPath}/" class="logo">
            <span class="logo-mini"><b>XXL</b></span>
			<span class="logo-lg"><b>分布式服务中心</b></span>
		</a>
        <nav class="navbar navbar-static-top" role="navigation">

            <a href="#" class="sidebar-toggle" data-toggle="push-menu" role="button">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>

            <div class="navbar-custom-menu">
                <ul class="nav navbar-nav">
                    <li class="dropdown user user-menu">
	                    <a href=";" id="logoutBtn" class="dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                      		<span class="hidden-xs">注销</span>
	                    </a>
					</li>
				</ul>
			</div>
		</nav>
	</header>
</#macro>

<#macro commonLeft pageName >
	<!-- Left side column. contains the logo and sidebar -->
	<aside class="main-sidebar">
		<!-- sidebar: style can be found in sidebar.less -->
		<section class="sidebar">
			<!-- sidebar menu: : style can be found in sidebar.less -->
			<ul class="sidebar-menu">
				<li class="header">导航</li>
                <li class="nav-click <#if pageName == "index">active</#if>" ><a href="${request.contextPath}/"><i class="fa fa-circle-o text-aqua"></i><span>运行报表</span></a></li>
                <li class="nav-click <#if pageName == "registry">active</#if>" ><a href="${request.contextPath}/registry"><i class="fa fa-circle-o text-yellow"></i><span>服务列表</span></a></li>
                <li class="nav-click <#if pageName == "help">active</#if>" ><a href="${request.contextPath}/help"><i class="fa fa-circle-o text-gray"></i><span>使用教程</span></a></li>
			</ul>
		</section>
		<!-- /.sidebar -->
	</aside>
</#macro>

<#macro commonFooter >
	<footer class="main-footer">
		Powered by <b>XXL-RPC</b> 1.7.1-SNAPSHOT
		<div class="pull-right hidden-xs">
			<strong>Copyright &copy; 2015-${.now?string('yyyy')} &nbsp;
				<a href="https://www.xuxueli.com/" target="_blank" >xuxueli</a>
				&nbsp;
				<a href="https://github.com/xuxueli/xxl-rpc" target="_blank" >github</a>
			</strong><!-- All rights reserved. -->
		</div>
	</footer>
</#macro>