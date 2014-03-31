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
    update: function (accountInfo) {
      return this.pw.wrap(function () {

      });
    },
    remove: function (accountId) {
      return this.pw.wrap(function () {

      });
    }
  };
  app.service('IdentityService', IdentityService);
  return IdentityService;
}(angular.module('starbucks'), jsRoutes.controllers));
