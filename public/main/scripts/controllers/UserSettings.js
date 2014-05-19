'use strict';
(function (app) {
  var dependencies = ['$scope', 'UserService', '$state'];
  function UserSettingsController($scope, userService, $state) {
    $scope.vm = this;
    var that = this;
    this.$state = $state;
    this.userService = userService;
    userService.getSettings($state.params.userId).then(function(settings){
      that.settings = settings;
    });
  }

  UserSettingsController.prototype = {
    save: function() {
      var that = this;
      this.isUpdating = true;
      this.userService.saveSettings(this.settings, this.$state.params.userId).then(function(){
        that.isUpdating = false;
      });
    }
  };
  UserSettingsController.$inject = dependencies;
  app.controller('UserSettingsController', UserSettingsController);
  return UserSettingsController;
}(angular.module('starwallet')));