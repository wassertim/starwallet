(function (app) {
  MainController.$inject = ['$state', 'AuthService'];
  function MainController($state, authService) {
    authService.checkAuth().then(function (auth) {
      if (auth.isAuthenticated) {
        $state.go('accountList', {userId: auth.userId});
      } else {
        $state.go('login');
      }
    });
  }

  app.controller('MainController', MainController);
  return MainController;
}(angular.module('starbucks')));