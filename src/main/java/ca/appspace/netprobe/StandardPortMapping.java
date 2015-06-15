package ca.appspace.netprobe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Eugene on 2015-06-11.
 */
public class StandardPortMapping {

    private final static Map<Integer, String> PORTS = new HashMap<>();

    static {
        PORTS.put(7,"Echo");
        PORTS.put(20,"FTP data transfer");
        PORTS.put(21,"FTP control");
        PORTS.put(22, "SSH");
        PORTS.put(23,"Telnet");
        PORTS.put(25,"SMTP");
        PORTS.put(53,"DNS");
        PORTS.put(80, "HTTP");
        //PORTS.put(443,"HTTPS");
    }

    public static Set<Integer> getAllPorts() {
        return new HashSet<>(PORTS.keySet());
    }

    public static Integer[] getAllPortsAsArray() {
        return PORTS.keySet().toArray(new Integer[PORTS.size()]);
    }

    public static String getProtocolName(int portNumber) {
        return PORTS.get(portNumber);
    }

}
