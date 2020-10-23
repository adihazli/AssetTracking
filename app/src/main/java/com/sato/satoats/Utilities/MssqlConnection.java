package com.sato.satoats.Utilities;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class MssqlConnection {

    Connection con;

//    String ip = "192.168.0.122";
//    String strport = "50321";
//    String db = "ATSNetR03";
//    String un = "sa";
//    String pass = "sa123";

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////                                        Connection                                                                     ///////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean checkSettingCon(String username,String upass,String uport,String udb,String ipadd ){
        boolean result = false;

        try
        {
            con = CONN(username, upass,uport, udb, ipadd); // Connect to database
            if (con == null)
            {
                result = false;
            }
            else
            {
                result = true;
                con.close();
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
        return result;
    }

    @SuppressLint("NewApi")
    public Connection CONN(String user, String password,String svport, String database, String server) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        String connection = null;
        connection = connectionURL(user, password, svport, database, server);

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
//            ConnURL = "jdbc:jtds:sqlserver://" + server +":" + svport +"/" + database + ";user=" + user+ ";password=" + password + ";";
//            ConnURL = "jdbc:jtds:sqlserver://" + server +";instance="+ svport +";"+ database + ";user="+ user+ ";password="+ password + ";integratedSecurity=true;";

            ConnURL = connection;
            DriverManager.setLoginTimeout(30);
            conn = DriverManager.getConnection(ConnURL);


        }catch (SQLException se)
        {
            Log.e("error here 1 : ", se.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            Log.e("error here 2 :" , e.getMessage());
        }
        catch (Exception e)
        {
            Log.e("error here 3 : ", e.getMessage());
        }

        return conn;
    }


    public boolean testCon(String username,String upass,String uport,String udb,String ipadd ){
        boolean result = false;

        try {
            con = CONN(username, upass,uport, udb, ipadd); // Connect to database
            if (con == null) {
                result = false;
            }
            else {
                result = true;
                con.close();
            }
        } catch (Exception ex) { }
        return result;
    }


    public String connectionURL(String username,String upass,String uport,String udb,String ipadd){
        String conn = " ";
        Boolean checkNum;

        CommonFunction commonFunction = new CommonFunction();
        checkNum = commonFunction.CheckStringIsNumber(uport);
        if(checkNum){
            System.out.println("its a Port No");
            conn = "jdbc:jtds:sqlserver://" + ipadd +":" + uport +"/" + udb + ";user=" + username+ ";password=" + upass + ";";
        } else {
            System.out.println("its an Instant");
            conn = "jdbc:jtds:sqlserver://" + ipadd +";instance="+ uport +";"+ udb + ";user="+ username+ ";password="+ upass + ";integratedSecurity=true;";
          //  conn = "jdbc:jtds:sqlserver://SAMET0289;instance=SQLEXPRESS;ATSNetR03;user=sa;password=sa123;integratedSecurity=true;";

        }

        return  conn;
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////                                       Query Function                                                                   ///////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public ArrayList<Object> getDBData(String query,String username,String upass,String uport,String udb,String ipadd){

        ArrayList<String> columname = new ArrayList<>();
        String getitem;
        ArrayList<Object> Ddata = new ArrayList<>();

        try {
            con = CONN(username, upass,uport, udb, ipadd); // Connect to database
            if (con == null) {
                Log.i("Error :", "Please enter correct username and password!");
            } else {
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                ResultSetMetaData metaData = rs.getMetaData();

                for (int i = 0;i<metaData.getColumnCount();i++){
                    columname.add(metaData.getColumnName(i+1));
                }

                while((rs.next())) {
                    HashMap<String, String> data = new HashMap<>();
                    for (int x = 0;x<metaData.getColumnCount();x++){
                        //check if data empty or not.
                        getitem = rs.getString(x+1 );
                        if(getitem == null||getitem.isEmpty()){
                            getitem = "";
                        }else {
                            getitem = rs.getString(x+1 ).trim();
                        }
                        data.put(columname.get(x),getitem.trim());
                    }
                    Ddata.add(data);
                }

            }con.close();
        } catch (Exception ex) {
            Log.i("Exception",ex.getMessage());
            System.out.println("Error :" + ex.getMessage());
            HashMap<String, String> data = new HashMap<>();
            Ddata.add(data);
        }
        return Ddata;
    }

    public String updateData(String query,String username,String upass,String uport,String udb,String ipadd){
        String result = null;

        try {
            con = CONN(username, upass,uport, udb, ipadd);
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);
            result = "OK";
        } catch (SQLException se) {
            Log.i("ERROR :", se.getMessage());
            System.out.println("Error :"+se.getMessage());
            result = "Error";

        }
        return result;
    }

    public String insertData(String query,String username,String upass,String uport,String udb,String ipadd){
        String result = null;
        try {
            con = CONN(username, upass,uport, udb, ipadd);
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);
            result = "Data Insert Succesful";
        } catch (SQLException se) {
            Log.e("ERROR", se.getMessage());
            System.out.println("Error :"+se.getMessage());
            result = "Error";
        }
        return result;
    }

    public String execSP(String query,String username,String upass,String uport,String udb,String ipadd){
        String result = null;
        try {
            con = CONN(username, upass,uport, udb, ipadd);
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);
            result = "Procedure execute ";
        } catch (SQLException se) {
            Log.e("ERROR", se.getMessage());
            System.out.println("Error :"+se.getMessage());
            result = "Error";
        }
        return result;
    }

}




