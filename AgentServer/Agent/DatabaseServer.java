package Agent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseServer {
	private Connection con = null;
	private DBConectClass dbcon;
	private PreparedStatement pre;
	private ResultSet rs;
	public DatabaseServer() {
		dbcon = new DBConectClass();
	}
	
	public String selectData(String team_name, String name) {
		if(team_name.length() > 40 || name.length() > 20) {
			return "0";
		}
		else {
			try {
				String sql = "select * from anuctf1121_information where ANUCTF_Team_Name = ? AND ANUCTF_Stu_Name = ?";			
				con = dbcon.getConnection();
				pre = con.prepareStatement(sql);
				
				pre.setString(1, team_name);
				pre.setString(2, name);
				
				rs = pre.executeQuery();
				
				if(!rs.next()) {
					dbClose();
					return "0";
				}
				else {
					if(rs.getString("ANUCTF_COM_IP").equals("0") && rs.getString("ANUCTF_COM_MAC").equals("0")) {
						return "1";
					}
					else {
						dbClose();
						System.out.println("mac 중복발생 " + name);
						return "2";
					}
				}
				
			}catch(Exception e) {
				e.printStackTrace();
				dbClose();
				return "0";
			}
		}
	}
	
	public String selectMacData(String mac) {
		try {
			String sql = "select * from anuctf1121_information where ANUCTF_COM_MAC = ?";
			pre = con.prepareStatement(sql);	
			pre.setString(1, mac);
			rs = pre.executeQuery();
			
			if(!rs.next()) {			
				return "1";
			}
			else {
				dbClose();
				System.out.println("2");
				return "0";
			}
		}catch(Exception e) {
			e.printStackTrace();
			return "0";
		}
	}
	
	public String insertData(String team_name, String name, String ip, String mac) {
		try {
			String check_mac = null;
			if(mac == "" || mac == null) {
				check_mac = "macCom";
			}
			else if(mac.equals("00:50:56:C0:00:08") || mac.equals("02:00:4C:4F:4F:50")) {
				check_mac = mac + "(VMorVPN)";
			}			
			else {
				check_mac = mac;
			}
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat ( "MM-dd HH:mm:ss");
			String sql = "update anuctf1121_information set ANUCTF_COM_IP = ?, ANUCTF_COM_MAC = ?, ANUCTF_DATE = ? where ANUCTF_Team_Name = ? AND ANUCTF_Stu_NAME = ?";
			pre = con.prepareStatement(sql);
			pre.setString(1, ip);
			pre.setString(2, check_mac);
			pre.setString(3, format.format(date));
			pre.setString(4, team_name);
			pre.setString(5, name);
			pre.executeQuery();
			dbClose();
			return "1";
		}catch(Exception e) {
			dbClose();
			e.printStackTrace();
			return "0";
		}
	}
	
	public void dbClose() {
	      if (rs != null) {
	         try {
	            rs.close();
	         } catch (SQLException e) {
	            System.out.println("ResultSet을 종료하는 동안 오류가 발생하였습니다.");
	         }
	      }
	      if (pre != null) {
	         try {
	            pre.close();
	         } catch (SQLException e) {
	            System.out.println("PrepareStatement를 종료하는 동안 오류가 발생하였습니다.");
	         }
	      }
	      if (con != null) {
	         try {
	            con.close();
	         } catch (SQLException e) {
	            System.out.println("Connection을 종료하는 동안 오류가 발생하였습니다.");
	         }
	      }
	   }
}