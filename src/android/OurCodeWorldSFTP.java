package com.ourcodeworld.plugins.sftp;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;

public class OurCodeWorldSFTP extends CordovaPlugin {
    private static final String ACTION_LIST = "list";
    private static final String ACTION_DOWNLOAD = "download";
    private static final String ACTION_UPLOAD = "upload";
    private static final String ACTION_DELETE = "delete";

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        final JSONObject arg_object = data.getJSONObject(0);
        final String hostname = arg_object.getString("host");
        final String login =  arg_object.getString("username");
        final String password =  arg_object.getString("password");
        final String directory =  arg_object.getString("path");
        final String port =  arg_object.getString("port");
        final CallbackContext callbacks = callbackContext;


        if (ACTION_LIST.equals(action)) {
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        java.util.Properties config = new java.util.Properties();
                        config.put("StrictHostKeyChecking", "no");

                        JSch ssh = new JSch();
                        
                        if (!arg_object.isNull("identity")){
                            ssh.addIdentity(arg_object.getString("identity"));
                        }
                        
                        Session session = ssh.getSession(login, hostname, Integer.parseInt(port));
                        session.setConfig(config);
                        session.setPassword(password);
                        session.connect();
                        Channel channel = session.openChannel("sftp");
                        channel.connect();

                        ChannelSftp sftp = (ChannelSftp) channel;

                        sftp.cd(directory);

                        JSONArray contenedor = new JSONArray();

                        @SuppressWarnings("unchecked")

                        java.util.Vector<LsEntry> flLst = sftp.ls(directory);

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

                        channel.disconnect();
                        session.disconnect();
                        PluginResult result = new PluginResult(PluginResult.Status.OK, contenedor.toString());
                        result.setKeepCallback(true);
                        callbacks.sendPluginResult(result);
                    } catch (JSchException e) {
                        callbacks.error(e.getMessage().toString());
                        e.printStackTrace();  
                    } catch (SftpException e) {
                        callbacks.error(e.getMessage().toString());
                        e.printStackTrace();
                    } catch (JSONException e) {
                            callbacks.error(e.getMessage().toString());
                            e.printStackTrace();
                    }
                }
            });

            PluginResult pluginResult = new  PluginResult(PluginResult.Status.NO_RESULT); 
            pluginResult.setKeepCallback(true); 
            return true;
            
        }else if(ACTION_DOWNLOAD.equals(action)){
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        java.util.Properties config = new java.util.Properties();
                        config.put("StrictHostKeyChecking", "no");

                        JSch ssh = new JSch();
                        if (!arg_object.isNull("identity")){
                            ssh.addIdentity(arg_object.getString("identity"));
                        }
                        Session session = ssh.getSession(login, hostname, Integer.parseInt(port));
                        session.setConfig(config);
                        session.setPassword(password);
                        session.connect();
                        Channel channel = session.openChannel("sftp");
                        channel.connect();

                        ChannelSftp sftp = (ChannelSftp) channel;

                        sftp.cd(directory);

                        sftp.get(arg_object.getString("filesource") , arg_object.getString("filedestination"),new progressMonitor(callbacks));

                        Boolean success = true;

                        if (success){
                            JSONObject item = new JSONObject();
                            item.put("finished", true);
                            item.put("success", true);
                            
                            PluginResult result = new PluginResult(PluginResult.Status.OK, item.toString());
                            result.setKeepCallback(true);
                            callbacks.sendPluginResult(result);
                        }
 
                        channel.disconnect();
                        session.disconnect();
                    } catch (JSchException e) {
                        callbacks.error(e.getMessage().toString());
                        e.printStackTrace();  
                    } catch (SftpException e) {
                        callbacks.error(e.getMessage().toString());
                        e.printStackTrace();
                    } catch (JSONException e) {
                        callbacks.error(e.getMessage().toString());
                        e.printStackTrace();
                    }
                }
            });

            PluginResult pluginResult = new  PluginResult(PluginResult.Status.NO_RESULT); 
            pluginResult.setKeepCallback(true); 
            return true;
        }else if(ACTION_UPLOAD.equals(action)){
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        java.util.Properties config = new java.util.Properties();
                        config.put("StrictHostKeyChecking", "no");

                        JSch ssh = new JSch();
                        if (!arg_object.isNull("identity")){
                            ssh.addIdentity(arg_object.getString("identity"));
                        }
                        Session session = ssh.getSession(login, hostname, Integer.parseInt(port));
                        session.setConfig(config);
                        session.setPassword(password);
                        session.connect();
                        Channel channel = session.openChannel("sftp");
                        channel.connect();

                        ChannelSftp sftp = (ChannelSftp) channel;

                        sftp.cd(directory);

                        sftp.put(arg_object.getString("filesource") , arg_object.getString("filedestination"),new progressMonitor(callbacks));

                        Boolean success = true;

                        if (success){
                            JSONObject item = new JSONObject();
                            item.put("finished", true);
                            item.put("success", true);
                            
                            PluginResult result = new PluginResult(PluginResult.Status.OK, item.toString());
                            result.setKeepCallback(true);
                            callbacks.sendPluginResult(result);
                        }
 
                        channel.disconnect();
                        session.disconnect();
                    } catch (JSchException e) {
                        callbacks.error(e.getMessage().toString());
                        e.printStackTrace();  
                    } catch (SftpException e) {
                        callbacks.error(e.getMessage().toString());
                        e.printStackTrace();
                    } catch (JSONException e) {
                        callbacks.error(e.getMessage().toString());
                        e.printStackTrace();
                    }
                }
            });

            return true;
        }else if(ACTION_DELETE.equals(action)){
            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        java.util.Properties config = new java.util.Properties();
                        config.put("StrictHostKeyChecking", "no");

                        JSch ssh = new JSch();
                        if (!arg_object.isNull("identity")){
                            ssh.addIdentity(arg_object.getString("identity"));
                        }
                        Session session = ssh.getSession(login, hostname, Integer.parseInt(port));
                        session.setConfig(config);
                        session.setPassword(password);
                        session.connect();
                        Channel channel = session.openChannel("sftp");
                        channel.connect();

                        ChannelSftp sftp = (ChannelSftp) channel;

                        sftp.cd(directory);

                        sftp.rm(arg_object.getString("remotepath"));

                        Boolean success = true;

                        if (success){
                            callbacks.success("Todo en orden, ELIMINADO");
                        }
 
                        channel.disconnect();
                        session.disconnect();
                    } catch (JSchException e) {
                        callbacks.error(e.getMessage().toString());
                        e.printStackTrace();  
                    } catch (SftpException e) {
                        callbacks.error(e.getMessage().toString());
                        e.printStackTrace();
                    } catch (JSONException e) {
                        callbacks.error(e.getMessage().toString());
                        e.printStackTrace();
                    }
                }
            });

            return true;
        }else {
            return false;
        }
    }
}
