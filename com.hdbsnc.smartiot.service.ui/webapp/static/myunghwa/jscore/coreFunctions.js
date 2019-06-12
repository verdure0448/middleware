$(document).ready(function(){
	$(".serverMessageInfo").hide();	
});


/*******************************************************/
/************** Search Set Put Del START ***************/
/*******************************************************/

function setPlcAttributeData(iid) {// 속성목록 조회
	otpInsAtt.plcSearchByIid(iid, cbInsAttSearchByIidSucessFunc,
			cbInsAttSearchByIidFailFunc);
}

function setPlcAttributeData(iid) {// 속성목록 조회
	otpInsAtt.plcSearchByIid(iid, cbInsAttSearchByIidSucessFunc,
			cbInsAttSearchByIidFailFunc);
}

function putPlcInstanceAttrDatas(iid, attrKey, content, contentType) {// 인스턴스 속성
	otpInsAtt.plcPut(iid, attrKey, content, 'json', insAttPutSucessFunc, cbPutFailFunc);
}

function setPlcInstanceAttrDatas(iid, attrKey, content, contentType) {// 인스턴스 속성
	otpInsAtt.plcSet(iid, attrKey, content, 'json', insAttSetSucessFunc, cbSetFailFunc);
}

function delPlcInstanceAttrDatas(iid, attrKey) {// 인스턴스 속성
	otpInsAtt.plcDel(iid, attrKey, insAttDelSucessFunc, cbDelFailFunc);
	refreshAttributeList();
}

function setPlcAdapterData() {
	otpAdt.plcGetAll(cbAdtGetAllSucessFunc, cbAdtGetAllFailFunc);
//	otpAdt.getAll(cbAdtGetAllSucessFunc, cbAdtGetAllFailFunc);
}
function setInstanceData(aid) {// 인스턴스 조회
	otpIns.searchByAid(aid, cbInsSearchByAidSucessFunc,
			cbInsSearchByAidFailFunc);
	$(".instanceState").text("");	
}

function setPlcMonitoringStart(did,content){
	
	otpEvent.plcMonitoringStart(did, content, "json",cbPlcStartSucessFunc, cbPlcStartFailFunc, cbPlcStartEventFunc);
}

function setPlcMonitoringStop(did,attKey){
	
	otpEvent.plcMonitoringStop(did,attKey, cbPlcStopSucessFunc, cbPlcStopFailFunc);
}

function setPlcGathringStart(did,content){
	
	otpEvent.plcGatheringStart(did, content, "json",cbInsAttStartSucessFunc, cbInsAttStartFailFunc, cbInsAttEventFunc);
}

function setPlcGathringStop(did,attKey){
	
	otpEvent.plcGatheringStop(did,attKey,cbInsAttStopSucessFunc, cbInsAttStopFailFunc);
}

/*******************************************************/
/*************** Search Set Put Del END ****************/
/*******************************************************/


/* Button Events */
function plcInstanceStart(iid) {
	otpIns.plcStart(iid, cbInsStartSucessFunc, cbInsStartFailFunc);
}

function plcInstanceStop(iid) {
	otpIns.plcStop(iid, cbInsStopSucessFunc, cbInsStopFailFunc);
}

/*******************************************************/
/****************** Call backs START *******************/
/*******************************************************/
function cbLoginSucessFunc(evt) {// 로그인 성공 처리
	
	/* alert("로그인 하셨습니다."); */
	
	var sucessLogin=function(){
		var authObjStr = otpAuth.toString();
		sessionStorage.setItem('authObj', authObjStr); // 세션스토리지에 등록
		window.location.href = "./monitoring.html";
	}	
	customConfirm("알림", "로그인 하셨습니다.", sucessLogin);
	$(".bootbox-close-button").click(sucessLogin);
	
}
function cbLoginFailFunc(evt) {// 로그인 실패 처리
//	commonErrorMessage(evt);
	commonAlert("에러",evt.msg,evt.code+"\n"+evt.msg);
}

function cbCloseFunc(evt) {// 웹소켓 종료 이벤트 --> 로그아웃 처리
//	commonAlert("알림", "로그아웃 되었습니다.", "로그아웃 성공");
}

function cbRestoreAuthSucessFunc(evt) {// 인증객체 복원 성공
	console.log(evt);
	initGetData();
}
function cbRestoreAuthCloseFunc(evt) {// 세션종료 또는 커넥션 종료 Event함수
	console.log(evt);
	commonAlert("알림", "로그아웃 되었습니다.", "세션 아웃");

	window.location.href = "./index.html";
}
function cbRestoreAuthFailFunc(evt) {// 인증객체 복원 실패
	console.log(evt);
	commonAlert("경고", "서버와의 연결이 종료 되었습니다. 로그인페이지로 이동합니다.", "인증객체 복원에 실패했습니다.")
	window.location.href = "./index.html";
}
function insAttPutSucessFunc(evt) { // PUT 성공
	console.log(evt);
	commonAlert("알림", "인스턴스속성 등록이 완료되었습니다.", "입력성공");
	refreshAttributeList();
}
function cbPutFailFunc(evt) { // PUT 실패
	console.log(evt);
	commonErrorMessage(evt);
}
function insAttDelSucessFunc(evt) { // PUT 성공
	console.log(evt);
	commonAlert("알림", "인스턴스속성 삭제가 완료되었습니다.", "삭제성공");
	refreshAttributeList();
}
function cbDelFailFunc(evt) { // DEL 실패
	console.log(evt);
	commonErrorMessage(evt);
	/*
	 * bootbox.alert("삭제를 실패했습니다.", function(){ console.log("Delete Fail!"); });
	 */
}

/*******************************************************/
/******************* Call backs END ********************/
/*******************************************************/



/*******************************************************/
/******************** Parser Start *********************/
/*******************************************************/
/**
 * 인스턴스 정보 Json데이터 변환
 */
function parseInstanceJsonData(evt_data) {

	var data = evt_data.content;
	var tmp_evt_json = JSON.parse(data);
	var rowData = [];
	var statArray = {
		0 : "없음",
		1 : "생성",//create
		2 : "생성",//begin
		4 : "생성",//doing
		8 : "완료",//completed
		16 : "완료",//end
		32 : "장애"//error
	};

	var statEventArray = {
		0 : "없음",
		1 : "초기",//create
		2 : "초기",//init
		4 : "기동",//start
		8 : "일시정지",//suspend
		16 : "정지",//stop
		32 : "해제"//dispose
	};
	
	
	console.log(tmp_evt_json);

	$.each(tmp_evt_json, function(index, value) {
		rowData.push({
			instanceId : value['instance.id'],
			devicePoolId : value['device.pool.id'],
			adapterId : value['adapter.id'],
			instanceName : value['instance.name'],
			instanceKind : value['instance.kind'],
			instanceType : value['instance.type'],
			defaultDeviceId : value['default.device.id'],
			instanceEventStatus : statEventArray[value['instance.event']],
			isUse : value['is.use'],
			sessionTimeout : value['session.timeout'],
			instanceStatus : statArray[value['instance.status']],
			initDeviceStatus : value['init.device.status'],
			ip : value['ip'],
			port : value['port'],
			url : value['url'],
			lat : value['latitude'],
			lon : value['longitude'],
			selfId : value['self.id'],
			selfPw : value['self.pw'],
			remark : value['remark'],
			alterDate : value['alter.date'],
			regDate : value['registration.date']
		});
	});

	return rowData;
}

function parsePlcEventJsonData(evt_data) {

	var deviceTypeArray={
			"SM":"특수 릴레이(SM)",
			"SD":"특수 레지스터(SD)",
			"X*":"입력(X*)",		
			"Y*":"출력(Y*)",		
			"M*":"내부 릴레이(M*)",		
			"L*":"래치 릴레이(L*)",			
			"F*":"어넌시에이터(F*)",
			"V*":"에지 릴레이(V*)",		
			"B*":"링크 릴레이(B*)",						
			"D*":"데이터 레지스터(D*)",					
			"W*":"링크 레지스터(W*)",					
			"TS":"타이머 접점(TS)",					
			"TC":"타이머 코일(TC)",					
			"TN":"타이머 현재값(TN)",					
			"SS":"적산타이머 접점(SS)",					
			"SC":"적산타이머 코일(SC)",			
			"SN":"적산타이머 현재값(SN)",				
			"CS":"카운터 접점(CS)",				
			"CC":"카운터 코일(CC)",				
			"CN":"카운터 현재값(CN)",				
			"SB":"링크 특수 릴레이(SB)",				
			"SW":"링크 특수 레지스터(SW)",				
			"S*":"스텝 릴레이(S*)",				
			"DX":"다이렉트 입력(DX)",				
			"DY":"다이렉트 출력(DY)"
	};
	
	var data = evt_data.content;
	var tmp_evt_json = JSON.parse(data);
	var rowData = [];

//	console.log(tmp_evt_json);

	rowData.push({
		deviceMessage : tmp_evt_json['msg'],
		instanceID : tmp_evt_json['instance.id'],
		attributionKey : tmp_evt_json['attribution.key'],
		deviceEnd : tmp_evt_json['code'],
		deviceStartAddr : tmp_evt_json['device.address'],
		deviceType : deviceTypeArray[tmp_evt_json['device.type']],
		instanceName : tmp_evt_json['instance.name'],
		deviceId : tmp_evt_json['device.id'],
		deviceScore : tmp_evt_json['device.score'],
		gatheringPeriod : tmp_evt_json['gathering.period'],
		deviceName : tmp_evt_json['device.name'],
		eventTime : tmp_evt_json['event.time'],
		attributeName : tmp_evt_json['attribution.description'],
		attributeValue : tmp_evt_json['attribution.value']
	});

	return rowData;
}

function parsePlcAttributeJsonData(evt_data) {

	var deviceTypeArray={
		"SM":"특수 릴레이(SM)",
		"SD":"특수 레지스터(SD)",
		"X*":"입력(X*)",		
		"Y*":"출력(Y*)",		
		"M*":"내부 릴레이(M*)",		
		"L*":"래치 릴레이(L*)",			
		"F*":"어넌시에이터(F*)",
		"V*":"에지 릴레이(V*)",		
		"B*":"링크 릴레이(B*)",						
		"D*":"데이터 레지스터(D*)",					
		"W*":"링크 레지스터(W*)",					
		"TS":"타이머 접점(TS)",					
		"TC":"타이머 코일(TC)",					
		"TN":"타이머 현재값(TN)",					
		"SS":"적산타이머 접점(SS)",					
		"SC":"적산타이머 코일(SC)",			
		"SN":"적산타이머 현재값(SN)",				
		"CS":"카운터 접점(CS)",				
		"CC":"카운터 코일(CC)",				
		"CN":"카운터 현재값(CN)",				
		"SB":"링크 특수 릴레이(SB)",				
		"SW":"링크 특수 레지스터(SW)",				
		"S*":"스텝 릴레이(S*)",				
		"DX":"다이렉트 입력(DX)",				
		"DY":"다이렉트 출력(DY)"
	};
	var eventArray={
		"0":"정지",
		"1":"기동",
	};
	
	var data = evt_data.content;
	var tmp_evt_json = JSON.parse(data);
	var rowData = [];
	console.log(tmp_evt_json);

	$.each(tmp_evt_json, function(index, value) {

		var id = value['instance.id'];
		var key = value['attribution.key'];
		var description = value['attribution.description'];
		var eventStatus = eventArray[value['event.status']];
		
//		var type = value['device.type'];
		var type =deviceTypeArray[value['device.type']];
					
		var startAddr = value['device.address'] ;
		var deviceScore = value['device.score'];
		var  gathering= value['gathering.period'];		
		

		rowData.push({
			eventStatus:eventStatus,
			description:description,
			key:key,
			deviceType:type,
			startAddr:startAddr,
			deviceScore:deviceScore,
			gathering:gathering
		});
	});

	return rowData;
}

/**
 * 아답터 정보 Json데이터 변환
 */
function parseAdapterJsonData(evt_data) {
	var data = evt_data.content;
	var tmp_evt_json = JSON.parse(data);

	var rowData = [];
	console.log(tmp_evt_json);
	$.each(tmp_evt_json, function(index, value) {

		var adapterId = value['adapter.id'];
		var adapterName = value['adapter.name'];
		var adapterKind = value['adapter.kind'];
		var adapterType = value['adapter.type'];
		var defaultDeviceId = value['default.device.id'];
		var sessionTimeout = value['session.timeout'];
		var initDeviceStatus = value['init.device.status'];
		var ip = value['ip'];
		var port = value['port'];
		var latitude = value['latitude'];
		var longitude = value['longitude'];
		var selfId = value['self.id'];
		var selfPw = value['self.pw'];
		var remark = value['remark'];
		var plcImage=value['adapter.image']
		
		rowData.push({
			adapterId : adapterId,
			adapterName : adapterName,
			adapterKind : adapterKind,
			adapterType : adapterType,
			defaultDeviceId : defaultDeviceId,
			sessionTimeout : sessionTimeout,
			initDeviceStatus : initDeviceStatus,
			ip : ip,
			port : port,
			lat : latitude,
			lon : longitude,
			selfId : selfId,
			selfPw : selfPw,
			remark : remark,
			plcImage:plcImage
		});
	});

	return rowData;
}



/**
 * ServerMessageJSON
 */
function parseMessageJsonData(jsonContents) {
	var data = jsonContents;
	var tmp_evt_json = JSON.parse(data);
	var rowData = [];

	console.log(tmp_evt_json);
	
	var sessionStatusArray = {		
		4 : "세션타임아웃",//deActivate
		8 : "로그아웃/인스턴스중지"//dispose
	};
	
	$.each(tmp_evt_json, function(index, value) {

		var instanceID = value['instance.id'];
		var instanceName = value['instance.name'];
		var deviceID = value['device.id'];
		var deviceName = value['device.name'];
		var userID = value['user.id'];
		var sessionStatus = sessionStatusArray[value['session.status']];
		var eventTime = value['event.time'];

		rowData.push({
			instanceID : instanceID,
			instanceName : instanceName,
			deviceID : deviceID,
			deviceName : deviceName,
			userID : userID,
			sessionStatus : sessionStatus,
			eventTime : eventTime,
		});
	});
	
	return rowData;
}


/*******************************************************/
/********************* Parser End **********************/
/*******************************************************/



/*******************************************************/
/*************** CommonFunction Start ******************/
/*******************************************************/

function loadData(scope, rowDataByJson) { // Load datas to grid
	scope.gridOptions.api.setRowData(rowDataByJson);
}

function customConfirm(title,contents,sucessFunc,failFunc){

	bootbox.dialog({
	   title:title,
	   message :contents,
	   buttons:{
	      success:{
	         label:"확인",
	         className:"modalDefaultBtn sucessBtn",
	         callback:sucessFunc   
	      },
	      cancle:{
		         label:"취소",
		         className:"modalCancleBtn cancleBtn",
		         callback:failFunc
	      }
	   }	   
	});      

	if(failFunc==undefined){
		$(".cancleBtn").css("display","none");
	}
	
}

function commonConfirm(tit, data, callbackFunc) {


	var callbackArg = Array.prototype.splice.call(arguments, 3);
	var callbackArg1 = Array.prototype.splice.call(arguments, 4);
	
	bootbox.dialog({
		message : data,
		title : tit,
		buttons : {
			main : {
				label : "확인",
				className : "modalDefaultBtn",
				callback : function() {
					callbackFunc.apply(this, callbackArg,callbackArg1);
				}
			},
			danger : {
				label : "취소",
				className : "modalCancleBtn",
				callback : function() {
					console.log(callbackFunc);
				}

			}
		}
	});
}

//영진 추가 함수
//공통 Alert함수
//tit->알림,경고 등 타이틀, data->UI출력내용, console->콘솔출력내용
function commonAlert(tit, data, log) {
	bootbox.dialog({
		message : data,
		title : tit,
		buttons : {
			main : {
				label : "확인",
				className : "modalDefaultBtn",
				callback : function() {
					console.log(log);
				}
			}
		}
	});
}
var formatTime = function(unixTimestamp) {
	var date = new Date(unixTimestamp);
	var year = date.getFullYear();
	var month = date.getMonth() + 1;
	var day = date.getDate();
	var hours = date.getHours();
	var minutes = date.getMinutes();
	var seconds = date.getSeconds();

	// the above dt.get...() functions return a single digit
	// so I prepend the zero here when needed
	if (month < 10)
		month = '0' + month;

	if (day < 10)
		day = '0' + day;

	if (hours < 10)
		hours = '0' + hours;

	if (minutes < 10)
		minutes = '0' + minutes;

	if (seconds < 10)
		seconds = '0' + seconds;

	return year + "" + month + "" + day + "" + hours + "" + minutes;// + "" +
																	// seconds;
}

//네비게이션 메뉴 동작 스크립트 Start
function userInfo(){
 
 bootbox.dialog({
    title:"사용자 정보",
    message : 
     '<div class="form-group col-xs-12">'+   
      '<label class="col-xs-3 control-label modalLabel">접속시간</label>'+
     '<div class="col-xs-9 "><span id="userInfoLoginTime" class="textStyle"></div>'+
     '</div>'+
     '<div class="form-group col-xs-12">'+
      '<label class="col-xs-3 control-label modalLabel">유저ID</label>'+
     '<div class="col-xs-9 "><span id="userInfoUserId" class="textStyle"></div>'+
     '</div>'+
     '<div class="form-group col-xs-12">'+
      '<label class="col-xs-3 control-label modalLabel">장치ID</label>'+
     '<div class="col-xs-9 "><span id="userInfoDeviceId" class="textStyle"></div>'+
     '</div>'+
     '<div class="frame_inner_search"></div>', 
    buttons:{
       success:{
          label:"확인",
          className:"btn btn-primary modalDefaultBtn",
       }
    }
 });      
// var login_ip= document.location.href.split("//")[1].split(":")[0];
// var loginInfo=sessionStorage.getItem("authObj");
// 
// console.log(loginInfo);
 
 $("#userInfoLoginTime").text(sessionStorage.getItem("loginTime"));
 $("#userInfoUserId").text(sessionStorage.getItem("uid"));
 $("#userInfoDeviceId").text(sessionStorage.getItem("did"));
 
}

//Nav Bar Start//

function versionInfo(){

 bootbox.dialog({
    title:"버전 정보",
    message :'<h2 class="form-group"><div class="col-xs-1"></div>HYUNDAI SmartIoT Middleware v2.0</h2>'+
     '<div class="col-xs-12">'+
     '<div class="col-xs-1"></div>'+
     '<div class="col-xs-11"><span id="smartIotVersion"/></div>'+
     '</div>'+
     '<div class="col-xs-12">'+
     '<div class="col-xs-1"></div>'+
     '<div class="col-xs-11"><span id="smartIotUpdateVersion"/></div>'+
     '</div>'+
     '<div class="col-xs-12">'+
     '<div class="col-xs-1"></div>'+
     '<div class="col-xs-11"><span id="smartIotId"/></div>'+
     '</div>'+
     '<div class="col-xs-12">'+
     '<div class="col-xs-1"></div>'+
     '<div class="col-xs-11"><a class="aTagLinkStyle" text-decoration:initial" href="http://www.hd-bsnc.com/index.html">Copyright © 2014 HYUNDAI BS&C Co., Ltd. All Rights Reserved</a></div>'+
     '</div>'+
     '<div class="frame_inner_search"></div>',  
    buttons:{
       success:{
          label:"확인",
          className:"btn btn-primary modalDefaultBtn",
          /*callback:function(){
             
          }      */         
       }
    }
 });      
 
 $("#smartIotVersion").text("버전 : 2.0");
 $("#smartIotUpdateVersion").text("업데이트 버전 : Ver.2.0(16.04.12)");
 $("#smartIotId").text("제품ID : 명화공업");
 
}

function navIconLogout(){
	commonConfirm("알림","로그아웃 하시겠습니까?",function(){  
		otpAuth.logout();
		location.href="index.html";

		sessionStorage.clear();
	});
}


/*******************************************************/
/***************** CommonFunction End ******************/
/*******************************************************/


/*******************************************************/
/****************** ErrorMessage Start******************/
/*******************************************************/
function commonErrorMessage(evt_data,comp) { // 에러 메세지
	
	console.log(evt_data);
	
	var errorMsg = evt_data.msg;
	var errorCode = evt_data.msgCode;
	var errorType = evt_data.msgType;
	
	if(errorType==="info" && comp==="blankInsAtt"){//인스턴스
		$(".blankAdapter").text("데이터가 존재하지 않습니다.");
	}else if(errorType==="info" && comp==="blankInstance"){
		$(".blankInstance").text("데이터가 존재하지 않습니다.");
	}else if(errorType==="info" && comp==="blankAttribute"){
		$(".blankAttribute").text("데이터가 존재하지 않습니다.");
	}else if(errorType=="error" && errorCode=="101"){
		var sessionTimeoutEvent=function(){
			location.href="index.html";
		};
		
		customConfirm("에러", errorMsg,sessionTimeoutEvent);		
		$(".bootbox-close-button").click(sessionTimeoutEvent);
				
	}else{
		commonAlert("에러",errorMsg, errorCode + "\n " + errorMsg);
	}
}

/*******************************************************/
/****************** ErrorMessage End *******************/
/*******************************************************/

/*************Start Stop Suspend Btn Activity NonActivity Start****************/

function setBtnState(state,event){	
	
	if(state==undefined&&event==undefined){
		$("#stopInstanceActivityBtn").hide();
		$("#stopInstanceNonActivityBtn").show();
		$("#startInstanceActivityBtn").hide();
		$("#startInstanceNonActivityBtn").show();	
	}else{
		$("#stopInstanceActivityBtn").show();
		$("#stopInstanceNonActivityBtn").hide();
		$("#startInstanceActivityBtn").show();
		$("#startInstanceNonActivityBtn").hide();
		
		if((state=="완료")&&event=="기동"){
			$("#startInstanceActivityBtn").hide();
			$("#startInstanceNonActivityBtn").show();
		}else if((state=="없음")&&event=="없음"){
			$("#stopInstanceActivityBtn").hide();
			$("#stopInstanceNonActivityBtn").show();
		}
	}
}

/*************Start Stop Suspend Btn Activity NonActivity End****************/


/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////

/*************************Session Server Message START ***************************/
function sessionEndMsgInit() {
	window.addEventListener("otp-sessionDisconnectEvt",
			serverMessageCallbackFunc);

	var sessionDisconnectInfo = sessionStorage.getItem("otp.session.disconnect:" + otpAuth.getLoginDID());
	if (sessionDisconnectInfo == null) {
		$(".serverMessageInfo").hide();

	} else {
		var rowDataByJson = parseMessageJsonData(sessionDisconnectInfo);
		loadData(serverMessageScope, rowDataByJson);
		gRowData = rowDataByJson;
		$(".serverMessageInfo").show();
		$("#serverMessageCount").text(
				serverMessageScope.gridOptions.rowData.length + "건");
	}
	$("#sessionListCloseBtn").click(function() {
		clearSessionDisConnectFunc();
	});
}

function serverMessageCallbackFunc(evt) {
	// 서버의 메세지가 왔을 때 호출되는 콜백메서드
	var rowDataByJson = parseMessageJsonData(evt.content);
	serverMessageScope.addNewItem(rowDataByJson);

	$(".serverMessageInfo").show();
	$("#serverMessageCount").text(serverMessageScope.gridOptions.rowData.length + "건");
}


var messageColumnDefs = [
    {headerName: "세션종료일시", field: "eventTime",filter: 'text',width: 140},
	{headerName: "원인", field: "sessionStatus",filter: 'text',width: 150},
	{headerName: "장치ID", field: "deviceID",width: 100},
	{headerName: "장치명", field: "deviceName",width: 100},
	{headerName: "인스턴스ID", field: "instanceID", filter: 'text',width: 100},
	{headerName: "인스턴스명", field: "instanceName", filter: 'text',width: 100},
	{headerName: "유저ID", field: "userID",filter: 'text',width: 100}
];

var gRowData = [];
agGridModule.controller("serverMessageCtrl", function($scope) {

	serverMessageScope = $scope;

	$scope.gridOptions = {
		columnDefs: messageColumnDefs,
		rowSelection: 'single',
		onRowSelected: rowSelectedFunc,
		rowData: gRowData,
		enableSorting: true,
		enableFilter: true,
		enableColResize: true,
		showToolPanel: false,
		toolPanelSuppressValues: true,
		toolPanelSuppressPivot: true,
	}
	
	/* Normal Functions */
	function rowSelectedFunc(event) { 
		
		console.log(event.node.data);
	}
	$scope.addNewItem = function(addRows){
		//rowData.push({ sessionId: 'Test insert1 ', transmission: 'Test insert1' });
//		var updatedNodes = [];
//		$scope.gridOptions.api.forEachNode( function(node) {
//			updatedNodes.push({ sessionId:node.data.sessionId, transmission:node.data.transmission});
//		});
		//idx = idx + 1;
	
		for (var idx in addRows) {
			gRowData.unshift(addRows[idx]);
		};
		
		//$scope.gridOptions.api.setRowData($scope.data);
		//$scope.gridOptions.api.refreshRows($scope.data);
		//$scope.gridOptions.api.refreshView();
		//$scope.gridOptions.api.refreshRows(updatedNodes);
		$scope.gridOptions.api.setRowData(gRowData);
    };

});

function serverMessageListFunc(){
	
	var sessionDisconnectInfo = sessionStorage.getItem("otp.session.disconnect:" + otpAuth.getLoginDID());
	if(!sessionDisconnectInfo){
		return;
	}
	
	var rowDataByJson=parseMessageJsonData(sessionDisconnectInfo);
	gRowData = rowDataByJson;
	loadData(serverMessageScope,rowDataByJson);
}

function clearSessionDisConnectFunc(){
//	sessionStorage.setItem("otp.session.disconnect", "");
	sessionStorage.removeItem("otp.session.disconnect:" + otpAuth.getLoginDID());

	$(".serverMessageInfo").hide();
	gRowData=[];
//	serverMessageScope.gridOptions.rowData = [];
	loadData(serverMessageScope,gRowData);
}

/*************************Session Server Message END ***************************/
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////


var viewSynchronize=function(scopeArgs1, scopeArgs2, scopeArgs3){
	
	if((scopeArgs1!=null||typeof scopeArgs1=='undefined')&&(scopeArgs2!=null || typeof scopeArgs2=='undefined')&&(scopeArgs3!=null||typeof scopeArgs3=='undefined')){
	
		initAPI();
		sessionEndMsgInit();
		clearInterval(scopeSynchronizeIntervalId);
	}	
}
