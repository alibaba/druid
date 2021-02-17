/*
 * This software is released under a licence similar to the Apache Software Licence.
 * See org.logicalcobwebs.proxool.package.html for details.
 * The latest version is available at http://proxool.sourceforge.net
 */
package org.logicalcobwebs.proxool;


public interface ProxoolConstants {

    public final String PROXOOL = "proxool";

    /**
     * The namespace uri associated with namepace aware Proxool xml configurations.<br>
     * Value: The latest version is available at http://proxool.sourceforge.net/xml-namespace
     */
    public final String PROXOOL_XML_NAMESPACE_URI = "The latest version is available at http://proxool.sourceforge.net/xml-namespace";

    public final String ALIAS_DELIMITER = ".";

    public final String PROPERTY_PREFIX = PROXOOL + ".";

    public final String URL_DELIMITER = ":";

    /** Standard JDBC property */
    public final String USER_PROPERTY = "user";

    /** Standard JDBC property */
    public final String PASSWORD_PROPERTY = "password";

    /** Used to build up URL */
    public final String ALIAS_PROPERTY = PROPERTY_PREFIX + "alias";

    /** Instead of defining the driver in the url you can also use this property */
    public final String DELEGATE_DRIVER = "driver";

     public final String DELEGATE_DRIVER_PROPERTY = PROPERTY_PREFIX + DELEGATE_DRIVER;

    public final String DELEGATE_URL = "url";

     public final String DELEGATE_URL_PROPERTY = PROPERTY_PREFIX + DELEGATE_URL;

    public final String HOUSE_KEEPING_SLEEP_TIME = "house-keeping-sleep-time";

     public final String HOUSE_KEEPING_SLEEP_TIME_PROPERTY = PROPERTY_PREFIX + HOUSE_KEEPING_SLEEP_TIME;

    public final String HOUSE_KEEPING_TEST_SQL = "house-keeping-test-sql";

    public final String HOUSE_KEEPING_TEST_SQL_PROPERTY = PROPERTY_PREFIX + HOUSE_KEEPING_TEST_SQL;

    public final String TEST_BEFORE_USE = "test-before-use";

    public final String TEST_BEFORE_USE_PROPERTY = PROPERTY_PREFIX + TEST_BEFORE_USE;

    public final String TEST_AFTER_USE = "test-after-use";

    public final String TEST_AFTER_USE_PROPERTY = PROPERTY_PREFIX + TEST_AFTER_USE;

    public final String MAXIMUM_CONNECTION_COUNT = "maximum-connection-count";

    public final String MAXIMUM_CONNECTION_COUNT_PROPERTY = PROPERTY_PREFIX + MAXIMUM_CONNECTION_COUNT;

    public final String MAXIMUM_CONNECTION_LIFETIME = "maximum-connection-lifetime";

    public final String MAXIMUM_CONNECTION_LIFETIME_PROPERTY = PROPERTY_PREFIX + MAXIMUM_CONNECTION_LIFETIME;

    /**
     * @deprecated use {@link #SIMULTANEOUS_BUILD_THROTTLE} instead
     */
    public final String MAXIMUM_NEW_CONNECTIONS = "maximum-new-connections";

    /**
     * @deprecated use {@link #SIMULTANEOUS_BUILD_THROTTLE_PROPERTY} instead
     */
    public final String MAXIMUM_NEW_CONNECTIONS_PROPERTY = PROPERTY_PREFIX + MAXIMUM_NEW_CONNECTIONS;

    public final String SIMULTANEOUS_BUILD_THROTTLE = "simultaneous-build-throttle";

    public final String SIMULTANEOUS_BUILD_THROTTLE_PROPERTY = PROPERTY_PREFIX + SIMULTANEOUS_BUILD_THROTTLE;

    /** @see #MINIMUM_CONNECTION_COUNT_PROPERTY */
    public final String MINIMUM_CONNECTION_COUNT = "minimum-connection-count";

    public final String MINIMUM_CONNECTION_COUNT_PROPERTY = PROPERTY_PREFIX + MINIMUM_CONNECTION_COUNT;

    /** @see #PROTOTYPE_COUNT_PROPERTY */
    public final String PROTOTYPE_COUNT = "prototype-count";

    public final String PROTOTYPE_COUNT_PROPERTY = PROPERTY_PREFIX + PROTOTYPE_COUNT;

    public final String RECENTLY_STARTED_THRESHOLD = "recently-started-threshold";

    public final String RECENTLY_STARTED_THRESHOLD_PROPERTY = PROPERTY_PREFIX + RECENTLY_STARTED_THRESHOLD;

    public final String OVERLOAD_WITHOUT_REFUSAL_LIFETIME = "overload-without-refusal-lifetime";

    public final String OVERLOAD_WITHOUT_REFUSAL_LIFETIME_PROPERTY = PROPERTY_PREFIX + OVERLOAD_WITHOUT_REFUSAL_LIFETIME;

    public final String MAXIMUM_ACTIVE_TIME = "maximum-active-time";

    public final String MAXIMUM_ACTIVE_TIME_PROPERTY = PROPERTY_PREFIX + MAXIMUM_ACTIVE_TIME;

    public final String INJECTABLE_CONNECTION_INTERFACE_NAME = "injectable-connection-interface";

    public final String INJECTABLE_CONNECTION_INTERFACE_NAME_PROPERTY = PROPERTY_PREFIX + INJECTABLE_CONNECTION_INTERFACE_NAME;

    public final String INJECTABLE_STATEMENT_INTERFACE_NAME = "injectable-statement-interface";

    public final String INJECTABLE_STATEMENT_INTERFACE_NAME_PROPERTY = PROPERTY_PREFIX + INJECTABLE_STATEMENT_INTERFACE_NAME;

    public final String INJECTABLE_PREPARED_STATEMENT_INTERFACE_NAME = "injectable-prepared-statement-interface";

    public final String INJECTABLE_PREPARED_STATEMENT_INTERFACE_NAME_PROPERTY = PROPERTY_PREFIX + INJECTABLE_PREPARED_STATEMENT_INTERFACE_NAME;

    public final String INJECTABLE_CALLABLE_STATEMENT_INTERFACE_NAME = "injectable-callable-statement-interface";

    public final String INJECTABLE_CALLABLE_STATEMENT_INTERFACE_NAME_PROPERTY = PROPERTY_PREFIX + INJECTABLE_CALLABLE_STATEMENT_INTERFACE_NAME;

    /**
     * @deprecated use {@link #VERBOSE_PROPERTY verbose} instead.
     */
    public final String DEBUG_LEVEL_PROPERTY = PROPERTY_PREFIX + "debug-level";

    /** @see #VERBOSE_PROPERTY */
    public final String VERBOSE = "verbose";

    public final String VERBOSE_PROPERTY = PROPERTY_PREFIX + VERBOSE;

    /** @see #TRACE_PROPERTY */
    public final String TRACE = "trace";

    public final String TRACE_PROPERTY = PROPERTY_PREFIX + TRACE;

    /** @see #FATAL_SQL_EXCEPTION_PROPERTY **/
    public final String FATAL_SQL_EXCEPTION = "fatal-sql-exception";

    public final String FATAL_SQL_EXCEPTION_PROPERTY = PROPERTY_PREFIX + FATAL_SQL_EXCEPTION;

    /** @see #FATAL_SQL_EXCEPTION_WRAPPER_CLASS_PROPERTY**/
    public final String FATAL_SQL_EXCEPTION_WRAPPER_CLASS = "fatal-sql-exception-wrapper-class";

    public final String FATAL_SQL_EXCEPTION_WRAPPER_CLASS_PROPERTY = PROPERTY_PREFIX + FATAL_SQL_EXCEPTION_WRAPPER_CLASS;

    public static final String STATISTICS = "statistics";

    public final String STATISTICS_PROPERTY = PROPERTY_PREFIX + STATISTICS;

    public static final String STATISTICS_LOG_LEVEL = "statistics-log-level";

    public final String STATISTICS_LOG_LEVEL_PROPERTY = PROPERTY_PREFIX + STATISTICS_LOG_LEVEL;

    public static final String JNDI_NAME = "jndi-name";
    
    /** Prefix for generic JNDI properties. */
    public static final String JNDI_PROPERTY_PREFIX = "jndi-";

    public final String JNDI_NAME_PROPERTY = PROPERTY_PREFIX + JNDI_NAME;

// End JNDI

    public static final String STATISTICS_LOG_LEVEL_TRACE = "TRACE";

    public static final String STATISTICS_LOG_LEVEL_DEBUG = "DEBUG";

    public static final String STATISTICS_LOG_LEVEL_INFO = "INFO";

    /**
     * Element name for the container of properties passed directlry to the delegate driver.
     */
    public static final String DRIVER_PROPERTIES = "driver-properties";


    /**
     * Configuration attribute used to indicate that a pool should be registered with JMX.
     */
    public static final String JMX = "jmx";

    /**
     * "proxool." prefixed version of {@link #JMX}.
     */
    public final String JMX_PROPERTY = PROPERTY_PREFIX + JMX;

    public static final String JMX_AGENT_ID = "jmx-agent-id";

    /**
     * "proxool." prefixed version of {@link #JMX_AGENT_ID}.
     */
    public final String JMX_AGENT_PROPERTY = PROPERTY_PREFIX + JMX_AGENT_ID;

    /**
     *  Un-prefixed propety name for the Proxool alias configuration property. Value: alias
     */
    public final String ALIAS = "alias";

    /**
     *  Un-prefixed propety name for the Proxool driver class  configuration property. Value: driver-class
     */
    public final String DRIVER_CLASS = "driver-class";
    /**
     *  Prefixed propety name for the Proxool driver class  configuration property. Value: proxool.driver-class
     */
    public final String DRIVER_CLASS_PROPERTY = PROPERTY_PREFIX + DRIVER_CLASS;;
    /**
     *  Un-prefixed propety name for the Proxool driver url configuration property. Value: driver-url
     */
    public final String DRIVER_URL = "driver-url";
    /**
     *  Prefixed propety name for the Proxool driver url configuration property. Value: proxool.driver-url
     */
    public final String DRIVER_URL_PROPERTY = PROPERTY_PREFIX + DRIVER_URL;
}

/*
 Revision history:
 $Log: ProxoolConstants.java,v $
 Revision 1.21  2004/06/02 20:39:17  billhorsman
 New injectable interface constants

 Revision 1.20  2004/03/15 02:43:47  chr32
 Removed explicit JNDI properties. Going for a generic approach instead.
 Added constant for JNDI properties prefix.

 Revision 1.19  2003/09/30 18:39:08  billhorsman
 New test-before-use, test-after-use and fatal-sql-exception-wrapper-class properties.

 Revision 1.18  2003/09/29 17:48:21  billhorsman
 New fatal-sql-exception-wrapper-class allows you to define what exception is used as a wrapper. This means that you
 can make it a RuntimeException if you need to.

 Revision 1.17  2003/09/05 17:00:42  billhorsman
 New wrap-fatal-sql-exceptions property.

 Revision 1.16  2003/07/23 06:54:48  billhorsman
 draft JNDI changes (shouldn't effect normal operation)

 Revision 1.15  2003/03/05 23:28:56  billhorsman
 deprecated maximum-new-connections property in favour of
 more descriptive simultaneous-build-throttle

 Revision 1.14  2003/03/03 11:11:58  billhorsman
 fixed licence

 Revision 1.13  2003/02/26 16:05:52  billhorsman
 widespread changes caused by refactoring the way we
 update and redefine pool definitions.

 Revision 1.12  2003/02/24 18:02:24  chr32
 Added JMX related constants.

 Revision 1.11  2003/02/24 01:16:15  chr32
 Added constant for "driver-properties" property.

 Revision 1.10  2003/02/06 15:41:17  billhorsman
 add statistics-log-level

 Revision 1.9  2003/01/30 17:22:03  billhorsman
 new statistics property

 Revision 1.8  2003/01/23 10:41:05  billhorsman
 changed use of pool-name to alias for consistency

 Revision 1.7  2002/12/26 11:32:22  billhorsman
 Moved ALIAS, DRIVER_URL and DRIVER_CLASS constants
 from XMLConfgiurator to ProxoolConstants.

 Revision 1.6  2002/12/15 19:22:51  chr32
 Added constant for proxool xml namespace.

 Revision 1.5  2002/12/11 01:47:12  billhorsman
 extracted property names without proxool. prefix for use
 by XMLConfigurators.

 Revision 1.4  2002/11/09 15:50:49  billhorsman
 new trace constant

 Revision 1.3  2002/10/27 13:29:38  billhorsman
 deprecated debug-level in favour of verbose

 Revision 1.2  2002/10/25 15:59:32  billhorsman
 made non-public where possible

 Revision 1.1.1.1  2002/09/13 08:13:06  billhorsman
 new

 Revision 1.3  2002/08/24 19:57:15  billhorsman
 checkstyle changes

 Revision 1.2  2002/07/12 23:03:22  billhorsman
 added doc headers

 Revision 1.7  2002/07/10 16:14:47  billhorsman
 widespread layout changes and move constants into ProxoolConstants

 Revision 1.6  2002/07/02 11:19:08  billhorsman
 layout code and imports

 Revision 1.5  2002/07/02 08:27:47  billhorsman
 bug fix when settiong definition, displayStatistics now available to ProxoolFacade, prototyper no longer attempts to make connections when maximum is reached

 Revision 1.4  2002/06/28 11:19:47  billhorsman
 improved doc

*/
