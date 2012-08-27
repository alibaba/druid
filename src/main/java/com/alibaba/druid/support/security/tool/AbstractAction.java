package com.alibaba.druid.support.security.tool;

/**
 * @author Jonas Yang
 */
public abstract class AbstractAction implements Action {
    private int minKeyLength;

    private int maxKeyLength;

    private String prompt;

    public AbstractAction(int minKeyLength, int maxKeyLength, String prompt) {
        if (minKeyLength < 0 || maxKeyLength < 0) {
            throw new IllegalArgumentException("The key length must be a positive.");
        }

        if (maxKeyLength < minKeyLength) {
            throw new IllegalArgumentException("The max length of key cannot be less than min length.");
        }

        this.minKeyLength = minKeyLength;
        this.maxKeyLength = maxKeyLength;
        this.prompt = "[" + prompt + "]";
    }

    protected abstract String encrypt(String keyString, String plainString) throws Exception;

    @Override
    public void execute() {
        System.out.println();

        String keyString = readPassword();
        if (keyString == null) return;

        String plainString = System.console().readLine(this.prompt + "请输入要需要加密的明文: ");
        if (plainString == null) {
            System.err.println("输入不能为空.");
            return;
        }

        try {
            String encryptedString = encrypt(keyString, plainString);
            System.out.println("请记住以下的密文, 长度为[" + encryptedString.length() + "].");
            System.out.println();
            System.out.println(encryptedString);
            System.out.println();
        } catch (Exception e) {
            System.out.println("加密出错.");
            e.printStackTrace();
        }
    }

    protected String readPassword() {
        char[] keyChars = System.console().readPassword(this.prompt + "请输入密码[长度最大为" + this.maxKeyLength + "字节]: ");
        if (keyChars == null) {
            keyChars = new char[0];
        }

        String keyString = new String(keyChars);

        if (keyString.length() < this.minKeyLength || keyString.length() > this.maxKeyLength) {
            System.err.println("错误: 密码长度要大于等于" + this.minKeyLength + ", 小于等于" + this.maxKeyLength + ".");
            return null;
        }

        keyString = KEY_PADDING.substring(0, this.maxKeyLength - keyString.length()) + keyString;

        return keyString;
    }

}
