package Agent;

import java.net.InetAddress;

public class SendDatabaseServer {
   private DatabaseServer dbserver;
   private String team_name;
   private String name;
   
   public SendDatabaseServer() {
	   dbserver = new DatabaseServer();
   }
   
   public String Check(String team_name, String name) {
	   String check = dbserver.selectData(team_name, name);
	   System.out.println("ÀÔ·Â°ª : " +team_name + ", " + name);
	   if(check.equals("1")) {
		   this.team_name = team_name;
		   this.name = name;
		   return "1";
	   }
	   else {
		   System.out.println(check);
		   return check;
	   }
   }
   public String sendToDatabaseServer(InetAddress ip, String mac) {
      StringBuilder str_ip = new StringBuilder();
      str_ip.append(String.format("%s", ip));
      str_ip.delete(0, 1);
      System.out.println(str_ip + " " + mac + " " + name);
      if(dbserver.selectMacData(mac).equals("0")) {
    	  return "0";
      }
      else {
    	  return dbserver.insertData(team_name, name, str_ip.toString(), mac);
      }
   }
}