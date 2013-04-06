$.namespace("druid.common");
druid.common = function () {  
	var statViewOrderBy = '';
	var statViewOrderBy_old = '';
	var statViewOrderType = 'asc';

	// only one page for now
	var sqlViewPage = 1;
	var sqlViewPerPageCount = 1000000;
	
	return  {
		init : function() {
			this.buildFooter();
		},
		
		buildHead : function(index) {
			var html = '<div class="navbar navbar-fixed-top">'+
						'	<div class="navbar-inner">'+
						'		<div class="container">'+
						'			<a href="https://github.com/AlibabaTech/druid/wiki" target="_blank" class="brand">Druid Monitor</a>'+
						'			<div class="nav-collapse">'+
				      	'				<ul class="nav">'+
				      	'					<li><a href="index.html">Index</a></li>'+
				      	'					<li><a href="datasource.html">DataSource</a></li>'+
				      	'					<li><a href="sql.html">SQL</a></li>'+
				      	'					<li><a href="wall.html">Wall</a></li>'+
				      	'					<li><a href="webapp.html">WebApp</a></li>'+
				      	'					<li><a href="weburi.html">WebURI</a></li>'+
				      	'					<li><a href="websession.html">Web Session</a></li>'+
				      	'					<li><a href="spring.html">Spring</a></li>'+
				      	'					<li><a href="api.html">JSON API</a></li>'+
				      	'				</ul>'+
				      	'				<a class="btn btn-primary" href="javascript:druid.common.ajaxRequestForReset();">Reset All</a>'+
				      	'			</div>'+
				      	'		</div>'+
				      	'	</div>'+
						'</div>'; 
			$(document.body).prepend(html);
			$(".navbar .nav li").eq(index).addClass("active");
		},
		
		buildFooter : function() {
			var html = '<footer class="footer">'+
					  '    		<div class="container">'+
				  	  '	powered by <a href="https://github.com/AlibabaTech/" target="_blank">AlibabaTech</a> & <a href="http://www.sandzhang.com/" target="_blank">sandzhang</a> & <a href="http://melin.iteye.com/" target="_blank">melin</a> & <a href="https://github.com/shrekwang" target="_blank">shrek.wang</a><br/>'+
				  	  ' <img src="img/logo.jpg" />'
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
		},
		
		getAjaxUrl : function(uri) {
			var result = uri;

			if (statViewOrderBy != undefined)
				result += 'orderBy=' + statViewOrderBy + '&';

			if (statViewOrderType != undefined)
				result += 'orderType=' + statViewOrderType + '&';

			if (sqlViewPage != undefined)
				result += 'page=' + sqlViewPage + '&';

			if (sqlViewPerPageCount != undefined)
				result += 'perPageCount=' + sqlViewPerPageCount + '&';

			return result;
		},
		
		resetSortMark : function() {
			var divObj = document.getElementById('th-' + statViewOrderBy);
			var old_divObj = document.getElementById('th-' + statViewOrderBy_old);
			var replaceToStr = '';
			if (old_divObj) {
				var html = old_divObj.innerHTML;
				if (statViewOrderBy_old.indexOf('[') > 0)
					replaceToStr = '-';
				html = html.replace('▲', replaceToStr);
				html = html.replace('▼', replaceToStr);
				old_divObj.innerHTML = html
			}
			if (divObj) {
				var html = divObj.innerHTML;
				if (statViewOrderBy.indexOf('[') > 0)
					html = '';

				if (statViewOrderType == 'asc') {
					html += '▲';
				} else if (statViewOrderType == 'desc') {
					html += '▼';
				}
				divObj.innerHTML = html;
			}
			
			this.ajaxRequestForBasicInfo();
		},

		setOrderBy : function(orderBy) {
			if (statViewOrderBy != orderBy) {
				statViewOrderBy_old = statViewOrderBy;
				statViewOrderBy = orderBy;
				statViewOrderType = 'desc';
				druid.common.resetSortMark();
				return;
			}

			statViewOrderBy_old = statViewOrderBy;

			if (statViewOrderType == 'asc')
				statViewOrderType = 'desc'
			else
				statViewOrderType = 'asc';

			druid.common.resetSortMark();
		},
		
		ajaxuri : "",
		handleAjaxResult : null,
		ajaxRequestForBasicInfo : function() {
			$.ajax({
				type: 'POST',
				url: druid.common.getAjaxUrl(druid.common.ajaxuri),
				success: function(data) {
					druid.common.handleAjaxResult(data);
				},
				dataType: "json"
			});
		},
		
		subSqlString : function(sql, len) {
			if (sql == undefined || sql == null) {
				return '';
			}
			
			if (sql.length <= len)
				return sql;
			return sql.substr(0, len) + '...';
		},
		
		stripes : function() {
            $("#dataTable tbody tr").each(function () {
                $(this).removeClass("striped");
            });
            $("#dataTable tbody tr:even").each(function () {
                $(this).addClass("striped");
            });
        },
        
        getUrlVar : function(name) {
            var vars = {};
            var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
                vars[key] = value;
            });
        		return vars[name];
        }
	}
}();

$(document).ready(function() {
	druid.common.init();
});
