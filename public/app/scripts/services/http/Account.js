(function (app, routes) {
  var dependencies = ['$http'];
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
  AccountService.$inject = dependencies;
  app.service('AccountService', AccountService);
  return AccountService;
}(angular.module('starwallet'), jsRoutes.controllers));