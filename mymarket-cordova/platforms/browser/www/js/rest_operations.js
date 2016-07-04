function getCities() {
	latitude = localStorage.getItem("latitude");
	longitude = localStorage.getItem("longitude");
	showLoading();
	$.getJSON(rest_url + '/city/list',
			function(data) {
				// var onSuccessGetUserLocation = function(position) {
				// onSuccessGetLocation(position, data, $("#citiesSelect"));
				// }
				//
				// var onErrorGetUserLocation = function(error) {
				// alert('code: ' + error.code + '\n' + 'message: '
				// + error.message + '\n');
				// }
				//
				// navigator.geolocation.getCurrentPosition(
				// onSuccessGetUserLocation, onErrorGetUserLocation);

				for (i in data) {
					$('#citiesSelect').append($('<option>', {
						value : data[i].id,
						text : data[i].name
					}));
				}

				if (latitude != null && longitude != null) {
					onSuccessGetLocation(latitude, longitude, data,
							$("#citiesSelect"));
				}

				//$("#citiesSelect").change();
			});
	hideLoading();
}

function getPlaces() {
	latitude = localStorage.getItem("latitude");
	longitude = localStorage.getItem("longitude");
	showLoading();
	$('#placesSelect').find('option').remove();
	var cityId = $("#citiesSelect").val();

	if (cityId > 0) {
		$("#scanBarcodeButton").prop('disabled', null);
	}
	$.getJSON(rest_url + '/place/list?city=' + cityId,
			function(data) {
				// var onSuccessGetUserLocation = function(position) {
				// onSuccessGetLocation(position, data, $("#placesSelect"));
				// }
				//
				// var onErrorGetUserLocation = function(error) {
				// alert('code: ' + error.code + '\n' + 'message: '
				// + error.message + '\n');
				// }
				//
				// navigator.geolocation.getCurrentPosition(
				// onSuccessGetUserLocation, onErrorGetUserLocation);

				for (i in data) {
					$('#placesSelect').append($('<option>', {
						value : data[i].id,
						text : data[i].name
					}));
				}

				if (latitude != null && longitude != null) {
					onSuccessGetLocation(latitude, longitude, data,
							$("#placesSelect"));
				}

				//$("#placesSelect").change();
			});
	hideLoading();
}

function getMarkets() {
	latitude = localStorage.getItem("latitude");
	longitude = localStorage.getItem("longitude");
	showLoading();
	$('#marketsSelect').find('option').remove();
	var placeId = $("#placesSelect").val();
	if (placeId != null) {
		$.getJSON(rest_url + '/market/list?place=' + placeId, function(data) {
			//									
			// var onSuccessGetUserLocation = function(position) {
			// onSuccessGetLocation(position, data,
			// $("#marketsSelect"));
			// }
			//
			// var onErrorGetUserLocation = function(error) {
			// alert('code: ' + error.code + '\n'
			// + 'message: ' + error.message + '\n');
			// }
			//
			// navigator.geolocation.getCurrentPosition(
			// onSuccessGetUserLocation,
			// onErrorGetUserLocation);

			for (i in data) {
				$('#marketsSelect').append($('<option>', {
					value : data[i].id,
					text : data[i].name
				}));
			}

			if (latitude != null && longitude != null) {
				onSuccessGetLocation(latitude, longitude, data,
						$("#marketsSelect"));
			}

			//$("#marketsSelect").change();
		});
	}
	hideLoading();
}

function searchProduct(barcode) {
	if (barcode == null) {
		return;
	}

	var cityId = $("#citiesSelect").val();
	var marketId = $("#marketsSelect").val();
	$.getJSON(rest_url + '/search/prices-by-city', {
		city : cityId,
		barcode : barcode
	}, function(data) {
		for (i in data) {
			$('#marketCard').attr('style',
					'background-color: #ADD8E6 !important');
			$("#productName").text(data[i].product.name);
			$("#marketName").text(data[i].market.name);
			$("#productMarketAddress").text(data[i].market.address);
			$("#productPrice").text(
					"R$ " + parseFloat(data[i].price).toFixed(2));
			$("#productLastUpdate").text(
					new Date(data[i].lastUpdate).toString());
			$("#barcode").text(barcode);

			$.mobile.pageContainer.pagecontainer("change", "#ProductActivity",
					null);
		}
	});

}

function suggestMarket(city, place, market) {
	$.getJSON(rest_url + '/collaboration/suggest-market', {
		city : city,
		place : place,
		name : market
	}, function(data) {
		alert("Thank you for collaboration!");
		$.mobile.pageContainer.pagecontainer("change", "#MaintActivity", null);
	});

}
