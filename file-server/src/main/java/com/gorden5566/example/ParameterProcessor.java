package com.gorden5566.example;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * @author gorden5566
 * @date 2021/09/18
 */
public class ParameterProcessor {
    private static String javaModuleName;
    static {
        Package pack = ParameterProcessor.class.getPackage();
        javaModuleName = pack.getImplementationTitle();
    }

    @Parameter(names = {"-h", "--help"}, order = 1, description = "print help message")
    private boolean help = false;

    @Parameter(names = {"-s", "--ssl"}, order = 2, description = "set enable ssl")
    private boolean ssl = false;

    @Parameter(names = {"-p", "--port"}, order = 3, description = "set listen port")
    private int port = 0;

    @Parameter(names = {"-d", "--dir"}, order = 4, description = "set root dir")
    private String dir = null;

    public static void process(String[] args) {
        ParameterProcessor processor = new ParameterProcessor();

        JCommander jc = JCommander.newBuilder().addObject(processor).programName(getProgramName()).build();

        try {
            jc.parse(args);
        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            jc.usage();
            exit();
        }

        processor.run(jc);
    }

    public void run(JCommander jc) {
        if (help) {
            jc.usage();
            exit();
        }

        if (ssl) {
            System.setProperty("ssl", "true");
            log("enable ssl");
        }

        if (port > 0) {
            System.setProperty("port", String.valueOf(port));
            log("set port: " + port);
        }

        if (dir != null) {
            System.setProperty("user.dir", dir);
            log("set root dir: " + dir);
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
