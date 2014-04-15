(function (app) {
    CardDisplayController.$inject = ['$scope', 'CardService', '$state'];
    function CardDisplayController($scope, cardService, $state) {
      $scope.vm = this;
      var that = this;
      this.$state = $state;
      this.cardService = cardService;
      cardService.get($state.params.number, $state.params.userId).then(function(card){
        that.card = card;
      });
    }

    CardDisplayController.prototype = {
      savePin: function(){
        var that = this;
        that.isSaving = true;
        this.cardService.savePin(this.card.pinCode, this.card.number, this.$state.params.userId).then(function(){
          that.isSaving = false;
        });
      }
    };
    app.controller('CardDisplayController', CardDisplayController);
    return CardDisplayController;
}(angular.module('starwallet')));