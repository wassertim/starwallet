(function (app) {
  IdentityEditController.$inject = ['$scope', '$stateParams', 'IdentityService', '$state', '$window'];
  function IdentityEditController($scope, $stateParams, identityService, $state, $window) {
    $scope.vm = this;
    this.$window = $window;
    this.identityService = identityService;
    this.$state = $state;
    this.params = $stateParams;

    this.revealPassword = false;
    $scope.href = $state.href;
    this.isLoading = false;
    this.getForm();
  }

  IdentityEditController.prototype = {
    getForm: function(){
      var that = this;
      if (this.params['accountId']) {
        this.identityService.get(this.params['accountId'], this.params['userId']).then(function(accountInfo){
          that.account = accountInfo;
        })
      }
    },
    save: function(account){
      var that = this;
      that.alert = undefined;
      this.isLoading = true;
      if (!account.id) {
        account.id = 0;
        this.identityService.add(account, this.params['userId']).then(function(accountId){
          that.isLoading = false;
          that.$state.go('accountList.editAccount', angular.extend(that.params, {accountId: accountId}))
        }, function(){
          that.isLoading = false;
          that.alert = {
            message: 'Authentication error'
          };
        });
      } else {
        this.identityService.update(account, this.params['userId']).then(function(){
          that.isLoading = false;
        });
      }
    },
    remove: function(accountId){
      var that = this;
      if (this.$window.confirm("Do you really want to delete this account?")) {
        this.identityService.remove(accountId, this.params['userId']).then(function () {
          that.$state.go('accountList', that.params);
        });
      }
    }
  };
  app.controller('IdentityEditController', IdentityEditController);
  return IdentityEditController;
}(angular.module('starbucks')));