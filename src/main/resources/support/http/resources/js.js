var xmlHttpForBasicInfo;
var xmlHttpForDataSourceInfo;
var xmlHttpForDataSourceSqlStatInfo;
var xmlHttpForReset;

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
	xmlHttpForDataSourceSqlStatInfo.open("GET", 'sql.json', true);
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
	if (xmlHttpForReset.readyState == 4) {
		if (xmlHttpForReset.status == 200) {
			var jsonResp = eval("(" + xmlHttpForReset.responseText + ")");
			if (jsonResp.ResultCode == 1) {
				alert("already reset all stat");
			}
		}
	}
}
function ajaxResponseForBasicInfo() {
	var html = '';
	if (xmlHttpForBasicInfo.readyState == 4) {
		if (xmlHttpForBasicInfo.status == 200) {
			var jsonResp = eval("(" + xmlHttpForBasicInfo.responseText + ")");
			if (jsonResp.ResultCode == 1) {
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
		}
	}
}
function changeInnerHtml(id, divHtml) {
	var divObj = document.getElementById(id);
	if(!divHtml)
		divHtml='';
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
	changeInnerHtml("DS-Info-TransactionHistogramValues" + datasource.Identity, datasource.TransactionHistogramValues);
	
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
function ajaxResponseForDataSourceInfo() {
	if (xmlHttpForDataSourceInfo.readyState == 4) {
		if (xmlHttpForDataSourceInfo.status == 200) {
			var jsonResp = eval("(" + xmlHttpForDataSourceInfo.responseText + ")");
			if (jsonResp.ResultCode == 1) {
				var datasourceList = jsonResp.Content;
				for ( var i = 0; i < datasourceList.length; i++) {
					var datasource = datasourceList[i];
					if (!document.getElementById("dataSourceStat" + datasource.Identity)) {
						var listHtml = '';
						var datasourceId = datasource.Identity;
						listHtml += '<div id="dataSourceStat' + datasourceId + '">';
						listHtml += '<h2>Basic Info For <span id="DS-Info-Title' + datasourceId + '"></span><a href="datasource-' + datasourceId + '.json" target="_blank">[View JSON API]</a></h2>';
						listHtml += '<table cellpadding="5" cellspacing="1" width="99%">';
						
						listHtml += '<tr><td class="td_lable" width="230">UserName</td><td id="DS-Info-UserName' + datasourceId + '">&nbsp;</td><td>用户名称</td></tr>';
						listHtml += '<tr><td class="td_lable">URL</td><td id="DS-Info-URL' + datasourceId + '">&nbsp;</td><td>数据源的JdbcURL</td></tr>';
						listHtml += '<tr><td class="td_lable">DbType</td><td id="DS-Info-DbType' + datasourceId + '">&nbsp;</td><td>数据库类型</td></tr>';
						listHtml += '<tr><td class="td_lable">DriverClassName</td><td id="DS-Info-DriverClassName' + datasourceId + '">&nbsp;</td><td>Jdbc驱动的名称</td></tr>';
						listHtml += '<tr><td class="td_lable">FilterClassNames</td><td id="DS-Info-DriverClassName' + datasourceId + '">&nbsp;</td><td>启用的filter的名称</td></tr>';
						
						listHtml += '<tr><td class="td_lable">TestOnBorrow</td><td id="DS-Info-TestOnBorrow' + datasourceId + '">&nbsp;</td><td>借出连接时是否做测试</td></tr>';
						listHtml += '<tr><td class="td_lable">TestWhileIdle</td><td id="DS-Info-TestWhileIdle' + datasourceId + '">&nbsp;</td><td>归还连接时是否做测试</td></tr>';
						
						listHtml += '<tr><td class="td_lable">InitialSize</td><td id="DS-Info-InitialSize' + datasourceId + '">&nbsp;</td><td>初始连接池大小</td></tr>';
						listHtml += '<tr><td class="td_lable">MinIdle</td><td id="DS-Info-MinIdle' + datasourceId + '">&nbsp;</td><td>连接池最小连接数</td></tr>';
						listHtml += '<tr><td class="td_lable">MaxActive</td><td id="DS-Info-MaxActive' + datasourceId + '">&nbsp;</td><td>连接池最大连接数</td></tr>';
						
						listHtml += '<tr><td class="td_lable">NotEmptyWaitCount</td><td id="DS-Info-NotEmptyWaitCount' + datasourceId + '">&nbsp;</td><td>等待获取连接的次数</td></tr>';
						listHtml += '<tr><td class="td_lable">NotEmptyWaitMillis</td><td id="DS-Info-NotEmptyWaitMillis' + datasourceId + '">&nbsp;</td><td>获取连接等待的时间总</td></tr>';
						listHtml += '<tr><td class="td_lable">WaitThreadCount</td><td id="DS-Info-WaitThreadCount' + datasourceId + '">&nbsp;</td><td>当前正在等待获取连接的线程数量</td></tr>';
						
						listHtml += '<tr><td class="td_lable">StartTransactionCount</td><td id="DS-Info-StartTransactionCount' + datasourceId + '">&nbsp;</td><td>启动事务次数</td></tr>';
						listHtml += '<tr><td class="td_lable">TransactionHistogramValues</td><td id="DS-Info-TransactionHistogramValues' + datasourceId + '">&nbsp;</td><td>事务的持续时间直方图</td></tr>';
						
						listHtml += '<tr><td class="td_lable">PoolingCount</td><td id="DS-Info-PoolingCount' + datasourceId + '">&nbsp;</td><td>连接池可用连接数</td></tr>';
						listHtml += '<tr><td class="td_lable">PoolingPeak</td><td id="DS-Info-PoolingPeak' + datasourceId + '">&nbsp;</td><td>连接池可用连接数峰值</td></tr>';
						listHtml += '<tr><td class="td_lable">PoolingPeakTime</td><td id="DS-Info-PoolingPeakTime' + datasourceId + '">&nbsp;</td><td>连接池可用连接数峰值时间</td></tr>';

						listHtml += '<tr><td class="td_lable">ActiveCount</td><td id="DS-Info-ActiveCount' + datasourceId + '">&nbsp;</td><td>当前活动连接数</td></tr>';
						listHtml += '<tr><td class="td_lable">ActivePeak</td><td id="DS-Info-ActivePeak' + datasourceId + '">&nbsp;</td><td>连接数峰值</td></tr>';
						listHtml += '<tr><td class="td_lable">ActivePeakTime</td><td id="DS-Info-ActivePeakTime' + datasourceId + '">&nbsp;</td><td>连接数峰值时间</td></tr>';
						
						listHtml += '<tr><td class="td_lable">LogicConnectCount</td><td id="DS-Info-LogicConnectCount' + datasourceId + '">&nbsp;</td><td>申请连接的次数</td></tr>';
						listHtml += '<tr><td class="td_lable">LogicCloseCount</td><td id="DS-Info-LogicCloseCount' + datasourceId + '">&nbsp;</td><td>关闭连接的次数</td></tr>';
						listHtml += '<tr><td class="td_lable">LogicConnectErrorCount</td><td id="DS-Info-LogicConnectErrorCount' + datasourceId + '">&nbsp;</td><td>连接错误数</td></tr>';
						
						listHtml += '<tr><td class="td_lable">PhysicalConnectCount</td><td id="DS-Info-PhysicalConnectCount' + datasourceId + '">&nbsp;</td><td>创建物理连接次数</td></tr>';
						listHtml += '<tr><td class="td_lable">PhysicalCloseCount</td><td id="DS-Info-PhysicalCloseCount' + datasourceId + '">&nbsp;</td><td>物理连接关闭次数</td></tr>';
						listHtml += '<tr><td class="td_lable">PhysicalConnectErrorCount</td><td id="DS-Info-PhysicalConnectErrorCount' + datasourceId + '">&nbsp;</td><td>建立物理连接错误数</td></tr>';
						
						listHtml += '<tr><td class="td_lable">PSCacheAccessCount</td><td id="DS-Info-PSCacheAccessCount' + datasourceId + '">&nbsp;</td><td>PerpareStatement缓存次数</td></tr>';
						listHtml += '<tr><td class="td_lable">PSCacheHitCount</td><td id="DS-Info-PSCacheHitCount' + datasourceId + '">&nbsp;</td><td>PerpareStatement缓存命中次数</td></tr>';
						listHtml += '<tr><td class="td_lable">PSCacheMissCount</td><td id="DS-Info-PSCacheMissCount' + datasourceId + '">&nbsp;</td><td>PerpareStatement缓存未命中次数</td></tr>';
						listHtml += '</table>';
						listHtml += '</div>';

						document.getElementById("dataSourceStatList").innerHTML += listHtml;
					}
					fillDataSourceInfo(datasource);
				}
			}
		}
	}
}
function subSqlString(sql, len) {
	if (sql.length <= len)
		return sql;
	return sql.substr(0, len) + '...';
}
function ajaxResponseForDataSourceSqlStatInfo() {
	if (xmlHttpForDataSourceSqlStatInfo.readyState == 4) {
		if (xmlHttpForDataSourceSqlStatInfo.status == 200) {
			var jsonResp = eval("(" + xmlHttpForDataSourceSqlStatInfo.responseText + ")");
			if (jsonResp.ResultCode == 1) {
				var sqlStatList = jsonResp.Content;
				var sqlStatTable = document.getElementById("SqlStatTable");
				while (sqlStatTable.rows.length > 1) {
					sqlStatTable.deleteRow(1);
				}
				for ( var i = 0; i < sqlStatList.length; i++) {
					var sqlStat = sqlStatList[i];
					var newRow = sqlStatTable.insertRow(-1);
					newRow.insertCell(-1).innerHTML = '<a target="_blank" href="sql-' + sqlStat.ID + '.html">' + subSqlString(sqlStat.SQL, 25) + '</a>';
					if (sqlStat.File)
						newRow.insertCell(-1).innerHTML = sqlStat.File;
					else
						newRow.insertCell(-1).innerHTML = '';
					if (sqlStat.Name)
						newRow.insertCell(-1).innerHTML = sqlStat.Name;
					else
						newRow.insertCell(-1).innerHTML = '';
					newRow.insertCell(-1).innerHTML = sqlStat.ExecuteCount;
					newRow.insertCell(-1).innerHTML = sqlStat.ExecuteMillisTotal;
					newRow.insertCell(-1).innerHTML = sqlStat.ExecuteMillisMax;
					newRow.insertCell(-1).innerHTML = sqlStat.InTxnCount;
					newRow.insertCell(-1).innerHTML = sqlStat.ErrorCount;
					newRow.insertCell(-1).innerHTML = sqlStat.UpdateCount;
					newRow.insertCell(-1).innerHTML = sqlStat.FetchRowCount;
					newRow.insertCell(-1).innerHTML = sqlStat.RunningCount;
					newRow.insertCell(-1).innerHTML = sqlStat.ConcurrentMax;
					var hi = newRow.insertCell(-1);
					var hiHtml = '';
					hiHtml += '<a href="#' + sqlStat.ExecHistogram + '">ExecHistogram</a> |';
					hiHtml += '<a href="#' + sqlStat.FetchRowHistogram + '">FetchRow</a> | ';
					hiHtml += '<a href="#' + sqlStat.UpdateCountHistogram + '">UpdateCount</a> | ';
					hiHtml += '<a href="#' + sqlStat.ExecAndRsHoldHistogram + '">ExecAndRsHold</a>';
					hi.innerHTML = hiHtml;
				}
			}
		}
	}
}