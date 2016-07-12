function showLoading(id) {
	if (id == null) {
		id = "loading-indicator";
	}
	$('#' + id).modal({
		"backdrop" : "static",
		"keyboard" : true,
		"show" : true
	});
}

function hideLoading(id) {
	if (id == null) {
		id = "loading-indicator";
	}
	$('#' + id).modal('hide');
}

function formatDate(date) {
	var day = date.getDate();
	if (day.toString().length == 1)
		day = "0" + day;
	var month = date.getMonth() + 1;
	if (month.toString().length == 1)
		month = "0" + month;
	var year = date.getFullYear();
	return String(day + "/" + month + "/" + year);
}

function formatHour(date) {
	var hours = date.getHours();
	if (hours.toString().length == 1)
		hours = "0" + hours;
	var minutes = date.getMinutes() + 1;
	if (minutes.toString().length == 1)
		minutes = "0" + minutes;
	var seconds = date.getSeconds();
	if (seconds.toString().length == 1)
		seconds = "0" + seconds;	
	return String(hours + ":" + minutes + ":" + seconds);
}
