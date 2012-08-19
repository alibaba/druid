function ajaxRequestForSqlInfo() {
	var uri = getUrlVar("uri");
	xmlHttpForConnectionInfo = getRequestObject();
	sendRequest(xmlHttpForConnectionInfo, 'weburi-' + encodeURI(uri) + '.json', ajaxResponseForSqlInfo)
}

function ajaxResponseForSqlInfo() {
	var stat = getJSONResponseContent(xmlHttpForConnectionInfo);
	if (stat == null)
		return;

	changeInnerHtml("URI", stat.URI);
	changeInnerHtml("RequestCount", stat.RequestCount);
	changeInnerHtml("RequestTimeMillis", stat.RequestTimeMillis);
	changeInnerHtml("LastAccessTime", stat.LastAccessTime);
	changeInnerHtml("RunningCount", stat.RunningCount);
	changeInnerHtml("ConcurrentMax", stat.ConcurrentMax);
	changeInnerHtml("JdbcExecuteCount", stat.JdbcExecuteCount);
	changeInnerHtml("JdbcExecuteErrorCount", stat.JdbcExecuteErrorCount);
	changeInnerHtml("JdbcExecutePeak", stat.JdbcExecutePeak);
	changeInnerHtml("JdbcExecuteTimeMillis", stat.JdbcExecuteTimeMillis);
	changeInnerHtml("JdbcCommitCount", stat.JdbcCommitCount);
	changeInnerHtml("JdbcRollbackCount", stat.JdbcRollbackCount);
	changeInnerHtml("JdbcFetchRowCount", stat.JdbcFetchRowCount);
	changeInnerHtml("JdbcFetchRowPeak", stat.JdbcFetchRowPeak);
	changeInnerHtml("JdbcUpdateCount", stat.JdbcUpdateCount);
	changeInnerHtml("JdbcUpdatePeak", stat.JdbcUpdatePeak);
	changeInnerHtml("JdbcPoolConnectionOpenCount", stat.JdbcPoolConnectionOpenCount);
	changeInnerHtml("JdbcPoolConnectionCloseCount", stat.JdbcPoolConnectionCloseCount);
	changeInnerHtml("JdbcResultSetOpenCount", stat.JdbcResultSetOpenCount);
	changeInnerHtml("JdbcResultSetCloseCount", stat.JdbcResultSetCloseCount);
}
