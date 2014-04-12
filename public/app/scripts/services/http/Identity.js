'use strict';
(function (app, routes) {
  IdentityService.$inject = ['$http', 'PromiseWrapper'];
  function IdentityService($http, promiseWrapper) {
    this.$http = $http;
    this.pw = promiseWrapper;
  }

  IdentityService.prototype = {
    get: function (accountId, userId) {
      return this.$http(routes.Identity.get(accountId, userId)).then(function(response){
        return response.data;
      });
    },
    list: function (userId) {
      return this.$http(routes.Identity.list(userId)).then(function(response){
        return response.data;
      });
    },
    add: function (accountInfo, userId) {
      return this.$http(angular.extend(routes.Identity.add(userId), {data:accountInfo})).then(function (response) {
        return response.data;
      });
    },
    update: function (accountInfo, userId) {
      return this.$http(angular.extend(routes.Identity.update(userId), {data:accountInfo})).then(function (response) {
        return response.data;
      });
    },
    remove: function (accountId, userId) {
      return this.$http(routes.Identity.remove(accountId, userId)).then(function (response) {
        return response.data;
      });
    }
  };
  app.service('IdentityService', IdentityService);
  return IdentityService;
}(angular.module('starwallet'), jsRoutes.controllers));
