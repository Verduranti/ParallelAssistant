#!/bin/sh

#Connect to Android
#Start up video feed

#This assumes that activateWifi.sh was successfully run

#MotoX's MAC address
#14:1a:a3:63:d4:d1 -> bluetooth?
#14:1a:a3:63:d4:d2 -> normal wifi
#14:1a:a3:63:d4:d3 -> Wifi direct

#Old version: tried to use WPS PIN configuration. Not really functional
#in Android. Too many hurdles to jump.
#interfaceName=`wpa_cli -ip2p-dev-wlan0 interface | grep p2p-wlan0`
#if [ $? -ne 0 ]; then
#  wpa_cli -ip2p-dev-wlan0 p2p_group_add
#  interfaceName=`wpa_cli -ip2p-dev-wlan0 interface | grep p2p-wlan0`
#fi

#wifipin=`wpa_cli -i$interfaceName wps_pin any`
#echo $wifipin

#Broadcast Wifi Direct Signal
wpa_cli -ip2p-dev-wlan0 p2p_connect 14:1a:a3:63:d4:d3 pbc join