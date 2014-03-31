(function (app) {
  IdentityListController.$inject = ['$scope', 'IdentityService', '$stateParams', '$state', 'AccountService'];
  function IdentityListController($scope, identityService, $stateParams, $state, accountService) {
    $scope.vm = this;

    $scope.href = function(state, params){
      return $state.href(state, angular.extend($stateParams, params));
    };
    this.identityService = identityService;
    this.accountService = accountService;
    this.list($stateParams['userId']);
  }

  IdentityListController.prototype = {
    list: function(userId){
      var that = this;
      this.identityService.list(userId).then(function(list){
        that.list = list;
        return list;
      }).then(function(list){
        _.forEach(list, function(item){
          //TODO: Cache this info on the server
          that.accountService.getByIdentityId(item.id).then(function(accountInfo){
            item.activeCouponsCount = _.filter(accountInfo.coupons, 'isActive').length;
          });
        });
      });
    }
  };
  app.controller('IdentityListController', IdentityListController);
  return IdentityListController;
}(angular.module('starbucks')));