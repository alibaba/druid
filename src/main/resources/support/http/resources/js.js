var xmlHttpForBasicInfo;
var xmlHttpForDataSourceInfo;
var xmlHttpForDataSourceSqlStatInfo;
var xmlHttpForReset;

var sqlViewOrderBy = 'ID';
var sqlViewOrderType = 'asc';

// only one page for now
var sqlViewPage = 1;
var sqlViewPerPageCount = 1000000;

function setOrderBy(orderBy) {
	if (sqlViewOrderBy != orderBy) {
		sqlViewOrderBy = orderBy;
		sqlViewOrderType = 'desc'
		return;
	}

	if (sqlViewOrderType == 'asc')
		sqlViewOrderType = 'desc'
	else
		sqlViewOrderType = 'asc'
}

function getSqlViewJsonUrl() {
	var result = 'sql.json?';

	if (sqlViewOrderBy != undefined)
		result += 'orderBy=' + sqlViewOrderBy + '&';

	if (sqlViewOrderType != undefined)
		result += 'orderType=' + sqlViewOrderType + '&';

	if (sqlViewPage != undefined)
		result += 'page=' + sqlViewPage + '&';

	if (sqlViewPerPageCount != undefined)
		result += 'perPageCount=' + sqlViewPerPageCount + '&';

	return result;
}

function ajaxRequestForReset() {
	if (window.XMLHttpRequest)
		xmlHttpForReset = new XMLHttpRequest();
	else if (window.ActiveXObject)
		xmlHttpForReset = new ActiveXObject("Microsoft.XMLHTTP");
	xmlHttpForReset.onreadystatechange = ajaxResponseForReset;
	xmlHttpForReset.open("GET", 'reset-all.json', true);
	xmlHttpForReset.send(null);
	return false;
}
function ajaxRequestForBasicInfo() {
	if (window.XMLHttpRequest)
		xmlHttpForBasicInfo = new XMLHttpRequest();
	else if (window.ActiveXObject)
		xmlHttpForBasicInfo = new ActiveXObject("Microsoft.XMLHTTP");
	xmlHttpForBasicInfo.onreadystatechange = ajaxResponseForBasicInfo;
	xmlHttpForBasicInfo.open("GET", 'basic.json', true);
	xmlHttpForBasicInfo.send(null);
}
function ajaxRequestForDataSourceSqlStatInfo() {
	if (window.XMLHttpRequest)
		xmlHttpForDataSourceSqlStatInfo = new XMLHttpRequest();
	else if (window.ActiveXObject)
		xmlHttpForDataSourceSqlStatInfo = new ActiveXObject("Microsoft.XMLHTTP");
	xmlHttpForDataSourceSqlStatInfo.onreadystatechange = ajaxResponseForDataSourceSqlStatInfo;
	xmlHttpForDataSourceSqlStatInfo.open("GET", getSqlViewJsonUrl(), true);
	xmlHttpForDataSourceSqlStatInfo.send(null);
}
function ajaxRequestForDataSourceInfo() {
	if (window.XMLHttpRequest)
		xmlHttpForDataSourceInfo = new XMLHttpRequest();
	else if (window.ActiveXObject)
		xmlHttpForDataSourceInfo = new ActiveXObject("Microsoft.XMLHTTP");
	xmlHttpForDataSourceInfo.onreadystatechange = ajaxResponseForDataSourceInfo;
	xmlHttpForDataSourceInfo.open("GET", 'datasource.json', true);
	xmlHttpForDataSourceInfo.send(null);
}
function ajaxResponseForReset() {
	var html = '';
	if (xmlHttpForReset.readyState != 4) {
		return;
	}
	if (xmlHttpForReset.status != 200) {
		return;
	}
	var jsonResp = eval("(" + xmlHttpForReset.responseText + ")");
	if (jsonResp.ResultCode == 1) {
		alert("already reset all stat");
	}

}
function ajaxResponseForBasicInfo() {
	if (xmlHttpForBasicInfo.readyState != 4) {
		return;
	}
	if (xmlHttpForBasicInfo.status != 200) {
		return;
	}
	var jsonResp = eval("(" + xmlHttpForBasicInfo.responseText + ")");
	if (jsonResp.ResultCode != 1) {
		return;
	}

	document.getElementById("DruidVersion").innerHTML = jsonResp.Content.Version;

	var driversList = jsonResp.Content.Drivers;
	if (driversList) {
		var driverHtml = '';
		for ( var i = 0; i < driversList.length; i++) {
			var driver = driversList[i];
			driverHtml += driver + ' , ';
		}
		document.getElementById("DruidDrivers").innerHTML = driverHtml;
	}
}
function changeInnerHtml(id, divHtml) {
	var divObj = document.getElementById(id);
	if (divHtml == undefined)
		divHtml = '';
	if (divObj) {
		divObj.innerHTML = divHtml;
	}
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

	changeInnerHtml("DS-Info-InitialSize" + datasource.Identity, datasource.InitialSize);
	changeInnerHtml("DS-Info-MinIdle" + datasource.Identity, datasource.MinIdle);
	changeInnerHtml("DS-Info-MaxActive" + datasource.Identity, datasource.MaxActive);

	changeInnerHtml("DS-Info-NotEmptyWaitCount" + datasource.Identity, datasource.NotEmptyWaitCount);
	changeInnerHtml("DS-Info-NotEmptyWaitMillis" + datasource.Identity, datasource.NotEmptyWaitMillis);
	changeInnerHtml("DS-Info-WaitThreadCount" + datasource.Identity, datasource.WaitThreadCount);

	changeInnerHtml("DS-Info-StartTransactionCount" + datasource.Identity, datasource.StartTransactionCount);
	changeInnerHtml("DS-Info-TransactionHistogramValues" + datasource.Identity, '[' + datasource.TransactionHistogramValues + ']');

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

	changeInnerHtml("DS-Info-PSCacheAccessCount" + datasource.Identity, datasource.PSCacheAccessCount);
	changeInnerHtml("DS-Info-PSCacheHitCount" + datasource.Identity, datasource.PSCacheHitCount);
	changeInnerHtml("DS-Info-PSCacheMissCount" + datasource.Identity, datasource.PSCacheMissCount);
}
function generateDataSourceDiv(datasource) {
	var listHtml = '';
	var datasourceId = datasource.Identity;
	listHtml += '<div id="dataSourceStat' + datasourceId + '">';
	listHtml += '<h2>Basic Info For <span id="DS-Info-Title' + datasourceId + '"></span><a href="datasource-' + datasourceId + '.json" target="_blank">[View JSON API]</a></h2>';
	listHtml += '<table cellpadding="5" cellspacing="1" width="99%">';

	listHtml += '<tr><td class="td_lable" width="230">UserName</td><td id="DS-Info-UserName' + datasourceId + '">&nbsp;</td><td>Specify the username used when creating a new connection.</td></tr>';
	listHtml += '<tr><td class="td_lable">URL</td><td id="DS-Info-URL' + datasourceId + '">&nbsp;</td><td>The JDBC driver connection URL</td></tr>';
	listHtml += '<tr><td class="td_lable">DbType</td><td id="DS-Info-DbType' + datasourceId + '">&nbsp;</td><td>database type</td></tr>';
	listHtml += '<tr><td class="td_lable">DriverClassName</td><td id="DS-Info-DriverClassName' + datasourceId + '">&nbsp;</td><td>The fully qualifed name of the JDBC driver class</td></tr>';
	listHtml += '<tr><td class="td_lable">FilterClassNames</td><td id="DS-Info-FilterClassNames' + datasourceId + '">&nbsp;</td><td>All the fully qualifed name of the filter classes</td></tr>';

	listHtml += '<tr><td class="td_lable">TestOnBorrow</td><td id="DS-Info-TestOnBorrow' + datasourceId + '">&nbsp;</td><td>Test or not when borrow a connection</td></tr>';
	listHtml += '<tr><td class="td_lable">TestWhileIdle</td><td id="DS-Info-TestWhileIdle' + datasourceId + '">&nbsp;</td><td>Test or not when a connection is idle for a while</td></tr>';

	listHtml += '<tr><td class="td_lable">InitialSize</td><td id="DS-Info-InitialSize' + datasourceId + '">&nbsp;</td><td>The size of datasource connections to create when initial a datasource</td></tr>';
	listHtml += '<tr><td class="td_lable">MinIdle</td><td id="DS-Info-MinIdle' + datasourceId + '">&nbsp;</td><td>The minimum number of connections a pool should hold. </td></tr>';
	listHtml += '<tr><td class="td_lable">MaxActive</td><td id="DS-Info-MaxActive' + datasourceId + '">&nbsp;</td><td>The maximum number of connections for a pool</td></tr>';

	listHtml += '<tr><td class="td_lable">NotEmptyWaitCount</td><td id="DS-Info-NotEmptyWaitCount' + datasourceId + '">&nbsp;</td><td>Total times for wait to get a connection</td></tr>';
	listHtml += '<tr><td class="td_lable">NotEmptyWaitMillis</td><td id="DS-Info-NotEmptyWaitMillis' + datasourceId + '">&nbsp;</td><td>Total millins for wait to get a connection</td></tr>';
	listHtml += '<tr><td class="td_lable">WaitThreadCount</td><td id="DS-Info-WaitThreadCount' + datasourceId + '">&nbsp;</td><td>The current waiting thread count</td></tr>';

	listHtml += '<tr><td class="td_lable">StartTransactionCount</td><td id="DS-Info-StartTransactionCount' + datasourceId + '">&nbsp;</td><td>The count of start transaction</td></tr>';
	listHtml += '<tr><td class="td_lable">TransactionHistogramValues</td><td id="DS-Info-TransactionHistogramValues' + datasourceId + '">&nbsp;</td><td>The histogram values of start transaction</td></tr>';

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

	listHtml += '<tr><td class="td_lable">PSCacheAccessCount</td><td id="DS-Info-PSCacheAccessCount' + datasourceId + '">&nbsp;</td><td>PerpareStatement access count</td></tr>';
	listHtml += '<tr><td class="td_lable">PSCacheHitCount</td><td id="DS-Info-PSCacheHitCount' + datasourceId + '">&nbsp;</td><td>PerpareStatement hit count</td></tr>';
	listHtml += '<tr><td class="td_lable">PSCacheMissCount</td><td id="DS-Info-PSCacheMissCount' + datasourceId + '">&nbsp;</td><td>PerpareStatement miss count</td></tr>';
	listHtml += '</table>';
	listHtml += '</div>';

	document.getElementById("dataSourceStatList").innerHTML += listHtml;
}
function ajaxResponseForDataSourceInfo() {
	if (xmlHttpForDataSourceInfo.readyState != 4) {
		return;
	}
	if (xmlHttpForDataSourceInfo.status != 200) {
		return;
	}
	var jsonResp = eval("(" + xmlHttpForDataSourceInfo.responseText + ")");
	if (jsonResp.ResultCode != 1) {
		return;
	}
	var datasourceList = jsonResp.Content;
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
function subSqlString(sql, len) {
	if (sql.length <= len)
		return sql;
	return sql.substr(0, len) + '...';
}
function ajaxResponseForDataSourceSqlStatInfo() {
	if (xmlHttpForDataSourceSqlStatInfo.readyState != 4) {
		return;
	}
	if (xmlHttpForDataSourceSqlStatInfo.status != 200) {
		return;
	}
	var jsonResp = eval("(" + xmlHttpForDataSourceSqlStatInfo.responseText + ")");
	if (jsonResp.ResultCode != 1) {
		return;
	}
	var sqlStatList = jsonResp.Content;
	var sqlStatTable = document.getElementById("SqlStatTable");
	while (sqlStatTable.rows.length > 1) {
		sqlStatTable.deleteRow(1);
	}
	for ( var i = 0; i < sqlStatList.length; i++) {
		var sqlStat = sqlStatList[i];
		var newRow = sqlStatTable.insertRow(-1);
		newRow.insertCell(-1).innerHTML = '<a target="_blank" href="sql-' + sqlStat.ID + '.html">' + subSqlString(sqlStat.SQL, 25) + '</a>';
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
		newRow.insertCell(-1).innerHTML = sqlStat.MaxTimespan;
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
		newRow.insertCell(-1).innerHTML = '[' + sqlStat.FetchRowCountHistogram + ']';
		newRow.insertCell(-1).innerHTML = '[' + sqlStat.EffectedRowCountHistogram + ']';
		newRow.insertCell(-1).innerHTML = '[' + sqlStat.ExecuteAndResultHoldTimeHistogram + ']';
	}
}