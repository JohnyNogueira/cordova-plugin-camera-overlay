 
module.exports = {
    greet: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "PluginName", "new_activity", []);
    }
};