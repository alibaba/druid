$.namespace("druid.websession");
druid.websession = function () {  
	return  {
		init : function() {
			druid.common.buildHead(4);
			druid.common.ajaxuri = 'websession.json?';
			druid.common.handleAjaxResult = druid.websession.handleAjaxResult;
			druid.common.ajaxRequestForBasicInfo();
			setInterval("druid.common.ajaxRequestForBasicInfo();",5000);
		},
		
		handleAjaxResult : function(data) {
			var statList = data.Content;
			if(statList == null) return;
			
			var sqlStatTable = document.getElementById("WebSessionStatTable");
			while (sqlStatTable.rows.length > 1) {
				sqlStatTable.deleteRow(1);
			}
			
			for (var i = 0; i < statList.length; i++) {
				var stat = statList[i];
				var newRow = sqlStatTable.insertRow(-1);
				newRow.insertCell(-1).innerHTML = i+1;
				newRow.insertCell(-1).innerHTML = '<a target="_blank" href="websession-detail.html?sessionId=' + stat.SESSIONID + '">' + stat.SESSIONID + '</a>';
				
				if (stat.Principal) {
					newRow.insertCell(-1).innerHTML = stat.Principal;
				} else {
					newRow.insertCell(-1).innerHTML = '';
				}
				newRow.insertCell(-1).innerHTML = stat.CreateTime;
				newRow.insertCell(-1).innerHTML = stat.LastAccessTime;
				newRow.insertCell(-1).innerHTML = stat.RemoteAddress;
				newRow.insertCell(-1).innerHTML = stat.RequestCount;
				newRow.insertCell(-1).innerHTML = stat.RequestTimeMillisTotal;
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
	druid.websession.init();
});
