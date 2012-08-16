function changeInnerHtml(id, divHtml) {
	var divObj = document.getElementById(id);
	if (divHtml == undefined)
		divHtml = '';
	if (divObj) {
		divObj.innerHTML = divHtml;
	}
}

function checkResoponse(xmlHttpForRequest){
	if (xmlHttpForRequest.readyState != 4) {
		return false;
	}
	if (xmlHttpForRequest.status != 200) {
		return false;
	}
	return true;
}

function getJSONResponseContent(xmlHttpForRequest){
	if(checkResoponse(xmlHttpForRequest)){
		var jsonResp = eval("(" + xmlHttpForRequest.responseText + ")");
		if (jsonResp.ResultCode == 1) {
			return jsonResp.Content;
		}
	}
	return null;
}

function sendRequest(requestObject,url,callBack){
	requestObject.onreadystatechange = callBack;
	requestObject.open("GET", url, true);
	requestObject.send(null);
}

function getRequestObject(){
	var requestObject;
	if (window.XMLHttpRequest)
		requestObject = new XMLHttpRequest();
	else if (window.ActiveXObject)
		requestObject = new ActiveXObject("Microsoft.XMLHTTP");
	return requestObject;
}


function ajaxRequestForReset() {
	if(!confirm("Are you sure to reset all stat? It'll clear all stat data !")){
		return;
	}
	xmlHttpForReset =  getRequestObject();
	sendRequest(xmlHttpForReset,'reset-all.json',ajaxResponseForReset)
}

function ajaxResponseForReset() {
	if (!checkResoponse(xmlHttpForReset)) {
		return;
	}
	var jsonResp = eval("(" + xmlHttpForReset.responseText + ")");
	if (jsonResp.ResultCode == 1) {
		alert("already reset all stat");
	}

}

function getUrlVar(name) {
    var vars = {};
    var parts = window.location.href.replace(/[?&]+([^=&]+)=([^&]*)/gi, function(m,key,value) {
        vars[key] = value;
    });
		return vars[name];
}


