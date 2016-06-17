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
    initialize: function() {
        this.bindEvents();
    },
    // Bind Event Listeners
    //
    // Bind any events that are required on startup. Common events are:
    // 'load', 'deviceready', 'offline', and 'online'.
    bindEvents: function() {
        document.addEventListener('deviceready', this.onDeviceReady, false);

        getCities();
    },
    // deviceready Event Handler
    //
    // The scope of 'this' is the event. In order to call the 'receivedEvent'
    // function, we must explicitly call 'app.receivedEvent(...);'
    onDeviceReady: function() {
        app.receivedEvent('deviceready');
    },
    // Update DOM on a Received Event
    receivedEvent: function(id) {
        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');

        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');

        console.log('Received Event: ' + id);
    }
};

app.initialize();

document.getElementById('citiesSelect').addEventListener("change", getPlaces);
function getCities() {
	showLoading();
	$.getJSON("http://146.134.100.66:8080/mymarket-server/rest/city/list",
   		function(data) {
			for(i in data){
				$('#citiesSelect').append($('<option>', { 
        				value: data[i].id,
        				text : data[i].name 
    				}));	
			}
		$("#citiesSelect").change();
  	 	});
	hideLoading();
}

document.getElementById('placesSelect').addEventListener("change", getMarkets);
function getPlaces() {
	showLoading();
	$('#placesSelect').find('option').remove();
	var cityId = $("#citiesSelect").val();
	$.getJSON("http://146.134.100.66:8080/mymarket-server/rest/place/list?city=" + cityId,
   		function(data) {
			for(i in data){
				$('#placesSelect').append($('<option>', { 
        				value: data[i].id,
        				text : data[i].name 
    				}));	
			}
		$("#placesSelect").change();
  	 	});
	hideLoading();
}


function getMarkets() {
	showLoading();
	$('#marketsSelect').find('option').remove();
	var placeId = $("#placesSelect").val();
	if(placeId != null){
	$.getJSON("http://146.134.100.66:8080/mymarket-server/rest/market/list?place=" + placeId,
   		function(data) {
			for(i in data){
				$('#marketsSelect').append($('<option>', { 
        				value: data[i].id,
        				text : data[i].name 
    				}));
			}
  	 	});	
	}
	hideLoading();
}

document.getElementById('scanBarcodeButton').addEventListener("click", initScan);
function initScan() {
	cordova.plugins.barcodeScanner.scan(
		      function (result) {
		    	  if(!result.cancelled){
		    		  $("#barcode").text(result.text);
			    	  $.mobile.pageContainer.pagecontainer("change", "#ProductActivity", null);
		    	  }
		    	  alert("We got a barcode\n" +
		                "Result: " + result.text + "\n" +
		                "Format: " + result.format + "\n" +
		                "Cancelled: " + result.cancelled);
		      }, 
		      function (error) {
		          alert("Scanning failed: " + error);
		      },
		      {
		          "preferFrontCamera" : false, // iOS and Android
		          "showFlipCameraButton" : false, // iOS and Android
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
		          "orientation" : "landscape" // Android only
												// (portrait|landscape), default
												// unset so it rotates with the
												// device
		      }
		   );
}

document.getElementById('suggestMarketActivityButton').addEventListener("click", goToSuggestMarketActivity);
function goToSuggestMarketActivity() {
	  $.mobile.pageContainer.pagecontainer("change", "#SuggestMarketActivity", null);
}

document.getElementById('suggestMarketSendButton').addEventListener("click", suggestMarket);
function suggestMarket() {
	var city = $("#city").val();
	var place = $("#place").val();
	var market = $("#market").val();
	
	showLoading();
	
	$.getJSON('http://146.134.100.66:8080/mymarket-server/rest/collaboration/suggest-market', 
		     {
		        city: city,
		        place: place,
		        name: market
		     }, 
		     function(data) {
					alert("Thank you for collaboration!");
					$.mobile.pageContainer.pagecontainer("change", "#MaintActivity", null);
		     });
	hideLoading();
}

function showLoading(id) {
	if (id == null) {
		id = "loading-indicator";
	}
	$('#' + id).modal({
		"backdrop" : "static",
		"keyboard" : true,
		"show" : true
	});
}

function hideLoading(id) {
	if (id == null) {
		id = "loading-indicator";
	}
	$('#' + id).modal('hide');
}
