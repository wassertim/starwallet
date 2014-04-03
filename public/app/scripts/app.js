'use strict';
angular.module('starbucks', [
  'ngCookies',
  'ngResource',
  'ngSanitize',
  'ngRoute',
  'ui.router'
]).config(function ($routeProvider, $stateProvider) {
  function vw(url) {
    return '../' + url;
  }

  $stateProvider.state('home', {
    url: '',
    controller: 'MainController'
  }).state('home2', {
    url: '/',
    controller: 'MainController'
  }).state('accountList', {
    url: '/u:userId/account',
    controller: 'IdentityListController',
    templateUrl: vw('views/identity/identity-list.html')
  }).state('accountList.accountDisplay', {
    url: '/display/:accountId',
    controller: 'AccountDisplayController',
    templateUrl: vw('views/account/account-display.html')
  }).state('login', {
    url: '/login',
    controller: 'AuthController',
    templateUrl: vw('views/login.html')
  }).state('signUp', {
    url: '/signup',
    controller: 'AuthController',
    templateUrl: vw('views/signup.html')
  }).state('signOut', {
    url: 'signout',
    controller: ['AuthService', '$state', function (authService, $state) {
      authService.signOut().then(function () {
        $state.go('login');
      });
    }]
  }).state('accountList.addAccount', {
    url: '/edit',
    controller: 'IdentityEditController',
    templateUrl: vw('views/identity/identity-edit.html')
  }).state('accountList.editAccount', {
    url: '/edit/:accountId',
    controller: 'IdentityEditController',
    templateUrl: vw('views/identity/identity-edit.html')
  });
}).factory('HrefService', [function () {
  return function ($state) {
    return function (state, params, noExtend) {
      if (noExtend) {
        return $state.href(state, params);
      }
      return $state.href(state, angular.extend($state.params, params));
    };
  }
}]);
