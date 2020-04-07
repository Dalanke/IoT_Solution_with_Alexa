'''
A simple implementation of CoAP server. Add resources for
temperature, humidity, pressure sensor and led
'''
from coapthon.server.coap import CoAP
import logging
from project.tempSensorResource import tempSensorResource
from project.humiditySensorResource import HumiditySensorResource
from project.pressureSensorResource import PressureSensorResource
from project.ledResource import LedResource


class CoapServerConnector(CoAP):
    # instantiate resources
    tempResource=tempSensorResource()
    humResource=HumiditySensorResource()
    pressResource=PressureSensorResource()
    ledResource=LedResource()
    
    def __init__(self,  host, port):
        '''
        Constructor
        * server must initiate with host and port
        '''
        CoAP.__init__(self,(host,port))
        # add resources
        CoAP.add_resource(self, 'temp_sensor/', self.tempResource)
        CoAP.add_resource(self,'humidity_sensor/',self.humResource)
        CoAP.add_resource(self,'pressure_sensor/',self.pressResource)
        CoAP.add_resource(self,'led/',self.ledResource)
        CoAP.add_resource(self,'led/status',self.ledResource)
        
    def start(self):
        '''
        start the server and listen for incoming message
        timeout is set to 10s
        '''
        self.listen(10)
        logging.info("Server started!")

        
    def stop(self):
        '''
        close the server
        '''
        self.close()
        logging.info("Server shutdown!")
    

        