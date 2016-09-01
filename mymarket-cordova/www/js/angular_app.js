var mymarketAngularApp = angular.module('mymarketAngularApp', []);

mymarketAngularApp.controller('LocationSelectsController', function($scope,
		$http) {
	$scope.cities = [];
	$scope.places = [];
	$scope.markets = [];
	
	//utilizar
	$scope.selectedCity = null;
	$scope.selectedPlace = null;
	$scope.selectedMarket = null;

	$scope.getCities = function() {
		showLoading();
		latitude = localStorage.getItem("latitude");
		longitude = localStorage.getItem("longitude");
		$http.get(rest_url + '/city/list').then(
				function success(response) {
					$scope.cities = response.data;
					if (latitude != null && longitude != null) {
						onSuccessGetLocation(latitude, longitude,
								response.data, $("#citiesSelect"));
					}
					hideLoading();
				}, function error(failure) {
					$scope.error = true;
				});
	}

	$scope.getPlaces = function() {
		latitude = localStorage.getItem("latitude");
		longitude = localStorage.getItem("longitude");
		showLoading();
		// $('#placesSelect').find('option').remove();
		var cityId = $("#citiesSelect").val();

		if (cityId > 0) {
			$("#scanBarcodeButton").prop('disabled', null);
			$('#enterBarcodeButton').prop('disabled', null);
		} else {
			$("#scanBarcodeButton").prop('disabled', true);
			$('#enterBarcodeButton').prop('disabled', true);
		}
		if (cityId != null) {
			$http.get(rest_url + '/place/list', {
				params : {
					city : cityId
				}
			}).then(
					function success(response) {
						$scope.places = response.data;
						if (latitude != null && longitude != null) {
							onSuccessGetLocation(latitude, longitude,
									response.data, $("#placesSelect"));
						}
						hideLoading();
					}, function error(failure) {
						$scope.error = true;
					});
		}
	}

	$scope.getMarkets = function() {
		latitude = localStorage.getItem("latitude");
		longitude = localStorage.getItem("longitude");
		showLoading();
		// $('#marketsSelect').find('option').remove();
		var placeId = $("#placesSelect").val();
		if (placeId != null) {
			$http.get(rest_url + '/market/list', {
				params : {
					place : placeId
				}
			}).then(
					function success(response) {
						$scope.markets = response.data;
						if (latitude != null && longitude != null) {
							onSuccessGetLocation(latitude, longitude,
									response.data, $("#marketsSelect"));
						}
						hideLoading();
					}, function error(failure) {
						$scope.error = true;
					});
		}
	}

	$('#citiesSelect').on("change", $scope.getPlaces);
	$('#placesSelect').on("change", $scope.getMarkets);
	$scope.getCities();
});
