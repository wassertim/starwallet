'use strict';
angular.module('starbucks', [
  'ngCookies',
  'ngResource',
  'ngSanitize',
  'ngRoute',
  'ui.router'
])
    .config(function ($routeProvider, $stateProvider) {
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
        controller: 'AccountController',
        templateUrl: vw('views/account/account-list.html')
      }).state('accountDisplay', {
        url: '/u:userId/account/:account',
        controller: 'AccountDisplayController',
        templateUrl: vw('views/account/account-display.html')
      }).state('login', {
        url: '/login',
        controller: 'AuthController',
        templateUrl: vw('views/login.html')
      }).state('signOut', {
        url: 'signout',
        controller: ['AuthService', '$state', function (authService, $state) {
          authService.signOut().then(function () {
            $state.go('login');
          });
        }]
      });
    });
