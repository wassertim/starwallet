(function (app, routes) {
  var dependencies = ['$http'];
  function CardService($http) {
    this.$http = $http;
  }

  CardService.prototype = {
    list: function (userId) {
      return this.$http(routes.Card.list(userId)).then(function (response) {
        return response.data;
      });
    },
    get: function (number, userId) {
      return this.$http(routes.Card.get(number, userId)).then(function (response) {
        return response.data;
      });
    },
    savePin: function (pinCode, number, userId) {
      return this.$http(angular.extend(routes.Card.savePin(number, userId), {data: {pinCode: pinCode}})).then(function(response){
        return response.data;
      });
    }
  };
  CardService.$inject = dependencies;
  app.service('CardService', CardService);
  return CardService;
}(angular.module('starwallet'), jsRoutes.controllers));