$.namespace("druid.datasource");
druid.datasource = function () {  
	var keys = ["UserName", "URL", "DbType", "DriverClassName", "FilterClassNames", "TestOnBorrow", "TestWhileIdle", "TestOnReturn",
				"InitialSize", "MinIdle", "MaxActive", "QueryTimeout", "TransactionQueryTimeout", "LoginTimeout", "ValidConnectionCheckerClassName",
				"ExceptionSorterClassName", "DefaultAutoCommit", "DefaultReadOnly", "DefaultTransactionIsolation", "NotEmptyWaitCount",
				"NotEmptyWaitMillis", "WaitThreadCount", "StartTransactionCount", "TransactionHistogram", "PoolingCount", "PoolingPeak",
				"PoolingPeakTime", "ActiveCount", "ActivePeak", "ActivePeakTime", "LogicConnectCount", "LogicCloseCount", "LogicConnectErrorCount",
				"PhysicalConnectCount", "PhysicalCloseCount", "PhysicalConnectErrorCount", "ExecuteCount", "ErrorCount", "CommitCount",
				"RollbackCount", "PSCacheAccessCount", "PSCacheHitCount", "PSCacheMissCount", "ConnectionHoldTimeHistogram"]
	var descs = ["Specify the username used when creating a new connection.", 
	             "The JDBC driver connection URL", "database type", "The fully qualifed name of the JDBC driver class", 
	             "All the fully qualifed name of the filter classes", "	Test or not when borrow a connection", "Test or not when return a connection", 
	             "Test or not when a connection is idle for a while","The size of datasource connections to create when initial a datasource", 
	             "The minimum number of connections a pool should hold. ", "The maximum number of connections for a pool", "", "", "", "",
					"", "", "", "", "Total times for wait to get a connection","Total millins for wait to get a connection", "The current waiting thread count", 
					"The count of start transaction", "The histogram values of transaction time, [0-10 ms, 10-100 ms, 100-1 s, 1-10 s, 10-100 s, >100 s]", 
					"The current usefull connection count", "The usefull connection peak count",
					"The usefull connection peak time", "The current active connection count", "The current active connection peak count", "The active connection peak time", 
					"Total connect times from datasource", "Total close connect times from datasource", "Total connect error times",
					"Create physical connnection count", "Close physical connnection count", "Total physical connect error times", "", "", "",
					"", "PerpareStatement access count", "PerpareStatement hit count", "PerpareStatement miss count", "The histogram values of connection hold time, [0-1 ms, 1-10 ms, 10-100 ms, 100ms-1s, 1-10 s, 10-100 s, 100-1000 s, >1000 s]"]
	return  {
		init : function() {
			druid.common.buildHead(1);
			this.ajaxRequestForBasicInfo();
		},
		
		ajaxRequestForBasicInfo : function() {
			$.ajax({
				type: 'POST',
				url: "datasource.json",
				success: function(data) {
					var datasourceList = data.Content;
					for ( var i = 0; i < datasourceList.length; i++) {
						var datasource = datasourceList[i];
						druid.datasource.generateDataSourceTable(datasource);
					}
				},
				dataType: "json"
			});
		},
		
		generateDataSourceTable : function(datasource) {
			var datasourceId = datasource.Identity;
			var html = '<h4>Basic Info For DataSource-' + datasourceId + '<a href="datasource-' + datasourceId + '.json" target="_blank">View JSON API</a></h4>';
			html += '<table class="table table-bordered table-striped responsive-utilities">';
			
			for(var i=0, len=keys.length; i<len; i++) {
				var value = datasource[keys[i]];
				if(value == null)
					value = "";
				
				if(i<19)
					html += '<tr><td valign="top" class="td_lable">* ' + keys[i] + '</td><td>' + value + '</td><td>' + descs[i] + '</td></tr>'
				else
					html += '<tr><td valign="top" class="td_lable">' + keys[i] + '</td><td>' + value + '</td><td>' + descs[i] + '</td></tr>'
			}
			
			if (datasource.RemoveAbandoned == true)
				html += '<tr><td valign="top" class="td_lable">ActiveConnection StackTrace</td><td><a href="activeConnectionStackTrace.html?datasourceId=' + datasource.Identity + '">View</a></td><td>StackTrace for active Connection. <a href="activeConnectionStackTrace-'
					+ datasourceId + '.json" target="_blank">[View JSON API]</a></td></tr>'
			else
				html += '<tr><td valign="top" class="td_lable">ActiveConnection StackTrace</td><td>require set removeAbandoned=true</td><td>StackTrace for active Connection. <a href="activeConnectionStackTrace-'
				+ datasourceId + '.json" target="_blank">[View JSON API]</a></td></tr>'
			
			html += '<tr><td valign="top" class="td_lable">PollingConnection Info</td><td><a href="connectionInfo.html?datasourceId=' + datasourceId+ 
					'">View</a></td><td>Info for polling connection. <a href="connectionInfo-' + datasourceId + '.json" target="_blank">[View JSON API]</a></td></tr>'
			
			html += '</table>';
			$(".hero-unit h3").after(html);
		}
	}
}();

$(document).ready(function() {
	druid.datasource.init();
});