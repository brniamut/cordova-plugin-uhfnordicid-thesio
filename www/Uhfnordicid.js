var exec = cordova.require('cordova/exec');

var Uhfnordicid = function() {
    console.log('Uhfnordicid instanced');
};

Uhfnordicid.prototype.show = function(msg, onSuccess, onError) {
    var errorCallback = function(obj) {
        onError(obj);
    };

    var successCallback = function(obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'Uhfnordicid', 'show', [msg]);
};

if (typeof module != 'undefined' && module.exports) {
    module.exports = Uhfnordicid;
}