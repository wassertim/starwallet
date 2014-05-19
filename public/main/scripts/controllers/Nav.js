'use strict';
(function (app) {
  var dependencies = ['$scope', '$state', '$stateParams'];
  function NavController($scope, $state, $stateParams) {
    this.userId = $stateParams.userId;
  }

  NavController.prototype = {

  };
  NavController.$inject = dependencies;
  app.controller('NavController', NavController);
  return NavController;
}(angular.module('starwallet')));