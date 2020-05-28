package Chat01;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * 发送端 使用多线程封装
 */
public class Send implements Runnable {
    private BufferedReader console;
    private DataOutputStream dos;
    private Socket client;
    private boolean isRunning;
    private String name;
    public    Send(Socket client,String name){
        this.client=client;
        console=new BufferedReader(new InputStreamReader(System.in));
        this.isRunning=true;
        this.name=name;
        try {
            dos=new DataOutputStream(client.getOutputStream());
            //发送名称
            send(name);
        } catch (IOException e) {
            System.out.println("Send");
             this.release();
        }
    }

    @Override
    public void run() {
        while (isRunning){
            String msg=getStrFromConsole();
            if(!msg.equals("")){
                send(msg);
            }
        }
    }
    private void  send(String msg){
        try {
            dos.writeUTF(msg);
            dos.flush();
        } catch (IOException e) {
            System.out.println("send");
            release();
        }

    }
    //从控制台获取消息
    private String getStrFromConsole(){
        try {
            return console.readLine();
        } catch (IOException e) {

        }
        return "";
    }
    private void release(){
        this.isRunning=false;
        Utils.close(dos,client);
    }
}
