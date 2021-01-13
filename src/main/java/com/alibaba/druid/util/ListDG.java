/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.util;

/**
 * Java: 无回路有向图(Directed Acyclic Graph)的拓扑排序
 *       该DAG图是通过邻接表实现的。
 *
 * author skywang
 * date 2014/04/22
 */
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ListDG {
    public static class Edge {
        public Object from;
        public Object to;

        public Edge(Object from, Object to) {
            this.from = from;
            this.to = to;
        }
    }

    // 邻接表中表对应的链表的顶点
    private class ENode {
        int ivex;       // 该边所指向的顶点的位置
        ENode nextEdge; // 指向下一条弧的指针
    }

    // 邻接表中表的顶点
    private class VNode {
        Object data;          // 顶点信息
        ENode firstEdge;    // 指向第一条依附该顶点的弧
    };

    private List<VNode> mVexs;  // 顶点数组

    /*
     * 创建图(用已提供的矩阵)
     *
     * 参数说明：
     *     vexs  -- 顶点数组
     *     edges -- 边数组
     */
    public ListDG(List vexs, List<Edge> edges) {

        // 初始化"顶点数"和"边数"
        int vlen = vexs.size();
        int elen = edges.size();

        // 初始化"顶点"
        mVexs = new ArrayList<VNode>();
        for (int i = 0; i < vlen; i++) {
            // 新建VNode
            VNode vnode = new VNode();
            vnode.data = vexs.get(i);
            vnode.firstEdge = null;
            // 将vnode添加到数组mVexs中
            mVexs.add(vnode);
        }

        // 初始化"边"
        for (int i = 0; i < elen; i++) {
            // 读取边的起始顶点和结束顶点
            Object c1 = edges.get(i).from;
            Object c2 = edges.get(i).to;
            // 读取边的起始顶点和结束顶点
            int p1 = getPosition(edges.get(i).from);
            int p2 = getPosition(edges.get(i).to);

            // 初始化node1
            ENode node1 = new ENode();
            node1.ivex = p2;
            // 将node1链接到"p1所在链表的末尾"
            if(mVexs.get(p1).firstEdge == null)
                mVexs.get(p1).firstEdge = node1;
            else
                linkLast(mVexs.get(p1).firstEdge, node1);
        }
    }

    /*
     * 将node节点链接到list的最后
     */
    private void linkLast(ENode list, ENode node) {
        ENode p = list;

        while(p.nextEdge!=null)
            p = p.nextEdge;
        p.nextEdge = node;
    }

    /*
     * 返回ch位置
     */
    private int getPosition(Object ch) {
        for(int i=0; i<mVexs.size(); i++)
            if(mVexs.get(i).data == ch)
                return i;
        return -1;
    }

    /*
     * 深度优先搜索遍历图的递归实现
     */
    private void DFS(int i, boolean[] visited) {
        ENode node;

        visited[i] = true;
        node = mVexs.get(i).firstEdge;
        while (node != null) {
            if (!visited[node.ivex])
                DFS(node.ivex, visited);
            node = node.nextEdge;
        }
    }

    /*
     * 深度优先搜索遍历图
     */
    public void DFS() {
        boolean[] visited = new boolean[mVexs.size()];       // 顶点访问标记

        // 初始化所有顶点都没有被访问
        for (int i = 0; i < mVexs.size(); i++)
            visited[i] = false;

        for (int i = 0; i < mVexs.size(); i++) {
            if (!visited[i])
                DFS(i, visited);
        }
    }

    /*
     * 广度优先搜索（类似于树的层次遍历）
     */
    public void BFS() {
        int head = 0;
        int rear = 0;
        int[] queue = new int[mVexs.size()];            // 辅组队列
        boolean[] visited = new boolean[mVexs.size()];  // 顶点访问标记

        for (int i = 0; i < mVexs.size(); i++)
            visited[i] = false;

        for (int i = 0; i < mVexs.size(); i++) {
            if (!visited[i]) {
                visited[i] = true;
                System.out.printf("%c ", mVexs.get(i).data);
                queue[rear++] = i;  // 入队列
            }

            while (head != rear) {
                int j = queue[head++];  // 出队列
                ENode node = mVexs.get(j).firstEdge;
                while (node != null) {
                    int k = node.ivex;
                    if (!visited[k])
                    {
                        visited[k] = true;
                        System.out.printf("%c ", mVexs.get(k).data);
                        queue[rear++] = k;
                    }
                    node = node.nextEdge;
                }
            }
        }
    }

    /*
     * 打印矩阵队列图
     */
    public void print() {
        System.out.printf("== List Graph:\n");
        for (int i = 0; i < mVexs.size(); i++) {
            System.out.printf("%d(%c): ", i, mVexs.get(i).data);
            ENode node = mVexs.get(i).firstEdge;
            while (node != null) {
                System.out.printf("%d(%c) ", node.ivex, mVexs.get(node.ivex).data);
                node = node.nextEdge;
            }
        }
    }

    public boolean topologicalSort() {
        return topologicalSort(new Object[mVexs.size()]);
    }

    /*
     * 拓扑排序
     *
     * 返回值：
     *     true 成功排序，并输入结果
     *     false 失败(该有向图是有环的)
     */
    public boolean topologicalSort(Object[] tops) {
        int index = 0;
        int num = mVexs.size();
        int[] ins;               // 入度数组
        //Object[] tops;             // 拓扑排序结果数组，记录每个节点的排序后的序号。
        Queue<Integer> queue;    // 辅组队列

        ins   = new int[num];
        //tops  = new Object[num];
        queue = new LinkedList<Integer>();

        // 统计每个顶点的入度数
        for(int i = 0; i < num; i++) {

            ENode node = mVexs.get(i).firstEdge;
            while (node != null) {
                ins[node.ivex]++;
                node = node.nextEdge;
            }
        }

        // 将所有入度为0的顶点入队列
        for(int i = 0; i < num; i ++)
            if(ins[i] == 0)
                queue.offer(i);                 // 入队列

        while (!queue.isEmpty()) {              // 队列非空
            int j = queue.poll().intValue();    // 出队列。j是顶点的序号
            tops[index++] = mVexs.get(j).data;  // 将该顶点添加到tops中，tops是排序结果
            ENode node = mVexs.get(j).firstEdge;// 获取以该顶点为起点的出边队列

            // 将与"node"关联的节点的入度减1；
            // 若减1之后，该节点的入度为0；则将该节点添加到队列中。
            while(node != null) {
                // 将节点(序号为node.ivex)的入度减1。
                ins[node.ivex]--;
                // 若节点的入度为0，则将其"入队列"
                if( ins[node.ivex] == 0)
                    queue.offer(node.ivex);    // 入队列

                node = node.nextEdge;
            }
        }

        if(index != num) {
            return false;
        }

        return true;
    }

//    public static void main(String[] args) {
//        Object[] vexs = {'A', 'B', 'C', 'D', 'E', 'F', 'G'};
//        Edge[] edges = new Edge[]{
//                new Edge(vexs[0], vexs[6]),
//                new Edge(vexs[1], vexs[0]),
//                new Edge(vexs[1], vexs[3]),
//                new Edge(vexs[2], vexs[5]),
//                new Edge(vexs[2], vexs[6]),
//                new Edge(vexs[3], vexs[4]),
//                new Edge(vexs[3], vexs[5])};
//        ListDG pG;
//
//        // 自定义"图"(输入矩阵队列)
//        //pG = new ListDG();
//        // 采用已有的"图"
//        pG = new ListDG(Arrays.asList(vexs), Arrays.asList(edges));
//
//        pG.print();   // 打印图
//        //pG.DFS();     // 深度优先遍历
//        //pG.BFS();     // 广度优先遍历
//        pG.topologicalSort();     // 拓扑排序
//    }
}