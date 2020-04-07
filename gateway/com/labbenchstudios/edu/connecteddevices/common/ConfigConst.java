/**
 * Copyright (c) 2018-2019. Andrew D. King. All Rights Reserved.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.labbenchstudios.edu.connecteddevices.common;

/**
 * Configuration and other constants for use when looking up
 * configuration values or when default values may be needed.
 * 
 * @author aking
 */
public class ConfigConst
{
	public static final String SECTION_SEPARATOR        = ".";
	public static final String DATA_PATH                = "/home/pi/Applications/data/";
	public static final String DEFAULT_CONFIG_FILE_NAME = DATA_PATH + "ConnectedDevicesConfig.props";

	public static final String DEFAULT_COAP_PROTOCOL  = "coap";
	public static final String SECURE_COAP_PROTOCOL   = "coaps";
	public static final String DEFAULT_COAP_SERVER    = "californium.eclipse.org";
//	public static final String DEFAULT_COAP_SERVER    = "127.0.0.1";
	public static final int    DEFAULT_COAP_PORT      = 5683;
	public static final int    SECURE_COAP_PORT       = 5684;
	
	public static final String DEFAULT_MQTT_PROTOCOL  = "tcp";
	public static final String SECURE_MQTT_PROTOCOL   = "ssl";
//	public static final String DEFAULT_MQTT_SERVER    = "test.mosquitto.org";
	public static final String DEFAULT_MQTT_SERVER    = "iot.eclipse.org";
//	public static final String DEFAULT_MQTT_SERVER    = "broker.hivemq.com";
//	public static final String DEFAULT_MQTT_SERVER    = "127.0.0.1";
	public static final int    DEFAULT_MQTT_PORT      = 1883;
	public static final int    SECURE_MQTT_PORT       = 8883;
	public static final int    DEFAULT_QOS_LEVEL      =    0;
	
	public static final String CLOUD                  = "cloud";
	public static final String MQTT                   = "mqtt";
	public static final String COAP                   = "coap";
	public static final String SMTP                   = "smtp";
	public static final String GATEWAY_DEVICE         = "gateway";
	public static final String CONSTRAINED_DEVICE     = "device";
	public static final String CERT_FILE_EXT          = "_cert.pem";
	
	public static final String UBIDOTS                = "ubidots";
	public static final String DEFAULT_UBIDOTS_SERVER = "things.ubidots.com";
	public static final String UBIDOTS_CERT_FILE      = DATA_PATH + UBIDOTS + CERT_FILE_EXT;

	public static final String AWS                    = "aws";
	public static final String GCP                    = "gcp";
	public static final String AZURE                  = "azure";
	
	public static final String GATEWAY_SECTION            = GATEWAY_DEVICE;
	public static final String CONSTRAINED_DEVICE_SECTION = CONSTRAINED_DEVICE;
	
	public static final String UBIDOTS_CLOUD_SECTION  = UBIDOTS + SECTION_SEPARATOR + CLOUD;
	public static final String AWS_CLOUD_SECTION      = AWS     + SECTION_SEPARATOR + CLOUD;
	public static final String GCP_CLOUD_SECTION      = GCP     + SECTION_SEPARATOR + CLOUD;
	public static final String AZURE_CLOUD_SECTION    = AZURE   + SECTION_SEPARATOR + CLOUD;
	public static final String SMTP_CLOUD_SECTION     = SMTP    + SECTION_SEPARATOR + CLOUD;
	public static final String MQTT_CLOUD_SECTION     = MQTT    + SECTION_SEPARATOR + CLOUD;
	public static final String COAP_CLOUD_SECTION     = COAP    + SECTION_SEPARATOR + CLOUD;
	public static final String MQTT_GATEWAY_SECTION   = MQTT    + SECTION_SEPARATOR + GATEWAY_DEVICE;
	public static final String COAP_GATEWAY_SECTION   = COAP    + SECTION_SEPARATOR + GATEWAY_DEVICE;
	public static final String MQTT_DEVICE_SECTION    = MQTT    + SECTION_SEPARATOR + CONSTRAINED_DEVICE;
	public static final String COAP_DEVICE_SECTION    = COAP    + SECTION_SEPARATOR + CONSTRAINED_DEVICE;

	public static final String CLOUD_API_KEY          = "apiKey";
	public static final String CLOUD_CERT_FILE        = "certFile";
	public static final String CLOUD_MQTT_BROKER      = "mqttBroker";
	public static final String CLOUD_MQTT_PORT        = "mqttPort";
	public static final String CLOUD_COAP_HOST        = "coapHost";
	public static final String CLOUD_COAP_PORT        = "coapPort";

	public static final String SMTP_PROP_HOST_KEY       = "mail.smtp.host";
	public static final String SMTP_PROP_PORT_KEY       = "mail.smtp.port";
	public static final String SMTP_PROP_AUTH_KEY       = "mail.smtp.auth";
	public static final String SMTP_PROP_ENABLE_TLS_KEY = "mail.smtp.starttls.enable";
	
	public static final String FROM_ADDRESS_KEY       = "fromAddr";
	public static final String TO_ADDRESS_KEY         = "toAddr";
	public static final String TO_MEDIA_ADDRESS_KEY   = "toMediaAddr";
	public static final String TO_TXT_ADDRESS_KEY     = "toTxtAddr";
	
	public static final String HOST_KEY               = "host";
	public static final String PORT_KEY               = "port";
	public static final String SECURE_PORT_KEY        = "securePort";
	public static final String PROTOCOL_KEY           = "protocol";
	public static final String CERT_FILE_KEY          = "certFile";
	public static final String DEFAULT_QOS_KEY        = "defaultQos";
	public static final String CONFIG_PATH_KEY        = "configPath";

	public static final String API_KEY                = "apiKey";
	public static final String USER_NAME_TOKEN_KEY    = "userNameToken";
	public static final String USER_AUTH_TOKEN_KEY    = "authToken";
	public static final String TOPIC_PREFIX_KEY       = "topicPrefix";
	public static final String BASE_URL_KEY           = "baseUrl";

	public static final String ENABLE_AUTH_KEY        = "enableAuth";
	public static final String ENABLE_CRYPT_KEY       = "enableCrypt";
	public static final String ENABLE_EMULATOR_KEY    = "enableEmulator";
	public static final String ENABLE_LOGGING_KEY     = "enableLogging";
	public static final String USE_WEB_ACCESS_KEY     = "useWebAccess";
	public static final String POLL_CYCLES_KEY        = "pollCycleSecs";
	public static final String CLOUD_SVC_TYPE_KEY     = "cloudSvcType";

	public static final String KEEP_ALIVE_KEY         = "keepAlive";
	
	public static final String CONFIG_FILE_PARAM      = "configFile";

}
