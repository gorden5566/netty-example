package com.gorden5566.example;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * @author gorden5566
 * @date 2021/09/18
 */
public class Command {
    private static String javaModuleName;
    static {
        Package pack = Command.class.getPackage();
        javaModuleName = pack.getImplementationTitle();
    }

    @Parameter(names = {"-h", "--help"}, order = 1, description = "print help message")
    private boolean help = false;

    @Parameter(names = {"-p", "--port"}, order = 2, description = "set proxy server port")
    private Integer localPort = null;

    @Parameter(names = {"-t", "--targetServer"}, order = 3, description = "set target server's ip and port. examples: 127.0.0.1:8080")
    private String remoteServer = null;

    @Parameter(names = {"-d", "--debug"}, order = 4, description = "enable print debug info")
    private boolean debug = false;

    public static void run(String[] args) throws Exception {
        Command command = new Command();

        JCommander jc = JCommander.newBuilder().addObject(command).programName(getProgramName()).build();

        try {
            jc.parse(args);
        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            jc.usage();
            exit();
        }

        command.parse(jc);

        TcpProxy.main(args);
    }

    public void parse(JCommander jc) {
        if (help) {
            jc.usage();
            exit();
        }

        if (localPort != null && localPort > 0) {
            System.setProperty("localPort", String.valueOf(localPort));
            log("set local port: " + localPort);
        }

        if (remoteServer != null && !remoteServer.isEmpty()) {
            final String[] ipAndPort = remoteServer.split(":");
            if (ipAndPort != null && ipAndPort.length == 2) {
                final String ip = ipAndPort[0];
                final String port = ipAndPort[1];
                System.setProperty("remoteHost", ip);
                System.setProperty("remotePort", port);
                log("set remote server[ip:port] = [" + ip + ":" + port + "]");
            }
        }

        if (debug) {
            System.setProperty("debug", String.valueOf(true));
            log("enable print debug info");
        }
    }

    private static void exit() {
        System.exit(0);
    }

    private static String getProgramName() {
        StringBuilder sb = new StringBuilder();
        sb.append("java -jar ").append(javaModuleName).append(".jar");
        return sb.toString();
    }

    private void log(String str) {
        System.out.println(str);
    }
}
