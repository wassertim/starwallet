(function (app) {
  AccountDisplayController.$inject = ['$scope', '$stateParams', 'AccountService', '$state'];
  function AccountDisplayController($scope, $stateParams, accountService, $state) {
    $scope.vm = this;
    this.accountService = accountService;
    this.getAccountInfo($stateParams['accountId']);
    $scope.href = function(state, params){
      return $state.href(state, angular.extend($stateParams, params));
    };
  }

  AccountDisplayController.prototype = {
    getAccountInfo: function(accountId) {
      var that = this;
      this.accountService.getAccountInfo(accountId).then(function(accountInfo) {
        that.accountInfo = accountInfo;
      });
    }
  };
  app.controller('AccountDisplayController', AccountDisplayController);
  return AccountDisplayController;
}(angular.module('starbucks')));