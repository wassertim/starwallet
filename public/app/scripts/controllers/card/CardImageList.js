'use strict';
(function (app) {
  var dependencies = ['$scope'];

  function CardImageListController($scope) {
    var baseUrl = 'https://cabinet.plas-tek.ru/cardimage.ashx?c=';
    $scope.images = this.getImages(baseUrl);
  }

  CardImageListController.prototype = {
    getImages: function(url) {
      var images = [];
      for (var i = 100; i < 155; i++) {
        var cardNumber = '7280' + i + '79097';
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