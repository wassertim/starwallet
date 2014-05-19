'use strict';
(function (app, routes) {
  var dependencies = ['$http', 'PromiseWrapper'];
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
    register: function(account, userId) {
      return this.$http(angular.extend(routes.Identity.register(userId), {data:account})).then(function(response){
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
  IdentityService.$inject = dependencies;
  app.service('IdentityService', IdentityService);
  return IdentityService;
}(angular.module('starwallet'), jsRoutes.controllers));
