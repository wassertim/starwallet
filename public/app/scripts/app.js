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

      $stateProvider.state('accountList', {
        url: '/u:userId/account',
        controller: 'AccountController',
        templateUrl: vw('views/account/account-list.html')
      }).state('home2', {
        url: '',
        controller: 'AccountController',
        templateUrl: vw('views/account/account-list.html')
      }).state('accountDisplay', {
        url: '/u:userId/account/:account',
        controller: 'AccountDisplayController',
        templateUrl: vw('views/account/account-display.html')
      });
    });
