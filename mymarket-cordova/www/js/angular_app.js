var CONFIG = {
	ROOT_URL : "http://146.134.100.66:8080/mymarket-server"
};

var mymarketAngularApp = angular.module('mymarketAngularApp', ["ngCordovaOauth"]);

mymarketAngularApp.controller('MainController', [ "$scope", "$rootScope",
		"$http", "$cordovaOauth", function($scope, $rootScope, $http, $cordovaOauth) {	
	 
	    $scope.facebookLogin = function() {
	        $cordovaOauth.facebook("1781335808818687", ["email", "public_profile"]).then(function(result){
	        	 $http.get("https://graph.facebook.com/v2.2/me", { params: { access_token: result.access_token, fields: "id,name,gender,location,website,email,picture,relationship_status", format: "json" }}).then(function(result) {
	        		 alert(JSON.stringify(result.data));
	        		 $rootScope.uid = result.data.id;
	        		 $scope.name = result.data.name;
	        		 $scope.email = result.data.email;
	        		 var url = CONFIG.ROOT_URL + "/rest/user/add/" + $scope.uid + "/" + $scope.name + "/" + $scope.email;						
                     $http.get(url);	
	             }, function(error) {
	                 alert("There was a problem getting your profile.  Check the logs for details.");
	                 console.log(error);
	             });
	        	
	        	
	        }, function(error) {
	            // error
	        });
	    }

	 
		$scope.facebookLogout = function() {
			$rootScope.uid = null;
			$scope.name = null;
			$scope.email = null;
			$scope.accessToken = null;
			
			
			FB.getLoginStatus(function(response) {
				if (response.status === 'connected') {
					FB.logout(function(response) {
//						$scope.$apply(function() {
//					
//						});
					});
				}
			});
		}
	
	$scope.listCities = function () {
		showLoading();
		latitude = localStorage.getItem("latitude");
		longitude = localStorage.getItem("longitude");
		
		$http.get(CONFIG.ROOT_URL + "/rest/city/list")
		.then(function success(response) {			
			$rootScope.cities = response.data;
			
			if (latitude != null && longitude != null && $rootScope.cities != null) {
				$rootScope.$emit("askForLocation", {});
			}
			hideLoading();
		}, 
		function error(failure) {
			hideLoading();
		});			
	}
	
	$scope.listPlaces = function() {
		latitude = localStorage.getItem("latitude");
		longitude = localStorage.getItem("longitude");
		showLoading();	
		if($scope.city){
			$http.get(CONFIG.ROOT_URL + "/rest/place/list/" + $scope.city.id)
			.then(function success(response) {			
				$rootScope.places = response.data;
				if (latitude != null && longitude != null && $rootScope.places != null) {
					$rootScope.$emit("askForLocation", {});
				}
			}, 
			function error(failure) {
			});	
		}
		hideLoading();
	};
	
	$scope.listMarkets = function() {
		latitude = localStorage.getItem("latitude");
		longitude = localStorage.getItem("longitude");
		showLoading();
		$rootScope.market = null;
		if($scope.place){
			$http.get(CONFIG.ROOT_URL + "/rest/market/list/" + $scope.place.id)
			.then(function success(response) {			
				$rootScope.markets = response.data;
				if (latitude != null && longitude != null && $rootScope.markets != null) {
					$rootScope.$emit("askForLocation", {});
				}
			}, 
			function error(failure) {
			});	
		}
		hideLoading();
	};
	
	$scope.initScanBarcode = function() {
				cordova.plugins.barcodeScanner
				.scan(
						function(result) {
							if (!result.cancelled) {
								localStorage.setItem(
										"confirmActived", true);
								$rootScope.barcode = result.text;
								$rootScope.$emit("searchProduct", {});
							}
						},
						function(error) {
						},
						{
							"preferFrontCamera" : false,
							"showFlipCameraButton" : false,
							"prompt" : "Posicione o código de barras na área de leitura",
							"formats" : "UPC_E,UPC_A,EAN_8,EAN_13,CODE_128,CODE_39,CODE_93,CODABAR",
							"orientation" : "landscape"
						});
	};
			
	$scope.askBarcode = function() {
		$rootScope.barcode = '';
		$.mobile.changePage('#dialogEnterBarcode', {
			role : 'dialog'
		});
	};
	
	$scope.suggestMarketView = function() {
		$.mobile.changePage('#dialogSuggestMarket', {
			role : 'dialog'
		});	
	};
	
	$scope.marketListsView = function() {
		$rootScope.$emit("getMarketLists", {});
		$.mobile.changePage('#pageMarketLists', {
			role : 'page'
		});	
	};
	
	
} ]);


mymarketAngularApp.controller('EnterBarcodeController', [ "$scope",
                                                   		"$rootScope", "$http", function($scope, $rootScope, $http) {
	$scope.barcode = null;
	$scope.enterBarcode = function() {
		if($scope.barcode != "" && $scope.barcode != null){
			localStorage.setItem("confirmActived", true);
			$rootScope.barcode = $scope.barcode;			
			$rootScope.$emit("searchProduct", {});
		}
	}
}]);

mymarketAngularApp.controller('SuggestMarketController', [ "$scope",
                                                     		"$rootScope", "$http", function($scope, $rootScope, $http) {
  	
			$scope.suggestMarket = function() {												
						if (($scope.cityToSuggestion == null || $scope.cityToSuggestion == "")
								|| ($scope.placeToSuggestion == null || $scope.placeToSuggestion == "")
								|| ($scope.marketToSuggestion == null || $scope.marketToSuggestion == "")) {
							navigator.notification
									.alert(
											"Por favor, preencha os campos necessários!",
											null, "e-Mercado",
											null);
							return;
						}
		
						showLoading();
						suggestMarket($scope.marketToSuggestion, $scope.placeToSuggestion, $scope.marketToSuggestion);
						hideLoading();
		
						navigator.notification
								.alert(
										"Obrigado pela colaboração!\nEm breve o mercado será adicionado!",
										null, "e-Mercado", null);
		
						$('[data-role=dialog]').dialog("close");
		
						$scope.cityToSuggestion = null;
						$scope.placeToSuggestion = null;
						$scope.marketToSuggestion = null;;
					};
  }]);


mymarketAngularApp.controller('MarketListsController', [ "$scope",
		"$rootScope", "$http", function($scope, $rootScope, $http) {	
		$scope.getMarketLists = function() {
			var url = CONFIG.ROOT_URL + "/rest/shopping-list/list/" + $rootScope.uid;
			$http.get(url)
			.then(function success(result) {
				$scope.marketLists = result.data;
				
				//id, user (email, name, uid)
			}, 
			function error(failure) {
				
			});	
		};
		
		
		$scope.viewMarketList = function (id){
			alert(id);
		}
		
		$rootScope.$on("getMarketLists", function(){
	        $scope.getMarketLists();
	     });
	}	
]);
	

mymarketAngularApp.controller('SearchResultsController', [ "$scope",
		"$rootScope", "$http", function($scope, $rootScope, $http) {
	
	$scope.searchProduct = function (){
		$scope.selectedMarketFound = false;
		
		if ($rootScope.barcode == null) {
			return;
	    }
		showLoading();
		
		$.ajax({
				   url : CONFIG.ROOT_URL + '/rest/search/prices-by-city/' + $rootScope.barcode + '/'
							+ $rootScope.city.id,
					dataType : 'json',
					async : false,
					type : "GET",
					success : function(data) {
						$scope.results = data;
						
						for (r in $scope.results){
							if($scope.results[r].market != undefined){
								if($scope.results[r].market.id == $rootScope.market.id){
									$scope.selectedMarketFound = true; 
								}
							}
						}
						
						
						$.mobile.pageContainer.pagecontainer("change",
								"#pageSearchResults", null);
						hideLoading();
					}
		 });	
	};
	
	$rootScope.$on("searchProduct", function(){
        $scope.searchProduct();
     });
} ]);


mymarketAngularApp.controller('GeolocationAskController', [ "$scope",
		"$rootScope", "$http", function($scope, $rootScope, $http) {
	
		
	$scope.askForLocation = function() {
		showLoading();
		var locationNearId = null;
		var distanceToLocationNear = null;
		
		$scope.userLatitude = localStorage.getItem("latitude");
		$scope.userLongitude = localStorage.getItem("longitude");
		
		if($scope.userLatitude != null && $scope.userLongitude != null){
			if ($rootScope.cities){
				$scope.kind = "CITIES";
				$scope.data = $rootScope.cities;
			}
			
			if ($rootScope.places){
				$scope.kind = "PLACES";
				$scope.data = $rootScope.places;
			} 
			
			if($rootScope.markets){
				$scope.kind = "MARKETS";
				$scope.data = $rootScope.markets;
			} 
						
			if($scope.data){
				for (i in $scope.data) {
						var dist = $scope.getDistanceFromLocations($scope.userLatitude, $scope.userLongitude,
								$scope.data[i].latitude, $scope.data[i].longitude);
						if (distanceToLocationNear == null || distanceToLocationNear > dist) {
							locationNearId = i;
							distanceToLocationNear = dist;
						}
					}
				}
				if (locationNearId != null) {
					$scope.locationNear = $scope.data[locationNearId];
					$.mobile.changePage('#dialogAskLocation', {
						role : 'dialog'
					});
				}
				
		   }
		  hideLoading();
    	};
	
	$scope.getDistanceFromLocations = function (lat1, lon1, lat2, lon2) {
		var R = 6371; // Radius of the earth in km
		var dLat = degTorad(lat2 - lat1); // degTorad below
		var dLon = degTorad(lon2 - lon1);
		var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(degTorad(lat1))
				* Math.cos(degTorad(lat2)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		var d = R * c; // Distance in km
		return d;
	};
	
	$scope.degTorad = function (deg) {
		return deg * (Math.PI / 180)
	};
	
	$scope.yesToLocation = function() {			
		switch ($scope.kind) {
		case "CITIES":
			$rootScope.city = {id: $scope.locationNear.id, name: $scope.locationNear.name};
			break;
		case "PLACES":
			$rootScope.place = {id: $scope.locationNear.id, name: $scope.locationNear.name};
			break;
		case "MARKETS":
			$rootScope.market = {id: $scope.locationNear.id, name: $scope.locationNear.name};
			break;
		default:
			break;
		}
		$('[data-role=dialog]').dialog("close");
	};
	
	$scope.noToLocation = function() {
		$('[data-role=dialog]').dialog("close");
	};
	
	$rootScope.$on("askForLocation", function(){
        $scope.askForLocation();
     });
	
} ]);