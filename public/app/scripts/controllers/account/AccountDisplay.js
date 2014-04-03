(function (app) {
  AccountDisplayController.$inject = ['$scope', '$stateParams', 'AccountService', '$state'];
  function AccountDisplayController($scope, $stateParams, accountService, $state) {
    $scope.vm = this;
    this.accountService = accountService;
    this.accountId = $state.params.accountId;
    this.isLoading = false;
    this.get($stateParams['accountId']);
    this.showAll = false;
    var that = this;
    $scope.$watch('vm.showAll', function(){
      that.couponsFilter = that.getCouponsFilter(that.showAll);
    });
    $scope.href = $state.href;

    this.couponsFilter = this.getCouponsFilter(this.showAll);
  }

  AccountDisplayController.prototype = {
    getCouponsFilter: function (showAll) {
      if (showAll) {
        return {};
      } else {
        return {isActive: true};
      }
    },
    get: function (identityId) {
      var that = this;
      this.isLoading = true;
      this.accountService.getByIdentityId(identityId).then(function (accountInfo) {
        that.activeCouponsCount = that.countActiveCoupons(accountInfo.coupons);
        that.accountInfo = accountInfo;
        that.isLoading = false;
      });
    },
    countActiveCoupons: function (coupons) {
      return _.filter(coupons, 'isActive').length;
    }
  };
  app.controller('AccountDisplayController', AccountDisplayController);
  return AccountDisplayController;
}(angular.module('starbucks')));