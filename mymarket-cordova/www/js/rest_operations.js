function getCities() {
	showLoading();
	latitude = localStorage.getItem("latitude");
	longitude = localStorage.getItem("longitude");
	$.getJSON(rest_url + '/city/list',
			function(data) {
				$('#citiesSelect').append($('<option>', {
					value : 0,
					text : "Selecione a cidade"
				}));
				$('#citiesSelect').change();
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
				hideLoading();
			});
}

function getPlaces() {
	latitude = localStorage.getItem("latitude");
	longitude = localStorage.getItem("longitude");
	showLoading();
	$('#placesSelect').find('option').remove();
	var cityId = $("#citiesSelect").val();

	if (cityId > 0) {
		$("#scanBarcodeButton").prop('disabled', null);
	} else {
		$("#scanBarcodeButton").prop('disabled', true);
	}
	$.getJSON(rest_url + '/place/list?city=' + cityId,
			function(data) {
				$('#placesSelect').append($('<option>', {
					value : 0,
					text : "Selecione o bairro"
				}));
				$('#placesSelect').change();
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
				$('#placesSelect').selectmenu("refresh", true);
				hideLoading();
			});
}

function getMarkets() {
	latitude = localStorage.getItem("latitude");
	longitude = localStorage.getItem("longitude");
	showLoading();
	$('#marketsSelect').find('option').remove();
	var placeId = $("#placesSelect").val();
	if (placeId != null) {
		$.getJSON(rest_url + '/market/list?place=' + placeId, function(data) {
			$('#marketsSelect').append($('<option>', {
				value : 0,
				text : "Selecione o mercado"
			}));
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
			$('#marketsSelect').selectmenu("refresh", true);
			hideLoading();
		});
	}
}

function searchProduct(barcode) {
	if (barcode == null) {
		return;
	}
	showLoading();
	var cityId = $("#citiesSelect").val();
	var marketId = $("#marketsSelect").val();
	$
			.getJSON(
					rest_url + '/search/prices-by-city',
					{
						city : cityId,
						barcode : barcode
					},
					function(data) {
						var $productFieldSet = $("<fieldset>", {
							id : "productsFieldset",
							class : "scheduler-border"
						});

						$productFieldSet.append('<p data-position="fixed">'
								+ barcode + '</p>');

						if (data.length > 0 && typeof data[0] !== 'undefined'
								&& data[0] !== null) {

							$legendName = $("<legend>", {
								class : "scheduler-border",
								text : data[0].product.name,
								"data-position" : "fixed"
							});

							$renameButton = $("<span>", {
								class : "glyphicon glyphicon-pencil",
								"data-role" : "none",
								"aria-hidden" : true
							})
									.click(
											function() {
												$("#productNameRenameDialog")
														.val(
																data[0].product.name);

												$("#renameSendButton")
														.click(
																function() {
																	renameProduct(
																			barcode,
																			$(
																					"#productNameRenameDialog")
																					.val());
																	navigator.notification
																			.alert(
																					"Obrigado pela colaboração!",
																					null,
																					"e-Mercado",
																					null);
																	searchProduct(barcode);
																});

												$.mobile.pageContainer
														.pagecontainer(
																"change",
																"#dialogRenameProduct",
																null);
											});

							$legendName.append($renameButton);

							$productFieldSet.prepend($legendName);

							$listGroupDiv = $("<div>", {
								class : "list-group"
							});

							var foundOnSelectedMarket = false;

							for (i in data) {
								var $productDiv = $("<div>", {
									id : "productDiv" + i,
									class : "list-group-item"
								});

								if (data[i].market != null) {
									$productDiv
											.append('<h4 class="list-group-item-heading"><p>'
													+ data[i].market.name
													+ '</p></h4>');
									$productDiv
											.append('<div class="list-group-item-text"><p>'
													+ data[i].market.address
													+ '</p></div>');

									var $dlElement = $("<dl>");

									$dlElement
											.append('<dd><code>Preço</code><p>R$ '
													+ parseFloat(data[i].price)
															.toFixed(2)
													+ '</p></dd>');

									var lastUpdate = new Date(
											data[i].last_update);

									$dlElement
											.append('<dd><code>Última Atualização</code><p>'
													.concat(
															formatDate(lastUpdate))
													.concat('</p><p>')
													.concat(
															formatHour(lastUpdate))
													.concat('</p></dd>'));

									$productDiv.append($dlElement);

									if (data[i].market.id == marketId) {
										$productDiv
												.attr('style',
														'background-color: #96EEB5 !important');

										var $confirmPriceButton = $("<button>",
												{
													type : "button",
													"data-role" : "none",
													class : "btn btn-primary",
													text : "Confirmar preço"
												})
												.click(
														function() {
															navigator.notification
																	.alert(
																			"Obrigado pela colaboração!",
																			null,
																			"e-Mercado",
																			null);
															confirmPrice(
																	marketId,
																	barcode);
															searchProduct(barcode);
														});

										var confirmActived = localStorage
												.getItem("confirmActived");
										if (confirmActived == "false") {
											$confirmPriceButton.prop(
													'disabled', true);
										}

										var $updatePriceButton = $("<button>",
												{
													type : "button",
													"data-role" : "none",
													class : "btn btn-primary",
													text : "Atualizar preço"
												})
												.click(
														function() {
															$(
																	"#updatePriceSendButton")
																	.click(
																			function() {
																				navigator.notification
																						.alert(
																								"Obrigado pela colaboração!",
																								null,
																								"e-Mercado",
																								null);
																				updatePrice(
																						marketId,
																						barcode,
																						$(
																								"#priceUpdatePriceDialog")
																								.maskMoney('unmasked')[0]);
																				searchProduct(barcode);
																			});

															$(
																	"#priceUpdatePriceDialog")
																	.text("");

															$(
																	"#priceUpdatePriceDialog")
																	.maskMoney({thousands:'', decimal:'.', allowZero:true, prefix: ' R$'});

															$.mobile.pageContainer
																	.pagecontainer(
																			"change",
																			"#dialogUpdatePrice",
																			null);
														});

										$productDiv.append($confirmPriceButton);
										$productDiv.append("&nbsp;");
										$productDiv.append($updatePriceButton);
										$listGroupDiv.prepend($productDiv);
										foundOnSelectedMarket = true;
									} else {
										$productDiv
												.attr('style',
														'background-color: #ADD8E6 !important');
										$listGroupDiv.append($productDiv);
									}
								} else {
									if (marketId == null || marketId == 0) {
										$listGroupDiv
												.append("<h4 class='list-group-item-heading'><p>Sem registros!</p></h4>'");
									}
								}
							}
							if (!foundOnSelectedMarket && marketId != null
									&& marketId > 0) {
								var $selectedMarketDiv = $("<div>", {
									class : "list-group-item"
								}).attr('style',
										'background-color: #F5F5DC !important');

								$selectedMarketDiv
										.append('<h4 class="list-group-item-heading"><p>'
												+ $(
														"#marketsSelect option:selected")
														.text() + '</p></h4>');

								$selectedMarketDiv
										.append('<div class="list-group-item-text"><p>Sem registro para esse mercado!</p></div>');

								var addProductActivityButton = $("<button>", {
									id : "addProductActivityButton",
									type : "button",
									"data-role" : "none",
									class : "btn btn-success",
									text : "Adicionar produto"
								})
										.click(
												function() {
													$.mobile.pageContainer
													.pagecontainer(
															"change",
															"#AddProductActivity",
															null);
													
													$("#barcodeAddProduct")
															.val(barcode);

													$("#nameAddProduct")
															.val(
																	data[0].product.name);

													// $("#nameAddProduct").prop(
													// 'disabled', true);

													$("#nameAddProduct")
															.textinput(
																	'disable');

													$("#marketAddProduct")
															.val(
																	$(
																			"#marketsSelect option:selected")
																			.text());

													$("#priceAddProduct").text(
															"");
													
													$("#priceAddProduct").textinput("enable");

													$("#priceAddProduct")
															.maskMoney({thousands:'', decimal:'.', allowZero:true, prefix: ' R$'});

													$("#addProdutctButton")
															.on(
																	"click",
																	function() {
																		var market = $(
																				"#marketsSelect")
																				.val();
																		var name = $(
																				"#nameAddProduct")
																				.val();
																		var price = $(
																				"#priceAddProduct")
																				.maskMoney('unmasked')[0];
																		addProduct(
																				market,
																				barcode,
																				name,
																				price);
																		navigator.notification
																				.alert(
																						"Obrigado pela colaboração!",
																						null,
																						"e-Mercado",
																						null);
																		searchProduct(barcode);
																	});

//													$.mobile.pageContainer
//															.pagecontainer(
//																	"change",
//																	"#AddProductActivity",
//																	null);
												});

								$selectedMarketDiv
										.append(addProductActivityButton);

								$listGroupDiv.prepend($selectedMarketDiv);
							}

							$productFieldSet.append($listGroupDiv);
						} else {
							$productFieldSet
									.prepend('<legend class="scheduler-border">Não encontrado!!</legend>');

							var addProductActivityButton = $("<button>", {
								type : "button",
								"data-role" : "none",
								class : "btn btn-success",
								text : "Adicionar produto"
							})
									.click(
											function() {
												$.mobile.pageContainer
												.pagecontainer(
														"change",
														"#AddProductActivity",
														null);
												
												$("#barcodeAddProduct").val(
														barcode);

												$("#nameAddProduct").val("");

												$("#nameAddProduct").textinput("enable");

												if (marketId != null
														&& marketId > 0) {
													
													$("#marketAddProduct").textinput("disable");
													
													$("#marketAddProduct")
															.val(
																	$(
																			"#marketsSelect option:selected")
																			.text());

													$("#priceAddProduct").textinput("enable");

													$("#priceAddProduct").val(
															"");

													$("#priceAddProduct")
															.maskMoney({thousands:'', decimal:'.', allowZero:true, prefix: ' R$'});
												} else {
													$("#marketAddProduct").val(
															"");

													$("#priceAddProduct").val(
															"");

													$("#marketAddProduct").textinput("disable");
													
//													$("#marketAddProduct")
//															.prop('disabled',
//																	true);

													
													$("#priceAddProduct").textinput("disable");

//													$("#priceAddProduct").prop(
//															'disabled', true);
												}

												$("#addProdutctButton")
														.on(
																"click",
																function() {																	
																	var market = $(
																			"#marketsSelect")
																			.val();
																	var name = $(
																			"#nameAddProduct")
																			.val();
																	var price = $(
																			"#priceAddProduct")
																			.maskMoney('unmasked')[0];
																	addProduct(
																			market,
																			barcode,
																			name,
																			price);
																	navigator.notification
																			.alert(
																					"Obrigado pela colaboração!",
																					null,
																					"e-Mercado",
																					null);
																	$(
																			"#nameAddProduct")
																			.val(
																					"");
																	$(
																			"#priceAddProduct")
																			.val(
																					"");
																	searchProduct(barcode);
																});

//												$.mobile.pageContainer
//														.pagecontainer(
//																"change",
//																"#AddProductActivity",
//																null);
											});

							$productFieldSet.append(addProductActivityButton);

						}

						$("#productsFieldset").remove();

						$("#ProductsActivity").append($productFieldSet);

						if ($.mobile.activePage.is('#MainActivity')) {
							$.mobile.pageContainer.pagecontainer("change",
									"#ProductsActivity", null);
						} else {
							// não funciona
							$.mobile.pageContainer.pagecontainer("change",
									"#ProductsActivity", {
										reverse : true,
										changeHash : false
									});
						}
						hideLoading();
					});

}

function suggestMarket(city, place, market) {
	$.getJSON(rest_url + '/collaboration/suggest-market', {
		city : city,
		place : place,
		name : market
	}, function(data) {
	});
	navigator.notification.alert("Obrigado pela colaboração!", null,
			"e-Mercado", null);
	$.mobile.pageContainer.pagecontainer("change", "#MainActivity", {
		reverse : false,
		changeHash : false
	});
	document.addEventListener('backbutton', function() {

	}, false);
}

function addProduct(market, barcode, name, price) {
	localStorage.setItem("confirmActived", false);
	$.getJSON(rest_url + '/collaboration/suggest-product', {
		market : market,
		barcode : barcode,
		name : name,
		price : price
	}, function(data) {
	});

	showLoading();
	sleep(7000);
	hideLoading();
}

function updatePrice(market, barcode, price) {
	localStorage.setItem("confirmActived", false);
	$.getJSON(rest_url + '/collaboration/suggest-price', {
		market : market,
		product : barcode,
		price : price
	}, function(data) {
	});
	showLoading();
	sleep(7000);
	hideLoading();
}

function confirmPrice(market, barcode) {
	localStorage.setItem("confirmActived", false);
	$.getJSON(rest_url + '/collaboration/confirm-price', {
		market : market,
		barcode : barcode
	}, function(data) {
	});
	showLoading();
	sleep(7000);
	hideLoading();
}

function renameProduct(barcode, name) {
	$.getJSON(rest_url + '/collaboration/suggest-name', {
		product : barcode,
		name : name
	}, function(data) {
	});
}
