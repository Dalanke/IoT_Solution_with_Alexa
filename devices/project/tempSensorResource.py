'''
Extended resource for temperature sensor
ONLY Handle GET request, will return temperature in Celsius if possible
if successfully handled, return 2.05 CONTENT; else, return 5.03 Service Unavailable
'''
from coapthon.resources.resource import Resource
from sense_hat import SenseHat
from coapthon import defines


class tempSensorResource(Resource):
    sensehat = None  # senseHat object
    payload = ""

    def __init__(self, name="tempSensorResource", coap_server=None):
        '''
        Constructor
        '''
        super(tempSensorResource, self).__init__(name, coap_server, visible=True, observable=True, allow_children=True)
        # instantiate senseHat
        self.sensehat = SenseHat()
    
    def render_GET_advanced(self, request, response):
        '''
        GET request handler
        return sensor data if possible (2.05 CONTENT)
        if error, return 5.03
        '''
        try:
            self.payload = str(self.sensehat.get_temperature())
            response.payload = self.payload
            response.code = defines.Codes.CONTENT[0]
            return self, response
        except (RuntimeError, OSError):
            response.code = defines.Codes.SERVICE_UNAVAILABLE[0]
            return self, response
