$.namespace("druid.index");
druid.index = function () {  
	return  {
		init : function() {
			this.ajaxRequestForBasicInfo();
		},
		
		ajaxRequestForBasicInfo : function() {
			$.ajax({
				type: 'POST',
				url: "basic.json",
				data: $("#loginForm").serialize(),
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
					console.info(data.Content);
				},
				dataType: "json"
			});
		}
	}
}();

$(document).ready(function() {
	druid.index.init();
});