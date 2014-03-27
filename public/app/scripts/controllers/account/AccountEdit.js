(function (app) {
  AccountEditController.$inject = ['$scope', '$stateParams', 'AccountService', '$state', '$window'];
  function AccountEditController($scope, $stateParams, accountService, $state, $window) {
    $scope.vm = this;
    this.$window = $window;
    this.accountService = accountService;
    this.$state = $state;
    this.params = $stateParams;
    this.revealPassword = false;
    $scope.href = function(state, params){
      return $state.href(state, angular.extend($stateParams, params));
    };
    this.getForm();
  }

  AccountEditController.prototype = {
    getForm: function(){
      var that = this;
      if (this.params['accountId']) {
        this.accountService.get(this.params['accountId']).then(function(accountInfo){
          that.account = accountInfo;
        })
      }
    },
    save: function(account){
      var that = this;
      if (!this.params['accountId']) {
        this.accountService.add(account).then(function(accountId){
          that.$state.go('editAccount', angular.extend(that.params, {accountName: accountId}))
        });
      } else {
        this.accountService.update(account).then(function(){

        });
      }
    },
    remove: function(accountId){
      var that = this;
      if (this.$window.confirm("Do you really want to delete this account?")) {
        this.accountService.remove(accountId).then(function () {
          that.$state.go('accountList', that.params);
        });
      }
    }
  };
  app.controller('AccountEditController', AccountEditController);
  return AccountEditController;
}(angular.module('starbucks')));