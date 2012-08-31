package com.alibaba.druid.filter.config.impl;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

/**
 * @author Jonas Yang
 */
public class HttpConfigLoaderTest1 {
    protected String protocol = "localhost:19999/test.properties";

    protected ServerSocket server;

    @Before
    public void setUp() {
        try {
            this.server = new ServerSocket(19999);
            this.server.setReuseAddress(true);
            Thread t = new Thread("Http Server Thread") {
                @Override
                public void run() {
                    try {
                        Socket socket = HttpConfigLoaderTest1.this.server.accept();
//                            InputStream in = socket.getInputStream();
//                            int b;
//                            while ((b = in.read()) != -1) {
//                                if (b == '\r') {
//                                    b = in.read();
//                                    if (b == '\n') {
//                                        b = in.read();
//                                        if (b == '\r') {
//                                            b = in.read();
//                                            if (b == '\n') {
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//
//                                if (b == -1) {
//                                    break;
//                                }
//                            }

                        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                        out.println("a1=test1");
                        out.println("b2=test2");
                        out.flush();
                        socket.close();
                    } catch (IOException e) {
                        if (e.getMessage().indexOf("Socket closed") == -1
                                && e.getMessage().indexOf("Socket is closed") == -1)
                            e.printStackTrace();
                    }
                }
            };
            t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        if (this.server != null) {
            try {
                this.server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testLoadConfig() {
        HttpConfigLoader configLoader = new HttpConfigLoader();
        Properties p = configLoader.loadConfig(HttpConfigLoader.PROTOCOL_PREFIX + this.protocol);

        Assert.assertNotNull("The properties is null", p);
        Assert.assertEquals("The value is " + p.getProperty("a1") + ", not test1", "test1", p.getProperty("a1"));
    }

    @Test
    public void testLoadConfigByNotExistUrl() {
        HttpConfigLoader configLoader = new HttpConfigLoader();
        Properties p = configLoader.loadConfig(HttpConfigLoader.PROTOCOL_PREFIX + "localhost/jonas");

        Assert.assertNull("The properties is not null", p);
    }
}
