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
    },
    signIn: function(user) {
      return this.pw.wrap(function(){
        if (user.name === 'test' && user.password === 'test') {
          return {
            isAuthenticated: true,
            userId: 1
          }
        } else {
          return {
            isAuthenticated: false
          }
        }
      });
    },
    signUp: function(user) {
      return this.pw.wrap(function(){
        if (user.name === 'test' && user.password === 'test') {
          return {
            isAuthenticated: true,
            userId: 1
          }
        } else {
          return {
            isAuthenticated: false
          }
        }
      });
    },
    signOut: function() {
      return this.pw.wrap(function(){
        return {};
      });
    }
  };
  app.service('AuthService', AuthService);
  return AuthService;
}(angular.module('starbucks')));