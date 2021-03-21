package com.util;

import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

public class JavaMailSenderImpl {
    private String Host;

    private String Username;

    private String Password;

    private Boolean Property;

    public void setHost(String host) {
        Host = host;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setProperty(Boolean property) {
        Property = property;
    }

    public JavaMailSenderImpl(String host, String username, String password, Boolean property) {
        Host = host;
        Username = username;
        Password = password;
        Property = property;
    }
    public JavaMailSender GetJavaMailSender(){
        org.springframework.mail.javamail.JavaMailSenderImpl jms = new org.springframework.mail.javamail.JavaMailSenderImpl();
        jms.setHost(Host);
        //jms.setPort(994);
        jms.setUsername(Username);
        jms.setPassword(Password);
        jms.setDefaultEncoding("Utf-8");
        Properties p = new Properties();
        p.setProperty("mail.smtp.auth", Property.toString());
        jms.setJavaMailProperties(p);
        return jms; 
    }
}
