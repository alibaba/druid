package com.alibaba.druid.http;

import java.io.IOException;
import java.net.BindException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.QueuedThreadPool;
import org.mortbay.util.MultiException;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;

public class HttpServer {

    private final static Log              LOG             = LogFactory.getLog(HttpServer.class);

    protected final Server                webServer;
    protected final Connector             listener;

    protected final WebAppContext         webAppContext;
    protected final Map<Context, Boolean> defaultContexts = new HashMap<Context, Boolean>();

    public HttpServer() throws IOException{
        this("stat", "0.0.0.0", 19790);
    }

    public HttpServer(String name, String bindAddress, int port) throws IOException{
        webServer = new Server();

        listener = createBaseListener();
        listener.setHost(bindAddress);
        listener.setPort(port);

        webServer.addConnector(listener);
        webServer.setThreadPool(new QueuedThreadPool());

        final String appDir = getWebAppsPath();
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        webServer.setHandler(contexts);

        webAppContext = new WebAppContext();
        webAppContext.setDisplayName("WepAppsContext");
        webAppContext.setContextPath("/");
        webAppContext.setWar(appDir + "/" + name);
        webServer.addHandler(webAppContext);

        addDefaultApps(contexts, appDir);

        addDefaultServlets();
    }

    /**
     * Create a required listener for the Jetty instance listening on the port provided. This wrapper and all subclasses
     * must create at least one listener.
     */
    public Connector createBaseListener() throws IOException {
        return HttpServer.createDefaultChannelConnector();
    }

    // LimitedPrivate for creating secure datanodes
    public static Connector createDefaultChannelConnector() {
        SelectChannelConnector ret = new SelectChannelConnector();
        ret.setLowResourceMaxIdleTime(10000);
        ret.setAcceptQueueSize(128);
        ret.setResolveNames(false);
        ret.setUseDirectBuffers(false);
        return ret;
    }

    /**
     * Get the pathname to the webapps files.
     * 
     * @return the pathname as a URL
     * @throws IOException if 'webapps' directory cannot be found on CLASSPATH.
     */
    protected String getWebAppsPath() throws IOException {
        URL url = getClass().getClassLoader().getResource("druid-webapps");
        if (url == null) throw new IOException("webapps not found in CLASSPATH");
        return url.toString();
    }

    /**
     * stop the server
     */
    public void stop() throws Exception {
        listener.close();
        webServer.stop();
    }

    public void join() throws InterruptedException {
        webServer.join();
    }

    /**
     * Start the server. Does not wait for the server to start.
     */
    public void start() throws IOException {
        try {
            int port = 0;
            int oriPort = listener.getPort(); // The original requested port
            while (true) {
                try {
                    port = webServer.getConnectors()[0].getLocalPort();
                    LOG.debug("Port returned by webServer.getConnectors()[0]." + "getLocalPort() before open() is "
                              + port + ". Opening the listener on " + oriPort);
                    listener.open();
                    port = listener.getLocalPort();
                    LOG.debug("listener.getLocalPort() returned " + listener.getLocalPort()
                              + " webServer.getConnectors()[0].getLocalPort() returned "
                              + webServer.getConnectors()[0].getLocalPort());
                    LOG.debug("Jetty bound to port " + port);
                    webServer.start();
                    break;
                } catch (IOException ex) {
                    // if this is a bind exception,
                    // then try the next port number.
                    if (ex instanceof BindException) {
                        throw (BindException) ex;
                    } else {
                        LOG.debug("HttpServer.start() threw a non Bind IOException");
                        throw ex;
                    }
                } catch (MultiException ex) {
                    LOG.debug("HttpServer.start() threw a MultiException");
                    throw ex;
                }
            }
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IOException("Problem starting http server", ex);
        }
    }

    /**
     * Add default apps.
     * 
     * @param appDir The application directory
     * @throws IOException
     */
    protected void addDefaultApps(ContextHandlerCollection parent, final String appDir) throws IOException {
        // set up the context for "/static/*"
        Context staticContext = new Context(parent, "/static");
        staticContext.setResourceBase(appDir + "/static");
        staticContext.addServlet(DefaultServlet.class, "/*");
        staticContext.setDisplayName("static");
        setContextAttributes(staticContext);
        defaultContexts.put(staticContext, true);
    }

    private void setContextAttributes(Context context) {

    }

    protected void addDefaultServlets() {

    }
}
