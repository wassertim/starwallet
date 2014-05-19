'use strict';
(function (app) {
  var dependencies = ['$q'];
  function PromiseWrapper($q) {
    this.$q = $q;
  }

  PromiseWrapper.prototype = {
    wrap: function (fn) {
      var deferred = this.$q.defer();
      deferred.resolve(fn());
      return deferred.promise;
    }
  };
  PromiseWrapper.$inject = dependencies;
  app.service('PromiseWrapper', PromiseWrapper);
  return PromiseWrapper;
}(angular.module('starwallet')));