package advance;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author:profiteur
 * @create 2020-07-13 22:02
 * @description
 **/
public class NewUDPSearcher {
    private static final int LISTEN_PORT = 30000;
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("base.UDPSearcher started.");
        Listener listener = listen();
        //发送广播
        setBroadcast();

        //读取任意键盘信息后可以退出
        System.in.read();
        List<Device> devices = listener.getDevicesAndClose();
        for (Device device : devices) {
            System.out.println("Device : "+ device.toString());
        }

        //完成
        System.out.println("base.UDPSearcher finished.");
    }

    private static Listener listen() throws InterruptedException {
        System.out.println("base.UDPSearcher start listen.");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Listener listener = new Listener(LISTEN_PORT,countDownLatch);
        listener.start();
        countDownLatch.await();
        return listener;

    }

    private static void setBroadcast()throws IOException {
        System.out.println("base.UDPSearcher listener started.");

        //作为搜索方（发送方），端口系统自动分配
        DatagramSocket ds = new DatagramSocket();
        //构建发送数据
        String responseData = MessageCreator.buildWithPort(LISTEN_PORT);
        byte[] requestDataBytes = responseData.getBytes();
        //构建 packet
        DatagramPacket responsePacket = new DatagramPacket(
                requestDataBytes,
                requestDataBytes.length);
        //20000端口，广播地址
        responsePacket.setAddress(InetAddress.getByName("255.255.255.255"));
        responsePacket.setPort(20000);
        //发送
        ds.send(responsePacket);
        ds.close();

        System.out.println("base.UDPSearcher listener finished.");
    }

    private static class Device{
        final int port;
        final String ip;
        final String sn;

        public Device(int port, String ip, String sn) {
            this.port = port;
            this.ip = ip;
            this.sn = sn;
        }

        @Override
        public String toString() {
            return "Device{" +
                    "port=" + port +
                    ", ip='" + ip + '\'' +
                    ", sn='" + sn + '\'' +
                    '}';
        }
    }
    private static class Listener extends Thread{
        private final int LISTEN_PORT;
        private final CountDownLatch countDownLatch;
        private final List<Device> deviceList = new ArrayList<>();
        private boolean done = false;
        private DatagramSocket ds;

        public Listener(int LISTEN_PORT,CountDownLatch countDownLatch){
            super();
            this.LISTEN_PORT = LISTEN_PORT;
            this.countDownLatch = countDownLatch;
        }
        @Override
        public void run() {
            super.run();
            //通知已启动
            countDownLatch.countDown();
            try {
                ds = new DatagramSocket(LISTEN_PORT);

                while (!done){
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
                    System.out.println("base.UDPProvider receive from ip:" + ip + "\tport:"
                            +port + "\tdata:" + data);

                    String sn = MessageCreator.parseSn(data);
                    if (sn != null){
                        Device device = new Device(port,ip,sn);
                        deviceList.add(device);
                    }
                }
            }catch (Exception e){

            }finally {
                close();
            }
            System.out.println("base.UDPSearcher finished.");

        }

        private void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        List<Device> getDevicesAndClose(){
            done = true;
            close();
            return deviceList;
        }
    }
}
