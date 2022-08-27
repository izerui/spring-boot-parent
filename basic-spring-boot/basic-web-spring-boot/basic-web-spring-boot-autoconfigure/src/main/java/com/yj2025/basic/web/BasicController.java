package com.yj2025.basic.web;

import com.yj2025.basic.support.Context;
import com.yj2025.basic.web.support.AuthAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public abstract class BasicController implements AuthAware {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    protected ResponseEntity<byte[]> download(File localFile) throws IOException {
        byte[] bytes = Files.readAllBytes(localFile.toPath());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", localFile.getName());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(bytes.length);
        return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.CREATED);
    }

    protected ResponseEntity<byte[]> download(InputStream inputStream, String fileName) throws IOException {
        byte[] bytes = inputStream.readAllBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(bytes.length);
        return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.CREATED);
    }

    /**
     * 获取bean
     *
     * @param beanClass
     * @param <T>
     * @return
     */
    protected  <T> T $(Class<T> beanClass) {
        return Context.getBean(beanClass);
    }
}
