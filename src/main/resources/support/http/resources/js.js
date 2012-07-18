
var datasourceId;
var xmlHttpForBasicInfo;
var xmlHttpForDataSourceInfo;
var xmlHttpForDataSourceSqlStatInfo;
function ajaxRequestForBasicInfo(){
     if(window.XMLHttpRequest) xmlHttpForBasicInfo=new XMLHttpRequest();
     else if(window.ActiveXObject) xmlHttpForBasicInfo=new ActiveXObject("Microsoft.XMLHTTP");
     xmlHttpForBasicInfo.onreadystatechange=ajaxResponseForBasicInfo;
     xmlHttpForBasicInfo.open("GET",'json/basic',true);
     xmlHttpForBasicInfo.send(null);
}
function ajaxRequestForDataSourceSqlStatInfo(){
	if(!datasourceId){
		return;
	}
     if(window.XMLHttpRequest) xmlHttpForDataSourceSqlStatInfo=new XMLHttpRequest();
     else if(window.ActiveXObject) xmlHttpForDataSourceSqlStatInfo=new ActiveXObject("Microsoft.XMLHTTP");
     xmlHttpForDataSourceSqlStatInfo.onreadystatechange=ajaxResponseForDataSourceSqlStatInfo;
     xmlHttpForDataSourceSqlStatInfo.open("GET",'json/sql?identity='+datasourceId,true);
     xmlHttpForDataSourceSqlStatInfo.send(null);
}
function ajaxRequestForDataSourceInfo(){
	if(!datasourceId){
		return;
	}
    if(window.XMLHttpRequest) xmlHttpForDataSourceInfo=new XMLHttpRequest();
    else if(window.ActiveXObject) xmlHttpForDataSourceInfo=new ActiveXObject("Microsoft.XMLHTTP");
    xmlHttpForDataSourceInfo.onreadystatechange=ajaxResponseForDataSourceInfo;
    xmlHttpForDataSourceInfo.open("GET",'json/datasource?identity='+datasourceId,true);
    xmlHttpForDataSourceInfo.send(null);
}
function ajaxResponseForBasicInfo(){
    var html='';
    var tabHtml='';
    if(xmlHttpForBasicInfo.readyState==4){
		if(xmlHttpForBasicInfo.status==200){
            var jsonResp=eval("("+xmlHttpForBasicInfo.responseText+")");
            if(jsonResp.ResultCode==1){
				document.getElementById("DruidVersion").innerHTML=jsonResp.Content.Version;
				
				var driversList=jsonResp.Content.Drivers;
				if(driversList){
					var driverHtml='';
					for(var i=0;i<driversList.length;i++){
						var driver=driversList[i];
						driverHtml+=driver +' , ';
					}
					document.getElementById("DruidDrivers").innerHTML=driverHtml;
				}
               	var dsList=jsonResp.Content.DataSources;
				if(dsList){
				
					//start get default info
					if(!datasourceId){
						datasourceId=dsList[0].Identity;
                		ajaxRequestForDataSourceSqlStatInfo();
                		ajaxRequestForDataSourceInfo();
					}
					
                	for(var i=0;i<dsList.length;i++){
                		var ds=dsList[i];
                		
                		tabHtml+='<a href="javascript:showDataSource('+ds.Identity+')">['+ds.Name+']</a> ';
                	}
				}
            }
            document.getElementById("menu").innerHTML=tabHtml;
        }
    }
}
function ajaxResponseForDataSourceInfo(){
    var html='';
    if(xmlHttpForDataSourceInfo.readyState==4){
		if(xmlHttpForDataSourceInfo.status==200){
            var jsonResp=eval("("+xmlHttpForDataSourceInfo.responseText+")");
            if(jsonResp.ResultCode==1){
				var ds=jsonResp.Content;
				document.getElementById("DS-Info-Title").innerHTML=ds.Name;
				document.getElementById("DS-Info-UserName").innerHTML=ds.UserName;
				document.getElementById("DS-Info-URL").innerHTML=ds.URL;
				document.getElementById("DS-Info-DbType").innerHTML=ds.DbType;
				document.getElementById("DS-Info-DriverClassName").innerHTML=ds.DriverClassName;
				document.getElementById("DS-Info-TestOnBorrow").innerHTML=ds.TestOnBorrow;
				document.getElementById("DS-Info-TestWhileIdle").innerHTML=ds.TestWhileIdle;
				document.getElementById("DS-Info-InitialSize").innerHTML=ds.InitialSize;
				document.getElementById("DS-Info-MinIdle").innerHTML=ds.MinIdle;
				document.getElementById("DS-Info-MaxActive").innerHTML=ds.MaxActive;
				document.getElementById("DS-Info-LogicConnectCount").innerHTML=ds.LogicConnectCount;
				document.getElementById("DS-Info-LogicCloseCount").innerHTML=ds.LogicCloseCount;
				document.getElementById("DS-Info-LogicConnectErrorCount").innerHTML=ds.LogicConnectErrorCount;
				document.getElementById("DS-Info-PhysicalConnectCount").innerHTML=ds.PhysicalConnectCount;
				document.getElementById("DS-Info-PhysicalCloseCount").innerHTML=ds.PhysicalCloseCount;
				document.getElementById("DS-Info-PhysicalConnectErrorCount").innerHTML=ds.PhysicalConnectErrorCount;
				document.getElementById("DS-Info-PSCacheAccessCount").innerHTML=ds.PSCacheAccessCount;
				document.getElementById("DS-Info-PSCacheHitCount").innerHTML=ds.PSCacheHitCount;
				document.getElementById("DS-Info-PSCacheMissCount").innerHTML=ds.PSCacheMissCount;
				
            }
        }
    }
}
function ajaxResponseForDataSourceSqlStatInfo(){
    if(xmlHttpForDataSourceSqlStatInfo.readyState==4){
		if(xmlHttpForDataSourceSqlStatInfo.status==200){
            var jsonResp=eval("("+xmlHttpForDataSourceSqlStatInfo.responseText+")");
            if(jsonResp.ResultCode==1){
				var sqlStatList=jsonResp.Content;
				var sqlStatTable=document.getElementById("SqlStatTable");
				while(sqlStatTable.rows.length>1){
					sqlStatTable.deleteRow(1);
				}
				for(var i=0;i<sqlStatList.length;i++){
					var sqlStat=sqlStatList[i];
					var newRow = sqlStatTable.insertRow(-1);
					newRow.insertCell(-1).innerHTML=sqlStat.SQL;
					if(sqlStat.File) newRow.insertCell(-1).innerHTML=sqlStat.File;
					else newRow.insertCell(-1).innerHTML='';
					if(sqlStat.Name) newRow.insertCell(-1).innerHTML=sqlStat.Name;
					else newRow.insertCell(-1).innerHTML='';
					newRow.insertCell(-1).innerHTML=sqlStat.ExecuteCount;
					newRow.insertCell(-1).innerHTML=sqlStat.ExecuteMillisTotal;
					newRow.insertCell(-1).innerHTML=sqlStat.ExecuteMillisMax;
					newRow.insertCell(-1).innerHTML=sqlStat.InTxnCount;
					newRow.insertCell(-1).innerHTML=sqlStat.ErrorCount;
					newRow.insertCell(-1).innerHTML=sqlStat.UpdateCount;
					newRow.insertCell(-1).innerHTML=sqlStat.FetchRowCount;
					newRow.insertCell(-1).innerHTML=sqlStat.RunningCount;
					newRow.insertCell(-1).innerHTML=sqlStat.ConcurrentMax;
					var hi=newRow.insertCell(-1);
					var hiHtml='';
					hiHtml+='<a href="#'+sqlStat.ExecHistogram+'">ExecHistogram</a> |';
					hiHtml+='<a href="#'+sqlStat.FetchRowHistogram+'">FetchRow</a> | ';
					hiHtml+='<a href="#'+sqlStat.UpdateCountHistogram+'">UpdateCount</a> | ';
					hiHtml+='<a href="#'+sqlStat.ExecAndRsHoldHistogram+'">ExecAndRsHold</a>';
					hi.innerHTML=hiHtml;
				}
            }
        }
    }
}
function showDataSource(id){
	datasourceId=id;
	ajaxRequestForDataSourceInfo();
	ajaxRequestForDataSourceSqlStatInfo();
}
function init(){
	ajaxRequestForBasicInfo();
	var time1=setInterval("ajaxRequestForBasicInfo();",1000);
	var time2=setInterval("ajaxRequestForDataSourceInfo();",1000);
	var time3=setInterval("ajaxRequestForDataSourceSqlStatInfo();",1000);
}