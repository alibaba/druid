/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.proxy.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.util.DruidLoaderUtils;

/**
 * 读配置
 * 
 * @author gang.su
 */
public class DruidFilterConfigLoader {

    private static final String NAME_TAG         = "name";

    private static final String VALUE_TAG        = "value";

    private static final String CLAZZ_TAG        = "class";

    private static final String CONFIG_CLAZZ_TAG = "configClass";

    public static void loadConfig(String config, List<AbstractDruidFilterConfig> druidFilterConfigList)
                                                                                                       throws SQLException {
        URL url = findResource(config);
        if (url != null) {
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                // Turn on validation, and turn off namespaces
                factory.setValidating(false);
                factory.setNamespaceAware(false);
                DocumentBuilder builder;

                builder = factory.newDocumentBuilder();

                Document doc;
                doc = builder.parse(new File(url.getPath()));
                if (doc == null) {
                    return;
                }

                Node filtersNode = doc.getFirstChild();
                if (filtersNode == null) {
                    return;
                }
                NodeList filterNodeList = filtersNode.getChildNodes();
                for (int i = 0; i < filterNodeList.getLength(); i++) {
                    Node filterNode = filterNodeList.item(i);
                    if (filterNode != null && filterNode.getNodeType() == Node.ELEMENT_NODE
                        && filterNode.getAttributes().getNamedItem(NAME_TAG) != null) {
                        Node configClassNode = filterNode.getAttributes().getNamedItem(CONFIG_CLAZZ_TAG);
                        if (configClassNode != null && !"".equalsIgnoreCase(configClassNode.getNodeValue())) {
                            Class<?> configClass = DruidLoaderUtils.loadClass(configClassNode.getNodeValue());
                            if (configClass != null) {
                                try {
                                    AbstractDruidFilterConfig druidFilterConfig = (AbstractDruidFilterConfig) configClass.newInstance();
                                    if (filterNode.getAttributes().getNamedItem(NAME_TAG) != null) {
                                        druidFilterConfig.setName(filterNode.getAttributes().getNamedItem(NAME_TAG).getNodeValue());
                                    }
                                    if (filterNode.getAttributes().getNamedItem(CLAZZ_TAG) != null) {
                                        druidFilterConfig.setClazz(filterNode.getAttributes().getNamedItem(CLAZZ_TAG).getNodeValue());
                                    }
                                    NodeList propNodeList = filterNode.getChildNodes();
                                    for (int j = 0; j < propNodeList.getLength(); j++) {
                                        Node propNode = propNodeList.item(j);
                                        if (propNode != null && propNode.getNodeType() == Node.ELEMENT_NODE) {
                                            Node nameNode = propNode.getAttributes().getNamedItem(NAME_TAG);
                                            Node valueNode = propNode.getAttributes().getNamedItem(VALUE_TAG);
                                            if (nameNode != null && valueNode != null) {
                                                Class<?> partypes[] = new Class[1];
                                                partypes[0] = String.class;
                                                Method meth = null;
                                                try {
                                                    String name1 = "set" + nameNode.getNodeValue();
                                                    meth = configClass.getMethod(name1, partypes);
                                                    Object arglist[] = new Object[1];
                                                    arglist[0] = valueNode.getNodeValue();
                                                    meth.invoke(druidFilterConfig, arglist);
                                                } catch (SecurityException e) {
                                                } catch (NoSuchMethodException e) {
                                                } catch (IllegalArgumentException e) {
                                                } catch (InvocationTargetException e) {
                                                }
                                            }
                                        }
                                    }
                                    druidFilterConfigList.add(druidFilterConfig);
                                } catch (InstantiationException e) {
                                } catch (IllegalAccessException e) {
                                } catch (SecurityException e) {
                                }
                            }
                        } else {
                            DruidFilterConfig druidFilterConfig = new DruidFilterConfig();
                            if (filterNode.getAttributes().getNamedItem(NAME_TAG) != null) {
                                druidFilterConfig.setName(filterNode.getAttributes().getNamedItem(NAME_TAG).getNodeValue());
                            }
                            if (filterNode.getAttributes().getNamedItem(CLAZZ_TAG) != null) {
                                druidFilterConfig.setClazz(filterNode.getAttributes().getNamedItem(CLAZZ_TAG).getNodeValue());
                            }
                            druidFilterConfigList.add(druidFilterConfig);
                        }
                    }
                }
            } catch (ParserConfigurationException e) {
            } catch (SAXException e) {
            } catch (IOException e) {
            }
        }
    }

    public static URL findResource(String resource) throws SQLException {
        try {
            return new URL(resource);
        } catch (MalformedURLException e) {
            return findClassLoaderResource(resource);
        }
    }

    private static URL findClassLoaderResource(String resource) throws SQLException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resource);

        if (url == null) {
            url = DruidDriver.class.getClassLoader().getResource(resource);
        }

        if (url == null) {
            url = ClassLoader.getSystemResource(resource);
        }

        if (url == null) {
            throw new SQLException("config not found:" + resource);
        }

        return url;
    }

}
