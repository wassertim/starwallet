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

    $scope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
      that.setActive(+toParams.accountId);
    });
  }

  IdentityListController.prototype = {
    list: function(userId){
      var that = this;
      this.identityService.list(userId).then(function(list){
        that.list = list;
        that.setActive(+that.$state.params.accountId);
        return list;
      }).then(function(list){
        _.forEach(list, function(item){
          //TODO: Cache this info on the server
          that.accountService.getByIdentityId(item.id).then(function(accountInfo){
            item.activeCouponsCount = _.filter(accountInfo.coupons, 'isActive').length;
          });
        });
      });
    },
    setActive: function(id){
      _.forEach(this.list, function(item){
          item.isActive = item.id === id;
      });
    }
  };
  app.controller('IdentityListController', IdentityListController);
  return IdentityListController;
}(angular.module('starbucks')));