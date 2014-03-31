(function (app) {
  AccountDisplayController.$inject = ['$scope', '$stateParams', 'AccountService', '$state'];
  function AccountDisplayController($scope, $stateParams, accountService, $state) {
    $scope.vm = this;
    this.accountService = accountService;
    this.get($stateParams['accountId']);
    $scope.href = function(state, params){
      return $state.href(state, angular.extend($stateParams, params));
    };
  }

  AccountDisplayController.prototype = {
    get: function(identityId) {
      var that = this;
      this.accountService.getByIdentityId(identityId).then(function(accountInfo) {
        that.accountInfo = accountInfo;
      });
    },
    countActiveCoupons: function(coupons) {
      return _.filter(coupons, 'isActive').length;
    }
  };
  app.controller('AccountDisplayController', AccountDisplayController);
  return AccountDisplayController;
}(angular.module('starbucks')));