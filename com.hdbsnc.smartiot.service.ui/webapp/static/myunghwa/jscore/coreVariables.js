/* Variables */
//Auth
var otpAuth = new bsnc.otp.api.auth(); //Instance Object  
var otpAdt = new bsnc.otp.api.adt(otpAuth);
var otpIns = new bsnc.otp.api.ins(otpAuth);
var otpSession = new bsnc.otp.api.ins.session(otpAuth);
var otpInsFunc = new bsnc.otp.api.ins.func(otpAuth);
var otpInsAtt = new bsnc.otp.api.ins.att(otpAuth);
var otpCtrl = new bsnc.otp.api.dev.control(otpAuth);//Control Object
var otpDevPool = new bsnc.otp.api.devpool(otpAuth); 
var otpDev = new bsnc.otp.api.dev(otpAuth);
var otpUsePool = new bsnc.otp.api.userpool(otpAuth);
var otpUse = new bsnc.otp.api.user(otpAuth);
var otpUseFilt = new bsnc.otp.api.user.filter(otpAuth);
var otpEvent = new bsnc.otp.api.event(otpAuth);

var scopeSynchronizeIntervalId=null;
//Scopes
var instanceScope = null;
var attributeScope = null;
var adaptaSearchScope = null;
var eventScope= null;

//Selected Value
var selectedAdapterId=null;

var selectedInstanceId=null;
var selectedDeviceId = null;
var selectedInstanceEvent=null;
var selectedInstanceStatus=null;

var selectedAttributeKey = null;

//Module
var agGridModule = angular.module("agApps", ["agGrid"]);

var attributeKey=null;
var attributeDescription=null;
var attributeDeviceType=null;
var attributeStartAddr=null;
var attributeDeviceScore=null;
var attributeGathering=null;