var CONFIG = {
	ROOT_URL : "http://mymarket.bastosbf.com/mymarket-server-new"
};

var mymarketAngularApp = angular.module('mymarketAngularApp', ["ngCordova", "ngCordovaOauth", "ui.bootstrap.modal"]);

mymarketAngularApp.controller('MainController', [ "$scope", "$rootScope",
		"$http", "$cordovaOauth", "$cordovaProgress", function($scope, $rootScope, $http, $cordovaOauth, $cordovaProgress) {	
	 
	$scope.uid = localStorage.uid;
	
	$scope.checkTermsOfUse = function() {
		if ("termsOfUseAccepted" in localStorage) {
			$scope.checkForMessages();
			$scope.listCities();
		} else {
			$.mobile.pageContainer.pagecontainer("change",
					"#pageTermsOfUse", {reverse: false, changeHash: false});
		}
	};
	
	$scope.acceptTerms = function(){
		localStorage.setItem("termsOfUseAccepted", $scope.termsCheckbox);
		$.mobile.pageContainer.pagecontainer("change",
				"#MainActivity", null);
		$scope.checkForMessages();
		$scope.listCities();
	}
	
	 $scope.checkForMessages = function() {
		$.getJSON(CONFIG.ROOT_URL + '/rest/notification/list', function(data) {
			for (i in data) {
				switch (data[i].kind) {
				case 'U':
					if (localStorage.getItem(data[i].id) != "VISUALISED") {
						navigator.notification.alert(data[i].message, onConfirm, "e-Mercado", null);
						function onConfirm(buttonIndex) {
							switch (buttonIndex) {
							case 0:
								localStorage.setItem(data[i].id, "VISUALISED");
								break;
							default:
								break;
							}
						}
					}
					break;
				case 'S':
					navigator.notification.alert(data[i].message, null,
							"e-Mercado", null);
					break;
				case 'F':
					navigator.notification.alert(data[i].message, onConfirm,
							"e-Mercado", null);
					function onConfirm(buttonIndex) {
						switch (buttonIndex) {
						case 0:
							navigator.app.exitApp();
							break;
						default:
							break;
						}
					}
					break;
				default:
					break;
				}
			}
		});
	}
	
    $scope.facebookLogin = function() {
        $cordovaOauth.facebook("1781335808818687", ["email", "public_profile"]).then(function(result){
        	 $http.get("https://graph.facebook.com/v2.2/me", { params: { access_token: result.access_token, fields: "id,name,gender,location,website,email,picture,relationship_status", format: "json" }}).then(function(result) {
        		 localStorage.uid = result.data.id;
        		 $scope.uid = result.data.id;
        		 $scope.name = result.data.name;
        		 $scope.email = result.data.email;
        		 var url = CONFIG.ROOT_URL + "/rest/user/add/" + $scope.uid + "/" + $scope.name + "/" + $scope.email;						
                 $http.get(url);
                 $scope.showShoppingListMsg = false;
             }, function(error) {
                 alert("There was a problem getting your profile.  Check the logs for details.");
             });
        	
        	
        }, function(error) {
            // error
        });
    }

 
	$scope.facebookLogout = function() {
		$scope.uid = null;
		$scope.name = null;
		$scope.email = null;
		$scope.accessToken = null;
		localStorage.removeItem("uid");
	}
	
        $scope.listCities = function () {
                $scope.loading = true;
                latitude = localStorage.getItem("latitude");
                longitude = localStorage.getItem("longitude");

                $http.get(CONFIG.ROOT_URL + "/rest/city/list")
                .then(function success(response) {
                        $scope.cities = response.data;

                        if ($scope.cities != null) {
                                if (latitude != null && longitude != null) {
                                        $scope.askForLocation();
                                }

                                localStorage.setItem("cityList", JSON.stringify($scope.cities));
                        }

                        $scope.loading = false;
                },
                function error(failure) {
                        if ("cityList" in localStorage) {
                                $scope.cities = JSON.parse(localStorage.getItem("cityList"));
                        }
                        $scope.loading = false;
                });
        }

	$scope.listPlaces = function() {
		latitude = localStorage.getItem("latitude");
		longitude = localStorage.getItem("longitude");
		$scope.loading = true;
		if($scope.city){
			$http.get(CONFIG.ROOT_URL + "/rest/place/list/" + $scope.city.id)
			.then(function success(response) {			
				$scope.places = response.data;
				if (latitude != null && longitude != null && $scope.places != null) {
					$scope.askForLocation();
				}
			}, 
			function error(failure) {
			});	
		}
		$scope.loading = false;
	};
	
	$scope.listMarkets = function() {
		latitude = localStorage.getItem("latitude");
		longitude = localStorage.getItem("longitude");
		$scope.loading = true;
		$scope.market = null;
		if($scope.place){
			$http.get(CONFIG.ROOT_URL + "/rest/market/list-by-place/" + $scope.place.id)
			.then(function success(response) {			
				$scope.markets = response.data;
				if (latitude != null && longitude != null && $scope.markets != null) {
					$scope.askForLocation();
				}
			}, 
			function error(failure) {
			});	
		}
		$scope.loading = false;
	};
	
	$scope.initScanBarcode = function() {
		        $scope.barcode = '';
				cordova.plugins.barcodeScanner
				.scan(
						function(result) {
							if (!result.cancelled) {
								$scope.confirmPriceActived = true;
								$scope.barcode = result.text;
								if(isValidBarcode($scope.barcode)){
									$scope.searchProduct();
								} else {
									navigator.notification
									.alert("Código de barras inválido!",
											null, "e-Mercado", null);
								}
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
		$scope.barcode = '';
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
		$scope.getMarketLists();
		$.mobile.changePage('#pageMarketLists', {
			role : 'page'
		});	
	};
	
	
	
	$scope.askForLocation = function() {
		$scope.loading = true;
		$scope.locationNearId = null;
		var distanceToLocationNear = null;
		
		$scope.userLatitude = localStorage.getItem("latitude");
		$scope.userLongitude = localStorage.getItem("longitude");
		
		if($scope.userLatitude != null && $scope.userLongitude != null){
			if ($scope.cities){
				$scope.kind = "CITIES";
				$scope.data = $scope.cities;
			}
			
			if ($scope.places){
				$scope.kind = "PLACES";
				$scope.data = $scope.places;
			} 
			
			if($scope.markets){
				$scope.kind = "MARKETS";
				$scope.data = $scope.markets;
			} 
						
			if($scope.data){
				for (i in $scope.data) {
						var dist = $scope.getDistanceFromLocations($scope.userLatitude, $scope.userLongitude,
								$scope.data[i].latitude, $scope.data[i].longitude);
						if (distanceToLocationNear == null || distanceToLocationNear > dist) {
							$scope.locationNearId = i;
							distanceToLocationNear = dist;
						}
					}
				}
				if ($scope.locationNearId != null) {
					$scope.locationNear = $scope.data[$scope.locationNearId];
					$.mobile.changePage('#dialogAskLocation', {
						role : 'dialog'
					});
				}
				
		   }
		  $scope.loading = false;
    	};
	
	$scope.getDistanceFromLocations = function (lat1, lon1, lat2, lon2) {
		var R = 6371; // Radius of the earth in km
		var dLat = $scope.degTorad(lat2 - lat1); // degTorad below
		var dLon = $scope.degTorad(lon2 - lon1);
		var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos($scope.degTorad(lat1))
				* Math.cos($scope.degTorad(lat2)) * Math.sin(dLon / 2)
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
     		$scope.city = $scope.cities[$scope.locationNearId];
			$scope.listPlaces();
			break;
		case "PLACES":
			$scope.place =$scope.places[$scope.locationNearId];
			$scope.listMarkets();
			break;
		case "MARKETS":
			$scope.market = $scope.markets[$scope.locationNearId];
			break;
		default:
			break;
		}
		$('[data-role=dialog]').dialog("close");
	};
	
	$scope.noToLocation = function() {
		$('[data-role=dialog]').dialog("close");
	};
	
	$scope.barcode = null;
	$scope.enterBarcode = function() {
		if($scope.barcode != "" && $scope.barcode != null){
			$scope.confirmPriceActived = true;
			if(isValidBarcode($scope.barcode)){
				$scope.searchProduct();
			} else {
				navigator.notification
				.alert("Código de barras inválido!",
						null, "e-Mercado", null);
			}
		}
	};
	
	$scope.suggestMarket = function() {												
		if (($scope.cityToSuggestion == null || $scope.cityToSuggestion == "")
				|| ($scope.placeToSuggestion == null || $scope.placeToSuggestion == "")
				|| ($scope.marketToSuggestion == null || $scope.marketToSuggestion == "")) {
			navigator.notification
					.alert("Por favor, preencha os campos necessários!",
							null, "e-Mercado",
							null);
			return;
		}

		$scope.loading = true;
		
		$http.get(CONFIG.ROOT_URL + '/rest/collaboration/suggest-market/' + $scope.marketToSuggestion + '/'
				+ $scope.placeToSuggestion + '/' + $scope.cityToSuggestion).then(function success(response) {			
				}, 
				function error(failure) {
				});;
								
		$scope.loading = false;

		navigator.notification
				.alert(
						"Obrigado pela colaboração!\nEm breve o mercado será adicionado!",
						null, "e-Mercado", null);

		$('[data-role=dialog]').dialog("close");

		$scope.cityToSuggestion = null;
		$scope.placeToSuggestion = null;
		$scope.marketToSuggestion = null;;
	};
	
	$scope.getMarketLists = function() {
		if($scope.uid){
			var url = CONFIG.ROOT_URL + "/rest/shopping-list/list/" + $scope.uid;
			$http.get(url)
			.then(function success(result) {
				if(result.data != null && result.data.length > 0){
					$scope.marketLists = result.data;
				} else {
					$scope.showShoppingListMsg = true;
				}
			}, 
			function error(failure) {
				
			});	
		} else {
			$scope.showShoppingListMsg = true;
		}
	};
	
	
	$scope.viewMarketList = function (list){
		var url = CONFIG.ROOT_URL + "/rest/shopping-list/list-products/" + list.id;
		$http.get(url)
		.then(function success(result) {
			$scope.shoppingList = result.data;
			$.mobile.changePage('#pageShoppingListProducts', {
				role : 'page'
			});	
		}, 
		function error(failure) {
			
		});
	};
	
	$scope.searchProduct = function (){
		$scope.selectedMarketFound = false;
		$scope.confirmPriceActived = true;
		
		if ($scope.barcode == null) {
			return;
	    }
		$scope.loading = true;
		
		var url = CONFIG.ROOT_URL + '/rest/search/prices-by-city/' + $scope.barcode + '/' + $scope.city.id;
		
		$http.get(url)
		.then(function success(result) {
			$scope.results = result.data;
			angular.forEach($scope.results, function(r) {
				if(r.market != null){
					if(r.market.id == $scope.market.id){
						$scope.selectedMarketFound = true; 
					}
				}
			});
				
			$.mobile.pageContainer.pagecontainer("change",
					"#pageSearchResults", null);
			$scope.loading = false;
		}, 
		function error(failure) {
			
		});
	};
	
	$scope.showRenameProductDialog = function() {
		$.mobile.changePage('#dialogRenameProduct', {
			role : 'dialog'
		});
	};
	
	$scope.confirmPrice = function (marketProduct) {
		$scope.loading = true;
		$scope.confirmPriceActived = false;
		
		var url = CONFIG.ROOT_URL + '/rest/collaboration/confirm-price/' + $scope.market.id + '/' + marketProduct.product.id;
		$http.get(url)
		.then(function success(result) {
			marketProduct.last_update = new Date ();
		}, 
		function error(failure) {
		});	
		
		navigator.notification.alert("Obrigado pela colaboração!", null, "e-Mercado", null);
		$scope.loading = false;
	};
	
	
	$scope.showAddProductDialog = function() {
		$.mobile.changePage('#dialogAddProductName', {
			role : 'dialog'
		});
	};
	
	$scope.showAddNameAndPriceDialog = function() {
		$scope.newProduct = null;
		$.mobile.changePage('#dialogAddMarketProduct', {
			role : 'dialog'
		});
	};
	
	
	$scope.showUpdateMarketPriceDialog = function(marketProduct, existsOnMarket) {
		$scope.marketProduct = marketProduct;
		$scope.existsOnMarket = existsOnMarket;
		$scope.price = null;
		if($scope.existsOnMarket){
			$scope.price = $scope.marketProduct.price; 
		}
		$scope.offer = false;
		$.mobile.changePage('#dialogUpdateMarketPrice', {
			role : 'dialog'
		});
	};
	
	$scope.updateMarketPrice = function(){
		$scope.loading = true;
		
		var url = CONFIG.ROOT_URL + '/rest/collaboration/suggest-price/' + $scope.market.id + '/' + $scope.marketProduct.product.id + '/' + $scope.price + '/' + $scope.offer;
		console.log(url);
		$http.get(url)
		.then(function success(result) {
			navigator.notification.alert("Obrigado pela colaboração!",null, "e-Mercado",null);
			$('[data-role=dialog]').dialog("close");
			$scope.marketProduct.last_update = new Date ();
			$scope.offer = false;
			$scope.searchProduct();
			$scope.confirmPriceActived = false;
			$scope.loading = false;
		}, 
		function error(failure) {
		});	
	}
	
	 $scope.addMarketProduct = function() {
		$scope.loading = true;
		$scope.confirmPriceActived = false;
		
		if (($scope.barcode == null || $scope.barcode == "") || ($scope.market.id == null || $scope.market.id == "") || ($scope.newProduct.name == null || $scope.newProduct.name  == "") || ($scope.newProduct.price == null || $scope.newProduct.price == "")) {
			return;
		}

		var url = CONFIG.ROOT_URL + '/rest/collaboration/suggest-product/' + $scope.market.id + '/' + $scope.barcode + '/' + $scope.newProduct.name + '/' + $scope.newProduct.price + '/' + $scope.newProduct.offer;
		$http.get(url).then(function success(result) {
		}, 
		function error(failure) {
		});	
		
		$scope.loading = false;
		navigator.notification.alert("Obrigado pela colaboração! O produto será adicionado em breve.",null, "e-Mercado",null);
		$('[data-role=dialog]').dialog("close");
		$scope.newProduct = null;
		// TODO: Voltar para tela inicial
	}
	 
	$scope.renameProduct = function () {
	     	$scope.loading = true;
			var url = CONFIG.ROOT_URL + '/rest/collaboration/suggest-name/' + $scope.results[0].product.id + '/' + $scope.newName;
			$http.get(url)
			.then(function success(result) {
			}, 
			function error(failure) {
			});	
			$scope.loading = false;
			navigator.notification.alert("Obrigado pela colaboração!",null, "e-Mercado",null);
			$scope.newName = null;
			$('[data-role=dialog]').dialog("close");
		}
} ]);


function isValidBarcode(barcode) {
	  // check length
	  if (barcode.length < 8 || barcode.length > 18 ||
	      (barcode.length != 8 && barcode.length != 12 &&
	      barcode.length != 13 && barcode.length != 14 &&
	      barcode.length != 18)) {
	    return false;
	  }

	  var lastDigit = Number(barcode.substring(barcode.length - 1));
	  var checkSum = 0;
	  if (isNaN(lastDigit)) { return false; } // not a valid upc/ean

	  var arr = barcode.substring(0,barcode.length - 1).split("").reverse();
	  var oddTotal = 0, evenTotal = 0;

	  for (var i=0; i<arr.length; i++) {
	    if (isNaN(arr[i])) { return false; } // can't be a valid upc/ean
												// we're checking for

	    if (i % 2 == 0) { oddTotal += Number(arr[i]) * 3; }
	    else { evenTotal += Number(arr[i]); }
	  }
	  checkSum = (10 - ((evenTotal + oddTotal) % 10)) % 10;

	  // true if they are equal
	  return checkSum == lastDigit;
	}
