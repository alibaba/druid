package com.alibaba.druid.filter.config.security.tool;

/**
 * @author Jonas Yang
 */
public class QuitAction implements Action {

    @Override
    public String getId() {
        return "QUIT";
    }

    public void execute() {
        foot();
        System.exit(0);
    }

    private static void foot() {
        System.out.println("**************************************");
        System.out.println("*                                    *");
        System.out.println("*           再    见   了             *");
        System.out.println("*                                    *");
        System.out.println("**************************************");
    }
}
