package ca.appspace.netprobe;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by Eugene on 2015-06-05.
 */
public class MainObservable {

    private final static int port = 8080;

    public static void main(String... args) throws IOException {

        final ServerSocketChannel tcpSocketChannel = ServerSocketChannel.open();
        tcpSocketChannel.configureBlocking(false);

        Observable<ServerSocketChannel> channelObservable = Observable.create(
                new Observable.OnSubscribe<ServerSocketChannel>() {
                    @Override
                    public void call(Subscriber<? super ServerSocketChannel> subscriber) {
                        System.out.println("channelObservable.call "+subscriber+" invoked");
                        try {
                            tcpSocketChannel.socket().bind(new InetSocketAddress(port));
                        } catch (Exception e) {
                            subscriber.onError(e);
                            e.printStackTrace();
                        }
                    }
                });

        Observable<SocketChannel> socketObservable = channelObservable.flatMap(
                new Func1<ServerSocketChannel, Observable<SocketChannel>>() {
            @Override
            public Observable<SocketChannel> call(final ServerSocketChannel tcpSocketChannel1) {
                System.out.println("socketObservable.call "+tcpSocketChannel+" invoked");
                return Observable.create(new Observable.OnSubscribe<SocketChannel>() {
                    @Override
                    public void call(Subscriber<? super SocketChannel> subscriber) {
                        try {
                            subscriber.onNext(tcpSocketChannel1.accept());
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
            }
        });

        socketObservable.subscribe(new Action1<SocketChannel>() {
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
        });

        while (true) {
            try {   Thread.sleep(100);  } catch (Exception e) {}

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
