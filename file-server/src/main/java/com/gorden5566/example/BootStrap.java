package com.gorden5566.example;

import com.gorden5566.example.http.HttpStaticFileServer;

/**
 * @author gorden5566
 * @date 2021/09/18
 */
public class BootStrap {
    public static void main(String[] args) throws Exception {
        ParameterProcessor.process(args);
        HttpStaticFileServer.main(args);
    }
}
