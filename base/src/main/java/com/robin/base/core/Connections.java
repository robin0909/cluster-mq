package com.robin.base.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Connections {

    private HostAndPort[] cList;

    public Connections(String uri) {

        String[] split = uri.split(",");

        ArrayList<HostAndPort> list = new ArrayList<>();

        for (String s : split) {

            String[] split1 = s.split(":");

            list.add(new HostAndPort(split1[0], Integer.valueOf(split1[1])));
        }
        
        cList = list.toArray(new HostAndPort[list.size()]);
        

        Arrays.sort(cList);
    }

    public List<HostAndPort> getcList() {
        return Arrays.asList(cList);
    }

    public static class HostAndPort implements Comparable<HostAndPort> {
        private String host;
        private int port;

        public HostAndPort(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        @Override
        public int hashCode() {
            return host.hashCode() & Integer.valueOf(port).hashCode();
        }

        @Override
        public int compareTo(HostAndPort o) {
            if (this.hashCode() > o.hashCode()) {
                return 1;
            } else if (this.hashCode() < o.hashCode()) {
                return -1;
            }
            return 0;
        }
    }
}
