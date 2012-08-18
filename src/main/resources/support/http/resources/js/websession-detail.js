function ajaxRequestForSqlInfo() {
	var sessionId = getUrlVar("sessionId");
	xmlHttpForConnectionInfo = getRequestObject();
	sendRequest(xmlHttpForConnectionInfo, 'websession-' + sessionId + '.json', ajaxResponseForSqlInfo)
}

function ajaxResponseForSqlInfo() {
	var stat = getJSONResponseContent(xmlHttpForConnectionInfo);
	if (stat == null)
		return;

	changeInnerHtml("SESSIONID", stat.SESSIONID);
	if (stat.Principal) {
		changeInnerHtml("Principal", stat.Principal);
	}
	changeInnerHtml("CreateTime", stat.CreateTime);
	changeInnerHtml("LastAccessTime", stat.LastAccessTime);
	changeInnerHtml("UserAgent", stat.UserAgent);
	changeInnerHtml("RemoteAddress", stat.RemoteAddress);
	changeInnerHtml("RequestCount", stat.RequestCount);
	changeInnerHtml("RequestTimeMillisTotal", stat.RequestTimeMillisTotal);
	changeInnerHtml("RunningCount", stat.RunningCount);
	changeInnerHtml("ConcurrentMax", stat.ConcurrentMax);
	changeInnerHtml("JdbcExecuteCount", stat.JdbcExecuteCount);
	changeInnerHtml("JdbcExecuteTimeMillis", stat.JdbcExecuteTimeMillis);
	changeInnerHtml("JdbcCommitCount", stat.JdbcCommitCount);
	changeInnerHtml("JdbcRollbackCount", stat.JdbcRollbackCount);
	changeInnerHtml("JdbcFetchRowCount", stat.JdbcFetchRowCount);
	changeInnerHtml("JdbcUpdateCount", stat.JdbcUpdateCount);
}
