
var xmlHttpForDataSourceSqlStatInfo;

var sqlViewOrderBy = '';
var sqlViewOrderBy_old = '';
var sqlViewOrderType = 'asc';

// only one page for now
var sqlViewPage = 1;
var sqlViewPerPageCount = 1000000;

function resetSortMark() {
	var divObj = document.getElementById('th-' + sqlViewOrderBy);
	var old_divObj = document.getElementById('th-' + sqlViewOrderBy_old);
	var replaceToStr = '';
	if (old_divObj) {
		var html = old_divObj.innerHTML;
		if (sqlViewOrderBy_old.indexOf('[') > 0)
			replaceToStr = '-';
		html = html.replace('↑', replaceToStr);
		html = html.replace('↓', replaceToStr);
		old_divObj.innerHTML = html
	}
	if (divObj) {
		var html = divObj.innerHTML;
		if (sqlViewOrderBy.indexOf('[') > 0)
			html = '';

		if (sqlViewOrderType == 'asc') {
			html += '&uarr;';
		} else if (sqlViewOrderType == 'desc') {
			html += '&darr;';
		}
		divObj.innerHTML = html;
	}
}

function setOrderBy(orderBy) {
	if (sqlViewOrderBy != orderBy) {
		sqlViewOrderBy_old = sqlViewOrderBy;
		sqlViewOrderBy = orderBy;
		sqlViewOrderType = 'desc';
		resetSortMark();
		return;
	}

	sqlViewOrderBy_old = sqlViewOrderBy;

	if (sqlViewOrderType == 'asc')
		sqlViewOrderType = 'desc'
	else
		sqlViewOrderType = 'asc';

	resetSortMark();

}

function getSqlViewJsonUrl() {
	var result = 'websession.json?';

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

function ajaxRequestForDataSourceSqlStatInfo() {
	xmlHttpForDataSourceSqlStatInfo =  getRequestObject();
	sendRequest(xmlHttpForDataSourceSqlStatInfo,getSqlViewJsonUrl(),ajaxResponseForDataSourceSqlStatInfo)
}

function subSqlString(sql, len) {
	if (sql.length <= len)
		return sql;
	return sql.substr(0, len) + '...';
}
function ajaxResponseForDataSourceSqlStatInfo() {
	var sqlStatList = getJSONResponseContent(xmlHttpForDataSourceSqlStatInfo);
	if(sqlStatList==null) return;
	
	var sqlStatTable = document.getElementById("WebSessionStatTable");
	while (sqlStatTable.rows.length > 1) {
		sqlStatTable.deleteRow(1);
	}
	for ( var i = 0; i < sqlStatList.length; i++) {
		var sqlStat = sqlStatList[i];
		var newRow = sqlStatTable.insertRow(-1);
		newRow.insertCell(-1).innerHTML = i+1;
		newRow.insertCell(-1).innerHTML = sqlStat.SESSIONID;
		newRow.insertCell(-1).innerHTML = sqlStat.RequestCount;
		newRow.insertCell(-1).innerHTML = sqlStat.RunningCount;
		newRow.insertCell(-1).innerHTML = sqlStat.ConcurrentMax;
		newRow.insertCell(-1).innerHTML = sqlStat.JdbcExecuteCount;
		newRow.insertCell(-1).innerHTML = sqlStat.JdbcCommitCount;
		newRow.insertCell(-1).innerHTML = sqlStat.JdbcRollbackCount;
		newRow.insertCell(-1).innerHTML = sqlStat.JdbcFetchRowCount;
		newRow.insertCell(-1).innerHTML = sqlStat.JdbcUpdateCount;
	}
}