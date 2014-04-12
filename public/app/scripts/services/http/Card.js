(function (app, routes) {
  CardService.$inject = ['$http'];
  function CardService($http) {
    this.$http = $http;
  }

  CardService.prototype = {
    list: function(userId) {
      return this.$http(routes.Card.list(userId)).then(function(response){
        return response.data;
      });
    }
  };
  app.service('CardService', CardService);
  return CardService;
}(angular.module('starwallet'), jsRoutes.controllers));