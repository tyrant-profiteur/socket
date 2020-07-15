import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.UUID;

/**
 * @author:profiteur
 * @create 2020-07-13 22:01
 * @description UDP 提供者
 **/
public class NewUDPProvider {
    public static void main(String[] args) throws IOException {
        //生成一份唯一标示
        String sn = UUID.randomUUID().toString();
        Provider provider = new Provider(sn);
        provider.start();

        //读取任意键盘信息后可以退出
        System.in.read();
        provider.exit();
    }

    private static class Provider extends Thread {
        private final String sn;
        private boolean done = false;
        private DatagramSocket ds = null;

        public Provider(String sn) {
            super();
            this.sn = sn;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("UDPProvider started.");

            try {
                //作为接收者，指定一个端口用于数据接收
                ds = new DatagramSocket(20000);
                while (!done) {
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
                    System.out.println("UDPProvider receive from ip:" + ip + "\tport:"
                            + port + "\tdata:" + data);

                    //解析端口号
                    int responsePort = MessageCreator.parsePort(data);
                    if (responsePort != -1){
                        //构建回送数据
                        String responseData = MessageCreator.buildWithSn(sn);
                        byte[] respinseDataBytes = responseData.getBytes();
                        //直接根据发送者构建一个份回送信息
                        DatagramPacket responsePacket = new DatagramPacket(
                                respinseDataBytes,
                                respinseDataBytes.length,
                                receivePacket.getAddress(),
                                responsePort);
                        ds.send(responsePacket);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                ds.close();
            }
            System.out.println("UDPProvider finished.");
        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        void exit() {
            done = true;
            close();
        }
    }
}
