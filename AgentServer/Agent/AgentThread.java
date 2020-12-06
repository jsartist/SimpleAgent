package Agent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import javax.crypto.Cipher;

public class AgentThread extends Thread {
   
   private Socket socket;
   private DataInputStream dis;
   private DataOutputStream dos;
   private String team_name;
   private String name;
   private InetAddress ip;
   private SendDatabaseServer dbserver;
   private String mac;
   private KeyPairGenerator keypairgenerator;
   private KeyPair keypair;
   private Key publicKey;
   private Key privateKey;
   private ObjectOutputStream oos;
   private ObjectInputStream ois;

   public AgentThread(Socket socket) {
      try {
         this.socket = socket;
         dis = new DataInputStream(socket.getInputStream());
         dos = new DataOutputStream(socket.getOutputStream());
         oos = new ObjectOutputStream(socket.getOutputStream());
         ois = new ObjectInputStream(socket.getInputStream());
         dbserver = new SendDatabaseServer();
         keypairgenerator = null;
         keypair = null;
         publicKey = null;
         privateKey = null;
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private String checkData() {
      try {
         team_name = dis.readUTF();
         dos.writeUTF("1");
         name = dis.readUTF();
         if(team_name.trim().equals("") || name.trim().equals("") || team_name.length() > 40 || name.length() > 14) {
        	 dos.writeUTF("0");
        	 return "0";
         }
         else {
        	 String result = dbserver.Check(team_name, name);
        	 dos.writeUTF(result);
        	 return result;
         }
      }catch(Exception e) {
         e.printStackTrace();
         return "0";
      }
   }
   
   private void makeRSA() {
	   try {
		   keypairgenerator = KeyPairGenerator.getInstance("RSA");
		   keypairgenerator.initialize(2048);
		   keypair = keypairgenerator.genKeyPair();
		   publicKey = keypair.getPublic();
		   privateKey = keypair.getPrivate();
	   }catch(Exception e) {
		   e.printStackTrace();
	   }
   }

   private void Address() {
      try {
         ip = socket.getInetAddress();
         KeyData key = new KeyData(publicKey);
         oos.writeObject(key);
         EncryptedData data = (EncryptedData) ois.readObject();
         decode(data.getbyte());
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void decode(byte[] encryptedData) {
	   try {
		   Cipher cipher = Cipher.getInstance("RSA");
		   cipher.init(Cipher.DECRYPT_MODE, privateKey);
		   byte[] data = cipher.doFinal(encryptedData);
		   mac = new String(data);
	   }catch(Exception e) {
		   e.printStackTrace();
	   }
   }
   
   private void socketClose() {
	   try {
		   dis.close();
		   dos.close();
		   ois.close();
		   oos.close();
		   socket.close();
	   }catch(Exception e) {
		   e.printStackTrace();
	   }
   }

   public void run() {
	   try {
		   String check = checkData();
		   if(check.equals("1")) {
			   makeRSA();
			   Address();
			   dos.writeUTF(dbserver.sendToDatabaseServer(ip, mac));
		   }
	   }catch(Exception e) {
		   e.printStackTrace();
		   socketClose();
	   }finally {
		   System.out.println(socket.getInetAddress() + " Á¾·á");
		   socketClose();
	   }
   }
}