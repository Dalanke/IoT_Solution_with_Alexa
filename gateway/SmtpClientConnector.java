

import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.labbenchstudios.edu.connecteddevices.common.ConfigConst;
import com.labbenchstudios.edu.connecteddevices.common.ConfigUtil;

/**
 * A class for sending email, account/server information read from configuration
 * file
 */
public class SmtpClientConnector {
	private Boolean success = false; // indicate whether message successfully sent
	private Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * send the email
	 * 
	 * @param playload The content of email
	 * @param subject  The subject of email
	 * @return boolean,true indicates email sent successfully
	 */
	public boolean publishMessage(String playload, String subject) {
		// read configuration from config file
		log.info("Loading configuration for SMTP...");
		ConfigUtil conf = ConfigUtil.getInstance();
		conf.loadConfig("/Users/conan/eclipse-workspace/iot-gateway/config/ConnectedDevicesConfig.props");
		String port = conf.getProperty(ConfigConst.SMTP_CLOUD_SECTION, ConfigConst.PORT_KEY);
		String host = conf.getProperty(ConfigConst.SMTP_CLOUD_SECTION, ConfigConst.HOST_KEY);
		final String fromAddr = conf.getProperty(ConfigConst.SMTP_CLOUD_SECTION, ConfigConst.FROM_ADDRESS_KEY);
		String toAddr = conf.getProperty(ConfigConst.SMTP_CLOUD_SECTION, ConfigConst.TO_ADDRESS_KEY);
		final String password = conf.getProperty(ConfigConst.SMTP_CLOUD_SECTION, ConfigConst.USER_AUTH_TOKEN_KEY);
		// create a session for smtp connection
		// set up session properties first
		Properties props = new Properties();
		props.put(ConfigConst.SMTP_PROP_HOST_KEY, host);
		props.put(ConfigConst.SMTP_PROP_PORT_KEY, port);
		props.put(ConfigConst.SMTP_PROP_AUTH_KEY, ConfigConst.ENABLE_AUTH_KEY);
		props.put(ConfigConst.SMTP_PROP_ENABLE_TLS_KEY, true);
		try {
			// use properties to initialize session, note that for TLS use port 587!
			Session smtpSession = Session.getInstance(props);
			// for debug
			// smtpSession.setDebug(true);
			// create a message object
			Message smtpMsg = new MimeMessage(smtpSession);
			smtpMsg.setFrom(new InternetAddress(fromAddr));
			// type: TO/BCC/CC
			smtpMsg.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(toAddr));
			smtpMsg.setSubject(subject);
			smtpMsg.setText(playload);
			// use transport static method to send mail
			Transport.send(smtpMsg, fromAddr, password);
			success = true;
			log.info("Message sent successful");
		} catch (Exception e) {
			log.warning("Failed to send SMTP message" + e);
		}

		return success;
	}
}
