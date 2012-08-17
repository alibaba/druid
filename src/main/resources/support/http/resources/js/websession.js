
var xmlHttpForDataSourceSqlStatInfo;

var statViewOrderBy = '';
var statViewOrderBy_old = '';
var statViewOrderType = 'asc';

// only one page for now
var sqlViewPage = 1;
var sqlViewPerPageCount = 1000000;

function resetSortMark() {
	var divObj = document.getElementById('th-' + statViewOrderBy);
	var old_divObj = document.getElementById('th-' + statViewOrderBy_old);
	var replaceToStr = '';
	if (old_divObj) {
		var html = old_divObj.innerHTML;
		if (statViewOrderBy_old.indexOf('[') > 0)
			replaceToStr = '-';
		html = html.replace('↑', replaceToStr);
		html = html.replace('↓', replaceToStr);
		old_divObj.innerHTML = html
	}
	if (divObj) {
		var html = divObj.innerHTML;
		if (statViewOrderBy.indexOf('[') > 0)
			html = '';

		if (statViewOrderType == 'asc') {
			html += '&uarr;';
		} else if (statViewOrderType == 'desc') {
			html += '&darr;';
		}
		divObj.innerHTML = html;
	}
}

function setOrderBy(orderBy) {
	if (statViewOrderBy != orderBy) {
		statViewOrderBy_old = statViewOrderBy;
		statViewOrderBy = orderBy;
		statViewOrderType = 'desc';
		resetSortMark();
		return;
	}

	statViewOrderBy_old = statViewOrderBy;

	if (statViewOrderType == 'asc')
		statViewOrderType = 'desc'
	else
		statViewOrderType = 'asc';

	resetSortMark();

}

function getAjaxUrl() {
	var result = 'websession.json?';

	if (statViewOrderBy != undefined)
		result += 'orderBy=' + statViewOrderBy + '&';

	if (statViewOrderType != undefined)
		result += 'orderType=' + statViewOrderType + '&';

	if (sqlViewPage != undefined)
		result += 'page=' + sqlViewPage + '&';

	if (sqlViewPerPageCount != undefined)
		result += 'perPageCount=' + sqlViewPerPageCount + '&';

	return result;
}

function ajaxRequestForDataSourceSqlStatInfo() {
	xmlHttpForDataSourceSqlStatInfo =  getRequestObject();
	sendRequest(xmlHttpForDataSourceSqlStatInfo, getAjaxUrl(), handleAjaxResult)
}

function subSqlString(sql, len) {
	if (sql.length <= len)
		return sql;
	return sql.substr(0, len) + '...';
}

function handleAjaxResult() {
	var statList = getJSONResponseContent(xmlHttpForDataSourceSqlStatInfo);
	if(statList == null) return;
	
	var sqlStatTable = document.getElementById("WebSessionStatTable");
	while (sqlStatTable.rows.length > 1) {
		sqlStatTable.deleteRow(1);
	}
	
	for (var i = 0; i < statList.length; i++) {
		var stat = statList[i];
		var newRow = sqlStatTable.insertRow(-1);
		newRow.insertCell(-1).innerHTML = i+1;
		newRow.insertCell(-1).innerHTML = stat.SESSIONID;
		if (stat.Principal) {
			newRow.insertCell(-1).innerHTML = stat.Principal;
		} else {
			newRow.insertCell(-1).innerHTML = '';
		}
		newRow.insertCell(-1).innerHTML = stat.CreateTime;
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