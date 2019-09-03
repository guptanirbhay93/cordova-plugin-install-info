cordova.define("com.progcap.install.InstallInfo", function(
    require,
    exports,
    module
) {
    var installInfo = {};

    installInfo.startConnection = function(successCallback, failureCallback) {
        cordova.exec(
            successCallback,
            failureCallback,
            "InstallInfo",
            "startConnection",
            []
        );
    };
    installInfo.getInstallReferrer = function(
        successCallback,
        failureCallback
    ) {
        cordova.exec(
            successCallback,
            failureCallback,
            "InstallInfo",
            "getInstallReferrer",
            []
        );
    };

    installInfo.endConnection = function(successCallback, failureCallback) {
        cordova.exec(
            successCallback,
            failureCallback,
            "InstallInfo",
            "endConnection",
            []
        );
    };

    module.exports = installInfo;
});
