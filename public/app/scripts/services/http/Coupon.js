(function (app, routes) {
  CouponService.$inject = ['$http'];
  function CouponService($http) {
    this.$http = $http;
  }

  CouponService.prototype = {
    list: function(userId) {
      return this.$http(routes.Coupon.list(userId)).then(function(response){
        return response.data;
      });
    }
  };
  app.service('CouponService', CouponService);
  return CouponService;
}(angular.module('starwallet'), jsRoutes.controllers));