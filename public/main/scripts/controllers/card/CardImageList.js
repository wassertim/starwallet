'use strict';
(function (app) {
  var dependencies = ['$scope'];

  function CardImageListController($scope) {
    //var baseUrl = 'https://cabinet.plas-tek.ru/cardimage.ashx?c=';
    var baseUrl = 'https://cabinet.plas-tek.ru/cardimage.ashx?c=728014766415&w=108&s=';
    $scope.images = this.getImages(baseUrl);
  }

  CardImageListController.prototype = {
    getImages: function(url) {
      var images = [];
      for (var i = 0; i < 1000; i++) {

        //var cardNumber = '7280' + i + '59097';
        var cardNumber = i;
        var imageAnchor = {
          url: url+cardNumber,
          cardNumber: cardNumber
        };
        images.push(imageAnchor);
      }
      return images;
    }
  };
  CardImageListController.$inject = dependencies;
  app.controller('CardImageListController', CardImageListController);
  return CardImageListController;
}(angular.module('starwallet')));