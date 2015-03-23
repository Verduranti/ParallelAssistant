//Require things
var util = require('util');
var bleno = require('..');

var BatteryLevelCharacteristic = require('./battery-level-characteristic');
var ActivateWIFICharacteristic = require('./activate-wifi-characteristic');
var GetAddressCharacteristic = require('./get-address-characteristic');


function ParallelService() {
  ParallelService.super_.call(this, {
    uuid: '5b580496-d85f-4cd0-8001-46ff648c706c',
    characteristics: [
      new BatteryLevelCharacteristic(),
      new ActivateWIFICharacteristic(),
      new GetAddressCharacteristic()
    ]
  });
}

util.inherits(ParallelService, bleno.PrimaryService);

module.exports = ParallelService;