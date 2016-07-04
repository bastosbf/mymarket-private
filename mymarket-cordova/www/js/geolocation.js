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
	/*
	 * alert('Latitude: ' + position.coords.latitude + '\n' + 'Longitude: ' +
	 * position.coords.longitude + '\n' + 'Altitude: ' +
	 * position.coords.altitude + '\n' + 'Accuracy: ' + position.coords.accuracy +
	 * '\n' + 'Altitude Accuracy: ' + position.coords.altitudeAccuracy + '\n' +
	 * 'Heading: ' + position.coords.heading + '\n' + 'Speed: ' +
	 * position.coords.speed + '\n' + 'Timestamp: ' + position.timestamp +
	 * '\n');
	 */

	alert($(select).attr('id'));

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

	$('#locationName').text("Você está em " + data[locationNear].name + "?");

	var locationYesButtonOnCLick = function() {
		$(select).find('option').each(function() {
			if ($(this).val() == data[locationNear].id) {
				alert("Dentro do button: " + $(select).attr('id'));
				alert($(this).val());
				alert($(this).text());
				$(this).prop('selected', true);
			}
		});

		$.mobile.pageContainer.pagecontainer("change", "#MaintActivity", null);

		$(select).change();
	};

	$('#locationYesButton').attr("onclick", locationYesButtonOnCLick);

	$('#locationNoButton').on("click", function() {
		$.mobile.pageContainer.pagecontainer("change", "#MaintActivity", null);
	});

	$.mobile.pageContainer.pagecontainer("change", "#dialogLocation", null);
}