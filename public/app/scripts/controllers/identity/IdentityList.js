(function (app) {
  IdentityListController.$inject = ['$scope', 'IdentityService', '$stateParams', '$state', 'AccountService'];
  function IdentityListController($scope, identityService, $stateParams, $state, accountService) {
    $scope.vm = this;
    this.$state = $state;
    $scope.href = function(state, params){
      return $state.href(state, angular.extend($stateParams, params));
    };
    this.accountId = +$state.params.accountId;
    this.identityService = identityService;
    this.accountService = accountService;
    this.list($stateParams['userId']);
    var that = this;
    that.responsiveClasses = "";
    this.getResponsiveClasses();
    $scope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
      that.setActive(+toParams.accountId);
      that.getResponsiveClasses(+toParams.accountId);
    });
  }

  IdentityListController.prototype = {
    list: function(userId){
      var that = this;
      this.identityService.list(userId).then(function(items){
        that.items = items;
        that.setActive(+that.$state.params.accountId);
        return items;
      }).then(function(items){
        _.forEach(items, function(item){
          //TODO: Cache this info on the server. Temporary disabled
          /*that.accountService.getByIdentityId(item.id).then(function(accountInfo){
            item.activeCouponsCount = _.filter(accountInfo.coupons, 'isActive').length;
          });*/
        });
      });
    },
    getResponsiveClasses: function(accountId){
      if (accountId) {
        this.responsiveClasses = " visible-lg visible-md visible-sm";
      } else {
        this.responsiveClasses = "";
      }
    },
    setActive: function(id){
      if (this.items) {
        _.forEach(this.items, function (item) {
          item.isActive = item.id === id;
        });
      }
    }
  };
  app.controller('IdentityListController', IdentityListController);
  return IdentityListController;
}(angular.module('starbucks')));