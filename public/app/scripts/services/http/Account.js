(function (app, routes) {
  AccountService.$inject = ['$http'];
  function AccountService($http) {
    this.$http = $http;
  }

  AccountService.prototype = {
    getByIdentityId: function(identityId) {
      return this.$http(routes.Account.getByIdentityId(identityId)).then(function(response){
        return response.data;
      });
    }
  };
  app.service('AccountService', AccountService);
  return AccountService;
}(angular.module('starbucks'), jsRoutes.controllers));