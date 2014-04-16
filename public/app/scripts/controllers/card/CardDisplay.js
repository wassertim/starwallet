(function (app, routes) {
  CardDisplayController.$inject = ['$scope', 'CardService', '$state'];
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
      this.cardService.savePin(this.card.pinCode, this.card.number, this.$state.params.userId).then(function () {
        that.isSaving = false;
        that.barcodeUrl = that.getBarcodeUrl(that.card);
      });
    },
    getBarcodeUrl: function (card) {
      if (card.pinCode) {
        var v = Math.round(Math.random() * 1000000000);
        return routes.BarCode.cardBarCode(this.$state.params.number, this.$state.params.userId).url + "?v=" + v;
      } else {
        return "";
      }
    }
  };
  app.controller('CardDisplayController', CardDisplayController);
  return CardDisplayController;
}(angular.module('starwallet'), jsRoutes.controllers));