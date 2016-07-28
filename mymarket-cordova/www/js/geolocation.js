function getDistanceFromLocations(lat1, lon1, lat2, lon2) {
	var R = 6371; // Radius of the earth in km
	var dLat = degTorad(lat2 - lat1); // degTorad below
	var dLon = degTorad(lon2 - lon1);
	var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(degTorad(lat1))
			* Math.cos(degTorad(lat2)) * Math.sin(dLon / 2)
			* Math.sin(dLon / 2);
	var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	var d = R * c; // Distance in km
	return d;
}

function degTorad(deg) {
	return deg * (Math.PI / 180)
}

function onSuccessGetLocation(latitude, longitude, data, select) {
	showLoading();
	var locationNear = null;
	var distanceToLocationNear = null;
	for (i in data) {
		if (latitude != null && longitude != null) {
			var dist = getDistanceFromLocations(latitude, longitude,
					data[i].latitude, data[i].longitude);
			if (distanceToLocationNear == null || distanceToLocationNear > dist) {
				locationNear = i;
				distanceToLocationNear = dist;
			}
		}
	}

	if (locationNear != null) {
		$('#locationName')
				.text("Você está em " + data[locationNear].name + "?");

		$('#locationYesButton').unbind('click').on("click", function() {
			$(select).find('option').each(function() {
				if ($(this).val() == data[locationNear].id) {
					$(this).prop('selected', true);
				}
			});
			$(select).change();
			$.mobile.pageContainer.pagecontainer("change", "#MainActivity", {
				reverse : false,
				changeHash : false
			});
		});

		$('#locationNoButton').on("click", function() {
			$.mobile.pageContainer.pagecontainer("change", "#MainActivity", {
				reverse : false,
				changeHash : false
			});
		});

		hideLoading();

		$.mobile.pageContainer.pagecontainer("change", "#dialogLocation", {
			reverse : false,
			changeHash : false
		});
	}

}