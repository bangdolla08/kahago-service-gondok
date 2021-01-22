package com.kahago.kahagoservice.broadcast;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * @author Hendro yuwono
 */
@Component
@Log4j2
public class MailerComponent {

    @Autowired
    private Message message;

    public static String SUBJECT_PAY = "Payment Notification";
	public static String SUBJECT_DEP = "Topup Notification";
	
    @Async
    public void sendPasswordSignUp(String username, String password, String email) throws MessagingException {

        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject("Registration Completed Kaha Go");
        message.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        String content = "<p>Terima kasih telah register di KAHAGO. Berikut User Login di aplikasi.</p>" +
                "<h3>Username : " + username + "</h3>" +
                "<h3>Password : " + password + "</h3>" +
                "</br></br>" +
                "<h3>Note:</h3>" +
                "<h3>Segera ganti password anda untuk keamanan dari akun yang telah teregister.</h3>";
        messageBodyPart.setContent(content, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        message.setContent(multipart);
        Transport.send(message);
        log.info("Email successfully sent to {} ", email);
    }

    @Async
    public void sendResetPassword(String email, String token, String url) throws MessagingException {
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject("Reset Password Kaha Go");
        message.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        String content = "<h3>Silahkan klik url dibawah ini untuk Reset Password</h3>"
                + "<br><h4>" + url + "/reset-password?token=" + token + "</h4>";
        messageBodyPart.setContent(content, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        message.setContent(multipart);
        Transport.send(message);
        log.info("Email successfully sent to {} ", email);
    }

    @Async
    public void sendNotifDeposit(String emailto,String emailcc,String content,String Subject) throws AddressException, MessagingException, IOException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.zoho.com");
        props.put("mail.smtp.host", "smtp.zoho.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("no-reply@kahago.com", "S0n!cM@ster");
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("no-reply@kahago.com", false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailto));
        msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailcc));
        msg.setSubject(Subject);
//		   msg.setContent("<h3>Berikut adalah dokumentasinya</h3>", "text/html");
        msg.setSentDate(new Date());

        String data = content;
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(data, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
//		   MimeBodyPart attachPart = new MimeBodyPart();
//
//		   attachPart.attachFile("/home/web/API Partner Pickup v1.2.pdf");
//		   multipart.addBodyPart(attachPart);
        msg.setContent(multipart);
        Transport.send(msg);
    }


    public static String getContent() {
        String content = "<p>Terdapat transaksi <b>#judul</b> dengan informasi sebagai berikut:</p>" +
                "<table>" +
                "<tr>" +
                "<td>Nomor Tiket</td>" +
                "<td>:</td>" +
                "<td>#notiket</td>" +
                "</tr>" +
                "<tr>" +
                "<td>User Id</td>" +
                "<td>:</td>" +
                "<td>#userid</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Nominal</td>" +
                "<td>:</td>" +
                "<td>#nominal</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Nama Bank</td>" +
                "<td>:</td>" +
                "<td>#namabank</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Nomor Akun</td>" +
                "<td>:</td>" +
                "<td>#noakun</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Nama Akun</td>" +
                "<td>:</td>" +
                "<td>#namaakun</td>" +
                "</tr>" +
                "<tr>" +
                "<td>Keterangan</td>" +
                "<td>:</td>" +
                "<td>#ket</td>" +
                "</tr>" +
                "</table>" +
                "<p>Mohon segera melakukan pengecekan di <a href=\"#urlboc\">Back Office</a> "
                + "atas transaksi #judul dan memberi persetujuan jika transaksi telah diterima. "
                + "Terima Kasih</p>";
        return content;
    }
}
