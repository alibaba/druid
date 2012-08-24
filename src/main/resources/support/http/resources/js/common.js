$.namespace("druid.common");
druid.common = function () {  
	return  {
		init : function() {
			this.buildHead();
			this.buildMenu();
			this.buildFooter();
		},
		
		buildHead : function() {
			var html = '<div class="navbar navbar-fixed-top">'+
				       '	<div class="navbar-inner">'+
				       ' 		<div class="container-fluid">'+
				       '   			<a class="brand" href="#">Druid Monitor</a>'+
				       '   			<div class="pull-right">'+
				       '   				<a href="javascript:druid.common.ajaxRequestForReset()" class="btn">Reset All</a>'+
				       '   			</div>'+
				       ' 		</div>'+
				       '		</div>'+
				       '</div>';
			$(document.body).prepend(html);
		},
		
		buildMenu : function() {
			var html = '<div class="span2">'+
				      	'		<div class="well sidebar-nav">'+
						'	<ul class="nav nav-list">'+
				  		'		<li class="nav-header"><h3>Menu</h3></li>'+
				  		'		<li id="configId"><a href="index.html">Index</a></li>'+
				  		'		<li id="userAddId"><a href="datasource.html">DataSource</a></li>'+
				  		'		<li id="userAddId"><a href="sql.html">Sql</a></li>'+
				  		'		<li id="userAddId"><a href="weburi.html">WebURI</a></li>'+
				  		'		<li id="userAddId"><a href="websession.html">Web Session</a></li>'+
				  		'		<li id="userAddId"><a href="spring.html">Spring</a></li>'+
				  		'		<li id="userAddId"><a href="api.html">JSON API</a></li>'+
						'	</ul>'+
						'	</div>'+
					'</div>';
			$(".row-fluid").prepend(html);
		},

		buildFooter : function() {
			var html = '<footer class="footer">'+
					  '    		<div class="container">'+
				  		'	powered by <a href="mailto:sandzhangtoo@gmail.com">sandzhang</a> & <a href="mailto:libinsong1204@gmail.com">melin</a>'+
				  	'	</div>'+
					'</footer>';
			$(document.body).append(html);
		},
		
		ajaxRequestForReset : function() {
			if(!confirm("Are you sure to reset all stat? It'll clear all stat data !")){
				return;
			}
			
			$.ajax({
				type: 'POST',
				url: "reset-all.json",
				success: function(data) {
					if (data.ResultCode == 1) {
						alert("already reset all stat");
					}
				},
				dataType: "json"
			});
		}
	}
}();

$(document).ready(function() {
	druid.common.init();
});

function getUrlVar(name) {
    var vars = {};
    var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
        vars[key] = value;
    });
		return vars[name];
}