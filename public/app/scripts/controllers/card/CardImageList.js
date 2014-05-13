'use strict';
(function (app) {
  var dependencies = ['$scope'];

  function CardImageListController($scope) {

  }

  CardImageListController.prototype = {

  };
  CardImageListController.$inject = dependencies;
  app.controller('CardImageListController', CardImageListController);
  return CardImageListController;
}(angular.module('starwallet')));