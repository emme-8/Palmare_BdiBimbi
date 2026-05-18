package com.emme.palmarebdibimbi;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailSender extends javax.mail.Authenticator {
    private String user;
    private String password;
    private Session session;

    public MailSender(String user, String password, String host, String port) {
        this.user = user;
        this.password = password;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.ssl.trust", host);

        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
    }

    public void sendMail(String subject, String body, String senderName, String to, String cc, File attachment) throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(user, senderName));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        if (cc != null && !cc.isEmpty()) {
            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
        }
        message.setSubject(subject);

        Multipart multipart = new MimeMultipart();

        // Corpo testo
        MimeBodyPart textBodyPart = new MimeBodyPart();
        textBodyPart.setText(body, "utf-8");
        multipart.addBodyPart(textBodyPart);

        // Allegato
        if (attachment != null && attachment.exists()) {
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(attachment);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName(attachment.getName());
            multipart.addBodyPart(attachmentBodyPart);
        }

        message.setContent(multipart);
        Transport.send(message);
    }
}