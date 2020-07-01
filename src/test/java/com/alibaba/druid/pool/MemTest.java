package com.alibaba.druid.pool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class MemTest {

    HashMap               m     = new HashMap();
    HashMap               m1    = new HashMap(1);
    ConcurrentHashMap     cm    = new ConcurrentHashMap();
    ConcurrentHashMap     cm1   = new ConcurrentHashMap(1, 0.75f, 1);
    ConcurrentSkipListMap csm   = new ConcurrentSkipListMap();
    Hashtable             ht    = new Hashtable();
    Properties            p     = new Properties();
    TreeMap               t     = new TreeMap();
    LinkedHashMap         l     = new LinkedHashMap();

    TreeSet               ts    = new TreeSet();
    HashSet               hs    = new HashSet();
    LinkedHashSet         lhs   = new LinkedHashSet();

    ArrayList             list  = new ArrayList();
    ArrayList             list1 = new ArrayList(1);

    CopyOnWriteArrayList  cpl   = new CopyOnWriteArrayList();
    CopyOnWriteArraySet   cps   = new CopyOnWriteArraySet();
    Vector                v     = new Vector();
    LinkedList            ll    = new LinkedList();
    Stack                 stack = new Stack();
    PriorityQueue         pq    = new PriorityQueue();
    ConcurrentLinkedQueue clq   = new ConcurrentLinkedQueue();

    public static void main(String[] args) throws Exception {
        MemTest o = new MemTest();

        Thread.sleep(1000 * 1000);

        System.out.println(o);
    }
}
