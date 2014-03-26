(function (app) {
  AccountDisplayController.$inject = ['$scope', '$stateParams', 'AccountService'];
  function AccountDisplayController($scope, $stateParams, accountService) {
    $scope.vm = this;
    this.accountService = accountService;
    this.getAccountInfo($stateParams['account']);
  }

  AccountDisplayController.prototype = {
    getAccountInfo: function(account) {
      var that = this;
      this.accountService.getAccountInfo(account).then(function(accountInfo) {
        that.accountInfo = accountInfo;
      });
    }
  };
  app.controller('AccountDisplayController', AccountDisplayController);
  return AccountDisplayController;
}(angular.module('starbucks')));