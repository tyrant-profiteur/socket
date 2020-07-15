package base;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @author:profiteur
 * @create 2020-07-13 22:01
 * @description UDP 提供者
 **/
public class UDPProvider {
    public static void main(String[] args) throws IOException {
        System.out.println("UDPProvider started.");

        //作为接收者，指定一个端口用于数据接收
        DatagramSocket ds = new DatagramSocket(20000);

        //构建接受实体
        final byte[] buf = new byte[512];
        DatagramPacket receivePacket = new DatagramPacket(buf,buf.length);

        //接受
        ds.receive(receivePacket);

        //打印接收到的信息与发送者的信息
        //发送者的 IP 地址
        String ip = receivePacket.getAddress().getHostAddress();
        int port = receivePacket.getPort();
        int dataLen = receivePacket.getLength();
        String data = new String(receivePacket.getData(),0,dataLen);
        System.out.println("UDPProvider receive from ip:" + ip + "\tport:"
                            +port + "\tdata:" + data);

        //构建回送数据
        String responseData = "Receive data with len "+dataLen;
        byte[] respinseDataBytes = responseData.getBytes();
        //直接根据发送者构建一个份回送信息
        DatagramPacket responsePacket = new DatagramPacket(
                respinseDataBytes,
                respinseDataBytes.length,
                receivePacket.getAddress(),
                receivePacket.getPort());
        ds.send(responsePacket);

        System.out.println("UDPProvider finished.");
        ds.close();
     }
}
