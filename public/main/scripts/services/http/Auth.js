'use strict';
(function (app, routes) {
  var dependencies = ['$http', 'PromiseWrapper'];
  function AuthService($http, promiseWrapper) {
    this.pw = promiseWrapper;
    this.$http = $http;
  }

  AuthService.prototype = {
    checkAuth: function () {
      return this.$http(routes.Security.checkAuth()).then(function (response) {
        return response.data;
      });
    },
    signIn: function (user) {
      return this.$http(angular.extend(routes.Security.signIn(), {data:user})).then(function(response){
        return response.data;
      });
    },
    signUp: function (user) {
      return this.$http(angular.extend(routes.Security.signUp(), {data: user})).then(function (response) {
        return response.data;
      });
    },
    signOut: function () {
      return this.$http(routes.Security.signOut()).then(function(response){
        return response.data;
      });
    }
  };
  AuthService.$inject = dependencies;
  app.service('AuthService', AuthService);
  return AuthService;
}(angular.module('starwallet'), jsRoutes.controllers));