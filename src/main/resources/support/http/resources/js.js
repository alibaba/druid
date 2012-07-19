var xmlHttpForBasicInfo;
var xmlHttpForDataSourceInfo;
var xmlHttpForDataSourceSqlStatInfo;
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
	changeInnerHtml("DS-Info-TestOnBorrow" + datasource.Identity, datasource.TestOnBorrow);
	changeInnerHtml("DS-Info-TestWhileIdle" + datasource.Identity, datasource.TestWhileIdle);
	changeInnerHtml("DS-Info-InitialSize" + datasource.Identity, datasource.InitialSize);
	changeInnerHtml("DS-Info-MinIdle" + datasource.Identity, datasource.MinIdle);
	changeInnerHtml("DS-Info-MaxActive" + datasource.Identity, datasource.MaxActive);
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
						listHtml += '<h2>Basic Info For <span id="DS-Info-Title' + datasourceId + '"></span></h2>';
						listHtml += '<table cellpadding="5" cellspacing="1" width="960">';
						listHtml += '<tr>';
						listHtml += '<td class="td_lable" width="150">UserName</td><td width="150" id="DS-Info-UserName' + datasourceId + '">&nbsp;</td>';
						listHtml += '<td class="td_lable" width="140">URL</td><td id="DS-Info-URL' + datasourceId + '" colspan="3">&nbsp;</td>';
						listHtml += '</tr>';

						listHtml += '<tr >';
						listHtml += '<td class="td_lable">DbType</td><td id="DS-Info-DbType' + datasourceId + '">&nbsp;</td>';
						listHtml += '<td class="td_lable">DriverClassName</td><td id="DS-Info-DriverClassName' + datasourceId + '" colspan="3">&nbsp;</td>';
						listHtml += '</tr>';
						listHtml += '<tr>';
						listHtml += '<td class="td_lable">TestOnBorrow</td><td id="DS-Info-TestOnBorrow' + datasourceId + '">&nbsp;</td>';
						listHtml += '<td class="td_lable">TestWhileIdle</td><td id="DS-Info-TestWhileIdle' + datasourceId + '" colspan="3">&nbsp;</td>';
						listHtml += '</tr>';
						listHtml += '<tr>';
						listHtml += '<td class="td_lable">InitialSize</td><td id="DS-Info-InitialSize' + datasourceId + '">&nbsp;</td>';
						listHtml += '<td class="td_lable">MinIdle</td><td id="DS-Info-MinIdle' + datasourceId + '" width="150">&nbsp;</td>';
						listHtml += '<td class="td_lable"  width="190">MaxActive</td><td id="DS-Info-MaxActive' + datasourceId + '">&nbsp;</td>';
						listHtml += '</tr>';
						listHtml += '<tr>';
						listHtml += '<td class="td_lable">LogicConnectCount</td><td id="DS-Info-LogicConnectCount' + datasourceId + '">&nbsp;</td>';
						listHtml += '<td class="td_lable">LogicCloseCount</td><td id="DS-Info-LogicCloseCount' + datasourceId + '">&nbsp;</td>';
						listHtml += '<td class="td_lable">LogicConnectErrorCount</td><td id="DS-Info-LogicConnectErrorCount' + datasourceId + '">&nbsp;</td>';
						listHtml += '</tr>';
						listHtml += '<tr>';
						listHtml += '<td class="td_lable">PhysicalConnectCount</td><td id="DS-Info-PhysicalConnectCount' + datasourceId + '">&nbsp;</td>';
						listHtml += '<td class="td_lable">PhysicalCloseCount</td><td id="DS-Info-PhysicalCloseCount' + datasourceId + '">&nbsp;</td>';
						listHtml += '<td class="td_lable">PhysicalConnectErrorCount</td><td id="DS-Info-PhysicalConnectErrorCount' + datasourceId + '">&nbsp;</td>';
						listHtml += '</tr>';
						listHtml += '<tr>';
						listHtml += '<td class="td_lable">PSCacheAccessCount</td><td id="DS-Info-PSCacheAccessCount' + datasourceId + '">&nbsp;</td>';
						listHtml += '<td class="td_lable">PSCacheHitCount</td><td id="DS-Info-PSCacheHitCount' + datasourceId + '">&nbsp;</td>';
						listHtml += '<td class="td_lable">PSCacheMissCount</td><td id="DS-Info-PSCacheMissCount' + datasourceId + '">&nbsp;</td>';
						listHtml += '</tr>';
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
					newRow.insertCell(-1).innerHTML = sqlStat.SQL;
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