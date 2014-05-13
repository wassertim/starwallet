'use strict';
(function (app, routes) {
  var dependencies = ['$scope', 'CardService', '$state'];
  function CardDisplayController($scope, cardService, $state) {
    $scope.vm = this;
    var that = this;
    this.$state = $state;
    this.cardService = cardService;
    this.card = {};
    cardService.get($state.params.number, $state.params.userId).then(function (card) {
      that.card = card;
      that.barcodeUrl = that.getBarcodeUrl(that.card);
    });
  }

  CardDisplayController.prototype = {
    savePin: function () {
      var that = this;
      that.isSaving = true;
      this.cardService.savePin(this.card.data.pin, this.card.data.number, this.$state.params.userId).then(function () {
        that.isSaving = false;
        that.barcodeUrl = that.getBarcodeUrl(that.card);
        that.isPinCodeVisible = false;
      });
    },
    getBarcodeUrl: function (card) {
      if (card.data.pin) {
        var v = Math.round(Math.random() * 1000000000);
        return routes.BarCode.cardBarCode(this.$state.params.number, this.$state.params.userId).url + '?v=' + v;
      } else {
        return '';
      }
    },
    showPinCode: function(e) {
      e.preventDefault();
      this.isPinCodeVisible = !this.isPinCodeVisible;
    }
  };
  CardDisplayController.$inject = dependencies;
  app.controller('CardDisplayController', CardDisplayController);
  return CardDisplayController;
}(angular.module('starwallet'), jsRoutes.controllers));