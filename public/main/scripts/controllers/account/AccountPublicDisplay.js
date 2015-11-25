'use strict';
(function (app, _) {
  var dependencies = ['$scope', 'AccountService', '$state'];
  function AccountPublicDisplayController($scope, accountService, $state) {
    $scope.vm = this;
    this.accountService = accountService;
    this.accountId = $state.params.accountId;
    this.userId = $state.params.userId;
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

  AccountPublicDisplayController.prototype = {
    update: function (e) {
      var that = this;
      that.alert = undefined;
      that.identity = undefined;
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
    activate: function(){
      var that = this;
      this.accountService.activate(this.accountId, this.userId).then(function(){
        that.update();
      });
    },
    get: function (identityId) {
      var that = this;
      this.isLoading = true;
      var success = function (accountInfo) {
        that.activeCouponsCount = that.countActiveCoupons(accountInfo.coupons);
        that.accountInfo = accountInfo;
        that.isLoading = false;
        if ((new Date() - new Date(accountInfo.syncDate)) > 120000) {
          that.update();
        }
      };
      var error = function (response) {
        that.identity = response.data;
        that.isLoading = false;
        that.showActivationButton = true;
        that.alert = {
          message: 'Could not load the data from Starbucks'
        };
      };
      this.accountService.getByIdentityId(identityId, false).then(success, error);
    },
    countActiveCoupons: function (coupons) {
      return _.filter(coupons, 'isActive').length;
    }
  };

  AccountPublicDisplayController.$inject = dependencies;
  app.controller('AccountPublicDisplayController', AccountPublicDisplayController);

  return AccountPublicDisplayController;
}(angular.module('starwallet'), _));
