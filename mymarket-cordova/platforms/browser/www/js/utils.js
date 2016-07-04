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
