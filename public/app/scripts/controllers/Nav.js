(function (app) {
  NavController.$inject = ['$scope', '$state', '$stateParams'];
  function NavController($scope, $state, $stateParams) {
    this.userId = $stateParams.userId;
  }

  NavController.prototype = {

  };
  app.controller('NavController', NavController);
  return NavController;
}(angular.module('starwallet')));