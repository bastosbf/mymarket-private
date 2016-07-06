function getCities() {
	latitude = localStorage.getItem("latitude");
	longitude = localStorage.getItem("longitude");
	showLoading();
	$.getJSON(rest_url + '/city/list',
			function(data) {
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
		if (data.length > 0 && typeof data[0] !== 'undefined'
				&& data[0] !== null) {
			$("#addProductActivityButton").hide();
			$("#productMarketAddress").show();
			$("#marketName").show();
			$("#priceLine").show();
			$("#lastUpdateLine").show();
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
			}
		} else {
			$('#marketCard').attr('style',
			'background-color: ##B23E3E !important');
			
			$("#productName").text("Não encontrado!!");
			$("#productMarketAddress").hide();
			$("#marketName").hide();
			$("#priceLine").hide();
			$("#lastUpdateLine").hide();
			$("#addProductActivityButton").show();
			
			$("#addProductActivityButton").on("click", function() {
				$("#barcodeAddProduct").val(barcode);
				$("#marketAddProduct").val($("#marketsSelect option:selected").text());
				$("#priceAddProduct").on("blur", function (){    
				    //number-format the user input
				    this.value = parseFloat(this.value.replace(/,/g, ""))
				                    .toFixed(2)
				                    .toString()
				                    .replace(/\B(?=(\d{3})+(?!\d))/g, ".");
				});
				
				$("#addProdutctButton").on("click", function() {
					var market = $("#marketsSelect").val();
					var name = $("#nameAddProduct").val();
					var price = $("#priceAddProduct").val();
					addProduct(market, barcode, name, price);
				});
				
				$.mobile.pageContainer.pagecontainer("change",
						"#AddProductActivity", null);
			});
		}
		
		$.mobile.pageContainer.pagecontainer("change",
				"#ProductActivity", null);
	});

}

function suggestMarket(city, place, market) {
	$.getJSON(rest_url + '/collaboration/suggest-market', {
		city : city,
		place : place,
		name : market
	}, function(data) {
		alert("Thank you for collaboration!");
		$.mobile.pageContainer.pagecontainer("change", "#MainActivity", null);
	});

}
function addProduct(market, barcode, name, price) {
	$.getJSON(rest_url + '/collaboration/suggest-product', {
		market : market,
		barcode : barcode,
		name : name,
		price : price
	}, function(data) {
		alert("Thank you for collaboration!");
		$.mobile.pageContainer.pagecontainer("change", "#MainActivity", null);
	});
}

