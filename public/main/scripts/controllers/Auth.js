'use strict';
(function (app) {
  var dependencies = ['$scope', 'AuthService', '$state'];

  function AuthController($scope, authService, $state) {
    $scope.vm = this;
    this.$state = $state;
    this.authService = authService;
  }

  AuthController.prototype = {
    signIn: function () {
      if (!this.user || !this.user.userName || !this.user.password) {
        return;
      }
      var that = this;
      that.alert = undefined;
      that.isLoading = true;
      this.authService.signIn(this.user).then(function (identity) {
        if (identity.isAuthenticated) {
          that.$state.go('withNav.accountList', { userId: identity.userId });
        } else {
          that.isLoading = false;
          that.alert = {
            type: 'error',
            message: 'Incorrect email or password'
          };
        }
      });
    },
    signUp: function () {
      if (!this.user || !this.user.userName || !this.user.password) {
        return;
      }
      var that = this;
      that.alert = undefined;
      that.isLoading = true;
      this.authService.signUp(this.user).then(function (identity) {
        if (identity.isAuthenticated) {
          that.$state.go('withNav.accountList', { userId: identity.userId });
        } else {
          that.isLoading = false;
          that.alert = {
            type: 'error',
            message: 'An error occured during signing up'
          };
        }
      });
    }
  };
  AuthController.$inject = dependencies;
  app.controller('AuthController', AuthController);
  return AuthController;
}(angular.module('starwallet')));