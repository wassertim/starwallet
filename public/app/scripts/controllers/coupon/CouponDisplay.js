(function (app) {
    CouponDisplayController.$inject = ['$scope', 'CouponService', '$state'];
    function CouponDisplayController($scope, couponService, $state) {
      $scope.vm = this;
      couponService.get($state.params.number).then(function(coupon){
        this.coupon = coupon;
      });
    }

    CouponDisplayController.prototype = {

    };
    app.controller('CouponDisplayController', CouponDisplayController);
    return CouponDisplayController;
}(angular.module('starwallet')));