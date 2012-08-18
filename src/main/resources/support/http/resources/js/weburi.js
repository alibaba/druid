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
	ajaxRequestForDataSourceSqlStatInfo();
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
	var result = 'weburi.json?';

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
	xmlHttpForDataSourceSqlStatInfo = getRequestObject();
	sendRequest(xmlHttpForDataSourceSqlStatInfo, getSqlViewJsonUrl(),
			ajaxResponse)
}

function subSqlString(sql, len) {
	if (sql.length <= len)
		return sql;
	return sql.substr(0, len) + '...';
}

function ajaxResponse() {
	var statList = getJSONResponseContent(xmlHttpForDataSourceSqlStatInfo);
	if (statList == null)
		return;

	var sqlStatTable = document.getElementById("WebUriStatTable");
	while (sqlStatTable.rows.length > 1) {
		sqlStatTable.deleteRow(1);
	}

	for ( var i = 0; i < statList.length; i++) {
		var stat = statList[i];
		var newRow = sqlStatTable.insertRow(-1);
		newRow.insertCell(-1).innerHTML = i + 1;
		newRow.insertCell(-1).innerHTML = '<a target="_blank" href="weburi-detail.html?uri='
				+ encodeURI(stat.URI) + '">' + subSqlString(stat.URI, 64) + '</a>';
		newRow.insertCell(-1).innerHTML = stat.RequestCount;
		newRow.insertCell(-1).innerHTML = stat.RequestTimeMillis;
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
