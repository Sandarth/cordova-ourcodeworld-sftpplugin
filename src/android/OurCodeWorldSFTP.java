package com.ourcodeworld.plugins.sftp;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import java.util.Arrays;

public class OurCodeWorldSFTP extends CordovaPlugin {
    private static final String ACTION_CONNECT = "connect";
    private static final String ACTION_DISCONNECT = "disconnect";
    private static final String ACTION_LIST = "list";
    private static final String ACTION_DOWNLOAD = "download";
    private static final String ACTION_RENAME = "rename";
    private static final String ACTION_UPLOAD = "upload";
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_DIR_CREATE = "dir_create";
    private static final String ACTION_DIR_DELETE = "dir_delete";

    private static final String[] ACTIONS = new String[] {ACTION_CONNECT, ACTION_DISCONNECT, ACTION_LIST, ACTION_DOWNLOAD, ACTION_RENAME, ACTION_UPLOAD, ACTION_DELETE, ACTION_DIR_CREATE, ACTION_DIR_DELETE};

    private JSch jsch               = new JSch();
    private Session session         = null;
    private ChannelSftp sftpChannel = null;

    private void connect(JSONArray data, CallbackContext callbackContext) { 
        try {
            final JSONObject arg_object = data.getJSONObject(0);
            final String hostname = arg_object.getString("host");
            final String login =  arg_object.getString("username");
            final String password =  arg_object.getString("password");
            final String directory =  arg_object.getString("path");
            final String port =  arg_object.getString("port");

            this.session = this.jsch.getSession(login, hostname, Integer.parseInt(port));

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            this.session.setConfig(config);

            if (!arg_object.isNull("identity")){
                this.jsch.addIdentity(arg_object.getString("identity"));
            }else{
                this.session.setPassword(password);
            }
            
            this.session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();

            this.sftpChannel = (ChannelSftp) channel;
            callbackContext.success();
        } catch (Exception e) {
            callbackContext.error(e.toString());
        }
    }

    private void disconnect(CallbackContext callbackContext) {
        try {
            if(this.sftpChannel != null){
                this.sftpChannel.exit();
            }
            this.session.disconnect();
            callbackContext.success("Disconnect OK.");
        } catch (Exception e) {
            callbackContext.error(e.toString());
        }
    }

    private void ls(String directory, CallbackContext callbackContext){
        try {
            this.sftpChannel.cd(directory);

            JSONArray contenedor = new JSONArray();

            @SuppressWarnings("unchecked")

            java.util.Vector<LsEntry> flLst = this.sftpChannel.ls(directory);

            final int i = flLst.size();

            for(int j = 0; j<i ;j++){
                JSONObject item = new JSONObject();
                LsEntry entry = flLst.get(j);
                SftpATTRS attr = entry.getAttrs();

                item.put("name", entry.getFilename());
                item.put("filepath", directory + "/" + entry.getFilename());
                item.put("isDir", attr.isDir());
                item.put("isLink", attr.isLink());
                item.put("size",attr.getSize());
                item.put("permissions",attr.getPermissions());
                item.put("permissions_string",attr.getPermissionsString());
                item.put("longname",entry.toString());

                contenedor.put(item);
            }
            callbackContext.success(contenedor.toString());
        } catch (Exception e) {
            callbackContext.error(e.toString());
        }
    }

    private void download(JSONArray data, CallbackContext callbackContext) { 
        try {
            final JSONObject arg_object = data.getJSONObject(0);

            this.sftpChannel.get(arg_object.getString("filesource") , arg_object.getString("filedestination"),new progressMonitor(callbackContext));

            JSONObject item = new JSONObject();
            item.put("finished", true);
            item.put("success", true);

            callbackContext.success(item.toString());

        } catch (Exception e) {
            callbackContext.error(e.toString());
        }
    }

    private void upload(JSONArray data, CallbackContext callbackContext) { 
        try {
            final JSONObject arg_object = data.getJSONObject(0);

            this.sftpChannel.put(arg_object.getString("filesource") , arg_object.getString("filedestination"),new progressMonitor(callbackContext));

            JSONObject item = new JSONObject();
            item.put("finished", true);
            item.put("success", true);

            callbackContext.success(item.toString());

        } catch (Exception e) {
            callbackContext.error(e.toString());
        }
    }

    private void rename(JSONArray data, CallbackContext callbackContext) { 
        try {
            final JSONObject arg_object = data.getJSONObject(0);

            this.sftpChannel.rename(arg_object.getString("filesource") , arg_object.getString("filedestination"));

            JSONObject renamed = new JSONObject();
            renamed.put("renamed", true);
                            
            callbackContext.success(renamed.toString());

        } catch (Exception e) {
            callbackContext.error(e.toString());
        }
    }

    private void delete(JSONArray data, CallbackContext callbackContext) { 
        try {
            final JSONObject arg_object = data.getJSONObject(0);

            this.sftpChannel.rm(arg_object.getString("remotepath"));

            JSONObject deleted = new JSONObject();
            deleted.put("deleted", true);
                            
            callbackContext.success(deleted.toString());
        } catch (Exception e) {
            callbackContext.error(e.toString());
        }
    }

    private void dirDelete(JSONArray data, CallbackContext callbackContext) { 
        try {
            final JSONObject arg_object = data.getJSONObject(0);

            this.sftpChannel.rmdir(arg_object.getString("remotepath"));

            JSONObject deleted = new JSONObject();
            deleted.put("deleted", true);
                            
            callbackContext.success(deleted.toString());
        } catch (Exception e) {
            callbackContext.error(e.toString());
        }
    }

    private void dirCreate(JSONArray data, CallbackContext callbackContext) { 
        try {
            final JSONObject arg_object = data.getJSONObject(0);

            this.sftpChannel.mkdir(arg_object.getString("remotepath"));

            JSONObject created = new JSONObject();
            created.put("created", true);
                            
            callbackContext.success(created.toString());
        } catch (Exception e) {
            callbackContext.error(e.toString());
        }
    }

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        final CallbackContext callbacks = callbackContext;

        if (!Arrays.asList(ACTIONS).contains(action)) return false;

        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    if (ACTION_CONNECT.equals(action)){
                        connect(data, callbackContext);
                    } else if (ACTION_DISCONNECT.equals(action)){
                        disconnect(callbackContext);
                    } else if (ACTION_LIST.equals(action)) {
                        ls(data.getString(0), callbackContext);
                    } else if(ACTION_DOWNLOAD.equals(action)){
                        download(data, callbackContext);
                    } else if(ACTION_UPLOAD.equals(action)){
                        upload(data, callbackContext);
                    } else if(ACTION_RENAME.equals(action)){
                        rename(data, callbackContext);
                    } else if(ACTION_DELETE.equals(action)){
                        delete(data, callbackContext);
                    } else if(ACTION_DIR_DELETE.equals(action)){
                        dirDelete(data, callbackContext);
                    } else if(ACTION_DIR_CREATE.equals(action)){
                        dirCreate(data, callbackContext);
                    }
                    
                } catch (Exception e) {
                    callbacks.error(e.getMessage().toString());
                    e.printStackTrace();  
                } 
            }
        });
        return true;
    }
}
