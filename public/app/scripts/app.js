'use strict';
angular.module('starwallet', [
  'ngCookies',
  'ngResource',
  'ngSanitize',
  'ngRoute',
  'angularMoment',
  'ui.router',
  'ui.router.stateHelper'
]).config(['$routeProvider', '$stateProvider', 'stateHelperProvider', function ($routeProvider, $stateProvider, stateHelperProvider) {
  function vw(url) {
    return '../' + url;
  }

  $stateProvider.state('home', {
    url: '',
    controller: 'MainController'
  }).state('home2', {
    url: '/',
    controller: 'MainController'
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
  });;
  stateHelperProvider.setNestedState({
    name: 'withNav',
    url: '/u:userId',
    controller: 'NavController',
    templateUrl: vw('views/withNav.html'),
    children: [
      {
        name: 'cards',
        url: '/cards',
        controller: 'CardListController',
        templateUrl: vw('views/card/card-list.html')
      },
      {
        name: 'coupons',
        url: '/coupons',
        controller: 'CouponListController',
        templateUrl: vw('views/coupon/coupon-list.html')
      },
      {
        name: 'couponDisplay',
        url: '/coupons/:number',
        controller: 'CouponDisplayController',
        templateUrl: vw('views/coupon/coupon-display.html')
      },
      {
        name: 'cardDisplay',
        url: '/cards/:number',
        controller: 'CardDisplayController',
        templateUrl: vw('views/card/card-display.html')
      },
      {
        name: 'accountList',
        url: '/account',
        controller: 'IdentityListController',
        templateUrl: vw('views/identity/identity-list.html'),
        children: [
          {
            name: 'addAccount',
            url: '/edit',
            controller: 'IdentityEditController',
            templateUrl: vw('views/identity/identity-edit.html')
          },
          {
            name: 'editAccount',
            url: '/edit/:accountId',
            controller: 'IdentityEditController',
            templateUrl: vw('views/identity/identity-edit.html')
          },
          {
            name: 'accountDisplay',
            url: '/display/:accountId',
            controller: 'AccountDisplayController',
            templateUrl: vw('views/account/account-display.html')
          }
        ]
      }
    ]
  });
}]);
