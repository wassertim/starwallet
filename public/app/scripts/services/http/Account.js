'use strict';
(function (app, routes) {
  AccountService.$inject = ['$http', 'PromiseWrapper'];
  function AccountService($http, promiseWrapper) {
    this.$http = $http;
    this.pw = promiseWrapper;
  }

  AccountService.prototype = {
    get: function (accountId, userId) {
      return this.$http(routes.Account.get(accountId, userId)).then(function(response){
        return response.data;
      });
    },
    list: function (userId) {
      return this.$http(routes.Account.list(userId)).then(function(response){
        return response.data;
      });
    },
    add: function (accountInfo, userId) {
      return this.$http(angular.extend(routes.Account.add(userId), {data:accountInfo})).then(function (response) {
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
  app.service('AccountService', AccountService);
  return AccountService;
}(angular.module('starbucks'), jsRoutes.controllers));
