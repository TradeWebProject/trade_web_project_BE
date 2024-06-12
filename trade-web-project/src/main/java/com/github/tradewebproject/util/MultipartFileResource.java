package com.github.tradewebproject.util;

import org.springframework.core.io.ByteArrayResource;

public class MultipartFileResource extends ByteArrayResource {
    private final String filename;

    public MultipartFileResource(byte[] byteArray, String filename) {
        super(byteArray);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public long contentLength() {
        return getByteArray().length;
    }
}