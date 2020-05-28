package Chat01;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 在线聊天室 服务器
 *  * 目标 实现多个客户可以正常收发多条信息 加入多线程
 * 封装
 */
public class Chat {
    private static CopyOnWriteArrayList<Channel> all=new CopyOnWriteArrayList<Channel>();
    public static void main(String[] args) throws IOException {
        System.out.println("-----SERVER------");
        //指定端口 使用serversocket创建服务器
        ServerSocket server=new ServerSocket(9999);
        //阻塞式等待链接accept
        while (true) {
            Socket client = server.accept();
            System.out.println("一个用户加入了聊天室");
            Channel c=new Channel(client);
            all.add(c);//管理所有成员
            new  Thread(c).start();
        }
    }
//一个用户一个channel
    static class Channel implements Runnable{
        private  DataInputStream dis;
        private  DataOutputStream dos;
        private Socket client;
        private boolean isRunning;
        private String name;
        public Channel(Socket client){
            this.client=client;
            try {
                dis = new DataInputStream(client.getInputStream());
                dos = new DataOutputStream(client.getOutputStream());
                isRunning=true;
                //获取名称
                this.name=recevie();
                //欢迎
                this.send("欢迎加入鸡霸");
                sendOther(this.name+"加入了群聊",true);
            } catch (IOException e) {
                release();
            }
        }
        //接受消息
        private  String recevie(){
            String msg="";
            try {
                msg=dis.readUTF();
            } catch (IOException e) {
                release();
            }
            return msg;
        }
        //发送消息
        private void send(String msg){
            try {
                dos.writeUTF(msg);
                dos.flush();
            } catch (IOException e) {
                release();
            }
        }
        //群聊 获取自己的信息 发给他人
        //私聊 约定数据格式
    private void sendOther(String msg,boolean isSys){
            boolean isPrivate = msg.startsWith("@");
            if(isPrivate){//私聊
                int idx=msg.indexOf(":");
                //获取目标和数据
                String targetName=msg.substring(1,idx);
                msg=msg.substring(idx+1);
                for(Channel other:all){
                    if(other.name.equals(targetName)){//目标
                        other.send(this.name+"来自私聊:"+msg);
                    }
                }
            }else {
                for (Channel other : all) {
                    if (other == this) { //自己
                        continue;
                    }
                    if (!isSys) {
                        other.send(this.name + "：" + msg);//聊天信息
                    } else {
                        other.send(msg);//系统消息
                    }
                }
            }
    }
        //释放资源
        private void release(){
            Utils.close(dis,dos,client);
            this.isRunning=false;
            //退出
            all.remove(this);
        }

    @Override
    public void run() {
        while (isRunning){
            String msg=recevie();
            if(!msg.equals("")){
                sendOther(msg,false );
            }
        }
     }
  }
}
