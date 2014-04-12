(function (app) {
  CardListController.$inject = ['$scope', '$state', 'CardService'];
  function CardListController($scope, $state, cardService) {
    $scope.vm = this;
    var that = this;
    cardService.list($state.params.userId).then(function(cardList){
      that.cards = cardList;
    });
  }

  CardListController.prototype = {

  };
  app.controller('CardListController', CardListController);
  return CardListController;
}(angular.module('starwallet')));