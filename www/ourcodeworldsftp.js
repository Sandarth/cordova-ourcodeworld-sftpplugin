/*global cordova, module*/

module.exports = {
  createSFTPClient: function () {
    //This variable will be private

    return {
      connect: function (host, username, password, port, identity, success, error) {
        var _settings = {
          host: null,
          username: null,
          password: null,
          path: '/root',
          port: '22',
          identity: null,
          known_hosts: null,
        };
        if (typeof host === 'undefined') {
          _settings.host = null;
        } else {
          _settings.host = host;
        }

        if (typeof username === 'undefined') {
          _settings.username = null;
        } else {
          _settings.username = username;
        }

        if (typeof password === 'undefined') {
          _settings.password = null;
        } else {
          _settings.password = password;
        }

        if (typeof port === 'undefined') {
          _settings.port = '22';
        } else {
          _settings.port = port.toString();
        }

        if (typeof identity === 'undefined'){
          _settings.identity = null;
        } else {
         _settings.identity = identity; 
        }

        cordova.exec(
          function () {
            success();
          },
          function (err) {
            error(err);
          },
          'OurCodeWorldSFTP',
          'connect',
          [_settings]
        );
      },
      disconnect: function (success, error) {
        cordova.exec(
          function () {
            success();
          },
          function (err) {
            error(err);
          },
          'OurCodeWorldSFTP',
          'disconnect'
        );
      },
      /**
       * Set the global path of the remote connection
       *
       * @default /root
       * @param {type} path
       * @returns {undefined}
       */
      setPath: function (path) {
        if (typeof path === 'undefined') {
          _settings.path = '/root';
        } else {
          _settings.path = path;
        }
      },
      getPath: function () {
        return _settings.path;
      },
      /**
       * Returns a json object with the information of all folders and files of the global path
       *
       * @param {type} success
       * @param {type} error
       * @returns {undefined}
       */
      list: function (directory, success, error) {
        cordova.exec(
          function (data) {
            success(JSON.parse(data));
          },
          function (err) {
            error(err);
          },
          'OurCodeWorldSFTP',
          'list',
          [directory]
        );
      },
      /**
       * List the parent folder of the global path
       *
       * @param {type} success
       * @param {type} error
       * @returns {module.exports.createSFTPClient.ourcodeworldsftpAnonym$0.listParent.path|String}
       */
      listParent: function (success, error) {
        var parentPath = _settings.path.split('/').filter(function (n) {
          return n != undefined;
        });
        var path = 'LAST_FOLDER';
        parentPath.pop();

        if (parentPath.length == 1) {
          return path;
        } else {
          path = parentPath.join('/');
        }

        setPath(path);
        list(success, error);

        return path;
      },
      rename: function (sourcePath, destinationPath, success, error) {
        var datos = {};

        datos.filesource = sourcePath;
        datos.filedestination = destinationPath;

        cordova.exec(
          function (data) {
            try {
              success(JSON.parse(data));
            } catch (e) {
              error(data);
            }
          },
          function (err) {
            error(err);
          },
          'OurCodeWorldSFTP',
          'rename',
          [datos]
        );
      },
      downloadFile: function (sourcePath, destinationPath, success, error) {
        var datos = {};

        datos.filesource = sourcePath;
        datos.filedestination = destinationPath;

        cordova.exec(
          function (data) {
            try {
              success(JSON.parse(data));
            } catch (e) {
              error(data);
            }
          },
          function (err) {
            error(err);
          },
          'OurCodeWorldSFTP',
          'download',
          [datos]
        );
      },
      uploadFile: function (sourcePath, destinationPath, success, error) {
        var datos = {};

        datos.filesource = sourcePath;
        datos.filedestination = destinationPath;

        cordova.exec(
          function (data) {
            try {
              success(JSON.parse(data));
            } catch (e) {
              error(data);
            }
          },
          function (err) {
            error(err);
          },
          'OurCodeWorldSFTP',
          'upload',
          [datos]
        );
      },
      removeFile: function (remotePath, success, error) {
        var datos = {};

        datos.remotepath = remotePath;

        cordova.exec(
          function (data) {
            try {
              success(JSON.parse(data));
            } catch (e) {
              error(data);
            }
          },
          function (err) {
            error(err);
          },
          'OurCodeWorldSFTP',
          'delete',
          [datos]
        );
      },
      removeFolder: function (remotePathToDelete, success, error) {
        var datos = {};

        datos.remotepath = remotePathToDelete;

        cordova.exec(
          function (data) {
            try {
              success(JSON.parse(data));
            } catch (e) {
              error(data);
            }
          },
          function (err) {
            error(err);
          },
          'OurCodeWorldSFTP',
          'dir_delete',
          [datos]
        );
      },
      createFolder: function (remotePathToCreate, success, error) {
        var datos = {};

        datos.remotepath = remotePathToCreate;

        cordova.exec(
          function (data) {
            try {
              success(JSON.parse(data));
            } catch (e) {
              error(data);
            }
          },
          function (err) {
          },
          'OurCodeWorldSFTP',
          'dir_create',
          [datos]
        );
      },
      /**
       * If you need to add a private key to the connection use the addIdentity method
       * null to remove a identity and string to give a path
       *
       * @param {type} filepath
       * @returns {undefined}
       */
      setIdentity: function (filepath) {
        _settings.identity = filepath;
      },
      setKnownHosts: function (filepath) {
        _settings.known_hosts = filepath;
      },
    };
  },
};
