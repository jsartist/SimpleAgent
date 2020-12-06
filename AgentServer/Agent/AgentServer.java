package Agent;

import java.net.ServerSocket;
import java.net.Socket;

public class AgentServer {
   private ServerSocket serversocket;
   private Socket socket;

   public AgentServer() {
      try {
         serversocket = new ServerSocket(12347);
         socket = null;
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void connectClient() {
      while (true) {
         try {
            socket = serversocket.accept();
            System.out.println(socket.getInetAddress() + " Á¢¼ÓÇÔ");
            new AgentThread(socket).start();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   public static void main(String[] args) {
      new AgentServer().connectClient();
   }
}