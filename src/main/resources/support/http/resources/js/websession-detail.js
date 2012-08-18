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
	
	{
		var html = '[';
		html += '<a title="count of < 1 ms">' + stat.RequestInterval[0] + '</a>';
		html += ', <a title="count of 1 - 10 ms">' + stat.RequestInterval[1] + '</a>';
		html += ', <a title="count of 10 - 100 ms">' + stat.RequestInterval[2] + '</a>';
		html += ', <a title="count of 100 - 1000 ms">' + stat.RequestInterval[3] + '</a>';
		html += ', <a title="count of 1 - 10 s">' + stat.RequestInterval[4] + '</a>';
		html += ', <a title="count of 10 - 100 s">' + stat.RequestInterval[5] + '</a>';
		html += ', <a title="count of 100 - 1000 s">' + stat.RequestInterval[6] + '</a>';
		html += ', <a title="count of 1000 - 10000 s">' + stat.RequestInterval[7] + '</a>';
		html += ', <a title="count of > 10000 s">' + stat.RequestInterval[8] + '</a>';
		html += ']';
		
		changeInnerHtml("RequestInterval", html);
	}
}
