'''
this class is a simple wrap that provides MQTT client functions, 
including connect, publish, subscribe and disconnect. It also implements callback functions
including on_connect, on_disconnect, on_publish ,on_message and on_sub/unsub
'''
import paho.mqtt.client as mqtt
import logging
import ssl
import time

# global variable, indicate connect status
connect=False

class MqttClientConnector(object):
    client = None  # mqtt client
    message =""     # message received
    logger = logging.getLogger()
    logger.setLevel(logging.INFO)

    def __init__(self):
        self.client = mqtt.Client(client_id=str(time.time()))
    
    # callback when client receives a CONNACK response from the server    
    def on_connect(self, client, userdata, flags, rc):
        if rc == 0:
            global connect
            connect = True
            self.logger.info("Connected with result code: %s" , str(rc))
        else:
            self.logger.info("Connection failed with result code: %s" , str(rc))

    # Callback when the client disconnects from the broker.   
    def on_disconnect(self, client, userdata, rc):
        if rc != 0:
            self.logger.warning('Unexpected disconnect')
        else:
            global connect
            connect = False
            # stop the loop
            self.client.loop_stop()
            self.logger.info('Disconnected')

    # Callback when a message that was to be sent using the publish()
    def on_publish(self, client, userdata, mid):
        self.logger.info('message published')
        
    # Callback when received a message
    def on_message(self, client, userdata, msg):
        self.logger.info("topic: %s message: %s", str(msg.topic), str(msg.payload))
        self.message=msg.payload.decode('utf-8')
    
    # Callback when subscribe to topic
    def on_subscribe(self, client, userdata, mid, granted_qos):
        self.logger.info('subscribed! QoS= %s', granted_qos)
        
    # Callback when unsubscribe to one topic 
    def on_unsubscribe(self, client, userdata, mid):
        self.logger.info('Unsubscribed!')
        
    def setupConnection(self, username, password, certpath):
        '''
        setup encrypted TLS connection with broker
        @param username: user name
        @param password: password
        @param certpath: path of certificate file
        '''
        self.client.username_pw_set(username, password)
        self.client.tls_set(ca_certs=certpath, certfile=None,
                            keyfile=None, cert_reqs=ssl.CERT_REQUIRED,
                            tls_version=ssl.PROTOCOL_TLSv1_2, ciphers=None)
        self.client.tls_insecure_set(False)
        
    def connect(self, host, port):
        '''
        connect to broker
        @param host: host address
        @param port: port number
        note: use default keep alive time 60s
        '''
        # setup callback
        self.logger.info("Connecting to "+host+" "+str(port)+"...")
        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message
        global connect
        if not connect:
            self.client.connect(host, port)
            self.client.loop_start()
            # wait for connection
            attempts = 0
            while not connect and attempts < 5:
                self.logger.info("Attempting to connect...")
                time.sleep(1)
                attempts += 1
        if not connect:
            self.logger.info("connction failed")
        
    def publish(self, topic, payload, qos):
        '''
        publish to selected topic
        @param topic: topic to publish
        @param payload: message
        @param qos: QoS level
        '''
        self.client.on_publish = self.on_publish
        self.client.publish(topic, payload, qos, retain=False)
        self.logger.info('message:%s',payload)
        
    def subscribe(self, topic, qos):
        '''
        subscribe to a topic
        @param topic: topic to subscribe
        @param qos: QoS level
        '''
        self.client.on_subscribe = self.on_subscribe
        self.client.subscribe(topic, qos)
        self.logger.info('Subscribe to: %s', topic)
        
    def unsubscribe(self, topic):
        '''
        unsubscribe one topic
        @param topic: unsubscribe topic
        '''
        self.client.on_unsubscribe = self.on_unsubscribe
        self.client.unsubscribe(topic)
        self.logger.info('Unsubscribe to %s', topic)
        
    def disconnect(self):
        '''
        disconnect from broker
        '''
        self.client.on_disconnect = self.on_disconnect
        self.client.disconnect()
