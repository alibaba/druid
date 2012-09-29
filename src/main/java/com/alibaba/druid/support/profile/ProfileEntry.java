package com.alibaba.druid.support.profile;

public class ProfileEntry {

    private final Key    key;
    private final String type;

    public ProfileEntry(Key key, String type){
        super();
        this.key = key;
        this.type = type;
    }

    public Key getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public static class Key {

        private String parent;
        private String name;

        public Key(String parentName, String name){
            super();
            this.parent = parentName;
            this.name = name;
        }

        public String getParent() {
            return parent;
        }

        public String getName() {
            return name;
        }

    }
}
