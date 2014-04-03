(function (app) {
  AccountDisplayController.$inject = ['$scope', '$stateParams', 'AccountService', '$state', 'HrefService'];
  function AccountDisplayController($scope, $stateParams, accountService, $state, href) {
    $scope.vm = this;
    this.accountService = accountService;
    this.accountId = $state.params.accountId;
    this.isLoading = false;
    this.get($stateParams['accountId']);

    $scope.href = $state.href;

  }

  AccountDisplayController.prototype = {
    get: function(identityId) {
      var that = this;
      this.isLoading = true;
      this.accountService.getByIdentityId(identityId).then(function(accountInfo) {
        that.accountInfo = accountInfo;
        that.isLoading = false;
      });
    },
    countActiveCoupons: function(coupons) {
      return _.filter(coupons, 'isActive').length;
    }
  };
  app.controller('AccountDisplayController', AccountDisplayController);
  return AccountDisplayController;
}(angular.module('starbucks')));