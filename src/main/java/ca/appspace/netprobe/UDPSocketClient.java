package ca.appspace.netprobe;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Created by Eugene on 2015-06-08.
 */
public class UDPSocketClient {

    public static void main(String... args) throws Exception {
        DatagramSocket socket = DatagramChannel.open().socket();
        //socket.connect(InetAddress.getByName("localhost"), 8081);
        //if (socket.isConnected()) {
            //System.out.println("Socket connected!");
            ByteBuffer buf = ByteBuffer.allocate(1024);
            String toSend = "Hi";
            socket.send(new DatagramPacket(toSend.getBytes(), toSend.length(), InetAddress.getByName("localhost"), 8081));

        //}
    }
}
