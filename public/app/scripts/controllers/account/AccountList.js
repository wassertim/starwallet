(function (app) {
  AccountController.$inject = ['$scope', 'AccountService', '$stateParams', '$state'];
  function AccountController($scope, accountService, $stateParams, $state) {
    $scope.vm = this;
    $scope.href = $state.href;
    this.accountService = accountService;
    this.list($stateParams['userId']);
  }

  AccountController.prototype = {
    list: function(userId){
      var that = this;
      this.accountService.list(userId).then(function(list){
        that.list = list;
      });
    }
  };
  app.controller('AccountController', AccountController);
  return AccountController;
}(angular.module('starbucks')));