'use strict';
(function (app) {
  var dependencies = ['$scope', '$state', 'CardService'];
  function CardListController($scope, $state, cardService) {
    $scope.vm = this;
    var that = this;
    $scope.href = $state.href;
    cardService.list($state.params.userId).then(function(cardList){
      that.cards = cardList;
    });
  }

  CardListController.prototype = {

  };
  CardListController.$inject = dependencies;
  app.controller('CardListController', CardListController);
  return CardListController;
}(angular.module('starwallet')));