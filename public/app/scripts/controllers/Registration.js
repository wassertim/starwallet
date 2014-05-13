'use strict';
/*global Chance */
(function (app, chance) {
  var dependencies = ['$scope', 'IdentityService', '$state', 'UserService'];
  function RegistrationController($scope, identityService, $state, userService) {
    $scope.vm = this;
    this.$state = $state;
    this.identityService = identityService;
    this.params = $state.params;

    var that = this;
    var userName = chance.word({syllables: 3});
    that.acc = {
      auth: {
        id: 0,
        userName: userName,
        password: chance.string({pool:'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789', length:12})
      },
      card: {
        number: 7280
      },
      firstName: chance.first(),
      lastName: chance.last()
    };
    userService.getSettings(this.params.userId).then(function(settings){
      that.acc.phoneNumber = settings.phone;
      that.acc.firstName = settings.firstName;
      that.acc.email = userName + '@' + settings.emailDomain;
    });

  }

  RegistrationController.prototype = {
    saveNew: function(account){
      var that = this;
      that.alert = undefined;
      that.isUpdating = true;
      this.identityService.register(account, this.params.userId).then(function(){
        that.isUpdating = false;
        that.alert = {
          type: 'success',
          message: 'The card is successfully registered!'
        };
      }, function (response) {
        that.isUpdating = false;
        that.alert = {
          type: 'danger',
          message: response.data
        };
      });
    }
  };
  RegistrationController.$inject = dependencies;
  app.controller('RegistrationController', RegistrationController);
  return RegistrationController;
}(angular.module('starwallet'), new Chance()));