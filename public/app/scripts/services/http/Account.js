(function (app, routes) {
  AccountService.$inject = ['$http'];
  function AccountService($http) {
    this.$http = $http;
  }

  AccountService.prototype = {
    getByIdentityId: function(identityId, resync) {
      return this.$http(routes.Account.get(identityId, resync)).then(function(response){
        return response.data;
      });
    }
  };
  app.service('AccountService', AccountService);
  return AccountService;
}(angular.module('starwallet'), jsRoutes.controllers));