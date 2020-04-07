'''
A simple CoAP server that interact with the gateway APP
handle the Gateway request to return sensor data or trigger actuator
'''

from project.CoapServerConnector import CoapServerConnector


server=CoapServerConnector('0.0.0.0',5683)
server.start()

