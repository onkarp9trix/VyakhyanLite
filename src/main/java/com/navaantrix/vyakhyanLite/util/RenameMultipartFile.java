package com.navaantrix.vyakhyanLite.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class RenameMultipartFile implements MultipartFile {

    private final MultipartFile delegate;
    private final String newFileName;

    public RenameMultipartFile(
            MultipartFile delegate,
            String newFileName
    ) {
        this.delegate = delegate;
        this.newFileName = newFileName;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getOriginalFilename() {
        return newFileName;
    }

    @Override
    public String getContentType() {
        return delegate.getContentType();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public long getSize() {
        return delegate.getSize();
    }

    @Override
    public byte[] getBytes() throws IOException {
        return delegate.getBytes();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return delegate.getInputStream();
    }

    @Override
    public void transferTo(File dest)
            throws IOException, IllegalStateException {
        delegate.transferTo(dest);
    }
}