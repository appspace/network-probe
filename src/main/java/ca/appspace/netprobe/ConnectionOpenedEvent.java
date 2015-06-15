package ca.appspace.netprobe;

import java.io.Serializable;
import java.net.InetSocketAddress;

/**
 * Created by Eugene on 2015-06-11.
 */
public class ConnectionOpenedEvent implements Serializable {

    private final InetSocketAddress _local;
    private final InetSocketAddress _remote;
    private final long _openedAt;

    public ConnectionOpenedEvent(InetSocketAddress local, InetSocketAddress remote) {
        _local = local;
        _remote = remote;
        _openedAt = System.currentTimeMillis();
    }

    public InetSocketAddress getLocal() {
        return _local;
    }

    public InetSocketAddress getRemote() {
        return _remote;
    }

    public long getOpenedAt() {
        return _openedAt;
    }
}
