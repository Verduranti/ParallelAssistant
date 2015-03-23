//Require things
var util = require('util');
var bleno = require('..');
var os = require('os');
var child_process = require('child_process')

//Some basics
var BlenoPrimaryService = bleno.PrimaryService;
var BlenoCharacteristic = bleno.Characteristic;
var BlenoDescriptor = bleno.Descriptor;

var ActivateWIFICharacteristic = function() {
  ActivateWIFICharacteristic.super_.call(this, {
    uuid: 'f8b67f8a-d10e-4c29-bab7-b604f681bf41',
    properties: ['read']
  });
};

util.inherits(ActivateWIFICharacteristic, BlenoCharacteristic);

ActivateWIFICharacteristic.prototype.onReadRequest = function(offset, callback) {
  var result = this.RESULT_SUCCESS;
  //var data = new Buffer('TEST');
  //console.log('Fake-running hack script ' + data);
  console.log('About to run wifi code');
  console.log(child_process.execSync('./hack.sh', { encoding: 'utf8' }));
  var data = new Buffer('42');
  console.log('Ran hack script ' + data);

  if (offset > data.length) {
    result = this.RESULT_INVALID_OFFSET;
    data = null;
  }

  callback(result, data);
};

module.exports = ActivateWIFICharacteristic;
