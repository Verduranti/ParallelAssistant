#!/bin/sh

#Enable Wifi Direct if necessary
#Connect to Android
#Start up video feed

#echo Hello great and gloriously hacktastic world
#This enables wpa_supplicant if inactive
systemctl status wpa_supplicant | grep inactive
if [ $? -eq 0 ]; then
  systemctl start wpa_supplicant
fi

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
wpa_cli -ip2p-dev-wlan0 p2p_find
