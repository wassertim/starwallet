'use strict';

(function (app){
  var MainController = function($scope) {
    $scope.vm = this;
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
  };
  MainController.$inject = ['$scope'];

  MainController.prototype = {

  };

  app.controller('MainController', MainController);
  return MainController;
}(angular.module('starbucks')));
