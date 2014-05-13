'use strict';
(function (app, _) {
  var dependencies = ['$scope', '$stateParams', 'AccountService', '$state'];
  function AccountDisplayController($scope, $stateParams, accountService, $state) {
    $scope.vm = this;
    this.accountService = accountService;
    this.accountId = $state.params.accountId;
    this.isLoading = false;
    this.get(this.accountId);
    this.showAll = false;
    var that = this;
    $scope.$watch('vm.showAll', function () {
      that.couponsFilter = that.getCouponsFilter(that.showAll);
    });
    $scope.href = $state.href;

    this.couponsFilter = this.getCouponsFilter(this.showAll);
  }

  AccountDisplayController.prototype = {
    update: function (e) {
      var that = this;
      if (e) {
        e.preventDefault();
      }
      that.isRefreshing = true;
      this.accountService.getByIdentityId(this.accountId, true).then(function (accountInfo) {
        that.activeCouponsCount = that.countActiveCoupons(accountInfo.coupons);
        that.accountInfo = accountInfo;
        that.isRefreshing = false;
      }, function () {
        that.isRefreshing = false;
        that.alert = {
          message: 'Could not load the data from Starbucks'
        };
      });
    },
    getLastTransactionDate: function (transactions) {
      return _.max(_.map(transactions, function (item) {
        return item.date;
      }));
    },
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
      this.accountService.getByIdentityId(identityId, false).then(function (accountInfo) {
        that.activeCouponsCount = that.countActiveCoupons(accountInfo.coupons);
        that.accountInfo = accountInfo;
        that.isLoading = false;
        if ((new Date() - new Date(accountInfo.syncDate)) > 120000) {
          that.update();
        }
      }, function () {
        that.isLoading = false;
        that.alert = {
          message: 'Could not load the data from Starbucks'
        };
      });
    },
    countActiveCoupons: function (coupons) {
      return _.filter(coupons, 'isActive').length;
    }
  };
  AccountDisplayController.$inject = dependencies;
  app.controller('AccountDisplayController', AccountDisplayController);
  return AccountDisplayController;
}(angular.module('starwallet'), _));