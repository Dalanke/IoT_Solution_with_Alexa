# -*- coding: utf-8 -*-

'''
This lambda function implemented multiple handlers that handle Alexa skill intents(request)
the Alexa request will be converted into mqtt message or topic to fullfill the request
then the MQTT message will be converted back to speak_output to build a Response for Alexa request
'''

# This lambda use the sample that demonstrates handling intents from an Alexa skill using the Alexa Skills Kit SDK for Python.
# For more examples,please visit https://alexa.design/cookbook
# This sample is built using the handler classes approach in skill builder.
import logging
import ask_sdk_core.utils as ask_utils

from ask_sdk_core.skill_builder import SkillBuilder
from ask_sdk_core.dispatch_components import AbstractRequestHandler
from ask_sdk_core.dispatch_components import AbstractExceptionHandler
from ask_sdk_core.handler_input import HandlerInput

from ask_sdk_model import Response

from MqttConnector import MqttClientConnector
from time import sleep
from datetime import datetime,timezone,timedelta
import time
import json


# configure log setting,set format and level(<=info)
logging.basicConfig(format='%(asctime)s:%(levelname)s:%(message)s', level=logging.INFO)
logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)
#put your ubidots token here
token=''
# note that lambda will return utc time
# set time zone= UTC-5
utc_5=timezone(timedelta(hours=-5))

# below are help functions

'''
!!! IMPORTANT NOTE !!!
do not instantiate mqtt client outside the handle function
as whole Handler class will be instantiated when adding Handler
if you instantiate mqtt as class variable, you will get same client 
when you try to reinvoke same intent can will casue errors
'''

def mqttSetup():
    '''
    setup the MQTT connection, return one mqttClient instance
    '''
    # instantiate mqttClient
    mqttClient=MqttClientConnector()
    # set up TLS connection/ import your .pem file here
    mqttClient.setupConnection(token, "", "cert.pem")
    # non-TLS 
    #mqttClient.client.username_pw_set(token,"")
    # return the client instance
    return mqttClient

def timestampChecker(timestamp):
    '''
    check the time elapse between current and input time stamp
    '''
    current = datetime.now().astimezone(utc_5)
    t = datetime.fromtimestamp(timestamp).astimezone(utc_5)
    if current.year == t.year:
        if current.month != t.month:
            d = current.month - t.month
            return "was " + str(d) + " months ago"
        else:
            if current.day != t.day:
                d = current.day - t.day
                return "was " + str(d) + " days ago"
            else:
                if current.hour != t.hour:
                    d=current.hour - t.hour
                    return "was " + str(d) + " hours ago"
                else:
                    d=current.minute-t.minute
                    return "was " + str(d) + " minutes ago"
    else:
        d=current.year-t.year
        return "was " + str(d) + " years ago"

# custom Handler

class LaunchRequestHandler(AbstractRequestHandler):
    """Handler for Skill Launch."""
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool

        return ask_utils.is_request_type("LaunchRequest")(handler_input)

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response
        # get input language
        lang = handler_input.request_envelope.request.locale
        if lang=="en-US":
            speak_output = "Welcome, what can I do for you"
        if lang=="ja-JP":
            speak_output = "何が御用でしょうか"

        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask(speak_output)
                .response
        )


class DeviceDiscoveryIntentHandler(AbstractRequestHandler):
    """Handler for Device Discovery Intent."""
    mqttClient=None
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_intent_name("DeviceDiscovery")(handler_input)

    def handle(self, handler_input):
        '''type: (HandlerInput) -> Response'''
        # get mqtt client instance
        self.mqttClient=mqttSetup()
        # connect
        self.mqttClient.connect("industrial.api.ubidots.com", 8883)
        # subscribe to active device topic
        self.mqttClient.subscribe("/v1.6/devices/info/device",1)
        # set timeout (5s)
        timeout=10
        # sleep to wait response
        while self.mqttClient.message=="" and timeout>0:
            sleep(0.5)
            timeout-=1
        # disconnect MQTT broker
        self.mqttClient.disconnect()
        if self.mqttClient.message!="":
            # convert message to dict
            m=json.loads(self.mqttClient.message)
            c_timestamp=int(datetime.now().astimezone(utc_5).timestamp()*1000)
            # validate timestamp, need to be within 5mins
            if c_timestamp-m["timestamp"]<300000:
                # get active devices number
                n=str(int(m["value"]))
                d_list=m["context"]["name"]
                speak_output = "Currently, "+n+" devices are available, there are: "+d_list
                # due to aws feature, reset message to empty
                self.mqttClient.message=""
            else:
                speak_output = "sorry, no device available recently"
                # due to aws feature, reset message to empty
                self.mqttClient.message=""
        else:
            speak_output = "sorry, no device available"

        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask("anything else can i do for you")
                .response
        )


class GetTemperatureIntentHandler(AbstractRequestHandler):
    """Handler for GetTemperature Intent."""
    mqttClient=None
    
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_intent_name("GetTemperature")(handler_input)

    def handle(self, handler_input):
        '''
        handle the request
        type: (HandlerInput) -> Response
        '''
        # get mqtt client instance
        self.mqttClient=mqttSetup()
        # connect
        self.mqttClient.connect("industrial.api.ubidots.com", 8883)
        #subscribe to temperature topic
        self.mqttClient.subscribe("/v1.6/devices/sensors/temperature",1)
        # set timeout (5s)
        timeout=10
        # sleep to wait response
        while self.mqttClient.message=="" and timeout>0:
            sleep(0.5)
            timeout-=1
        # disconnect MQTT broker
        self.mqttClient.disconnect()
        if self.mqttClient.message!="":
            # convert message to dict
            m=json.loads(self.mqttClient.message)
            c_timestamp=int(datetime.now().astimezone(utc_5).timestamp()*1000)
            # validate timestamp, need to be within 5mins
            if c_timestamp-m["timestamp"]<300000:
                speak_output = "Current temperature is "+str(m["value"])+"celsius degree"
                # due to aws feature, reset message to empty
                self.mqttClient.message=""
            else:
                elapse=timestampChecker(m["timestamp"]/1000.0)
                speak_output=" latest update "+elapse+" and temperature is "+str(m["value"])+" celsius degree"
                # due to aws feature, reset message to empty
                self.mqttClient.message=""
        else:
            speak_output = "Sorry,sensor is not available"

        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask("anything else can i do for you")
                .response
        )


class GetHumidityIntentHandler(AbstractRequestHandler):
    """Handler for GetHumidity Intent."""
    mqttClient=None
    
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_intent_name("GetHumidity")(handler_input)

    def handle(self, handler_input):
        '''
        handle the request
        type: (HandlerInput) -> Response
        '''
        # get mqtt client instance
        self.mqttClient=mqttSetup()
        # connect
        self.mqttClient.connect("industrial.api.ubidots.com", 8883)
        #subscribe to humidity topic
        self.mqttClient.subscribe("/v1.6/devices/sensors/humidity",1)
        # set timeout (5s)
        timeout=10
        # sleep to wait response
        while self.mqttClient.message=="" and timeout>0:
            sleep(0.5)
            timeout-=1
        # disconnect MQTT broker
        self.mqttClient.disconnect()
        if self.mqttClient.message!="":
            # convert message to dict
            m=json.loads(self.mqttClient.message)
            c_timestamp=int(datetime.now().astimezone(utc_5).timestamp()*1000)
            # validate timestamp, need to be within 5mins
            if c_timestamp-m["timestamp"]<300000:
                speak_output = "Current humidity is "+str(m["value"])+"%"
                # due to aws feature, reset message to empty
                self.mqttClient.message=""
            else:
                elapse=timestampChecker(m["timestamp"]/1000.0)
                speak_output=" latest update "+elapse+" and humidity is "+str(m["value"])+" %"
                # due to aws feature, reset message to empty
                self.mqttClient.message=""
        else:
            speak_output = "Sorry,sensor is not available"

        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask("anything else can i do for you")
                .response
        )


class GetPressureIntentHandler(AbstractRequestHandler):
    """Handler for GetPressure Intent."""
    mqttClient=None
    
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_intent_name("GetPressure")(handler_input)

    def handle(self, handler_input):
        '''
        handle the request
        type: (HandlerInput) -> Response
        '''
        # get mqtt client instance
        self.mqttClient=mqttSetup()
        # connect
        self.mqttClient.connect("industrial.api.ubidots.com", 8883)
        #subscribe to pressure topic
        self.mqttClient.subscribe("/v1.6/devices/sensors/pressure",1)
        # set timeout (5s)
        timeout=10
        # sleep to wait response
        while self.mqttClient.message=="" and timeout>0:
            sleep(0.5)
            timeout-=1
        # disconnect MQTT broker
        self.mqttClient.disconnect()
        if self.mqttClient.message!="":
            # convert message to dict
            m=json.loads(self.mqttClient.message)
            c_timestamp=int(datetime.now().astimezone(utc_5).timestamp()*1000)
            # validate timestamp, need to be within 5mins
            if c_timestamp-m["timestamp"]<300000:
                speak_output = "Current pressure is "+str(m["value"])+"kPa"
                # due to aws feature, reset message to empty
                self.mqttClient.message=""
            else:
                elapse=timestampChecker(m["timestamp"]/1000.0)
                speak_output=" latest update "+elapse+" and pressure is "+str(m["value"])+" kPa"
                # due to aws feature, reset message to empty
                self.mqttClient.message=""
        else:
            speak_output = "Sorry,sensor is not available"

        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask("anything else can i do for you")
                .response
        )


class LightControlIntentHandler(AbstractRequestHandler):
    """Handler for LightControl Intent."""
    mqttClient=None
    
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_intent_name("LightControl")(handler_input)

    def handle(self, handler_input):
        '''
        handle the request
        type: (HandlerInput) -> Response
        '''
        # get mqtt client instance
        self.mqttClient=mqttSetup()
        # get slots
        slots = handler_input.request_envelope.request.intent.slots
        action = slots["action"].value
        # connect
        self.mqttClient.connect("industrial.api.ubidots.com", 8883)
        # use MQTT publish to control the light, 1=on 2=dim 3=brighten 0=off
        if action=="on":
            self.mqttClient.publish("/v1.6/devices/led/action","{\"value\":1}",1)
        if action=="dim":
            self.mqttClient.publish("/v1.6/devices/led/action","{\"value\":2}",1)
        if action=="off":
            self.mqttClient.publish("/v1.6/devices/led/action","{\"value\":0}",1)
        if action=="brighten":
            self.mqttClient.publish("/v1.6/devices/led/action","{\"value\":3}",1)
        #subscribe to response topic, 
        self.mqttClient.subscribe("/v1.6/devices/led/response/lv",1)
        # set timeout (5s)
        timeout=10
        # sleep to wait response
        while self.mqttClient.message=="" and timeout>0:
            sleep(0.5)
            timeout-=1
        # disconnect MQTT broker
        self.mqttClient.disconnect()
        if self.mqttClient.message!="":
            # get response code 1=success 0=fail
            m=int(self.mqttClient.message)
            if  m==1:
                speak_output = "Ok"
                # due to aws feature, reset message to empty
                self.mqttClient.message=""
            else:
                speak_output="Sorry, unable to control the device"
                # due to aws feature, reset message to empty
                self.mqttClient.message=""
        else:
            speak_output = "Sorry, unable to control the device"

        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask("anything else can i do for you")
                .response
        )


class LightControlJPIntentHandler(AbstractRequestHandler):
    """Handler for LightControl (Japanese version) Intent."""
    mqttClient=None
    
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_intent_name("LightControlJP")(handler_input)

    def handle(self, handler_input):
        '''
        handle the request
        type: (HandlerInput) -> Response
        '''
        # get mqtt client instance
        self.mqttClient=mqttSetup()
        # get slots
        slots = handler_input.request_envelope.request.intent.slots
        action = slots["action"].value
        # connect
        self.mqttClient.connect("industrial.api.ubidots.com", 8883)
        # use MQTT publish to control the light, 1=on 2=dim 3=brighten 0=off
        if action=="つける":
            self.mqttClient.publish("/v1.6/devices/led/action","{\"value\":1}",1)
        if action=="消す":
            self.mqttClient.publish("/v1.6/devices/led/action","{\"value\":0}",1)
        #subscribe to response topic, 
        self.mqttClient.subscribe("/v1.6/devices/led/response/lv",1)
        # set timeout (5s)
        timeout=10
        # sleep to wait response
        while self.mqttClient.message=="" and timeout>0:
            sleep(0.5)
            timeout-=1
        # disconnect MQTT broker
        self.mqttClient.disconnect()
        if self.mqttClient.message!="":
            # get response code 1=success 0=fail
            m=int(self.mqttClient.message)
            if  m==1:
                speak_output = "かしこまりました"
                # due to aws feature, reset message to empty
                self.mqttClient.message=""
            else:
                speak_output=" 申し訳ありませんが、デバイスには応答がありません"
                # due to aws feature, reset message to empty
                self.mqttClient.message=""
        else:
            speak_output = "申し訳ありませんが、デバイスには応答がありません"

        return (
            handler_input.response_builder
                .speak(speak_output)
                #.ask("anything else can i do for you")
                .response
        )


class MqttTestIntentHandler(AbstractRequestHandler):
    """Handler for MqttTest Intent. This is used for testing mqtt connection ONLY"""
    
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_intent_name("MqttTest")(handler_input)

    def handle(self, handler_input):
        '''
        handle the request
        type: (HandlerInput) -> Response
        '''
        self.mqttClient=MqttClientConnector()
        # connect
        self.mqttClient.connect("nannoiot.franxx.live", 1883)
        #subscribe to temperature topic
        self.mqttClient.subscribe("/test",1)
        # set timeout (5s)
        timeout=10
        # sleep to wait response
        while self.mqttClient.message=="" and timeout>0:
            sleep(0.5)
            timeout-=1
        # disconnect MQTT broker
        self.mqttClient.disconnect()
        if self.mqttClient.message!="":
            speak_output=str(self.mqttClient.message)
            # due to aws feature, reset message to empty
            self.mqttClient.message=""
        else:
            speak_output = "test failed"

        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask("anything else can i do for you")
                .response
        )

'''
Below are defualt Intent Handlers
'''

class HelloWorldIntentHandler(AbstractRequestHandler):
    """Handler for Hello World Intent."""
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_intent_name("HelloWorldIntent")(handler_input)

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response
        speak_output = "Hello World!"

        return (
            handler_input.response_builder
                .speak(speak_output)
                # .ask("add a reprompt if you want to keep the session open for the user to respond")
                .response
        )


class HelpIntentHandler(AbstractRequestHandler):
    """Handler for Help Intent."""
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_intent_name("AMAZON.HelpIntent")(handler_input)

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response
        speak_output = "You can say hello to me! How can I help?"

        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask(speak_output)
                .response
        )


class CancelOrStopIntentHandler(AbstractRequestHandler):
    """Single handler for Cancel and Stop Intent."""
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return (ask_utils.is_intent_name("AMAZON.CancelIntent")(handler_input) or
                ask_utils.is_intent_name("AMAZON.StopIntent")(handler_input))

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response
        speak_output = "Goodbye!"

        return (
            handler_input.response_builder
                .speak(speak_output)
                .response
        )


class SessionEndedRequestHandler(AbstractRequestHandler):
    """Handler for Session End."""
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_request_type("SessionEndedRequest")(handler_input)

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response

        # Any cleanup logic goes here.

        return handler_input.response_builder.response


class IntentReflectorHandler(AbstractRequestHandler):
    """The intent reflector is used for interaction model testing and debugging.
    It will simply repeat the intent the user said. You can create custom handlers
    for your intents by defining them above, then also adding them to the request
    handler chain below.
    """
    def can_handle(self, handler_input):
        # type: (HandlerInput) -> bool
        return ask_utils.is_request_type("IntentRequest")(handler_input)

    def handle(self, handler_input):
        # type: (HandlerInput) -> Response
        intent_name = ask_utils.get_intent_name(handler_input)
        speak_output = "You just triggered " + intent_name + "."

        return (
            handler_input.response_builder
                .speak(speak_output)
                # .ask("add a reprompt if you want to keep the session open for the user to respond")
                .response
        )


class CatchAllExceptionHandler(AbstractExceptionHandler):
    """Generic error handling to capture any syntax or routing errors. If you receive an error
    stating the request handler chain is not found, you have not implemented a handler for
    the intent being invoked or included it in the skill builder below.
    """
    def can_handle(self, handler_input, exception):
        # type: (HandlerInput, Exception) -> bool
        return True

    def handle(self, handler_input, exception):
        # type: (HandlerInput, Exception) -> Response
        logger.error(exception, exc_info=True)

        speak_output = "Sorry, I had trouble doing what you asked. Please try again."

        return (
            handler_input.response_builder
                .speak(speak_output)
                .ask(speak_output)
                .response
        )



# The SkillBuilder object acts as the entry point for your skill, routing all request and response
# payloads to the handlers above. Make sure any new handlers or interceptors you've
# defined are included below. The order matters - they're processed top to bottom.


sb = SkillBuilder()

sb.add_request_handler(LaunchRequestHandler())
sb.add_request_handler(DeviceDiscoveryIntentHandler())
sb.add_request_handler(GetTemperatureIntentHandler())
sb.add_request_handler(GetHumidityIntentHandler())
sb.add_request_handler(GetPressureIntentHandler())
sb.add_request_handler(LightControlIntentHandler())
sb.add_request_handler(MqttTestIntentHandler())
sb.add_request_handler(LightControlJPIntentHandler())

# defualt handler
sb.add_request_handler(HelpIntentHandler())
sb.add_request_handler(CancelOrStopIntentHandler())
sb.add_request_handler(SessionEndedRequestHandler())
sb.add_request_handler(IntentReflectorHandler()) # make sure IntentReflectorHandler is last so it doesn't override your custom intent handlers

sb.add_exception_handler(CatchAllExceptionHandler())

lambda_handler = sb.lambda_handler()
