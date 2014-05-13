'use strict';
(function (app) {
  var dependencies = ['$scope', '$stateParams', 'IdentityService', '$state', '$window'];
  function IdentityEditController($scope, $stateParams, identityService, $state, $window) {
    $scope.vm = this;
    this.$window = $window;
    this.identityService = identityService;
    this.$state = $state;
    this.params = $stateParams;
    this.acc = {
      userName: '',
      password: '',
      cardNumber: '',
      pinCode: '',
      email: '',
      firstName: '',
      lastName: '',
      phoneNumber: ''
    };
    this.revealPassword = false;
    $scope.href = $state.href;
    this.isLoading = false;
    this.isUpdating = false;
    this.getForm();
  }

  IdentityEditController.prototype = {

    getForm: function () {
      var that = this;
      if (this.params.accountId) {
        that.isLoading = true;
        this.identityService.get(this.params.accountId, this.params.userId).then(function (accountInfo) {
          that.account = accountInfo;
          that.isLoading = false;
        });
      }
    },

    save: function (account) {
      var that = this;
      that.alert = undefined;
      that.isUpdating = true;
      if (!account.id) {
        account.id = 0;
        this.identityService.add(account, this.params.userId).then(function (accountId) {
          that.isUpdating = false;
          that.$state.go('withNav.accountList.editAccount', angular.extend(that.params, {accountId: accountId}));
        }, function () {
          that.isUpdating = false;
          that.alert = {
            message: 'Authentication error'
          };
        });
      } else {
        this.identityService.update(account, this.params.userId).then(function () {
          that.isUpdating = false;
        }, function () {
          that.isUpdating = false;
          that.alert = {
            message: 'Authentication error'
          };
        });
      }
    },

    remove: function (accountId) {
      var that = this;
      that.isUpdating = true;
      if (this.$window.confirm('Do you really want to delete this account?')) {
        this.identityService.remove(accountId, this.params.userId).then(function () {
          that.isUpdating = false;
          that.$state.go('withNav.accountList', that.params);
        });
      }
    }

  };
  IdentityEditController.$inject = dependencies;
  app.controller('IdentityEditController', IdentityEditController);
  return IdentityEditController;
}(angular.module('starwallet')));