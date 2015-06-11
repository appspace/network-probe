package ca.appspace.netprobe;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.AsyncSubject;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Future;

/**
 * Created by Eugene on 2015-06-05.
 */
public class MainObservable {

    private final static int port = 8080;

    public static void main(String... args) throws IOException {

        final ServerSocketChannel tcpSocketChannel = ServerSocketChannel.open();
        tcpSocketChannel.configureBlocking(false);
        tcpSocketChannel.socket().bind(new InetSocketAddress(port));

        final PublishSubject<SocketChannel> socketChannelPublishSubject = PublishSubject.create();

        socketChannelPublishSubject.flatMap(new Func1<SocketChannel, Observable<?>>() {
            @Override
            public Observable<?> call(SocketChannel socketChannel) {
                System.out.println("In flatMap");
                return null;
            }
        });

        socketChannelPublishSubject.forEach(new Action1<SocketChannel>() {
            @Override
            public void call(SocketChannel socketChannel) {
                try {
                    if (socketChannel != null) {
                        SocketAddress remoteAddress = socketChannel.getRemoteAddress();
                        StringBuilder sb = new StringBuilder("Received TCP connection: ");
                        appendAddress(sb, remoteAddress);
                        System.out.println(sb.toString());

                        String hello = "Hello";
                        ByteBuffer buf = ByteBuffer.allocate(hello.length());
                        buf.clear();
                        buf.put(hello.getBytes());
                        buf.flip();
                        socketChannel.write(buf);
                        socketChannel.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                System.out.println("Error in forEach: " + throwable.toString());
            }
        }, new Action0() {
            @Override
            public void call() {
                System.out.println("Complete in forEach");
            }
        });

        while (true) {
            try {
                socketChannelPublishSubject.onNext(tcpSocketChannel.accept());
            } catch (Exception e) {
                e.printStackTrace();
                socketChannelPublishSubject.onError(e);
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
