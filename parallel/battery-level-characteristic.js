//Require things
var util = require('util');
var bleno = require('..');
var os = require('os');

//Some basics
var BlenoCharacteristic = bleno.Characteristic;
var BlenoDescriptor = bleno.Descriptor;

console.log('bleno');

var BatteryLevelCharacteristic = function() {
  BatteryLevelCharacteristic.super_.call(this, {
      uuid: '00002a19-0000-1000-8000-00805f9b34fb',
      properties: ['read']
  });
};

util.inherits(BatteryLevelCharacteristic, BlenoCharacteristic);

BatteryLevelCharacteristic.prototype.onReadRequest = function(offset, callback) {
  console.log('Reading battery');
  if (os.platform() === 'darwin') {
    exec('pmset -g batt', function (error, stdout, stderr) {

      var data = stdout.toString();
      // data - 'Now drawing from \'Battery Power\'\n -InternalBattery-0\t95%; discharging; 4:11 remaining\n'
      var percent = data.split('\t')[1].split(';')[0];
      console.log(percent);
      percent = parseInt(percent, 10);
      callback(this.RESULT_SUCCESS, new Buffer([percent]));

    });
  } else {
    // return hardcoded value
    callback(this.RESULT_SUCCESS, new Buffer([98]));
  }
};

module.exports = BatteryLevelCharacteristic;