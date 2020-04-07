/**
 * This is the core function of the gateway APP, will poll sensor regularly using CoAP 
 * and convert it into MQTT message to publish. It also subscribes to MQTT topic to receive
 * command that trigger actuator.
 */


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

import com.labbenchstudios.edu.connecteddevices.common.ConfigConst;
import com.labbenchstudios.edu.connecteddevices.common.ConfigUtil;

public class GatewayAdaptor implements Runnable {
	// a list to store active device
	private ArrayList<String> active_device = new ArrayList<String>();
	private ConfigUtil config;
	private MqttClientConnector mqtt_client;
	private Date timeStamp;
	private Logger log = Logger.getLogger(GatewayAdaptor.class.getName());
	// default interval
	private int interval = 40;
	private SmtpClientConnector smtp_client = new SmtpClientConnector();
	// default threshold that trigger temperature warning
	private float threshold = 23.0F;
	// set a flag to indicate whether sent SMTP recently
	private boolean duplicated = false;
	// set a number that after how many intervals, duplicated flag will be
	// automatically set to false
	private int cooldown = 8;

	/**
	 * default constructor load configuration and instantiate mqtt client there
	 */
	public GatewayAdaptor() {
		super();
		config = ConfigUtil.getInstance();
		// load configuration for mqtt and instantiate mqtt client
		if (config.loadConfig("/Users/conan/eclipse-workspace/iot-gateway/config/ConnectedDevicesConfig.props")) {
			// load mqtt certificate
			String cert = config.getProperty(ConfigConst.UBIDOTS_CLOUD_SECTION, ConfigConst.CLOUD_CERT_FILE);
			String token = config.getProperty(ConfigConst.UBIDOTS_CLOUD_SECTION, ConfigConst.USER_AUTH_TOKEN_KEY);
			mqtt_client = new MqttClientConnector();
			// set up TLS connection
			mqtt_client.setupConnection(true, token, "", cert);
			// connect to broker
			mqtt_client.connect("ssl://industrial.api.ubidots.com:8883", "Gateway");
		}
	}

	/**
	 * main polling function subscribe to topic to receive actuator command poll
	 * temperature/humidity/pressure sensor at intervals check whether temperature
	 * is below the threshold, if true, send email
	 */
	public void run() {
		// subscribe to led control topic
		mqtt_client.subscribeToTopic("/v1.6/devices/led/action/lv", 1);
		// instantiate CoAP client to poll sensors
		CoapClientConnector c_temperature = new CoapClientConnector("coap://192.168.123.3:5683/temp_sensor");
		CoapClientConnector c_humidity = new CoapClientConnector("coap://192.168.123.3:5683/humidity_sensor");
		CoapClientConnector c_pressure = new CoapClientConnector("coap://192.168.123.3:5683/pressure_sensor");
		// instantiate CoAP client to poll led status
		CoapClientConnector c_led = new CoapClientConnector("coap://192.168.123.3:5683/led/status");
		while (true) {
			// poll temperature from sensor, using GET method
			c_temperature.GET(new CoapHandler() {

				@Override
				public void onLoad(CoapResponse response) {
					if (response.getCode().toString().equals("2.05")) {
						// Check if temperature is below threshold
						if (Float.parseFloat(response.getResponseText()) <= threshold) {
							// check duplicated flag
							if (!duplicated) {
								timeStamp = new Date();
								String time = timeStamp.toString();
								smtp_client.publishMessage(
										"Temperature is " + response.getResponseText() + " Celsius degree at " + time,
										"Warning!Temperature is below the threshold");
								// set flag=true
								duplicated=true;
							}
						}
						// if GET successfully, publish to cloud by MQTT
						byte[] payload = payload(response.getResponseText());
						mqtt_client.publish("/v1.6/devices/sensors/temperature", payload, 1, true);
						// check if active devices contain temperature sensor, if no, ADD it
						if (!active_device.contains("temperature sensor")) {
							active_device.add("temperature sensor");
						}
					} else {
						// if failed(5.03), try to remove it from active devices
						if (active_device.contains("temperature sensor")) {
							active_device.remove("temperature sensor");
						}
						log.warning("GET temperature data failed:" + response.getCode().toString());
					}

				}

				@Override
				public void onError() {
					// if request time out or rejected,try to remove it from active devices
					if (active_device.contains("temperature sensor")) {
						active_device.remove("temperature sensor");
					}
					log.warning("GET temperature data failed:request timeout or rejected");
				}
			});
			// poll humidity from sensor, using GET method
			c_humidity.GET(new CoapHandler() {

				@Override
				public void onLoad(CoapResponse response) {
					if (response.getCode().toString().equals("2.05")) {
						// if GET successfully, publish to cloud by MQTT
						byte[] payload = payload(response.getResponseText());
						mqtt_client.publish("/v1.6/devices/sensors/humidity", payload, 1, true);
						// check if active devices contain humidity sensor, if no, ADD it
						if (!active_device.contains("humidity sensor")) {
							active_device.add("humidity sensor");
						}
					} else {
						// if failed(5.03), try to remove it from active devices
						if (active_device.contains("humidity sensor")) {
							active_device.remove("humidity sensor");
						}
						log.warning("GET humidity data failed:" + response.getCode().toString());
					}

				}

				@Override
				public void onError() {
					// if request time out or rejected,try to remove it from active devices
					if (active_device.contains("humidity sensor")) {
						active_device.remove("humidity sensor");
					}
					log.warning("GET humidity data failed:request timeout or rejected");
				}
			});

			// poll pressure from sensor, using GET method
			c_pressure.GET(new CoapHandler() {

				@Override
				public void onLoad(CoapResponse response) {
					if (response.getCode().toString().equals("2.05")) {
						// if GET successfully, publish to cloud by MQTT
						byte[] payload = payload(response.getResponseText());
						mqtt_client.publish("/v1.6/devices/sensors/pressure", payload, 1, true);
						// check if active devices contain pressure sensor, if no, ADD it
						if (!active_device.contains("pressure sensor")) {
							active_device.add("pressure sensor");
						}
					} else {
						// if failed(5.03), try to remove it from active devices
						if (active_device.contains("pressure sensor")) {
							active_device.remove("pressure sensor");
						}
						log.warning("GET pressure data failed:" + response.getCode().toString());
					}

				}

				@Override
				public void onError() {
					// if request time out or rejected,try to remove it from active devices
					if (active_device.contains("pressure sensor")) {
						active_device.remove("pressure sensor");
					}
					log.warning("GET pressure data failed:request timeout or rejected");

				}
			});
			// poll led status, using GET method
			c_led.GET(new CoapHandler() {

				@Override
				public void onLoad(CoapResponse response) {
					if (response.getCode().toString().equals("2.05")) {
						// if GET successfully, publish to cloud by MQTT
						byte[] payload = led_status(response.getResponseText());
						mqtt_client.publish("/v1.6/devices/led/status", payload, 1, true);
						// check if active devices contain led, if no, ADD it
						if (!active_device.contains("LED")) {
							active_device.add("LED");
						}
					} else {
						// if failed(5.03), try to remove it from active devices
						if (active_device.contains("LED")) {
							active_device.remove("LED");
						}
						log.warning("GET led data failed:" + response.getCode().toString());
					}

				}

				@Override
				public void onError() {
					// if request time out or rejected,try to remove it from active devices
					if (active_device.contains("LED")) {
						active_device.remove("LED");
					}
					log.warning("GET led data failed:request timeout or rejected");

				}
			});
			// set 1s delay, ubidots cannot accept too many publish at a time
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// get available device list and time stamp
			byte[] payload = device_status();
			// publish active device list to cloud by MQTT
			mqtt_client.publish("/v1.6/devices/info/device", payload, 1, true);
			// sleep at intervals
			try {
				Thread.sleep(interval * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// cut down
			cooldown -= 1;
			if (cooldown == 0) {
				duplicated = false;
				cooldown = 8;
			}

		}
	}

	/**
	 * set up intervals
	 * 
	 * @param intervals :intervals in seconds
	 */
	public void SetInterval(int intervals) {
		this.interval = intervals;
	}

	/**
	 * set up threshold
	 * 
	 * @param t threshold in Celsius degree
	 */
	public void SetThreshold(float t) {
		this.threshold = t;
	}

	/**
	 * payload builder: input the float value (temperature/humidity/pressure) and
	 * return the payload
	 * 
	 * @param value :float value of temperature/humidity/pressure
	 * @return payload in byte[]
	 */
	private byte[] payload(String value) {
		Double d = Double.parseDouble(value);
		DecimalFormat df = new DecimalFormat("0.00");
		String round = df.format(d);
		timeStamp = new Date();
		String timestamp = String.valueOf(timeStamp.getTime());
		String payload = "{\"value\":" + round + "," + "\"timestamp\":" + timestamp + "}";
		return payload.getBytes();

	}

	/**
	 * payload builder for device status, will read the active_device list
	 * 
	 * @return payload that indicates active devices
	 */
	private byte[] device_status() {
		// v is the number of active devices
		int v = active_device.size();
		// build a string to represent device available
		String active_devices = "";
		for (String device : active_device) {
			active_devices += device + ";";
		}
		// check if it is empty
		if (active_devices.isEmpty()) {
			active_devices = "no device available";
		}
		// add time stamp and prepare payload to send
		timeStamp = new Date();
		String timestamp = String.valueOf(timeStamp.getTime());
		String p = "{\"value\":" + v + "," + "\"timestamp\":" + timestamp + ",\"context\":{\"name\":\"" + active_devices
				+ "\"}}";
		return p.getBytes();
	}

	/**
	 * payload builder for led status, will read the active_device list
	 * 
	 * @return payload that indicates led status
	 */
	private byte[] led_status(String status) {
		timeStamp = new Date();
		String timestamp = String.valueOf(timeStamp.getTime());
		if (status.equals("on") || status.equals("dim")) {
			// use 1 to represent led is working
			String p = "{\"value\":" + 1 + "," + "\"timestamp\":" + timestamp + ",\"context\":{\"status\":\"" + status
					+ "\"}}";
			return p.getBytes();
		} else {
			// use 0 to represent led is off
			String p = "{\"value\":" + 0 + "," + "\"timestamp\":" + timestamp + ",\"context\":{\"status\":\"" + status
					+ "\"}}";
			return p.getBytes();
		}

	}

}
