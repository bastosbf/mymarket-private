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

		$(document).bind('mobileinit', function() {
			$.mobile.loader.prototype.options.text = "Aguarde";
			$.mobile.loader.prototype.options.textVisible = true;
			$.mobile.loader.prototype.options.theme = "a";
		});

		showLoading();
		$("#AddProductActivity").load("activities/addProductActivity.html");
		$("#AddMarketProductActivity").load(
				"activities/addMarketProductActivity.html");
		$("#AddMarketPriceActivity").load(
				"activities/addMarketPriceActivity.html");

		$("#SuggestMarketActivity").load(
				"activities/suggestMarketActivity.html");
		$("#dialogLocation").load("dialogs/dialogLocation.html");
		$("#dialogRenameProduct").load("dialogs/dialogRenameProduct.html");
		$("#dialogUpdatePrice").load("dialogs/dialogUpdatePrice.html");
		$("#dialogEnterBarcode").load("dialogs/dialogEnterBarcode.html");

		if (device.platform == "iOS") {
			$('.header-title').each(function() {
				$(this).addClass('header-title-ios');
			});

			$('.header-back-button').each(function() {
				$(this).addClass('header-back-button-ios');
			});
		}

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
					timeout : 6000,
					enableHighAccuracy : true
				});
	}
};

$('#citiesSelect').on("change", getPlaces);
$('#placesSelect').on("change", getMarkets);

$('#enterBarcodeButton').unbind('click').on("click", function() {
	$("#barcodeEnterBarcodeDialog").val("");
	$('#enterBarcodeSendButton').unbind('click').on("click", function() {
		localStorage.setItem(
				"confirmActived", true);
		searchProduct($("#barcodeEnterBarcodeDialog").val());
		$('[data-role=dialog]').dialog("close");
	});
	$.mobile.changePage('#dialogEnterBarcode', {
		role : 'dialog'
	});
});

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
											searchProduct(result.text);
										}
									},
									function(error) {
										// alert("Scanning failed: " + error);
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

				});

$('#suggestMarketActivityButton')
		.unbind('click')
		.on(
				"click",
				function() {
					// $.mobile.pageContainer.pagecontainer("change",
					// "#SuggestMarketActivity", null);

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
															null, "e-Mercado",
															null);
											return;
										}

										showLoading();
										suggestMarket(city, place, market);
										hideLoading();

										navigator.notification
												.alert(
														"Obrigado pela colaboração!\nEm breve o mercado será adicionado!",
														null, "e-Mercado", null);
										// $.mobile.pageContainer.pagecontainer(
										// "change", "#MainActivity", {
										// reverse : false,
										// changeHash : false
										// });

										$('[data-role=dialog]').dialog("close");

										$("#citySuggestMarketActivity").val("");
										$("#placeSuggestMarketActivity")
												.val("");
										$("#marketSuggestMarketActivity").val(
												"");
									});

					$.mobile.changePage('#SuggestMarketActivity', {
						role : 'dialog'
					});

				});

app.initialize();
