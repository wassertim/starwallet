(function (app) {
  IdentityDisplayController.$inject = ['$scope', '$stateParams', 'IdentityService', '$state'];
  function IdentityDisplayController($scope, $stateParams, identityService, $state) {
    $scope.vm = this;
    this.identityService = identityService;
    this.getAccountInfo($stateParams['accountId']);
    $scope.href = function(state, params){
      return $state.href(state, angular.extend($stateParams, params));
    };
  }

  IdentityDisplayController.prototype = {
    getAccountInfo: function(accountId) {
      var that = this;
      this.identityService.getAccountInfo(accountId).then(function(accountInfo) {
        that.accountInfo = accountInfo;
      });
    }
  };
  app.controller('IdentityDisplayController', IdentityDisplayController);
  return IdentityDisplayController;
}(angular.module('starbucks')));