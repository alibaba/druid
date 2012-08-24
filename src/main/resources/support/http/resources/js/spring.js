$.namespace("druid.spring");
druid.spring = function () {  
	return  {
		init : function() {
			druid.common.buildHead(5);
			druid.common.ajaxuri = 'spring.json?';
			druid.common.handleAjaxResult = druid.spring.handleAjaxResult;
			this.ajaxRequestForBasicInfo();
			setInterval("druid.common.ajaxRequestForBasicInfo();",5000);
		},
		
		handleAjaxResult : function(data) {
			var statList = data.Content;
			if(statList==null) return;
			
			var sqlStatTable = document.getElementById("SpringStatTable");
			while (sqlStatTable.rows.length > 1) {
				sqlStatTable.deleteRow(1);
			}
			for ( var i = 0; i < statList.length; i++) {
				var stat = statList[i];
				var newRow = sqlStatTable.insertRow(-1);
				newRow.insertCell(-1).innerHTML = i+1;
				newRow.insertCell(-1).innerHTML = stat.Class;
				newRow.insertCell(-1).innerHTML = '<a target="_blank" href="spring-detail.html?class=' + stat.Class + '&method=' + stat.Method + '">' + stat.Method + '</a>';
				
				newRow.insertCell(-1).innerHTML = stat.ExecuteCount;
				newRow.insertCell(-1).innerHTML = stat.ExecuteTimeMillis;
				newRow.insertCell(-1).innerHTML = stat.RunningCount;
				newRow.insertCell(-1).innerHTML = stat.ConcurrentMax;
				newRow.insertCell(-1).innerHTML = stat.JdbcExecuteCount;
				newRow.insertCell(-1).innerHTML = stat.JdbcExecuteTimeMillis;
				newRow.insertCell(-1).innerHTML = stat.JdbcCommitCount;
				newRow.insertCell(-1).innerHTML = stat.JdbcRollbackCount;
				newRow.insertCell(-1).innerHTML = stat.JdbcFetchRowCount;
				newRow.insertCell(-1).innerHTML = stat.JdbcUpdateCount;
			}
		}
	}
}();

$(document).ready(function() {
	druid.spring.init();
});

