(function (app, routes) {
  UserService.$inject = ['$http'];
  function UserService($http) {
    this.$http = $http;
  }

  UserService.prototype = {
    getSettings: function(userId) {
      return this.$http(routes.User.getSettings(userId)).then(function(response){
        return response.data;
      });
    },
    saveSettings: function(settings, userId) {
      return this.$http(angular.extend(routes.User.saveSettings(userId), {data:settings})).then(function(response){
        return response.data;
      });
    }
  };
  app.service('UserService', UserService);
  return UserService;
}(angular.module('starwallet'), jsRoutes.controllers));