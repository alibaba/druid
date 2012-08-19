function ajaxRequestForSqlInfo() {
	var clazz = getUrlVar("class");
	var method = getUrlVar("method");
	xmlHttpForConnectionInfo = getRequestObject();
	sendRequest(xmlHttpForConnectionInfo, 'spring-detail.json?class=' + clazz + '&method=' + method, ajaxResponseForSqlInfo)
}

function ajaxResponseForSqlInfo() {
	var stat = getJSONResponseContent(xmlHttpForConnectionInfo);
	if (stat == null)
		return;

	changeInnerHtml("Class", stat.Class);
	changeInnerHtml("Method", stat.Method);
	changeInnerHtml("ExecuteCount", stat.ExecuteCount);
	changeInnerHtml("ExecuteErrorCount", stat.ExecuteErrorCount);
	changeInnerHtml("ExecuteTimeMillis", stat.ExecuteTimeMillis);
	changeInnerHtml("RunningCount", stat.RunningCount);
	changeInnerHtml("ConcurrentMax", stat.ConcurrentMax);
	changeInnerHtml("JdbcExecuteCount", stat.JdbcExecuteCount);
	changeInnerHtml("JdbcExecuteErrorCount", stat.JdbcExecuteErrorCount);
	changeInnerHtml("JdbcExecuteTimeMillis", stat.JdbcExecuteTimeMillis);
	changeInnerHtml("JdbcCommitCount", stat.JdbcCommitCount);
	changeInnerHtml("JdbcRollbackCount", stat.JdbcRollbackCount);
	changeInnerHtml("JdbcFetchRowCount", stat.JdbcFetchRowCount);
	changeInnerHtml("JdbcUpdateCount", stat.JdbcUpdateCount);
}
