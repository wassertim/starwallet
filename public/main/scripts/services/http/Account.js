'use strict';
(function (app, routes) {
  var dependencies = ['$http'];

  function AccountService($http) {
    this.$http = $http;
  }

  AccountService.prototype = {
    getByIdentityId: function (identityId, resync) {
      return this.$http(routes.Account.get(identityId, resync)).then(function (response) {
        return response.data;
      });
    },
    activate: function(accountId, userId) {
      return this.$http(routes.Account.activate(accountId, userId)).then(function(response){
        return response.data;
      });
    },
    refreshAll: function() {
      return this.$http(routes.Account.refreshAll()).then(function(response){

      });
    }
  };
  AccountService.$inject = dependencies;
  app.service('AccountService', AccountService);
  return AccountService;
}(angular.module('starwallet'), jsRoutes.controllers));