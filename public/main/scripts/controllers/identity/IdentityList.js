'use strict';
(function (app) {
  var dependencies = ['$scope', 'IdentityService', 'AccountService', '$stateParams', '$state', '$timeout'];
  function IdentityListController($scope, identityService, accountService, $stateParams, $state, $timeout) {
    $scope.vm = this;
    this.$state = $state;
    $scope.href = $state.href;
    this.accountService = accountService;
    this.accountId = +$state.params.accountId;
    this.identityService = identityService;
    this.accountService = accountService;
    this.params = $state.params;
    this.list($stateParams.userId);
    var that = this;
    that.responsiveClasses = '';
    this.getResponsiveClasses();
    this.$timeout = $timeout;
    //that.refresh();
    $scope.$on('$stateChangeSuccess', function (event, toState, toParams) {
      that.setActive(+toParams.accountId);
      that.getResponsiveClasses(+toParams.accountId);
      that.list($stateParams.userId);
    });
  }

  IdentityListController.prototype = {
    refresh: function() {
      var that = this;
      if (this.items && this.items.length) {
        that.isLoading = true;
        that.accountService.refreshAll().then(function(){
          that.isLoading = false;
          that.list(that.$state.params.userId);
        });
      }
    },
    list: function(userId){
      var that = this;
      this.identityService.list(userId).then(function(items){
        that.items = items;
        that.setActive(+that.$state.params.accountId);
        return items;
      });
    },
    getResponsiveClasses: function(accountId){
      if (accountId) {
        this.responsiveClasses = ' visible-lg visible-md visible-sm';
      } else {
        this.responsiveClasses = '';
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
  IdentityListController.$inject = dependencies;
  app.controller('IdentityListController', IdentityListController);
  return IdentityListController;
}(angular.module('starwallet')));