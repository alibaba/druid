var xmlHttpForConnectionInfo;

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
	ajaxRequestForConnectionInfo();
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

function ajaxRequestForConnectionInfo() {
	var datasourceId = getUrlVar("datasourceId");
	xmlHttpForConnectionInfo = getRequestObject();
	sendRequest(xmlHttpForConnectionInfo, 'connectionInfo-' + datasourceId + '.json', ajaxResponseForConnectionInfo)
}
function getPsCacheInfo(pscache) {
	var result = '<table cellspacing="1" cellpadding="5" width="100%">';
	for ( var i = 0; i < pscache.length; i++) {
		var stmt = pscache[i];
		result += '<tr>';
		result += '<td>' + stmt.sql + "</td>";
		result += '<td width="50">' + stmt.defaultRowPretch + "</td>";
		result += '<td width="50">' + stmt.rowPrefetch + "</td>";
		result += '<td width="50">' + stmt.hitCount + "</td>";
		result += '</tr>';
	}

	result += "</table>";
	return result;
}
function ajaxResponseForConnectionInfo() {
	var connectionInfoList = getJSONResponseContent(xmlHttpForConnectionInfo);
	if (connectionInfoList == null)
		return;

	var connectionInfoTable = document.getElementById("connectionInfoTable");
	while (connectionInfoTable.rows.length > 1) {
		connectionInfoTable.deleteRow(1);
	}
	for ( var i = 0; i < connectionInfoList.length; i++) {
		var connectionInfo = connectionInfoList[i];
		var newRow = connectionInfoTable.insertRow(-1);

		newRow.insertCell(-1).innerHTML = connectionInfo.id;
		newRow.insertCell(-1).innerHTML = connectionInfo.useCount;

		if (connectionInfo.lastActiveTime)
			newRow.insertCell(-1).innerHTML = new Date(connectionInfo.lastActiveTime);
		else
			newRow.insertCell(-1).innerHTML = '';

		newRow.insertCell(-1).innerHTML = new Date(connectionInfo.connectTime);
		newRow.insertCell(-1).innerHTML = connectionInfo.holdability;
		newRow.insertCell(-1).innerHTML = connectionInfo.transactionIsolation;

		newRow.insertCell(-1).innerHTML = connectionInfo.autoCommit;
		newRow.insertCell(-1).innerHTML = connectionInfo.readoOnly;

		if (connectionInfo.pscache)
			newRow.insertCell(-1).innerHTML = getPsCacheInfo(connectionInfo.pscache);
		else
			newRow.insertCell(-1).innerHTML = '';
	}
}
