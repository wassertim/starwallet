'use strict';
(function(app){
  AccountService.$inject = ['$http', 'PromiseWrapper'];
  function AccountService($http, promiseWrapper){
    this.$http = $http;
    this.pw = promiseWrapper;
  }

  AccountService.prototype = {
    get: function(accountId) {
      return this.pw.wrap(function(){
        return {
          name: accountId,
          password: 'test'
        }
      });
    },
    list: function(userId) {
      return this.pw.wrap(function(){
        return [
          {id:1,name:'account1'},
          {id:2,name:'account2'}
        ]
      });
    },
    getAccountInfo: function(account) {
      return this.pw.wrap(function(){
        return {
          userName: account,
          starsCount: 5,
          cards: [{
            number: '342354342323'
          }],
          coupons: [{
            number: '342323223434'
          }]
        }
      });
    },
    add: function(accountInfo) {
      return this.pw.wrap(function(){
        return accountInfo.name;
      });
    },
    update: function(accountInfo) {
      return this.pw.wrap(function(){

      });
    },
    remove: function(accountId) {
      return this.pw.wrap(function(){

      });
    }
  };
  app.service('AccountService', AccountService);
  return AccountService;
}(angular.module('starbucks')));
