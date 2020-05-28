package Chat01;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 接收端 使用多线程封装
 */
public class Receive implements Runnable {
    private DataInputStream dis;
    private Socket client;
    private boolean isRunning;
         public Receive(Socket client){
             this.client=client;
             this.isRunning=true;
             try {
                 dis=new DataInputStream(client.getInputStream());
             } catch (IOException e) {
                 System.out.println("Receive");
                 release();
             }

         }
         private String receive(){
             String msg="";
             try {
                 msg=dis.readUTF();
             } catch (IOException e) {
                 System.out.println("receive ");
             }
             return msg;
         }
    public void run() {
             while (isRunning){
                 String msg=receive();
                 if (!msg.equals("")){
                     System.out.println(msg);
                 }

             }
    }
    private void release(){
        this.isRunning=false;
        Utils.close(dis,client);
    }
}
