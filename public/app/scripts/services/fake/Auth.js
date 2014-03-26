(function (app) {
  AuthService.$inject = ['PromiseWrapper'];
  function AuthService(promiseWrapper) {
    this.pw = promiseWrapper;
  }

  AuthService.prototype = {
    checkAuth: function(){
      return this.pw.wrap(function(){
        return {
          isAuthenticated: true,
          userId: 1
        }
      });
    }
  };
  app.service('AuthService', AuthService);
  return AuthService;
}(angular.module('starbucks')));