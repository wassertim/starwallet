(function (app) {
    AuthController.$inject = ['$scope', 'AuthService', '$state'];
    function AuthController($scope, authService, $state) {
      $scope.vm = this;
      this.$state = $state;
      this.authService = authService;
    }

    AuthController.prototype = {
      signIn: function(){
        if (!this.user || !this.user.name || !this.user.password)
          return;
        var that = this;
        that.alert = undefined;
        that.isLoading = true;
        this.authService.signIn(this.user).then(function (identity) {
          if (identity.isAuthenticated) {
            that.$state.go('accountList', { userId: identity.userId });
          } else {
            that.isLoading = false;
            that.alert = {
              type: 'error',
              message: 'Логин и/или пароль не верны'
            };
          }
        });
      }
    };
    app.controller('AuthController', AuthController);
    return AuthController;
}(angular.module('starbucks')));