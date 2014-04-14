(function (app) {
    CardDisplayController.$inject = ['$scope', 'CardService', '$state'];
    function CardDisplayController($scope, cardService, $state) {
      $scope.vm = this;
      cardService.get($state.params.number).then(function(card){
        this.card = card;
      });
    }

    CardDisplayController.prototype = {

    };
    app.controller('CardDisplayController', CardDisplayController);
    return CardDisplayController;
}(angular.module('starwallet')));