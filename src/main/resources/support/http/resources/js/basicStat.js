var xmlHttpForBasicInfo;

function ajaxRequestForBasicInfo() {
	xmlHttpForBasicInfo =  getRequestObject();
	sendRequest(xmlHttpForBasicInfo,'basic.json',ajaxResponseForBasicInfo);
}

function ajaxResponseForBasicInfo() {
	var jsonContent = getJSONResponseContent(xmlHttpForBasicInfo);
	if(jsonContent==null) return;

	document.getElementById("DruidVersion").innerHTML = jsonContent.Version;

	var driversList = jsonContent.Drivers;
	if (driversList) {
		var driverHtml = '';
		for ( var i = 0; i < driversList.length; i++) {
			var driver = driversList[i];
			driverHtml += driver + ' , ';
		}
		document.getElementById("DruidDrivers").innerHTML = driverHtml;
	}
}
