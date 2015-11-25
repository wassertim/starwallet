'use strict';
(function (app, routes) {

  var version = 1;

  function getBarcodeUrl(number) {
    return '/api/barcode/card-barcode/' + number + '?v=' + version;
  }

  function BarcodeFilter() {
    version = Math.round(Math.random() * 1000000000);
    return getBarcodeUrl;
  }

  app.filter('Barcode', BarcodeFilter);

  return BarcodeFilter;
}(angular.module('starwallet'), jsRoutes.controllers));
