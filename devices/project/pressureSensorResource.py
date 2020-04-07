'''
Extended resource for humidity sensor
ONLY Handle GET request, will return temperature in Celsius if possible
if successfully handled, return 2.05 CONTENT; else, return 5.03 Service Unavailable
'''
from sense_hat import SenseHat
from coapthon.resources.resource import Resource
from coapthon import defines


class PressureSensorResource(Resource):
    payload = ""
    sensehat = None

    def __init__(self, name="PressureSensorResource", coap_server=None):
        '''
        Constructor
        '''
        super(PressureSensorResource, self).__init__(name, coap_server, visible=True, observable=True, allow_children=True)
        # instantiate senseHat
        self.sensehat = SenseHat()
        
    def render_GET_advanced(self, request, response):
        '''
        GET request handler
        return sensor data if possible (2.05 CONTENT)
        if error, return 5.03
        '''
        try:
            self.payload = str(self.sensehat.get_pressure())
            response.payload = self.payload
            response.code = defines.Codes.CONTENT[0]
            return self, response
        except (RuntimeError, OSError):
            response.code = defines.Codes.SERVICE_UNAVAILABLE[0]
            return self, response
            
