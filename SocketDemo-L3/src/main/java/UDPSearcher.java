import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author:profiteur
 * @create 2020-07-13 22:02
 * @description
 **/
public class UDPSearcher {
    public static void main(String[] args) throws IOException {
        System.out.println("UDPSearcher started.");

        //作为搜索方（发送方），端口系统自动分配
        DatagramSocket ds = new DatagramSocket();
        //构建发送数据
        String responseData = "hello UDP";
        byte[] requestDataBytes = responseData.getBytes();
        //发送信息
        DatagramPacket responsePacket = new DatagramPacket(
                requestDataBytes,
                requestDataBytes.length);
        responsePacket.setAddress(InetAddress.getLocalHost());
        responsePacket.setPort(20000);
        ds.send(responsePacket);

        //构建接受实体
        final byte[] buf = new byte[512];
        DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
        //接受
        ds.receive(receivePacket);
        //打印接收到的信息与发送者的信息
        //发送者的 IP 地址
        String ip = receivePacket.getAddress().getHostAddress();
        int port = receivePacket.getPort();
        int dataLen = receivePacket.getLength();
        String data = new String(receivePacket.getData(), 0, dataLen);
        System.out.println("UDPSearcher receive from ip:" + ip + "\tport:"
                + port + "\tdata:" + data);

        System.out.println("UDPSearcher finished.");
        ds.close();
    }
}
