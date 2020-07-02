import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author:profiteur
 * @create 2020-07-02 20:52
 * @description 服务端
 **/
public class Server {
    public static void main(String[] args)throws IOException {
        ServerSocket server = new ServerSocket(2000);
        System.out.println("服务器准备就绪");
        System.out.println("服务器信息：" + server.getInetAddress() + " p:"+ server.getLocalPort());

        //等待客户端连接
        for (;;){
            //得到客户端
            Socket client = server.accept();
            //客户端构建异步线程
            ClientHandler clientHandler = new ClientHandler(client);
            //启动线程
            clientHandler.start();
        }
     }
}
