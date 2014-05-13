'use strict';
(function (app) {
  var dependencies = ['$scope', 'CouponService', '$state'];

  function CouponListController($scope, couponService, $state) {
    $scope.vm = this;
    var that = this;
    couponService.list($state.params.userId).then(function (coupons) {
      that.coupons = coupons;
    });
  }

  CouponListController.prototype = {

  };
  CouponListController.$inject = dependencies;
  app.controller('CouponListController', CouponListController);
  return CouponListController;
}(angular.module('starwallet')));