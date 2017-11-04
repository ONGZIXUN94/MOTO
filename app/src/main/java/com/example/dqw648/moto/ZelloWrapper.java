package com.example.dqw648.moto;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zello.sdk.AppState;
import com.zello.sdk.Contact;
import com.zello.sdk.ContactStatus;
import com.zello.sdk.ContactType;
import com.zello.sdk.Contacts;
import com.zello.sdk.Status;
import com.zello.sdk.Zello;
import com.zello.sdk.Tab;
import com.zello.sdk.Theme;
/**
 * Created by PMXT36 on 10/27/2017.
 */

public class ZelloWrapper {

    static String tag = "zello";
    static boolean resetOnce = false;

    public static void init(AppCompatActivity activity){
        Zello.getInstance().configure("net.loudtalks", activity);
        resetAllChannel();
    }

    public static void pttStart(){
        Zello.getInstance().beginMessage();
        Log.d(tag, "start ptt call\r\n");
    }

    public static void pttStop(){
        Zello.getInstance().endMessage();
        Log.d(tag, "stop ptt call\r\n");
    }

    /**
     * Configure zello call before user can begin message
     *
     * @param  mode  1: private call, 2: group call
     * @param  name the user name if it is a private call, channel name if group call
     * @return      void
     */
    public static void configureCall(int mode, String name) throws Exception{

        switch (mode){
            case 1:
                Zello.getInstance().setSelectedUserOrGateway(name);
                Log.d(tag, String.format("configure private call to %s\r\n", name));
                break;
            case 2:
                Zello.getInstance().connectChannel(name);
                Zello.getInstance().setSelectedChannelOrGroup(name);
                Log.d(tag, String.format("configure group call to %s\r\n", name));
                break;
            default:
                throw new Exception(String.format("Mode %d is not handled! ", mode));
        }
    }

    public static String getThisUserName(){
        AppState _appState = new AppState();
        Zello.getInstance().getAppState(_appState);
        String currentUser = _appState.getUsername();
        Log.d(tag, String.format("query current user name = %s\r\n", currentUser));
        return currentUser;
    }

    // TODO: do we really need this????
    private static void resetAllChannel(){
        if (!resetOnce){
            /* TODO: loop through channel list and disconnect to all, this should be only called
            once during app lifecycle
            Zello.getInstance().disconnectChannel(loopChannelNames);
             */
            resetOnce = true;
        }

    }

    // Hidden functions, make it public if needed
    private static void selectContact(){
        Zello.getInstance().selectContact("Select a contact", new Tab[]{Tab.RECENTS,
                Tab.USERS, Tab.CHANNELS}, Tab.RECENTS, Theme.DARK);
    }

    private static Contacts myContacts = null;

    public static boolean checkTrigerToJoinGroup(){
        if (myContacts == null){
            // refresh contacts once
            myContacts = Zello.getInstance().getContacts();
        }

        for (int i = 0; i < myContacts.getCount(); i++){
            Contact curContact = myContacts.getItem(i);
            ContactType contactType = curContact.getType();

            if (contactType == ContactType.USER){
                // String name = curContact.getDisplayName();
                String statusMessage = curContact.getStatusMessage();
                // Log.d(tag, String.format("%d %s %s \r\n", i, name, statusMessage));
                if(statusMessage != null && statusMessage.contains("JoinThisF-ingGroup")){
                    return true;
                }
            }
            else{
                // do nothing
            }
        }
        // Log.d(tag, "get contacts done");
        return false;
    }

    public static void setStatusText(String text){
        Zello.getInstance().setStatusMessage(text);
    }

    public static void setStatus(Status stat){
        Zello.getInstance().setStatus(stat);
    }

}
