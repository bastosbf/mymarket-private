var CONFIG = {
	ROOT_URL : "http://146.134.100.66:8080/mymarket-server/rest"
};

var mymarketAngularApp = angular.module('mymarketAngularApp',
		[ 'ui.bootstrap' ]);

mymarketAngularApp
		.controller(
				'MainController',
				[
						"$scope",
						"$rootScope",
						"$http",
						"$uibModal",
						"geolocationUtils",
						function($scope, $rootScope, $http, $uibModal,
								geolocationUtils) {

							$scope.getCities = function() {
								showLoading();
								$http
										.get(CONFIG.ROOT_URL + '/city/list')
										.then(
												function success(response) {
													$scope.cities = response.data;
													if ($scope.cities != null) {
														latitude = localStorage
																.getItem("latitude");
														longitude = localStorage
																.getItem("longitude");
														if (latitude != null
																&& longitude != null) {
															geolocationUtils
																	.askLocation(
																			latitude,
																			longitude,
																			$scope.cities,
																			$("#citiesSelect"));
														}
														hideLoading();
													}
												}, function error(failure) {
													$scope.error = true;
												});

							}

							$scope.getPlaces = function() {
								showLoading();
								$http.get(CONFIG.ROOT_URL + '/place/list', {
									params : {
										city : $scope.selectedCity
									}
								}).then(
										function success(response) {
											$scope.places = response.data;

											latitude = localStorage
													.getItem("latitude");
											longitude = localStorage
													.getItem("longitude");
											if (latitude != null
													&& longitude != null) {
												geolocationUtils.askLocation(
														latitude, longitude,
														response.data,
														$("#placesSelect"));
											}
											hideLoading();
										}, function error(failure) {
											$scope.error = true;
										});
							}

							$scope.getMarkets = function() {
								showLoading();
								$http.get(CONFIG.ROOT_URL + '/market/list', {
									params : {
										place : $scope.selectedPlace
									}
								}).then(
										function success(response) {
											$scope.markets = response.data;

											latitude = localStorage
													.getItem("latitude");
											longitude = localStorage
													.getItem("longitude");
											if (latitude != null
													&& longitude != null) {
												geolocationUtils.askLocation(
														latitude, longitude,
														response.data,
														$("#marketsSelect"));
											}
											hideLoading();
										}, function error(failure) {
											$scope.error = true;
										});
							}

							$scope.scanBarcodeView = function() {
								cordova.plugins.barcodeScanner
										.scan(
												function(result) {
													if (!result.cancelled) {
														localStorage
																.setItem(
																		"confirmActived",
																		true);
														$scope
																.searchProduct(result.text);
													}
												},
												function(error) {
													// alert("Scanning failed: "
													// +
													// error);
												},
												{
													"preferFrontCamera" : false, // iOS
													// and
													// Android
													"showFlipCameraButton" : false, // iOS
													// and
													// Android
													"prompt" : "Posicione o código de barras na área de leitura", // supported
													// on
													// Android
													// only
													"formats" : "UPC_E,UPC_A,EAN_8,EAN_13,CODE_128,CODE_39,CODE_93,CODABAR", // default:
													// all
													// but
													// PDF_417
													// and
													// RSS_EXPANDED
													"orientation" : "landscape" // Android
												// only
												// (portrait|landscape), default
												// unset so it rotates with the
												// device
												});
							}

							$scope.enterBarcodeView = function() {
								$("#barcodeEnterBarcodeDialog").val("");
								$('#enterBarcodeSendButton')
										.unbind('click')
										.on(
												"click",
												function() {
													$scope
															.searchProduct($(
																	"#barcodeEnterBarcodeDialog")
																	.val());
													$('[data-role=dialog]')
															.dialog("close");
												});
								$.mobile.changePage('#dialogEnterBarcode', {
									role : 'dialog'
								});
							}

							$scope.suggestMarketView = function() {
								$('#suggestMarketSendButton')
										.unbind('click')
										.on(
												"click",
												function() {
													var city = $(
															"#citySuggestMarketActivity")
															.val();
													var place = $(
															"#placeSuggestMarketActivity")
															.val();
													var market = $(
															"#marketSuggestMarketActivity")
															.val();

													if ((city == null || city == "")
															|| (place == null || place == "")
															|| (market == null || market == "")) {
														navigator.notification
																.alert(
																		"Por favor, preencha os campos necessários!",
																		null,
																		"e-Mercado",
																		null);
														return;
													}

													showLoading();
													$scope.suggestMarket(city,
															place, market);
													hideLoading();

													navigator.notification
															.alert(
																	"Obrigado pela colaboração!\nEm breve o mercado será adicionado!",
																	null,
																	"e-Mercado",
																	null);

													$('[data-role=dialog]')
															.dialog("close");

													$(
															"#citySuggestMarketActivity")
															.val("");
													$(
															"#placeSuggestMarketActivity")
															.val("");
													$(
															"#marketSuggestMarketActivity")
															.val("");
												});

								$.mobile
										.changePage(
												'activities/suggestMarketActivity.html',
												{
													role : 'dialog'
												});

								// $uibModal
								// .open({
								// templateUrl :
								// 'activities/suggestMarketActivity.html',
								// controller : 'SuggestMarketController',
								// scope : $scope
								// });

							}

							// $scope.suggestMarket = function(city, place,
							// market) {
							// // showLoading();
							// // if ((city == null || city == "")
							// // || (place == null || place == "")
							// // || (market == null || market == "")) {
							// // return;
							// // }
							// // $.getJSON(CONFIG.ROOT_URL
							// // + '/collaboration/suggest-market', {
							// // city : city,
							// // place : place,
							// // name : market
							// // }, function(data) {
							// // });
							// // hideLoading();
							// }

							$scope.searchProduct = function(barcode) {
								alert(barcode);
							}
						} ]);

mymarketAngularApp
		.controller(
				'SuggestMarketController',
				[
						"$scope",
						"$rootScope",
						"$http",
						function($scope, $rootScope, $http) {
							$scope.suggestMarket = function() {
								showLoading();
								if (($scope.cityToSuggest == null || $scope.cityToSuggest == "")
										|| ($scope.placeToSuggest == null || $scope.placeToSuggest == "")
										|| ($scope.marketToSuggest == null || $scope.marketToSuggest == "")) {

									navigator.notification
											.alert(
													"Por favor, preencha os campos necessários!",
													null, "e-Mercado", null);

									return;
								}
								$.getJSON(CONFIG.ROOT_URL
										+ '/collaboration/suggest-market', {
									city : $scope.cityToSuggest,
									place : $scope.placeToSuggest,
									name : $scope.marketToSuggest
								}, function(data) {
								});

								navigator.notification
										.alert(
												"Obrigado pela colaboração!\nEm breve o mercado será adicionado!",
												null, "e-Mercado", null);

								hideLoading();
							}
						} ]);

mymarketAngularApp.controller('ProductController', function($scope, $rootScope,
		$http, geolocationUtils) {

});

mymarketAngularApp.service('geolocationUtils', function() {
	this.getDistanceFromLocations = function(lat1, lon1, lat2, lon2) {
		var R = 6371; // Radius of the earth in km
		var dLat = this.degTorad(lat2 - lat1); // degTorad below
		var dLon = this.degTorad(lon2 - lon1);
		var a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(this.degTorad(lat1)) * Math.cos(this.degTorad(lat2))
				* Math.sin(dLon / 2) * Math.sin(dLon / 2);
		var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		var d = R * c; // Distance in km
		return d;
	}

	this.degTorad = function(deg) {
		return deg * (Math.PI / 180)
	}

	this.askLocation = function(latitude, longitude, data, select) {
		showLoading();
		var locationNear = null;
		var distanceToLocationNear = null;
		for (i in data) {
			if (latitude != null && longitude != null) {
				var dist = this.getDistanceFromLocations(latitude, longitude,
						data[i].latitude, data[i].longitude);
				if (distanceToLocationNear == null
						|| distanceToLocationNear > dist) {
					locationNear = i;
					distanceToLocationNear = dist;
				}
			}
		}

		if (locationNear != null) {
			$('#locationName').text(
					"Você está em " + data[locationNear].name + "?");

			$('#locationYesButton').unbind('click').on("click", function() {
				$(select).find('option').each(function() {
					if ($(this).val() == data[locationNear].id) {
						$(this).prop('selected', true);
					}
				});
				$(select).change();
				$('[data-role=dialog]').dialog("close");
			});

			$('#locationNoButton').on("click", function() {
				$('[data-role=dialog]').dialog("close");
			});

			hideLoading();

			$.mobile.changePage('dialogs/dialogLocation.html', {
				role : 'dialog'
			});
		}
	}
});
