## IoT_Solution_with_Alexa

An IoT end-to-end solution with Alexa and Ubidots. Project provides an Alexa skill that connect to IoT gateway by MQTT.

IoT gateway is connected to a raspberry Pi (with senseHat) by CoAP. You can control senseHat LED, get temperature/humidity/pressure sensor
readout by talking to any Alexa device that enabled the Alexa skill provided by this project.

High level desgin:

![image](https://github.com/Dalanke/IoT_Solution_with_Alexa/blob/master/images/HighLevel.png)

Application UML design:

![image](https://github.com/Dalanke/IoT_Solution_with_Alexa/blob/master/images/UML.png)

## Usage

* aws-lambda: Alexa skill files and  Alexa hosted Lambda function
* gateway: gateway application(Java application)
* devices: device application, tested on Raspberry Pi with senseHat(python application)


## Contributions
This is an NEU Connected Device course project.
The gateway application used the package provided by Andrew D. King.
