function ajaxRequestForActiveConnectionStackTrace() {
	var datasourceId = getUrlVar("datasourceId");
	var xmlHttpForActiveConnectionStackTrace=getRequestObject();
	sendRequest(xmlHttpForActiveConnectionStackTrace,'activeConnectionStackTrace-' + datasourceId + '.json', function() {
		ajaxResponseForActiveConnectionStackTrace(xmlHttpForActiveConnectionStackTrace, datasourceId);
	});
}

function ajaxResponseForActiveConnectionStackTrace(xmlHttpForActiveConnectionStackTrace, datasourceId) {
	var conntionStackTraceList = getJSONResponseContent(xmlHttpForActiveConnectionStackTrace);
	if (conntionStackTraceList == null)
		return;

	var activeConnectionStackTraceTable = document.getElementById("activeConnectionStackTraceTable");
	while (activeConnectionStackTraceTable.rows.length > 0) {
		activeConnectionStackTraceTable.deleteRow(0);
	}
	for ( var i = 0; i < conntionStackTraceList.length; i++) {
		var conntionStackTrace = conntionStackTraceList[i];
		var newRow = activeConnectionStackTraceTable.insertRow(-1);
		newRow.insertCell(-1).innerHTML = conntionStackTrace;
	}

}
