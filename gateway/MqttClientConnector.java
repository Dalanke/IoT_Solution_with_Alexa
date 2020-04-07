/**
 * This class provides a simple wrap of MQTT client. It contains connect/disconnect, publish/
 * subscribe functions and also implements the callback functions, defines what to do when receive 
 * message and when there is an connection failure. 
 * when connect to server, it support both unencrypted and TLS connection
 */


import java.util.logging.Logger;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.labbenchstudios.edu.connecteddevices.common.CertManagementUtil;
import com.labbenchstudios.edu.connecteddevices.common.ConfigConst;
import com.labbenchstudios.edu.connecteddevices.common.ConfigUtil;

public class MqttClientConnector {
	private MqttClient myClient;
	private MqttConnectOptions options;
	private Logger log = Logger.getLogger(MqttClientConnector.class.getSimpleName());
	// instantiate a certManagementUtil to load TLS certificate
	private CertManagementUtil certManager = CertManagementUtil.getInstance();

	/**
	 * setup unencrypted connection
	 * 
	 * @param clean :flag that indicates whether use clean connection
	 */

	public void setupConnection(boolean clean) {
		options = new MqttConnectOptions();
		options.setCleanSession(clean);
	}

	/**
	 * setting up encrypted tls connection
	 * 
	 * @param clean    :flag that indicates whether use clean connection
	 * @param username :user name
	 * @param password :password
	 * @param certfile :file path of certificate
	 */

	public void setupConnection(boolean clean, String username, String password, String certfile) {
		options = new MqttConnectOptions();
		options.setCleanSession(clean);
		options.setUserName(username);
		options.setPassword(password.toCharArray());
		options.setSocketFactory(certManager.loadCertificate(certfile));
	}

	/**
	 * connect to broker using pre-setup connect options
	 * 
	 * @param serverURI :serverURI
	 * @param clientId  :client ID
	 */

	public void connect(String serverURI, String clientId) {
		try {
			myClient = new MqttClient(serverURI, clientId, new MemoryPersistence());
			// setup call back by creating a new callback instance
			Clientcallback callback = new Clientcallback();
			myClient.setCallback(callback);
			// connect with connect options
			myClient.connect(options);
			log.info("Connected to broker:" + serverURI);

		} catch (MqttException e) {
			System.out.println("reason:" + e.getReasonCode());
			System.out.println("msg:" + e.getMessage());
		}
	}

	/**
	 * subscribe to topic
	 * 
	 * @param topic :topic to be subscribed
	 * @param qos   :QoS level (0-2)
	 */

	public void subscribeToTopic(String topic, int qos) {
		try {
			myClient.subscribe(topic, qos);
			log.info("Successfully subscribe to " + topic);
		} catch (MqttException e) {
			System.out.println("reason:" + e.getReasonCode());
			System.out.println("msg:" + e.getMessage());
		}
	}

	/**
	 * unsubscribe to topic
	 * 
	 * @param topic :unsubscribe this topic
	 */

	public void unsubscribeTopic(String topic) {
		try {
			myClient.unsubscribe(topic);
			log.info("Successfully unsubscribe to " + topic);
		} catch (MqttException e) {
			System.out.println("reason:" + e.getReasonCode());
			System.out.println("msg:" + e.getMessage());
		}
	}

	/**
	 * publish message to selected topic
	 * 
	 * @param topic    :topic to publish
	 * @param payload  :message
	 * @param qos      :QoS level(0-2)
	 * @param retained :whether server should retain this message
	 */

	public void publish(String topic, byte[] payload, int qos, boolean retained) {
		try {
			myClient.publish(topic, payload, qos, retained);
			log.info("Successfully published to " + topic + " payload=" + new String(payload));
		} catch (MqttException e) {
			System.out.println("reason:" + e.getReasonCode());
			System.out.println("msg:" + e.getMessage());
		}
	}

	/**
	 * disconnect client from broker
	 */

	public void disconnect() {
		try {
			myClient.disconnect();
			myClient.close();
			log.info("Disconnected");
		} catch (MqttException e) {
			System.out.println("reason:" + e.getReasonCode());
			System.out.println("msg:" + e.getMessage());
		}
	}

	/**
	 * callback functions for MQTT client
	 */

	private class Clientcallback implements MqttCallback {

		// what to do when lose connection
		@Override
		public void connectionLost(Throwable cause) {
			log.warning("Connection Lost!:" + cause.getMessage() + "try to reconnect");
		}

		/**
		 * what to do when message received if received actuator command, convert it
		 * into CoAP format to trigger actuator the return the result if received
		 * anything else, simply display it.
		 */
		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			// try to get code to control light 1=on 2=dim 3=brighten 0=off
			if (topic.equals("/v1.6/devices/led/action/lv")) {
				CoapHandler ledhandler = new LedHandler();
				CoapClientConnector coapclient = new CoapClientConnector("coap://192.168.123.3:5683/led/control");
				String code = new String(message.getPayload());
				if (code.equals("0")) {
					coapclient.POST(ledhandler, "off", 0);
				}
				if (code.equals("1")) {
					coapclient.POST(ledhandler, "on", 0);
				}
				if (code.equals("2")) {
					coapclient.POST(ledhandler, "dim", 0);
				}
				if (code.equals("3")) {
					coapclient.POST(ledhandler, "brighten", 0);
				}

			} else {
				String newline = System.getProperty("line.separator");
				String payload = new String(message.getPayload());
				String msg = "New message: " + "Topic=" + topic + ",QoS=" + message.getQos() + newline + payload;
				log.info(msg);
			}
		}

		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * This class implements CoapHandler that define how to handle the CoAP response
	 * (of LED) and convert it into MQTT format to publish into cloud
	 */

	private class LedHandler implements CoapHandler {
		private MqttClientConnector mqtt_client;

		public LedHandler() {
			super();
			ConfigUtil config = ConfigUtil.getInstance();
			// load configuration for mqtt and instantiate mqtt client
			if (config.loadConfig("/Users/conan/eclipse-workspace/iot-gateway/config/ConnectedDevicesConfig.props")) {
				// load mqtt certificate
				String cert = config.getProperty(ConfigConst.UBIDOTS_CLOUD_SECTION, ConfigConst.CLOUD_CERT_FILE);
				String token = config.getProperty(ConfigConst.UBIDOTS_CLOUD_SECTION, ConfigConst.USER_AUTH_TOKEN_KEY);
				mqtt_client = new MqttClientConnector();
				// set up TLS connection
				mqtt_client.setupConnection(true, token, "", cert);
				// connect to broker
				mqtt_client.connect("ssl://industrial.api.ubidots.com:8883", "temp");
			}
		}

		@Override
		public void onLoad(CoapResponse response) {
			if (response.getResponseText().equals("OK")) {
				// 1=OK
				String payload = "{\"value\":1}";
				mqtt_client.publish("/v1.6/devices/led/response", payload.getBytes(), 1, false);
			} else {
				// 0=fail
				String payload = "{\"value\":0}";
				mqtt_client.publish("/v1.6/devices/led/response", payload.getBytes(), 1, false);
			}

		}

		@Override
		public void onError() {
			// 0=fail
			String payload = "{\"value\":0}";
			mqtt_client.publish("/v1.6/devices/led/response", payload.getBytes(), 1, false);

		}

	}

}
