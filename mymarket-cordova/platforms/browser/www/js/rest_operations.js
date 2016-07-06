function getCities() {
	latitude = localStorage.getItem("latitude");
	longitude = localStorage.getItem("longitude");
	showLoading();
	$.getJSON(rest_url + '/city/list',
			function(data) {
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
			});
	hideLoading();
}

function getPlaces() {
	latitude = localStorage.getItem("latitude");
	longitude = localStorage.getItem("longitude");
	showLoading();
	$('#placesSelect').find('option').remove();
	var cityId = $("#citiesSelect").val();

	if (cityId > 0) {
		$("#scanBarcodeButton").prop('disabled', null);
	}
	$.getJSON(rest_url + '/place/list?city=' + cityId,
			function(data) {
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
			});
	hideLoading();
}

function getMarkets() {
	latitude = localStorage.getItem("latitude");
	longitude = localStorage.getItem("longitude");
	showLoading();
	$('#marketsSelect').find('option').remove();
	var placeId = $("#placesSelect").val();
	if (placeId != null) {
		$.getJSON(rest_url + '/market/list?place=' + placeId, function(data) {
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
		});
	}
	hideLoading();
}

function searchProduct(barcode) {
	if (barcode == null) {
		return;
	}

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

						$productFieldSet.append('<p>' + barcode + '</p>');

						if (data.length > 0 && typeof data[0] !== 'undefined'
								&& data[0] !== null) {

							$legendName = $("<legend>", {
								class : "scheduler-border",
								text : data[0].product.name
							});

							$renameButton = $("<span>", {
								class : "glyphicon glyphicon-pencil",
								"data-role" : "none",
								"aria-hidden" : true
							}).click(
									function() {
										$("#productNameRenameDialog").val(
												data[0].product.name);
										
										$("#renameSendButton").click(function() {
											alert("Obrigado pela colaboração!");
											searchProduct(barcode);
										});

										$.mobile.pageContainer.pagecontainer(
												"change",
												"#dialogRenameProduct", null);
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
								$dlElement
										.append('<dd><code>Última Atualização</code><p>'
												+ new Date(data[i].lastUpdate)
														.toString()
												+ '</p></dd>');

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
									}).click(function() {
										alert("Obrigado pela colaboração!");
										searchProduct(barcode);
									});

									var $updatePriceButton = $("<button>", {
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
																			alert("Obrigado pela colaboração!");
																			searchProduct(barcode);
																		});

														$(
																"#priceUpdatePriceDialog")
																.on(
																		"blur",
																		function() {
																			// number-format
																			// the
   																			// user
																			// input
																			this.value = parseFloat(
																					this.value
																							.replace(
																									/,/g,
																									""))
																					.toFixed(
																							2)
																					.toString()
																					.replace(
																							/\B(?=(\d{3})+(?!\d))/g,
																							".");
																		});

														$.mobile.pageContainer
																.pagecontainer(
																		"change",
																		"#dialogUpdatePrice",
																		null);
													});

									$productDiv.append($confirmPriceButton);
									$productDiv.append($updatePriceButton);
									$listGroupDiv.prepend($productDiv);
									foundOnSelectedMarket = true;
								} else {
									$productDiv
											.attr('style',
													'background-color: #ADD8E6 !important');
									$listGroupDiv.append($productDiv);
								}
							}

							if (!foundOnSelectedMarket) {
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
													$("#barcodeAddProduct")
															.val(barcode);

													$("#nameAddProduct")
															.val(
																	data[0].product.name);

													$("#nameAddProduct").prop(
															'disabled', true);

													$("#marketAddProduct")
															.val(
																	$(
																			"#marketsSelect option:selected")
																			.text());
													$("#priceAddProduct")
															.on(
																	"blur",
																	function() {
																		// number-format
																		// the
																		// user
																		// input
																		this.value = parseFloat(
																				this.value
																						.replace(
																								/,/g,
																								""))
																				.toFixed(
																						2)
																				.toString()
																				.replace(
																						/\B(?=(\d{3})+(?!\d))/g,
																						".");
																	});

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
																				.val();
																		addProduct(
																				market,
																				barcode,
																				name,
																				price);
																	});

													$.mobile.pageContainer
															.pagecontainer(
																	"change",
																	"#AddProductActivity",
																	null);
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
												$("#barcodeAddProduct").val(
														barcode);

												$("#nameAddProduct").val("");

												$("#nameAddProduct").prop(
														'disabled', null);

												$("#marketAddProduct")
														.val(
																$(
																		"#marketsSelect option:selected")
																		.text());
												$("#priceAddProduct")
														.on(
																"blur",
																function() {
																	// number-format
																	// the user
																	// input
																	this.value = parseFloat(
																			this.value
																					.replace(
																							/,/g,
																							""))
																			.toFixed(
																					2)
																			.toString()
																			.replace(
																					/\B(?=(\d{3})+(?!\d))/g,
																					".");
																});

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
																			.val();
																	addProduct(
																			market,
																			barcode,
																			name,
																			price);
																});

												$.mobile.pageContainer
														.pagecontainer(
																"change",
																"#AddProductActivity",
																null);
											});

							$productFieldSet.append(addProductActivityButton);

						}

						$("#productsFieldset").remove();

						$("#ProductsActivity").append($productFieldSet);
						$.mobile.pageContainer.pagecontainer("change",
								"#ProductsActivity", null);
					});

}

function suggestMarket(city, place, market) {
	$.getJSON(rest_url + '/collaboration/suggest-market', {
		city : city,
		place : place,
		name : market
	}, function(data) {
		alert("Obrigado pela colaboração!");
		$.mobile.pageContainer.pagecontainer("change", "#MainActivity", null);
	});

}
function addProduct(market, barcode, name, price) {
	$.getJSON(rest_url + '/collaboration/suggest-product', {
		market : market,
		barcode : barcode,
		name : name,
		price : price
	}, function(data) {
		alert("Obrigado pela colaboração!");
		$.mobile.pageContainer.pagecontainer("change", "#MainActivity", null);
	});
}
