$.namespace("druid.weburi");
druid.weburi = function () {  
	return  {
		init : function() {
			druid.common.buildHead(2);
			druid.common.ajaxuri = 'weburi.json?';
			druid.common.handleAjaxResult = druid.weburi.handleAjaxResult;
			druid.common.setOrderBy("SQL");
			druid.common.ajaxRequestForBasicInfo();
			setInterval("druid.common.ajaxRequestForBasicInfo();",5000);
		},
		
		handleAjaxResult : function(data) {
			var sqlStatList = data.Content;
			if(sqlStatList==null) return;
			
			var sqlStatTable = document.getElementById("SqlStatTable");
			while (sqlStatTable.rows.length > 1) {
				sqlStatTable.deleteRow(1);
			}
			for ( var i = 0; i < sqlStatList.length; i++) {
				var sqlStat = sqlStatList[i];
				var newRow = sqlStatTable.insertRow(-1);
				newRow.insertCell(-1).innerHTML = i+1;
				newRow.insertCell(-1).innerHTML = '<a target="_blank" href="sql-detail.html?sqlId=' + sqlStat.ID + '">' + subSqlString(sqlStat.SQL, 25) + '</a>';
				// if (sqlStat.File)
				// newRow.insertCell(-1).innerHTML = sqlStat.File;
				// else
				// newRow.insertCell(-1).innerHTML = '';
				// if (sqlStat.Name)
				// newRow.insertCell(-1).innerHTML = sqlStat.Name;
				// else
				// newRow.insertCell(-1).innerHTML = '';
				newRow.insertCell(-1).innerHTML = sqlStat.ExecuteCount;
				newRow.insertCell(-1).innerHTML = sqlStat.TotalTime;
				//显示执行的时间比配置的均值时间慢的SQL链接
				var lastSlowHtml = sqlStat.MaxTimespan;
				if (sqlStat.LastSlowParameters!=null && sqlStat.LastSlowParameters.length>0) {
					lastSlowHtml ='<a target="_blank" style="color:red" href="sql-detail.html?sqlId=' + sqlStat.ID + '">'+sqlStat.MaxTimespan+'</a>';
				}
				newRow.insertCell(-1).innerHTML = lastSlowHtml;
				newRow.insertCell(-1).innerHTML = sqlStat.InTransactionCount;
				newRow.insertCell(-1).innerHTML = sqlStat.ErrorCount;
				newRow.insertCell(-1).innerHTML = sqlStat.EffectedRowCount;
				newRow.insertCell(-1).innerHTML = sqlStat.FetchRowCount;
				newRow.insertCell(-1).innerHTML = sqlStat.RunningCount;
				newRow.insertCell(-1).innerHTML = sqlStat.ConcurrentMax;
				// hiHtml += '<a href="#' + sqlStat.Histogram + '">ExecHistogram</a> |';
				// hiHtml += '<a href="#' + sqlStat.FetchRowCountHistogram +
				// '">FetchRow</a> | ';
				// hiHtml += '<a href="#' + sqlStat.EffectedRowCountHistogram +
				// '">UpdateCount</a> | ';
				// hiHtml += '<a href="#' + sqlStat.ExecuteAndResultHoldTimeHistogram +
				// '">ExecAndRsHold</a>';
				newRow.insertCell(-1).innerHTML = '[' + sqlStat.Histogram + ']';
				newRow.insertCell(-1).innerHTML = '[' + sqlStat.ExecuteAndResultHoldTimeHistogram + ']';
				newRow.insertCell(-1).innerHTML = '[' + sqlStat.FetchRowCountHistogram + ']';
				newRow.insertCell(-1).innerHTML = '[' + sqlStat.EffectedRowCountHistogram + ']';
			}
		}
	}
}();

$(document).ready(function() {
	druid.weburi.init();
});
