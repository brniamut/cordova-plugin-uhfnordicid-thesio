var exec = cordova.require('cordova/exec');

var Uhfnordicid = function() {
    console.log('Uhfnordicid instanced');
};

Uhfnordicid.prototype.scan = function(epc, waittime, txpower, onSuccess, onError) {
    var errorCallback = function(obj) {
        onError(obj);
    };

    var successCallback = function(obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'Uhfnordicid', 'scan', [epc, waittime, txpower]);
};

if (typeof module != 'undefined' && module.exports) {
    module.exports = Uhfnordicid;
}
