(function (app) {
  PromiseWrapper.$inject = ['$q'];
  function PromiseWrapper($q) {
    this.$q = $q;
  }
  PromiseWrapper.prototype.wrap = function (fn) {
    var deferred = this.$q.defer();
    deferred.resolve(fn());
    return deferred.promise;
  };

  app.service('PromiseWrapper', PromiseWrapper);
  return PromiseWrapper;
}(angular.module('starbucks')));