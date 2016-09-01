var CONFIG = {
		ROOT_URL: "http://146.134.100.70:8080/mymarket-server"
};

var app = angular.module('myMarketApp', ["ui.bootstrap.modal"]);
app.controller('myMarketController', function($scope, $rootScope, $http) {
	$scope.total = function() {
		return sessionStorage.total ? sessionStorage.total : 0;
	}
	$scope.listCities = function() {
		$http.get(CONFIG.ROOT_URL + "/rest/city/list")
		.then(function success(response) {			
			$scope.cities = response.data;
		}, 
		function error(failure) {
			
		});		
	}
	$scope.listPlaces = function() {
		$http.get(CONFIG.ROOT_URL + "/rest/place/list/" + $scope.city)
		.then(function success(response) {			
			$scope.places = response.data;			
		}, 
		function error(failure) {
			
		});		
	}
	$scope.searchProducts = function() {						
		$scope.products = [];		
		$scope.results = false;
		$scope.minlength = false;
		if(!$scope.product || $scope.product.length < 3) {
			$scope.minlength = true;
			return;
		} 
		$scope.loading = true;
		url = CONFIG.ROOT_URL + "/rest/product/get-products-with-price-by-name/" + $scope.product + "/" + $scope.city;
		if($scope.place) {
			url += "/" + $scope.place;
		}
		if($scope.searchWithoutPrices) {
			url = CONFIG.ROOT_URL + "/rest/product/get-products-by-name/" + $scope.product;			
		}
		$http.get(url)
		.then(function success(response) {			
			$scope.products = response.data;			
			$scope.loading = false;
			$scope.results = true;
		}, 
		function error(failure) {
			$scope.error = true;
			$scope.loading = false;
			$scope.results = true;
		});
	}
	$scope.open = function() {
		if(sessionStorage.total && sessionStorage.total > 0) {
			$scope.cartProducts = [];
			for (var i = 0; i < sessionStorage.length; i++){
				product = JSON.parse(sessionStorage.getItem(sessionStorage.key(i)));
				$scope.cartProducts.push(product);
			}
			$("#modal").modal('show');
		}
	}
});
app.controller('myMarketCartController', function($scope, $rootScope) {
	if(!sessionStorage.total) {
		sessionStorage.total = 0;
	}
	$scope.add = function(product) {
		sessionStorage.total++;
		if(!sessionStorage[product.barcode]) {
			sessionStorage[product.barcode] = JSON.stringify({'quantity': 1, 'name': product.name});
		} else {
			obj = JSON.parse(sessionStorage[product.barcode]);
			sessionStorage[product.barcode] = JSON.stringify({'quantity': ++obj.quantity, 'name': obj.name});
		}
	}
	$scope.remove = function(product) {		
		sessionStorage.total--;
		if(sessionStorage[product.barcode]) {
			obj = JSON.parse(sessionStorage[product.barcode]);
			if(obj.quantity == 1) {
				sessionStorage.removeItem(product.barcode);
			} else {				
				sessionStorage[product.barcode] = JSON.stringify({'quantity': --obj.quantity, 'name': obj.name});
			}
		}
	}
	$scope.canRemove = function(product) {		
		return !sessionStorage[product.barcode];
	}
	$scope.getQuantity = function(product) {		
		return sessionStorage[product.barcode] ? JSON.parse(sessionStorage[product.barcode]).quantity : 0;
	}
});