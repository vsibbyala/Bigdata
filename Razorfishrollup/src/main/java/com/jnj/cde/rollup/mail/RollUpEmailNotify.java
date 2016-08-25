package com.jnj.cde.rollup.mail;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

//import com.jnj.cde.workflow.exception.CDEWorkflowException;
import com.sun.mail.util.MailSSLSocketFactory;

/**
 * This is a utility class for the email notification.
 * 
 * @author vsibbyal
 *
 */
public class RollUpEmailNotify {

	/** Variable for the Logger */
	final static Logger LOG = Logger.getLogger(RollUpEmailNotify.class);

	public RollUpEmailNotify() {
	}

	public static void createMessageAndSendMail(String argBody,
			String argSubject, String argFromAddress, String argToAddresses)
			throws Exception {
		LOG.info("Entered createMessageAndSendMail()");
		try {
			Properties properties = new Properties();

			// Setup mail server
			properties.setProperty("mail.smtp.host", "smtp.aa.xxx.com");
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.port", "645");

			MailSSLSocketFactory sf = new MailSSLSocketFactory();
			sf.setTrustAllHosts(true);
			properties.put("mail.smtp.ssl.socketFactory", sf);

			// Get the default Session object.
			Session session = Session.getInstance(properties);
			session.setDebug(true);

			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			message.setFrom(new InternetAddress(argFromAddress));
			Address[] toAddresses = InternetAddress.parse(argToAddresses);

			message.addRecipients(Message.RecipientType.TO, toAddresses);

			message.setSubject(argSubject);
			// message.setText(String.format(argBody));
			message.setContent(argBody, "text/html");

			// Send message
			Transport.send(message);
			LOG.info("Email Sent Successfully....");
		} catch (RuntimeException objRuntimeException) {
			LOG.error("Runtime Exception occurred.The email was not sent.",
					objRuntimeException);
			throw new Exception(
					"Runtime Exception occurred.The email was not sent.");
		} catch (Exception objException) {
			LOG.error("Exception occurred.The email was not sent.",
					objException);
			throw objException;
		}
		LOG.info("Exiting createMessageAndSendMail()");
	}
}
