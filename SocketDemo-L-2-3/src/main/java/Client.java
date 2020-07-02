import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author:profiteur
 * @create 2020-07-02 20:52
 * @description 客户端
 **/
public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        //超时时间
        socket.setSoTimeout(3000);
        //连接本地 端口号 2000 超时时间3000ms
        socket.connect(new InetSocketAddress(InetAddress.getLocalHost(),2000),3000);
        System.out.println("已发起服务器连接，并进入后续流程");
        System.out.println("客户端信息：" + socket.getLocalAddress()+ " p:"+ socket.getLocalPort());
        System.out.println("服务器信息：" + socket.getInetAddress() + " p:"+ socket.getPort());

        try {
            //发送接收数据
            todo(socket);
        }catch (Exception e){
            System.out.println("异常关闭");
        }

        //释放资源
        socket.close();
        System.out.println("客户端已退出！");
    }

    /**
     * 发送数据
     * @param client
     * @throws IOException
     */
     private static void todo(Socket client) throws IOException{
         //构建键盘输入流
         BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

         //得到 Socket 输出流，并转化为打印流
         OutputStream outputStream = client.getOutputStream();
         PrintStream printStream = new PrintStream(outputStream);

         //得到 Socket 输入流,并转化为 BufferedReader
         InputStream inputStream = client.getInputStream();
         BufferedReader socketIn = new BufferedReader(new InputStreamReader(inputStream));

         boolean flag = true;
         do{
             //键盘读取一行
             String str = input.readLine();
             //发送到服务器
             printStream.println(str);

             //从服务器读取一行
             String echo = socketIn.readLine();
             if ("bye".equalsIgnoreCase(echo)){
                 flag = false;
             }else {
                 System.out.println(echo);
             }
         }while (flag);

         socketIn.close();
         printStream.close();
     }
}
