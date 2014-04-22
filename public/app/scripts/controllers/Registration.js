'use strict';
(function (app) {
  RegistrationController.$inject = ['$scope', 'IdentityService', '$state'];
  function RegistrationController($scope, identityService, $state) {
    $scope.vm = this;
    this.$state = $state;
    this.identityService = identityService;
    this.params = $state.params;
    var chance = new Chance();
    this.acc = {
      auth: {
        id: 0,
        userName: chance.word({syllables: 3}),
        password: chance.string({pool:'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789', length:12})
      },
      card: {
        number: 7280
      },
      firstName: chance.first(),
      lastName: chance.last()
    };
  }

  RegistrationController.prototype = {
    saveNew: function(account){
      var that = this;
      that.alert = undefined;
      that.isUpdating = true;
      this.identityService.register(account, this.params['userId']).then(function(accountId){
        that.isUpdating = false;
        that.alert = {
          type: 'success',
          message: 'The card is successfully registered!'
        }
      }, function (response) {
        that.isUpdating = false;
        that.alert = {
          type: 'danger',
          message: response.data
        };
      });
    }
  };
  app.controller('RegistrationController', RegistrationController);
  return RegistrationController;
}(angular.module('starwallet')));