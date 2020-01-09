var exec = cordova.require('cordova/exec');

var Uhfnordicid = function() {
    console.log('Uhfnordicid instanced');
};

Uhfnordicid.prototype.scan = function(epc, waittime, onSuccess, onError) {
    var errorCallback = function(obj) {
        onError(obj);
    };

    var successCallback = function(obj) {
        onSuccess(obj);
    };

    exec(successCallback, errorCallback, 'Uhfnordicid', 'scan', [epc, waittime]);
};

if (typeof module != 'undefined' && module.exports) {
    module.exports = Uhfnordicid;
}
