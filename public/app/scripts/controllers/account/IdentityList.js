(function (app) {
  IdentityListController.$inject = ['$scope', 'IdentityService', '$stateParams', '$state'];
  function IdentityListController($scope, identityService, $stateParams, $state) {
    $scope.vm = this;
    $scope.href = function(state, params){
      return $state.href(state, angular.extend($stateParams, params));
    };
    this.identityService = identityService;
    this.list($stateParams['userId']);
  }

  IdentityListController.prototype = {
    list: function(userId){
      var that = this;
      this.identityService.list(userId).then(function(list){
        that.list = list;
      });
    }
  };
  app.controller('IdentityListController', IdentityListController);
  return IdentityListController;
}(angular.module('starbucks')));