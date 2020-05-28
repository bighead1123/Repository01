package Chat01;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 在线聊天室 客户端
 * 目标 实现多个客户可以正常收发多条信息 加入多线程
 * 封装
 */
public class Client {
    public static void main(String[] args) throws IOException,UnknownHostException {
        System.out.println("----CLIENT-----");
        BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
        System.out.println("请输入用户名");
        String name =br.readLine();
        System.out.println("-----"+name+"的客户端-----");
        //建立链接 使用socket创建客户端 + 服务器的地址和端口
        Socket client=new Socket("localhost",9999);
        //客户端发送消息
        new Thread(new Send(client,name)).start();
        new Thread(new Receive(client)).start();
    }
}
