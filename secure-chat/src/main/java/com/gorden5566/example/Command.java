package com.gorden5566.example;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.gorden5566.example.securechat.SecureChatClient;
import com.gorden5566.example.securechat.SecureChatServer;

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

    @Parameter(names = {"-s", "--ssl"}, order = 2, description = "set enable ssl")
    private boolean ssl = false;

    @Parameter(names = {"-p", "--port"}, order = 3, description = "set port")
    private Integer port = null;

    @Parameter(names = {"-H", "--host"}, order = 4, description = "set host")
    private String host = null;

    @Parameter(names = {"-c", "--client"}, order = 5, description = "run in client mode")
    private boolean client = false;

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

        if (command.client) {
            SecureChatClient.main(args);
        } else {
            SecureChatServer.main(args);
        }
    }

    public void parse(JCommander jc) {
        if (help) {
            jc.usage();
            exit();
        }

        if (ssl) {
            System.setProperty("ssl", "true");
            log("enable ssl");
        }

        if (port != null && port > 0) {
            System.setProperty("port", String.valueOf(port));
            log("set port: " + port);
        }

        if (host != null) {
            System.setProperty("host", host);
            log("set host: " + host);
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
