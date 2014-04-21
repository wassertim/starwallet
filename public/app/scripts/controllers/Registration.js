'use strict';
(function (app) {
  RegistrationController.$inject = ['$scope', 'IdentityService', '$state'];
  function RegistrationController($scope, identityService, $state) {
    $scope.vm = this;
    this.identityService = identityService;
    this.params = $state.params;
  }

  RegistrationController.prototype = {
    saveNew: function(account){
      var that = this;
      that.alert = undefined;
      that.isUpdating = true;
      this.identityService.register(account, this.params['userId']).then(function(accountId){
        that.isUpdating = false;
        that.$state.go('withNav.accountList.editAccount', angular.extend(that.params, {accountId: accountId}));
      }, function () {
        that.isUpdating = false;
        that.alert = {
          message: 'An error occured'
        };
      });
    }
  };
  app.controller('RegistrationController', RegistrationController);
  return RegistrationController;
}(angular.module('starwallet')));