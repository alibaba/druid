function ajaxRequestForSqlInfo() {
	var sqlId = getUrlVar("sqlId");
	xmlHttpForConnectionInfo = getRequestObject();
	sendRequest(xmlHttpForConnectionInfo, 'sql-' + sqlId + '.json', ajaxResponseForSqlInfo)
}

function ajaxResponseForSqlInfo() {
	var sqlInfo = getJSONResponseContent(xmlHttpForConnectionInfo);
	if (sqlInfo == null)
		return;

	changeInnerHtml("fullSql",sqlInfo.SQL);
	changeInnerHtml("formattedSql",sqlInfo.formattedSql);
	changeInnerHtml("parsed.table",sqlInfo.parsedTable);
	changeInnerHtml("parsed.fields",sqlInfo.parsedFields);
	changeInnerHtml("parsed.conditions",sqlInfo.parsedConditions);
	changeInnerHtml("parsed.relationships",sqlInfo.parsedRelationships);
	changeInnerHtml("parsed.orderbycolumns",sqlInfo.parsedOrderbycolumns);
	
	changeInnerHtml("MaxTimespanOccurTime",sqlInfo.MaxTimespanOccurTime);
	changeInnerHtml("LastSlowParameters",sqlInfo.LastSlowParameters);
	changeInnerHtml("MaxTimespan",sqlInfo.MaxTimespan);
	
}
