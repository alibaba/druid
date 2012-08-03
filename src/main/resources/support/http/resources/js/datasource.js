var xmlHttpForDataSourceInfo;

function ajaxRequestForDataSourceInfo() {
	xmlHttpForDataSourceInfo = getRequestObject();
	sendRequest(xmlHttpForDataSourceInfo, 'datasource.json', ajaxResponseForDataSourceInfo)
}

function fillDataSourceInfo(datasource) {
	changeInnerHtml("DS-Info-Title" + datasource.Identity, datasource.Name);
	changeInnerHtml("DS-Info-UserName" + datasource.Identity, datasource.UserName);
	changeInnerHtml("DS-Info-URL" + datasource.Identity, datasource.URL);
	changeInnerHtml("DS-Info-DbType" + datasource.Identity, datasource.DbType);
	changeInnerHtml("DS-Info-DriverClassName" + datasource.Identity, datasource.DriverClassName);
	changeInnerHtml("DS-Info-FilterClassNames" + datasource.Identity, datasource.FilterClassNames);

	changeInnerHtml("DS-Info-TestOnBorrow" + datasource.Identity, datasource.TestOnBorrow);
	changeInnerHtml("DS-Info-TestWhileIdle" + datasource.Identity, datasource.TestWhileIdle);
	changeInnerHtml("DS-Info-TestOnReturn" + datasource.Identity, datasource.TestOnReturn);

	changeInnerHtml("DS-Info-InitialSize" + datasource.Identity, datasource.InitialSize);
	changeInnerHtml("DS-Info-MinIdle" + datasource.Identity, datasource.MinIdle);
	changeInnerHtml("DS-Info-MaxActive" + datasource.Identity, datasource.MaxActive);
	
	changeInnerHtml("DS-Info-QueryTimeout" + datasource.Identity, datasource.QueryTimeout);
	changeInnerHtml("DS-Info-TransactionQueryTimeout" + datasource.Identity, datasource.TransactionQueryTimeout);
	changeInnerHtml("DS-Info-LoginTimeout" + datasource.Identity, datasource.LoginTimeout);
	changeInnerHtml("DS-Info-ValidConnectionCheckerClassName" + datasource.Identity, datasource.ValidConnectionCheckerClassName);
	changeInnerHtml("DS-Info-ExceptionSorterClassName" + datasource.Identity, datasource.ExceptionSorterClassName);
	
	changeInnerHtml("DS-Info-DefaultAutoCommit" + datasource.Identity, datasource.DefaultAutoCommit);
	changeInnerHtml("DS-Info-DefaultReadOnly" + datasource.Identity, datasource.DefaultReadOnly);
	changeInnerHtml("DS-Info-DefaultTransactionIsolation" + datasource.Identity, datasource.DefaultTransactionIsolation);

	changeInnerHtml("DS-Info-NotEmptyWaitCount" + datasource.Identity, datasource.NotEmptyWaitCount);
	changeInnerHtml("DS-Info-NotEmptyWaitMillis" + datasource.Identity, datasource.NotEmptyWaitMillis);
	changeInnerHtml("DS-Info-WaitThreadCount" + datasource.Identity, datasource.WaitThreadCount);

	changeInnerHtml("DS-Info-StartTransactionCount" + datasource.Identity, datasource.StartTransactionCount);
	changeInnerHtml("DS-Info-TransactionHistogram" + datasource.Identity, '[' + datasource.TransactionHistogram + ']');

	changeInnerHtml("DS-Info-PoolingCount" + datasource.Identity, datasource.PoolingCount);
	changeInnerHtml("DS-Info-PoolingPeak" + datasource.Identity, datasource.PoolingPeak);
	changeInnerHtml("DS-Info-PoolingPeakTime" + datasource.Identity, datasource.PoolingPeakTime);

	changeInnerHtml("DS-Info-ActiveCount" + datasource.Identity, datasource.ActiveCount);
	changeInnerHtml("DS-Info-ActivePeak" + datasource.Identity, datasource.ActivePeak);
	changeInnerHtml("DS-Info-ActivePeakTime" + datasource.Identity, datasource.ActivePeakTime);

	changeInnerHtml("DS-Info-LogicConnectCount" + datasource.Identity, datasource.LogicConnectCount);
	changeInnerHtml("DS-Info-LogicCloseCount" + datasource.Identity, datasource.LogicCloseCount);
	changeInnerHtml("DS-Info-LogicConnectErrorCount" + datasource.Identity, datasource.LogicConnectErrorCount);

	changeInnerHtml("DS-Info-PhysicalConnectCount" + datasource.Identity, datasource.PhysicalConnectCount);
	changeInnerHtml("DS-Info-PhysicalCloseCount" + datasource.Identity, datasource.PhysicalCloseCount);
	changeInnerHtml("DS-Info-PhysicalConnectErrorCount" + datasource.Identity, datasource.PhysicalConnectErrorCount);
	
	changeInnerHtml("DS-Info-ExecuteCount" + datasource.Identity, datasource.ExecuteCount);
	changeInnerHtml("DS-Info-CommitCount" + datasource.Identity, datasource.CommitCount);
	changeInnerHtml("DS-Info-RollbackCount" + datasource.Identity, datasource.RollbackCount);

	changeInnerHtml("DS-Info-PSCacheAccessCount" + datasource.Identity, datasource.PSCacheAccessCount);
	changeInnerHtml("DS-Info-PSCacheHitCount" + datasource.Identity, datasource.PSCacheHitCount);
	changeInnerHtml("DS-Info-PSCacheMissCount" + datasource.Identity, datasource.PSCacheMissCount);

	changeInnerHtml("DS-Info-ConnectionHoldTimeHistogram" + datasource.Identity, '[' + datasource.ConnectionHoldTimeHistogram + ']');
	
	if (datasource.RemoveAbandoned == true)
		changeInnerHtml("DS-Info-ActiveConnectionStackTrace" + datasource.Identity, '<a href="activeConnectionStackTrace-' + datasource.Identity + '.html">View</a>');
	else
		changeInnerHtml("DS-Info-ActiveConnectionStackTrace" + datasource.Identity, "require set removeAbandoned=true");

}
function generateDataSourceDiv(datasource) {
	var listHtml = '';
	var datasourceId = datasource.Identity;
	listHtml += '<div id="dataSourceStat' + datasourceId + '">';
	listHtml += '<h2>Basic Info For <span id="DS-Info-Title' + datasourceId + '"></span><a href="datasource-' + datasourceId + '.json" target="_blank">[View JSON API]</a></h2>';
	listHtml += '<table cellpadding="5" cellspacing="1" width="99%">';

	listHtml += '<tr><td class="td_lable" width="230">* UserName</td><td id="DS-Info-UserName' + datasourceId + '">&nbsp;</td><td>Specify the username used when creating a new connection.</td></tr>';
	listHtml += '<tr><td class="td_lable">* URL</td><td id="DS-Info-URL' + datasourceId + '">&nbsp;</td><td>The JDBC driver connection URL</td></tr>';
	listHtml += '<tr><td class="td_lable">* DbType</td><td id="DS-Info-DbType' + datasourceId + '">&nbsp;</td><td>database type</td></tr>';
	listHtml += '<tr><td class="td_lable">* DriverClassName</td><td id="DS-Info-DriverClassName' + datasourceId + '">&nbsp;</td><td>The fully qualifed name of the JDBC driver class</td></tr>';
	listHtml += '<tr><td class="td_lable">* FilterClassNames</td><td id="DS-Info-FilterClassNames' + datasourceId + '">&nbsp;</td><td>All the fully qualifed name of the filter classes</td></tr>';

	listHtml += '<tr><td class="td_lable">* TestOnBorrow</td><td id="DS-Info-TestOnBorrow' + datasourceId + '">&nbsp;</td><td>Test or not when borrow a connection</td></tr>';
	listHtml += '<tr><td class="td_lable">* TestOnReturn</td><td id="DS-Info-TestOnReturn' + datasourceId + '">&nbsp;</td><td>Test or not when return a connection</td></tr>';
	listHtml += '<tr><td class="td_lable">* TestWhileIdle</td><td id="DS-Info-TestWhileIdle' + datasourceId + '">&nbsp;</td><td>Test or not when a connection is idle for a while</td></tr>';

	listHtml += '<tr><td class="td_lable">* InitialSize</td><td id="DS-Info-InitialSize' + datasourceId + '">&nbsp;</td><td>The size of datasource connections to create when initial a datasource</td></tr>';
	listHtml += '<tr><td class="td_lable">* MinIdle</td><td id="DS-Info-MinIdle' + datasourceId + '">&nbsp;</td><td>The minimum number of connections a pool should hold. </td></tr>';
	listHtml += '<tr><td class="td_lable">* MaxActive</td><td id="DS-Info-MaxActive' + datasourceId + '">&nbsp;</td><td>The maximum number of connections for a pool</td></tr>';
	
	listHtml += '<tr><td class="td_lable">* QueryTimeout</td><td id="DS-Info-QueryTimeout' + datasourceId + '">&nbsp;</td><td> </td></tr>';
	listHtml += '<tr><td class="td_lable">* TransactionQueryTimeout</td><td id="DS-Info-TransactionQueryTimeout' + datasourceId + '">&nbsp;</td><td> </td></tr>';
	listHtml += '<tr><td class="td_lable">* LoginTimeout</td><td id="DS-Info-LoginTimeout' + datasourceId + '">&nbsp;</td><td> </td></tr>';
	listHtml += '<tr><td class="td_lable">* ValidConnectionCheckerClassName</td><td id="DS-Info-ValidConnectionCheckerClassName' + datasourceId + '">&nbsp;</td><td> </td></tr>';
	listHtml += '<tr><td class="td_lable">* ExceptionSorterClassName</td><td id="DS-Info-ExceptionSorterClassName' + datasourceId + '">&nbsp;</td><td> </td></tr>';
	
	listHtml += '<tr><td class="td_lable">* DefaultAutoCommit</td><td id="DS-Info-DefaultAutoCommit' + datasourceId + '">&nbsp;</td><td> </td></tr>';
	listHtml += '<tr><td class="td_lable">* DefaultReadOnly</td><td id="DS-Info-DefaultReadOnly' + datasourceId + '">&nbsp;</td><td> </td></tr>';
	listHtml += '<tr><td class="td_lable">* DefaultTransactionIsolation</td><td id="DS-Info-DefaultTransactionIsolation' + datasourceId + '">&nbsp;</td><td> </td></tr>';

	listHtml += '<tr><td class="td_lable">NotEmptyWaitCount</td><td id="DS-Info-NotEmptyWaitCount' + datasourceId + '">&nbsp;</td><td>Total times for wait to get a connection</td></tr>';
	listHtml += '<tr><td class="td_lable">NotEmptyWaitMillis</td><td id="DS-Info-NotEmptyWaitMillis' + datasourceId + '">&nbsp;</td><td>Total millins for wait to get a connection</td></tr>';
	listHtml += '<tr><td class="td_lable">WaitThreadCount</td><td id="DS-Info-WaitThreadCount' + datasourceId + '">&nbsp;</td><td>The current waiting thread count</td></tr>';

	listHtml += '<tr><td class="td_lable">StartTransactionCount</td><td id="DS-Info-StartTransactionCount' + datasourceId + '">&nbsp;</td><td>The count of start transaction</td></tr>';
	listHtml += '<tr><td class="td_lable">TransactionHistogram</td><td id="DS-Info-TransactionHistogram' + datasourceId + '">&nbsp;</td><td>The histogram values of transaction time, [0-10 ms, 10-100 ms, 100-1 s, 1-10 s, 10-100 s, >100 s]</td></tr>';

	listHtml += '<tr><td class="td_lable">PoolingCount</td><td id="DS-Info-PoolingCount' + datasourceId + '">&nbsp;</td><td>The current usefull connection count</td></tr>';
	listHtml += '<tr><td class="td_lable">PoolingPeak</td><td id="DS-Info-PoolingPeak' + datasourceId + '">&nbsp;</td><td>The usefull connection peak count</td></tr>';
	listHtml += '<tr><td class="td_lable">PoolingPeakTime</td><td id="DS-Info-PoolingPeakTime' + datasourceId + '">&nbsp;</td><td>The usefull connection peak time</td></tr>';

	listHtml += '<tr><td class="td_lable">ActiveCount</td><td id="DS-Info-ActiveCount' + datasourceId + '">&nbsp;</td><td>The current active connection count</td></tr>';
	listHtml += '<tr><td class="td_lable">ActivePeak</td><td id="DS-Info-ActivePeak' + datasourceId + '">&nbsp;</td><td>The current active connection peak count</td></tr>';
	listHtml += '<tr><td class="td_lable">ActivePeakTime</td><td id="DS-Info-ActivePeakTime' + datasourceId + '">&nbsp;</td><td>The active connection peak time</td></tr>';

	listHtml += '<tr><td class="td_lable">LogicConnectCount</td><td id="DS-Info-LogicConnectCount' + datasourceId + '">&nbsp;</td><td>Total connect times from datasource</td></tr>';
	listHtml += '<tr><td class="td_lable">LogicCloseCount</td><td id="DS-Info-LogicCloseCount' + datasourceId + '">&nbsp;</td><td>Total close connect times from datasource</td></tr>';
	listHtml += '<tr><td class="td_lable">LogicConnectErrorCount</td><td id="DS-Info-LogicConnectErrorCount' + datasourceId + '">&nbsp;</td><td>Total connect error times</td></tr>';

	listHtml += '<tr><td class="td_lable">PhysicalConnectCount</td><td id="DS-Info-PhysicalConnectCount' + datasourceId + '">&nbsp;</td><td>Create physical connnection count</td></tr>';
	listHtml += '<tr><td class="td_lable">PhysicalCloseCount</td><td id="DS-Info-PhysicalCloseCount' + datasourceId + '">&nbsp;</td><td>Close physical connnection count</td></tr>';
	listHtml += '<tr><td class="td_lable">PhysicalConnectErrorCount</td><td id="DS-Info-PhysicalConnectErrorCount' + datasourceId + '">&nbsp;</td><td>Total physical connect error times</td></tr>';
	
	listHtml += '<tr><td class="td_lable">ExecuteCount</td><td id="DS-Info-ExecuteCount' + datasourceId + '">&nbsp;</td><td> </td></tr>';
	listHtml += '<tr><td class="td_lable">CommitCount</td><td id="DS-Info-CommitCount' + datasourceId + '">&nbsp;</td><td> </td></tr>';
	listHtml += '<tr><td class="td_lable">RollbackCount</td><td id="DS-Info-RollbackCount' + datasourceId + '">&nbsp;</td><td> </td></tr>';

	listHtml += '<tr><td class="td_lable">PSCacheAccessCount</td><td id="DS-Info-PSCacheAccessCount' + datasourceId + '">&nbsp;</td><td>PerpareStatement access count</td></tr>';
	listHtml += '<tr><td class="td_lable">PSCacheHitCount</td><td id="DS-Info-PSCacheHitCount' + datasourceId + '">&nbsp;</td><td>PerpareStatement hit count</td></tr>';
	listHtml += '<tr><td class="td_lable">PSCacheMissCount</td><td id="DS-Info-PSCacheMissCount' + datasourceId + '">&nbsp;</td><td>PerpareStatement miss count</td></tr>';
	
	listHtml += '<tr><td class="td_lable">ConnectionHoldTimeHistogram</td><td id="DS-Info-ConnectionHoldTimeHistogram' + datasourceId + '">&nbsp;</td><td>The histogram values of connection hold time, [0-1 ms, 1-10 ms, 10-100 ms, 100ms-1s, 1-10 s, 10-100 s, 100-1000 s, >1000 s]</td></tr>';

	listHtml += '<tr><td class="td_lable">ActiveConnection StackTrace</td><td id="DS-Info-ActiveConnectionStackTrace' + datasourceId + '">&nbsp;</td><td>StackTrace for active Connection. <a href="activeConnectionStackTrace-'
			+ datasourceId + '.json" target="_blank">[View JSON API]</a></td></tr>';

	listHtml += '<tr><td class="td_lable">PollingConnection Info</td><td id="DS-Info-PollingConnectionInfo' + datasourceId + '"><a href="connectionInfo-' + datasourceId
			+ '.html">View</a></td><td>Info for polling connection. <a href="connectionInfo-' + datasourceId + '.json" target="_blank">[View JSON API]</a></td></tr>';

	listHtml += '</table>';
	listHtml += '</div>';

	document.getElementById("dataSourceStatList").innerHTML += listHtml;
}
function ajaxResponseForDataSourceInfo() {
	var datasourceList = getJSONResponseContent(xmlHttpForDataSourceInfo);
	if (datasourceList == null)
		return;

	for ( var i = 0; i < datasourceList.length; i++) {
		var datasource = datasourceList[i];
		if (document.getElementById("dataSourceStat" + datasource.Identity)) {
			fillDataSourceInfo(datasource);
			continue;
		}
		generateDataSourceDiv(datasource);
		fillDataSourceInfo(datasource);
	}
}
