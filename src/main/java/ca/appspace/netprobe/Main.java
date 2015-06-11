package ca.appspace.netprobe;

import rx.Observable;
import rx.Subscriber;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Eugene on 2015-06-05.
 */
public class Main {

    public static void main(String... args) throws IOException {
        DatagramChannel udpChannel = DatagramChannel.open();
        SelectableChannel selectableChannel = udpChannel.configureBlocking(false);
        udpChannel.socket().bind(new InetSocketAddress(8081));

        ServerSocketChannel tcpSocketChannel = ServerSocketChannel.open();
        tcpSocketChannel.configureBlocking(false);

        tcpSocketChannel.socket().bind(new InetSocketAddress(8080));
        while (true) {
            SocketChannel tcpChannel = tcpSocketChannel.accept();

            if (tcpChannel!=null) {
                SocketAddress remoteAddress = tcpChannel.getRemoteAddress();
                StringBuilder sb = new StringBuilder("Received TCP connection: ");
                appendAddress(sb, remoteAddress);
                System.out.println(sb.toString());

                String hello = "Hello";
                ByteBuffer buf = ByteBuffer.allocate(hello.length());
                buf.clear();
                buf.put(hello.getBytes());
                buf.flip();
                tcpChannel.write(buf);
                tcpChannel.close();
            }


            ByteBuffer buf = ByteBuffer.allocate(1024);
            SocketAddress remoteAddress = udpChannel.receive(buf);
            if (remoteAddress!=null) {
                StringBuilder sb = new StringBuilder("Received UDP connection: ");
                appendAddress(sb, remoteAddress);
                System.out.println(sb.toString());

                String hello = "Hello";
                ByteBuffer buf1 = ByteBuffer.allocate(hello.length());
                buf1.clear();
                buf1.put(hello.getBytes());
                buf1.flip();
                udpChannel.send(buf1, remoteAddress);
            }
        }

    }

    private static void appendAddress(StringBuilder sb, SocketAddress remoteAddress) {
        if (remoteAddress instanceof InetSocketAddress) {
            InetSocketAddress addr = (InetSocketAddress) remoteAddress;
            sb.append("\n  Host Name  : " + addr.getHostName());
            sb.append("\n  Host String: " + addr.getHostString());
            sb.append("\n  Address    : " + addr.getAddress());
            sb.append("\n  Port       : " + addr.getPort());
        }
    }

}
