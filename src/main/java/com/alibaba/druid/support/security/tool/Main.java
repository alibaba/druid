package com.alibaba.druid.support.security.tool;

/**
 * <pre>
 * Druid 提供的加密工具. 如下运行
 *
 * <b>java -cp druid-x.x.x.jar com.alibaba.druid.support.security.tool.Main</b>
 *
 * </pre>
 * @author Jonas Yang
 */
public class Main {
    private static boolean isEnd = false;

    private static HelpAction help = new HelpAction();

    static {
        int i = 1;
        help.addAction(Integer.toString(i++), new RsaAction());
        help.addAction(Integer.toString(i++), new DesAction());
        help.addAction(Integer.toString(i++), new AesAction());
        help.addAction(Integer.toString(i++), new BlowfishAction());
        help.addAction("h", help);
        help.addAction("q", new QuitAction());
    }

    public static void main(String[] args) {
        header();
        help.execute();

        while(!isEnd) {
            String input = System.console().readLine("请输入选项: ");
            input = input.toLowerCase();
            Action action = help.getAction(input);
            if (action == null) {
                System.err.println(input + " 不是一个合法的选项.[如需要帮助请输入 h]");
                continue;
            } else {
                action.execute();
            }
        }
    }

    public static void header() {
        System.out.println("**************************************");
        System.out.println("*                                    *");
        System.out.println("*           Druid 加密工具            *");
        System.out.println("*                                    *");
        System.out.println("**************************************");
    }
}
