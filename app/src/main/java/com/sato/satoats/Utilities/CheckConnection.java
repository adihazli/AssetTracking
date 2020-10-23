package com.sato.satoats.Utilities;

import java.io.IOException;

public class CheckConnection {
    MssqlConnection conn;

    //check if there is internet by pinging
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    //check if there is connection with server
    public boolean checkServer(String uname,String upass,String uport,String udB,String ipAdd){
        conn = new MssqlConnection();
        boolean result = false;
        result = conn.checkSettingCon(uname,upass,uport,udB,ipAdd);
        return result;
    }


}
