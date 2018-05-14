package com.alibaba.druid.dfile;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class DFileReader {
    private final RandomAccessFile in;
    private final int rowCount;

    public DFileReader(RandomAccessFile in, int offset) throws IOException {
        this.in = in;
        this.rowCount = in.readInt();

        // block_count
        // 20
        // 1024 * 1024
    }

    public int getRowCount() {
        return rowCount;
    }
}
