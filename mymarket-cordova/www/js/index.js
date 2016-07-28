/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
	// Application Constructor
	initialize : function() {
		this.bindEvents();
	},
	// Bind Event Listeners
	//
	// Bind any events that are required on startup. Common events are:
	// 'load', 'deviceready', 'offline', and 'online'.
	bindEvents : function() {
		document.addEventListener('deviceready', this.onDeviceReady, false);

		// $(document).bind("mobileinit", function() {
		// // para resolver problema do jQuery de avançar para página seguinte,
		// // mas depois retornar
		// $.mobile.pushStateEnabled = false;
		// });

		$(document).bind('mobileinit', function() {
			$.mobile.loader.prototype.options.text = "Aguarde";
			$.mobile.loader.prototype.options.textVisible = true;
			$.mobile.loader.prototype.options.theme = "a";
			// $.mobile.loader.prototype.options.html = '"<div
			// id="loading-indicator" class="loading"> <img
			// src="img/loading.gif" /></div>";'
		});

		showLoading();
		$("#AddProductActivity").load("activities/addProductActivity.html");
		$("#SuggestMarketActivity").load(
				"activities/suggestMarketActivity.html");
		$("#dialogLocation").load("dialogs/dialogLocation.html");
		$("#dialogRenameProduct").load("dialogs/dialogRenameProduct.html");
		$("#dialogUpdatePrice").load("dialogs/dialogUpdatePrice.html");

		var onSuccessGetUserLocation = function(position) {
			var latitude = position.coords.latitude;
			var longitude = position.coords.longitude;
			localStorage.setItem("latitude", latitude);
			localStorage.setItem("longitude", longitude);
			getCities();
			hideLoading();
		}

		var onErrorGetUserLocation = function(error) {
			navigator.notification.alert(
					"Não foi possível adquirir localização por GPS!", null,
					"e-Mercado", null);
			getCities();
		}

		$.mobile.loading("show");
		navigator.geolocation.getCurrentPosition(onSuccessGetUserLocation,
				onErrorGetUserLocation, {
					timeout : 7000,
					enableHighAccuracy : true
				});
	},
	// deviceready Event Handler
	//
	// The scope of 'this' is the event. In order to call the 'receivedEvent'
	// function, we must explicitly call 'app.receivedEvent(...);'
	onDeviceReady : function() {
		app.receivedEvent('deviceready');
	},
	// Update DOM on a Received Event
	receivedEvent : function(id) {
		var parentElement = document.getElementById(id);
		var listeningElement = parentElement.querySelector('.listening');
		var receivedElement = parentElement.querySelector('.received');

		listeningElement.setAttribute('style', 'display:none;');
		receivedElement.setAttribute('style', 'display:block;');

		console.log('Received Event: ' + id);
	}
};

$('#citiesSelect').on("change", getPlaces);
$('#placesSelect').on("change", getMarkets);

$('#scanBarcodeButton')
		.on(
				"click",
				function() {
					cordova.plugins.barcodeScanner
							.scan(
									function(result) {
										if (!result.cancelled) {
											localStorage.setItem(
													"confirmActived", true);
											searchProduct(result.text)
										}
									},
									function(error) {
										alert("Scanning failed: " + error);
									},
									{
										"preferFrontCamera" : false, // iOS
										// and
										// Android
										"showFlipCameraButton" : false, // iOS
										// and
										// Android
										"prompt" : "Place a barcode inside the scan area", // supported
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

				});

$('#suggestMarketActivityButton').unbind('click').on(
		"click",
		function() {
			$.mobile.pageContainer.pagecontainer("change",
					"#SuggestMarketActivity", null);

			$('#suggestMarketSendButton').unbind('click').on("click",
					function() {
						var city = $("#citySuggestMarketActivity").val();
						var place = $("#placeSuggestMarketActivity").val();
						var market = $("#marketSuggestMarketActivity").val();

						if ((city == null || city == "") || (place == null || place == "")
								|| (market == null || market == "")) {
							navigator.notification.alert(
									"Por favor, preencha os campos necessários!", null,
									"e-Mercado", null);
							return;
						}
						
						showLoading();
						suggestMarket(city, place, market);
						hideLoading();
						
						navigator.notification.alert("Obrigado pela colaboração!", null,
								"e-Mercado", null);
						$.mobile.pageContainer.pagecontainer("change", "#MainActivity", {
							reverse : false,
							changeHash : false
						});
						
						$("#citySuggestMarketActivity").val("");
						$("#placeSuggestMarketActivity").val("");
						$("#marketSuggestMarketActivity").val("");
					});

		});

app.initialize();
