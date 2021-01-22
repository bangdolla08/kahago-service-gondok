package com.kahago.kahagoservice.util;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


import com.kahago.kahagoservice.enummodel.ApprovalTopUpEnum;
public class EmailSender {
	public static void sendNotifDeposit(String emailto,String emailcc,String content) throws AddressException, MessagingException, IOException {
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
		if(!emailcc.isEmpty())
			msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailcc));
		msg.setSubject("Topup Notification");
		msg.setSentDate(new Date());

		String data = content;
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(data, "text/html");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		msg.setContent(multipart);
		Transport.send(msg);
	}
	public static void sendSTT(String emailto,String emailcc,String content,String vendorName) throws AddressException, MessagingException, IOException {
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
		if(!emailcc.isEmpty())
			msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailcc));
//		if(!emailcc2.isEmpty())
//			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailcc2));
		msg.setSubject("Warning STT "+vendorName);
		msg.setSentDate(new Date());

		String data = content;
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(data, "text/html");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		msg.setContent(multipart);
		Transport.send(msg);
	}
	public static String getContent(Integer count,String vendor){
		String content ="<p>Dear All</p>" +
				"<p>STT Untuk Vendor  <b>"+vendor+"</b> Tersisa Sebanyak "+count+"</p>";
		return content;
	}
	public String getContent() {
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
				+ "atas transaksi TOP UP dan memberi persetujuan jika transaksi telah diterima. "
				+ "Terima Kasih</p>";
		return content;
	}

	public static String getContentEmailApproval(boolean isTopUpDeposit,String notiket,String userId,String nominal,String bankName,String accountNo,String accountName,String notes,ApprovalTopUpEnum approvalTopUpEnum){
		String content=null;
		String title="";
		if (isTopUpDeposit)
			title="TOP UP DEPOSIT";
		else
			title="TOP UP CREDIT";
		title = "Pembayaran";
		content="<p>"+title+" dengan informasi sebagai berikut:</p>" +
				"<table>" +
				"<tr>" +
				"<td>Nomor Tiket</td>" +
				"<td>:</td>" +
				"<td>"+notiket+"</td>" +
				"</tr>" +
				"<tr>" +
				"<td>User Id</td>" +
				"<td>:</td>" +
				"<td>"+userId+"</td>" +
				"</tr>" +
				"<tr>" +
				"<td>Nominal</td>" +
				"<td>:</td>" +
				"<td> Rp "+nominal+"</td>" +
				"</tr>" +
				"<tr>" +
				"<td>Nama Bank</td>" +
				"<td>:</td>" +
				"<td>"+bankName+"</td>" +
				"</tr>" +
				"<tr>" +
				"<td>Nomor Akun</td>" +
				"<td>:</td>" +
				"<td>"+accountNo+"</td>" +
				"</tr>" +
				"<tr>" +
				"<td>Nama Akun</td>" +
				"<td>:</td>" +
				"<td>"+accountName+"</td>" +
				"</tr>" +
				"<tr>" +
				"<td>Keterangan</td>" +
				"<td>:</td>" +
				"<td>"+notes+"</td>" +
				"</tr>" +
				"</table>";
		if(approvalTopUpEnum==ApprovalTopUpEnum.APPROVE)
			content+="<p>Telah <b>DITERIMA</b>, silahkan melanjutkan transaksi. Terimakasih telah menggunakan KAHAGO untuk melakukan pengiriman barang anda</p>";
		else
			content+="<p><b>Belum Kami Terima</b>, silahkan melakukan konfirmasi ke pihak KAHAGO jika sudah melakukan pembayaran. Terimakasih telah menggunakan KAHAGO untuk melakukan pengiriman barang anda</p>";
		return content;
	}
	
	public static void sendNotifError(String emailto,String emailcc,String content,String environment) throws AddressException, MessagingException, IOException{
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
		if(!emailcc.isEmpty())
			msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailcc));
		msg.setSubject("Error Auto Resi - "+environment);
		msg.setSentDate(new Date());
		String data = content;
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(data, "text/html");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);
		msg.setContent(multipart);
		Transport.send(msg);
	}
	
	public static String getContentError(String bookingCode,String message) {
		String content = "<p>Pesanan dengan Booking Code : <b>"+bookingCode+"</b> mengalami Error : <b>"+message+ "</b>.</p>";
		
		return content;
	}
}
