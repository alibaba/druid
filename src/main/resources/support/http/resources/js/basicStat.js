$.namespace("druid.index");
druid.index = function () {  
	return  {
		init : function() {
			druid.common.buildHead(0);
			this.ajaxRequestForBasicInfo();
		},
		
		ajaxRequestForBasicInfo : function() {
			$.ajax({
				type: 'POST',
				url: "basic.json",
				success: function(data) {
					$("#DruidVersion").text(data.Content.Version)
					var driversList = data.Content.Drivers;
					if (driversList) {
						var driverHtml = '';
						for ( var i = 0; i < driversList.length; i++) {
							var driver = driversList[i];
							driverHtml += driver + ' , ';
						}
						$("#DruidDrivers").text(driverHtml);
					}
					$("#ResetEnable").text(data.Content.ResetEnable)
					$("#ResetCount").text(data.Content.ResetCount)
				},
				dataType: "json"
			});
		}
	}
}();

$(document).ready(function() {
	druid.index.init();
});