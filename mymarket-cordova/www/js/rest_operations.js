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
		$('#enterBarcodeButton').prop('disabled', null);
	} else {
		$("#scanBarcodeButton").prop('disabled', true);
		$('#enterBarcodeButton').prop('disabled', true);
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
			.ajax({
				url : rest_url + '/search/prices-by-city',
				dataType : 'json',
				async : false,
				data : {
					"city" : cityId,
					"barcode" : barcode
				},
				type : "GET",
				success : function(data) {

					var $productFieldSet = $("<fieldset>", {
						id : "productsFieldset",
						class : "scheduler-border"
					});

					$productFieldSet.append('<p data-position="fixed">'
							+ barcode + '</p>');

					if (data.length > 0 && typeof data[0] !== 'undefined'
							&& data[0] !== null) {

						$legendName = $("<legend>", {
							id : "productName",
							class : "scheduler-border",
							text : data[0].product.name,
							"data-position" : "fixed"
						});

						$renameButton = $("<span>", {
							class : "glyphicon glyphicon-pencil",
							"data-role" : "none",
							"aria-hidden" : true
						})
								.unbind('click')
								.click(
										function() {
											$("#productNameRenameDialog").val(
													data[0].product.name);

											$("#renameSendButton")
													.unbind('click')
													.click(
															function() {
																var name = $(
																		"#productNameRenameDialog")
																		.val();
																if (name == null
																		|| name == "") {
																	navigator.notification
																			.alert(
																					"Informe um nome válido!",
																					null,
																					"e-Mercado",
																					null);
																} else {
																	renameProduct(
																			barcode,
																			name);

																	navigator.notification
																			.alert(
																					"Obrigado pela colaboração!\nEm breve o nome será atualizado!",
																					null,
																					"e-Mercado",
																					null);
																	// searchProduct(barcode);

																	$(
																			'[data-role=dialog]')
																			.dialog(
																					"close");
																}
															});

											$.mobile.changePage(
													'#dialogRenameProduct', {
														role : 'dialog'
													});
										});

						$legendName.append($renameButton);

						$productFieldSet.prepend($legendName);

						$listGroupDiv = $("<div>", {
							class : "list-group"
						});

						var foundOnSelectedMarket = false;

						for (i in data) {
							var $productDiv = $('<div>', {
								id : "productDiv" + i,
								class : "list-group-item product-info-div",
							});

							if (data[i].market != null) {
								$productDiv
										.append('<br><h4 class="list-group-item-heading"><p>'
												+ data[i].market.name
												+ '</p></h4>');
								$productDiv
										.append('<div class="list-group-item-text"><p>'
												+ data[i].market.address
												+ '</p></div>');

								var $dlElement = $("<dl>");

								$dlElement
										.append('<dd><code>Preço</code><p id="productPrice'
												+ data[i].market.id
												+ '">R$ '
												+ parseFloat(data[i].price)
														.toFixed(2)
												+ '</p></dd>');

								var lastUpdate = new Date(data[i].last_update);

								$dlElement
										.append('<dd><code>Última Atualização</code><p '
												.concat(
														'id= productLastUpdateDateVal')
												.concat(data[i].market.id)
												.concat('>')
												.concat(formatDate(lastUpdate))
												.concat('</p><p ')
												.concat(
														'id= productLastUpdateHourVal')
												.concat(data[i].market.id)
												.concat('>').concat(
														formatHour(lastUpdate))
												.concat('</p></dd>'));

								$productDiv.append($dlElement);

								if (data[i].market.id == marketId) {
									$productDiv
											.attr('style',
													'background-color: #96EEB5 !important');

									var $confirmPriceButton = $("<button>", {
										type : "button",
										"data-role" : "none",
										class : "btn btn-primary",
										text : "Confirmar preço"
									})
											.unbind('click')
											.click(
													function() {
														navigator.notification
																.alert(
																		"Obrigado pela colaboração!",
																		null,
																		"e-Mercado",
																		null);

														$confirmPriceButton
																.prop(
																		'disabled',
																		true);

														confirmPrice(marketId,
																barcode);
														$(
																"#productLastUpdateDateVal"
																		+ marketId)
																.text(
																		formatDate(new Date()));
														$(
																"#productLastUpdateHourVal"
																		+ marketId)
																.text(
																		formatHour(new Date()));
													});

									var confirmActived = localStorage
											.getItem("confirmActived");
									if (confirmActived == "false") {
										$confirmPriceButton.prop('disabled',
												true);
									}

									var $updatePriceButton = $("<button>", {
										type : "button",
										"data-role" : "none",
										class : "btn btn-primary",
										text : "Atualizar preço"
									})
											.unbind('click')
											.click(
													function() {
														$(
																"#priceUpdatePriceDialog")
																.val("");

														$(
																"#updatePriceSendButton")
																.unbind('click')
																.click(
																		function() {
																			var productNewPrice = $(
																					"#priceUpdatePriceDialog")
																					.val();

																			if (productNewPrice != null
																					&& productNewPrice > 0) {

																				$confirmPriceButton
																						.prop(
																								'disabled',
																								true);

																				navigator.notification
																						.alert(
																								"Obrigado pela colaboração!",
																								null,
																								"e-Mercado",
																								null);
																				updatePrice(
																						marketId,
																						barcode,
																						productNewPrice);
																				// searchProduct(barcode);

																				$(
																						"#productPrice"
																								+ marketId)
																						.text(
																								"R$"
																										+ productNewPrice);

																				$(
																						'[data-role=dialog]')
																						.dialog(
																								"close");

																			} else {
																				navigator.notification
																						.alert(
																								"Informe o preço do produto!",
																								null,
																								"e-Mercado",
																								null);
																			}
																		});

														$(
																"#priceUpdatePriceDialog")
																.text("");

														$.mobile
																.changePage(
																		'#dialogUpdatePrice',
																		{
																			role : 'dialog'
																		});
													});

									$productDiv.append($confirmPriceButton);
									$productDiv.append("&nbsp;");
									$productDiv.append($updatePriceButton);
									$productDiv.append("<br><br>");
									$listGroupDiv.prepend($productDiv);
									foundOnSelectedMarket = true;
								} else {
									$productDiv
											.attr('style',
													'background-color: #ADD8E6 !important');
									$productDiv.append("<br>");
									$listGroupDiv.append($productDiv);
								}
							} else {
								if (marketId == null || marketId == 0) {
									$listGroupDiv
											.append("<h4 id='noRegistryAlert' class='list-group-item-heading'><p>Sem registros!</p></h4>");
								}
							}
						}
						if (!foundOnSelectedMarket && marketId != null
								&& marketId > 0) {
							var $selectedMarketDiv = $("<div>", {
								class : "list-group-item"
							}).attr('style',
									'background-color: #FF5A5A !important');

							$selectedMarketDiv
									.append('<h4 class="list-group-item-heading"><p>'
											+ $(
													"#marketsSelect option:selected")
													.text() + '</p></h4>');

							$selectedMarketDiv
									.append('<div class="list-group-item-text"><p>Sem registro para esse mercado!</p></div>');

							var $addProductActivityButton = $("<button>", {
								id : "$addProductActivityButton",
								type : "button",
								"data-role" : "none",
								class : "btn btn-success",
								text : "Adicionar preço"
							})
									.unbind('click')
									.click(
											function() {

												$.mobile
														.changePage(
																'#AddMarketPriceActivity',
																{
																	role : 'dialog'
																});

												$("#barcodeAddMarketPrice")
														.text(barcode);

												$("#nameAddMarketPrice").text(
														data[0].product.name);

												$("#marketAddMarketPrice")
														.text(
																$(
																		"#marketsSelect option:selected")
																		.text());

												$("#priceAddMarketPrice").val(
														"");

												$("#addMarketPriceButton")
														.unbind('click')
														.click(
																function() {
																	var market = $(
																			"#marketsSelect")
																			.val();
																	var name = $(
																			"#nameAddMarketPrice")
																			.text();
																	var price = $(
																			"#priceAddMarketPrice")
																			.val();

																	if ((market != "" && market != null)
																			&& (name != "" && name != null)
																			&& (price != "" && price != null)) {
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
																				'[data-role=dialog]')
																				.dialog(
																						"close");
																		searchProduct(barcode);
																		hideLoading();
																	} else {
																		navigator.notification
																				.alert(
																						"Preencha os campos necessários!",
																						null,
																						"e-Mercado",
																						null);
																	}
																});

											});

							$selectedMarketDiv
									.append($addProductActivityButton);

							$listGroupDiv.prepend($selectedMarketDiv);
						}

						$productFieldSet.append($listGroupDiv);
					} else {
						$productFieldSet
								.prepend('<legend class="scheduler-border">Não encontrado!!</legend>');

						var $addProductActivityButton = $("<button>", {
							type : "button",
							"data-role" : "none",
							class : "btn btn-success",
							text : "Adicionar produto"
						})
								.unbind('click')
								.click(
										function() {
											if (marketId != null
													&& marketId > 0) {

												$.mobile
														.changePage(
																'#AddMarketProductActivity',
																{
																	role : 'dialog'
																});

												$("#barcodeAddMarketProduct")
														.text(barcode);

												$("#nameAddMarketProduct").val(
														"");


												$("#marketAddMarketProduct")
														.text(
																$(
																		"#marketsSelect option:selected")
																		.text());


												$("#priceAddMarketProduct")
														.val("");

												$("#addMarketProductButton")
														.unbind('click')
														.click(
																function() {
																	var market = $(
																			"#marketsSelect")
																			.val();
																	var name = $(
																			"#nameAddMarketProduct")
																			.val();

																	var price = $(
																			"#priceAddMarketProduct")
																			.val();


																	if ((name == null || name == "")
																			|| ((market == null
																					|| market == "" || market <= 0) || (price == null || price == ""))) {

																		navigator.notification
																				.alert(
																						"Por favor, preencha os campos necessários.",
																						null,
																						"e-Mercado",
																						null);
																		return;
																	}

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
																			"#nameMarketProduct")
																			.val(
																					"");
																	$(
																			"#priceMarketProduct")
																			.val(
																					"");
																	$(
																			'[data-role=dialog]')
																			.dialog(
																					"close");
																	searchProduct(barcode);
																	hideLoading();
																});

											} else {

												$.mobile.changePage(
														'#AddProductActivity',
														{
															role : 'dialog'
														});

												$("#barcodeAddProduct").text(
														barcode);

												$("#nameAddProduct").val("");

												$("#addProdutctButton")
														.unbind('click')
														.click(
																function() {

																	var name = $(
																			"#nameAddProduct")
																			.val();


																	if ((name == null || name == "")
																			|| (barcode == null || barcode == "")) {

																		navigator.notification
																				.alert(
																						"Por favor, preencha os campos necessários.",
																						null,
																						"e-Mercado",
																						null);
																		return;
																	}

																	addProduct(
																			null,
																			barcode,
																			name,
																			null);
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
																	$(
																			'[data-role=dialog]')
																			.dialog(
																					"close");
																	searchProduct(barcode);
																	hideLoading();
																});

											}

										});

						$productFieldSet.append($addProductActivityButton);

					}

					$("#productsFieldset").remove();

					$("#ProductsActivity").append($productFieldSet);

					$.mobile.pageContainer.pagecontainer("change",
							"#ProductsActivity", null);

					hideLoading();
				}
			});
	hideLoading();
}

function suggestMarket(city, place, market) {
	showLoading();
	if ((city == null || city == "") || (place == null || place == "")
			|| (market == null || market == "")) {
		return;
	}
	$.getJSON(rest_url + '/collaboration/suggest-market', {
		city : city,
		place : place,
		name : market
	}, function(data) {
	});
	hideLoading();
}

function addProduct(market, barcode, name, price) {
	showLoading();
	if ((barcode == null || barcode == "") || (name == null || name == "")) {
		return;
	}

	localStorage.setItem("confirmActived", false);

	$.ajax({
		url : rest_url + '/collaboration/suggest-product',
		dataType : 'json',
		async : false,
		data : {
			"market" : market,
			"barcode" : barcode,
			"name" : name,
			"price" : price
		},
		type : "GET",
		success : function(data) {
			hideLoading();
		}
	});
}

function updatePrice(market, barcode, price) {
	showLoading();
	localStorage.setItem("confirmActived", false);
	$.getJSON(rest_url + '/collaboration/suggest-price', {
		market : market,
		product : barcode,
		price : price
	}, function(data) {
		hideLoading();
	});
}

function confirmPrice(market, barcode) {
	showLoading();
	localStorage.setItem("confirmActived", false);
	$.getJSON(rest_url + '/collaboration/confirm-price', {
		market : market,
		barcode : barcode
	}, function(data) {
		hideLoading();
	});
}

function renameProduct(barcode, name) {
	showLoading();
	$.getJSON(rest_url + '/collaboration/suggest-name', {
		barcode : barcode,
		name : name
	}, function(data) {
		hideLoading();
	});
}
