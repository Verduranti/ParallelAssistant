//Require things
var util = require('util');
var bleno = require('..');
var os = require('os');
var child_process = require('child_process')

//Some basics
var BlenoCharacteristic = bleno.Characteristic;
var BlenoDescriptor = bleno.Descriptor;

var GetAddressCharacteristic = function() {
  GetAddressCharacteristic.super_.call(this, {
    uuid: 'e3251ee6-922e-44a7-8b35-4438bfc8a5f5',
    properties: ['read']
//    properties: ['write', 'writeWithoutResponse'],
//     descriptors: [
//          new BlenoDescriptor({
//              //uuid: 'e3251ee6-922e-44a7-8b35-4438bfc8a5f6',
//              uuid: '2901',
//              value: 'MAC Address'
//          })
//      ]
  });
};

util.inherits(GetAddressCharacteristic, BlenoCharacteristic);

GetAddressCharacteristic.prototype.onReadRequest = function(offset, callback) {
  var result = this.RESULT_SUCCESS;
  //var data = new Buffer('TEST');
  //console.log('Fake-running hack script ' + data);
  console.log('About to run connection code');
  var data = new Buffer(child_process.execSync('./connectPhone.sh', { encoding: 'utf8' }));
  console.log('Ran connection script ' + data);

  if (offset > data.length) {
    result = this.RESULT_INVALID_OFFSET;
    data = null;
  }

  callback(result, data);
};

// GetAddressCharacteristic.prototype.onWriteRequest = function(data, 
// offset, withoutResponse, callback) {
//   console.log('GetAddressCharacteristic write request: ' + data.toString('hex') + ' ' + offset + ' ' + withoutResponse);
// 
//   callback(this.RESULT_SUCCESS);
// };

module.exports = GetAddressCharacteristic;