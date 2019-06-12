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
var otpDomain = new bsnc.otp.api.domain(otpAuth);
var otpEvent = new bsnc.otp.api.event(otpAuth);

var scopeSynchronizeIntervalId=null;
//Scopes
var serverMessageScope=null;
var adapterScope = null;
var instanceScope = null;
var sessionScope = null;
var attributeScope = null;
var functionScope = null;
var controlScope = null;
var attControlScope=null;
var funcControlScope=null;
var logEventScope = null;
var adaptaSearchScope = null;
//var searchScope=null;
var searchDevScope = null;
var searchDevPoolScope = null;
var devicePoolScope = null;
var deviceScope = null;
var userPoolScope = null;
var userScope = null;
var userFilterScope = null;
var domainScope = null;
var instanceAttScope=null;
var instanceFuncScope=null;

//Selected Value
var selectedAdapterId = null;
var selectedAdapterKind=null;
var selectedAdapterDescription=null;
var selectedAdapterHyperink=null;


var selectedInstanceUrl = null;
var selectedInstanceSessionTimeout = null;
var selectedInstanceSelfId = null;
var selectedInstanceSelfPw = null;
var selectedInstanceRemark = null;

var selectedInstanceId = null;
var selectedInstanceName = null;
var selectedInstanceType = null;
var selectedInstanceKind = null;
var selectedInstanceStatus = null;
var selectedInstanceInitDevStatus = null;
var selectedInstanceIsUse = null;
var selectedInstanceIp = null;
var selectedInstancePort = null;
var selectedInstanceLatitude = null;
var selectedInstanceLongitude = null;

var selectedFunctionKey = null;
var selectedFunctionName = null;
var selectedFunctionType = null;
var selectedFunctionRemark = null;
var selectedFunctionParam1 = null;
var selectedFunctionParamType1 = null;
var selectedFunctionParam2 = null;
var selectedFunctionParamType2 = null;
var selectedFunctionParam3 = null;
var selectedFunctionParamType3 = null;
var selectedFunctionParam4 = null;
var selectedFunctionParamType4 = null;
var selectedFunctionParam5 = null;
var selectedFunctionParamType5 = null;

var selectedAttributeKey = null;
var selectedAttributeName = null;
var selectedAttributeType = null;
var selectedAttributeValue = null;
var selectedAttributeRemark = null;

var selectedSessionId = null;

var selectedControlId = null;
var selectedControlKey = null;
var selectedControlValue = null;
var selectedControlType = null;
var selectedControlDescruption=null;

var selectedSearchId = null;
var selectedSearchDevId = null;
var selectedSearchDevPoolId = null;

var selectedDeviceId = null;
var selectedDeviceName = null;
var selectedDeviceIsUse = null;
var selectedDeviceIp = null;
var selectedDevicePort = null;
var selectedDeviceLat = null;
var selectedDeviceLng = null;
var selectedDeviceRemark = null;
var selectedDeviceSessionTimeout=null;

var selectedDevicePoolId = null;
var selectedDevicePoolName = null;
var selectedDevicePoolRemark = null;

var selectedUserPoolId = null;
var selectedUserPoolName = null;
var selectedUserPoolRemark = null;

var selectedUserId = null;
var selectedUserPassword = null;
var selectedUserType = null;
var selectedUserName = null;
var selectedCompanyName = null;
var selectedDepartName = null;
var selectedJobTitle = null;
var selectedUserRemark = null;

var selectedUserFilterId = null;
var selectedAuthorityFilter = null;
var selectedUserPoolRemark = null;

var selectedDomainId = null;



//Module
var agGridModule = angular.module("agApps", ["agGrid"]);

