(function(d, s, id) {
 	  var js, fjs = d.getElementsByTagName(s)[0];
 	  if (d.getElementById(id)) return;
 	  js = d.createElement(s); js.id = id;
 	  js.src = "//connect.facebook.net/en_US/sdk.js#xfbml=1&version=v2.7&appId=1781335808818687";
 	  fjs.parentNode.insertBefore(js, fjs);
 }(document, 'script', 'facebook-jssdk'));

var CONFIG = {
		ROOT_URL: "http://146.134.100.70:8080/mymarket-server"
};

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

var app = angular.module('myMarketApp', ["ui.bootstrap.modal"]);
app.controller('myMarketController', function($scope, $rootScope, $window, $http) {
	//will check if the user is already logged in
	window.fbAsyncInit = function() {
		FB.Event.subscribe('auth.statusChange', function(response) {
		    if(response.status == 'connected') {
		    	$scope.$apply(function() {		    		  
					  $scope.uid = response.authResponse.userID;
					  $scope.accessToken = response.authResponse.accessToken;					  
				});
		    }		    
		});
	    FB.init({
	        appId      : '1781335808818687',
	        status     : true,
	        cookie     : true,
	        xfbml      : true,
	        version    : 'v2.4',
	 	    oauth      : true
	    });
	 };	
	//will login the user or get its credentials if the user is already logged in	
	$scope.login = function(){
		FB.getLoginStatus(function(response) {
			  if (!(response.status === 'connected')) {
				  FB.login(function(response) {
					  $scope.$apply(function() {
						  $scope.uid = response.authResponse.userID;						  
						  $scope.accessToken = response.authResponse.accessToken;
						  FB.api('/me', function(response) {
							  $scope.name = response.name;
							  $scope.email = response.email;
							  var url = CONFIG.ROOT_URL + "/rest/user/add/" + $scope.uid + "/" + $scope.name + "/" + $scope.email;						
							  $http.get(url);								
						  });						  
					  });
			      }, {scope:'public_profile,email'});
			  } else {				  
				 $scope.uid = response.authResponse.userID;				 
				 $scope.accessToken = response.authResponse.accessToken;				 
			  } 
		});        
    }
	$scope.logout = function() {
		FB.getLoginStatus(function(response) {
			if (response.status === 'connected') {
				FB.logout(function(response) {
					$scope.$apply(function() {
						$scope.uid = null;
						$scope.name = null;
						$scope.email = null;
						$scope.accessToken = null;
					});
				});
			}
		});
	}
	$scope.listCities = function() {
		$http.get(CONFIG.ROOT_URL + "/rest/city/list")
		.then(function success(response) {
			$scope.cities = response.data;
			if (navigator.geolocation) {
			    navigator.geolocation.getCurrentPosition(function(position){
			      $scope.$apply(function(){
			    	var latitude = position.coords.latitude;
			    	var longitude = position.coords.longitude;
			    	var locationNear = null;
		    		var distanceToLocationNear = null;
		    		for (i in $scope.cities) {
		    			if (latitude != null && longitude != null) {
		    				var dist = getDistanceFromLocations(latitude, longitude, $scope.cities[i].latitude, $scope.cities[i].longitude);
		    				if (distanceToLocationNear == null || distanceToLocationNear > dist) {
		    					locationNear = i;
		    					distanceToLocationNear = dist;
		    				}
		    			}
		    		}
		    		var r = $window.confirm("Você está em " + $scope.cities[locationNear].name + "?");
		    		if(r) {
		    			$scope.city = {id: $scope.cities[locationNear].id, name: $scope.cities[locationNear].name};		    			
		    			$scope.listPlaces();
		    		}
			      });
			    });
			 }
		}, 
		function error(failure) {
			
		});		
	}
	$scope.listPlaces = function() {
		if($scope.city) {						
			$http.get(CONFIG.ROOT_URL + "/rest/place/list/" + $scope.city.id)
			.then(function success(response) {			
				$scope.places = response.data;			
			}, 
			function error(failure) {
				
			});		
		}
	}
	$scope.listMarkets = function() {
		if($scope.city) {
			if($scope.place) {
				var url = CONFIG.ROOT_URL + "/rest/market/list-by-place/" + $scope.place.id						
				$http.get(url)
				.then(function success(response) {
					$scope.markets = response.data;			
				}, 
				function error(failure) {
					
				});		
			}
		}
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
		url = CONFIG.ROOT_URL + "/rest/product/get-products-with-price-by-name/" + $scope.product + "/" + $scope.city.id;
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
			$scope.listMarkets();
			$scope.shoppingListProducts = [];
			for (var i = 0; i < sessionStorage.length; i++){
				product = JSON.parse(sessionStorage.getItem(sessionStorage.key(i)));
				if(!angular.isNumber(product)) {
					$scope.shoppingListProducts.push(product);
				}
			}
			$("#modal").modal('show');
		}
	}	
	//shopping list operations
	if(!sessionStorage.total) {
		sessionStorage.total = 0;
	}
	$scope.totalOfProducts = function() {
		return sessionStorage.total ? sessionStorage.total : 0;
	}	
	$scope.addProduct = function(product) {		
		sessionStorage.total++;
		if(!sessionStorage[product.id]) {
			sessionStorage[product.id] = JSON.stringify({'quantity': 1, 'name': product.name, 'id': product.id});
		} else {
			obj = JSON.parse(sessionStorage[product.id]);
			sessionStorage[product.id] = JSON.stringify({'quantity': ++obj.quantity, 'name': obj.name, 'id': obj.id});
		}
	}
	$scope.removeProduct = function(product) {		
		sessionStorage.total--;
		if(sessionStorage[product.id]) {
			obj = JSON.parse(sessionStorage[product.id]);
			if(obj.quantity == 1) {
				sessionStorage.removeItem(product.id);
			} else {				
				sessionStorage[product.id] = JSON.stringify({'quantity': --obj.quantity, 'name': obj.name, 'id': obj.id});
			}
		}
	}
	$scope.containsProduct = function(product) {		
		return sessionStorage[product.id];
	}
	$scope.getShoppingListQuantity = function(product) {		
		return sessionStorage[product.id] ? JSON.parse(sessionStorage[product.id]).quantity : 0;
	}
	$scope.calculateShoppingList = function() {
		if($scope.market) {
			$scope.shoppingListTotal = 0;
			var ids = "";
			angular.forEach($scope.shoppingListProducts, function(product) {
				ids += "/" + product.id;
				product.price = null;
				product.offer = null;
			});
			var url = CONFIG.ROOT_URL + "/rest/product/get-products-with-price/" + $scope.market.id + ids
			$http.get(url)
			.then(function success(response) {			
				var priceMap = [];
				var offerMap = [];
				angular.forEach(response.data, function(product) {
					priceMap[product.id] = product.price;
					offerMap[product.id] = product.offer;
				});
				angular.forEach($scope.shoppingListProducts, function(product) {
					if(priceMap[product.id]) {
						product.price = priceMap[product.id];
						product.offer = offerMap[product.id];
						$scope.shoppingListTotal += (product.price * product.quantity);
					}
				});
			}, 
			function error(failure) {
				
			});
		}
	}
	$scope.saveShoppingList = function() {
		$scope.shoppingListName = $window.prompt("Qual o nome da sua lista de compras?");
		if($scope.shoppingListName) {
			var list = "";
			angular.forEach($scope.shoppingListProducts, function(product) {
				list += "/" + product.id + ":" + product.quantity;
			});
			var url = CONFIG.ROOT_URL + "/rest/shopping-list/save/" + $scope.uid +  "/" + $scope.shoppingListName + list;
			$http.get(url)
			.then(function success(response) {			
				$window.alert("Lista de compras " + $scope.shoppingListName + " salva com sucesso!");
			}, 
			function error(failure) {
				
			});
		}
	}
	$scope.printShoppingList = function() {
		$window.print();
	}
});