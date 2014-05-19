'use strict';
(function (app) {
  var dependencies = ['$state', 'AuthService'];
  function MainController($state, authService) {
    authService.checkAuth().then(function (auth) {
      if (auth.isAuthenticated) {
        $state.go('withNav.accountList', {userId: auth.userId});
      } else {
        $state.go('login');
      }
    });
  }
  MainController.$inject = dependencies;
  app.controller('MainController', MainController);
  return MainController;
}(angular.module('starwallet')));