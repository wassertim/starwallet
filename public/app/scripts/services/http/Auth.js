(function (app, routes) {
  AuthService.$inject = ['$http', 'PromiseWrapper'];
  function AuthService($http, promiseWrapper) {
    this.pw = promiseWrapper;
    this.$http = $http;
  }

  AuthService.prototype = {
    checkAuth: function () {
      return this.$http(routes.User.checkAuth()).then(function (response) {
        return response.data;
      });
    },
    signIn: function (user) {
      return this.$http(angular.extend(routes.User.signIn(), {data:user})).then(function(response){
        return response.data;
      });
    },
    signUp: function (user) {
      return this.$http(angular.extend(routes.User.signUp(), {data: user})).then(function (response) {
        return response.data;
      });
    },
    signOut: function () {
      return this.$http(routes.User.signOut()).then(function(response){
        return response.data;
      });
    }
  };
  app.service('AuthService', AuthService);
  return AuthService;
}(angular.module('starbucks'), jsRoutes.controllers));