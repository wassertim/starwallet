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
    that.refresh();
    $scope.$on('$stateChangeSuccess', function (event, toState, toParams) {
      that.setActive(+toParams.accountId);
      that.getResponsiveClasses(+toParams.accountId);
      that.list($stateParams.userId);
    });
  }

  IdentityListController.prototype = {
    refresh: function(){
      var that = this;
      if (this.items && this.items.length) {
        //TODO: Rewrite to one server call
        _.forEach(this.items, function (item) {
          if ((new Date() - new Date(item.lastUpdate)) > 120000) {
            that.accountService.getByIdentityId(item.id, true).then(function (account) {
              item.lastUpdate = new Date();
              item.starsCount = account.starsCount;
              item.activeCouponsCount = account.activeCouponsCount;
            });
          }
        });
      }
      this.$timeout(function(){
        that.refresh();
      }, 120000);
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