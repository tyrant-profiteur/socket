import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * @author:profiteur
 * @create 2020-07-02 22:05
 * @description 客户端异步处理
 **/
 class ClientHandler extends Thread {
    private Socket socket;
    private boolean flag = true;

    ClientHandler(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();
        System.out.println("新客户端连接：" + socket.getInetAddress()+
                " p:"+ socket.getPort());
        try{
            //得到打印流，用于数据输出；服务器回送数据使用
            PrintStream socketOutput = new PrintStream(socket.getOutputStream());
            BufferedReader socketInput = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            do {
                //客户端拿到一条数据
                String str = socketInput.readLine();
                if ("bye".equalsIgnoreCase(str)){
                    flag = false;
                    //回送
                    socketOutput.println(str);
                }else {
                    //打印，并回送
                    System.out.println(str);
                    socketOutput.println("回送："+str.length());
                }
            }while (flag);
            socketInput.close();
            socketOutput.close();
        }catch (Exception e){
            System.out.println("连接异常关闭");
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("客户端已关闭：" + socket.getInetAddress()+
                " p:"+ socket.getPort());
    }
}
