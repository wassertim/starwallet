(function (app) {
  AccountController.$inject = ['$scope', 'AccountService', '$stateParams', '$state'];
  function AccountController($scope, accountService, $stateParams, $state) {
    $scope.vm = this;
    $scope.href = $state.href;
    this.userId = $stateParams['userId'];
    accountService.list(this.userId).then(function(list){
      $scope.list = list;
    });
  }

  AccountController.prototype = {

  };
  app.controller('AccountController', AccountController);
  return AccountController;
}(angular.module('starbucks')));