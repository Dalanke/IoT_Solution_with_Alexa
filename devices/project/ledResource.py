'''
Extended resource for LED actuator, ONLY handle GET and POST request
'''
from coapthon.resources.resource import Resource
from sense_hat import SenseHat
from coapthon import defines


class LedResource(Resource):
    sensehat = None  # senseHat object
    payload = ""

    def __init__(self, name="LedResource", coap_server=None):
        '''
        Constructor
        '''
        super(LedResource, self).__init__(name, coap_server, visible=True, observable=True, allow_children=True)
        # instantiate senseHat
        self.sensehat = SenseHat()
    
    def render_GET_advanced(self, request, response):
        '''
        GET request handler
        return led status if possible (2.05 CONTENT)
        if error, return 5.03
        '''
        try:
            response.payload = self.LedStatus()
            response.code = defines.Codes.CONTENT[0]
            return self, response
        except (RuntimeError, OSError):
            response.code = defines.Codes.SERVICE_UNAVAILABLE[0]
            return self, response
            
    def render_POST_advanced(self, request, response):
        '''
        POST request handler
        try to trigger the LED actuator according to request
        return OK in payload and 2.01 CREATED
        if error, return 5.03
        '''
        # set default color
        color = (110, 90, 255)
        if request.payload == "on":
            try:
                self.sensehat.clear(color)
                response.payload = "OK"
                response.code = defines.Codes.CREATED[0]
                return self, response
            except (RuntimeError, OSError):
                response.code = defines.Codes.SERVICE_UNAVAILABLE[0]
                return self, response
        elif request.payload == "off":
            try:
                self.sensehat.clear()
                response.payload = "OK"
                response.code = defines.Codes.CREATED[0]
                return self, response
            except (RuntimeError, OSError):
                response.code = defines.Codes.SERVICE_UNAVAILABLE[0]
                return self, response
        elif request.payload == "dim":
            try:
                self.sensehat.low_light = True
                response.payload = "OK"
                response.code = defines.Codes.CREATED[0]
                return self, response
            except (RuntimeError, OSError):
                response.code = defines.Codes.SERVICE_UNAVAILABLE[0]
                return self, response
        elif request.payload == "brighten":
            try:
                self.sensehat.low_light = False
                response.payload = "OK"
                response.code = defines.Codes.CREATED[0]
                return self, response
            except (RuntimeError, OSError):
                response.code = defines.Codes.SERVICE_UNAVAILABLE[0]
                return self, response

    def LedStatus(self):
        '''
        Helper function to check the LED status
        '''
        # black/off RGB for led pixel
        black = [0, 0, 0]
        # get list containing 64 smaller lists of [R, G, B] pixels
        pixel_list = self.sensehat.get_pixels()
        # set default status to off
        status = "off"
        # check if it is on (no all pixel is [0,0,0])
        for p in pixel_list:
            if p != black:
                status = "on"
                break
        # check if it is in low_light mode
        if status == "on":
            if self.sensehat.low_light == True:
                status = "dim"
        return status
        
