package com.alibaba.druid.support.security.tool;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jonas Yang
 */
public class HelpAction implements Action {

    private List<ActionDesc> list = new ArrayList<ActionDesc>();

    @Override
    public String getId() {
        return "HELP";
    }

    public void execute() {
        System.out.println();
        for (ActionDesc desc : list) {
            System.out.println(desc.getKey() + ". " + desc.getAction().getId());
        }
    }

    public void addAction(String key, Action action) {
        this.list.add(new ActionDesc(key, action));
    }

    public Action getAction(String key) {
        for (ActionDesc desc : list) {
            if (desc.getKey().equals(key)) {
                return desc.getAction();
            }
        }

        return null;
    }

    private static class ActionDesc {
        private final String key;
        private final Action action;

        public ActionDesc(String key, Action action) {
            this.key = key;
            this.action = action;
        }

        public Action getAction() {
            return action;
        }

        public String getKey() {
            return key;
        }
    }
}
