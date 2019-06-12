$(document).ready(function(){
	$(".serverMessageInfo").hide();	
});


/** ***************************** API SCRIPTS START ***************************** */
/* Set Datas From API */
function setAdapterData() {
	otpAdt.getAll(cbAdtGetAllSucessFunc, cbAdtGetAllFailFunc);
}

function setSearchAdapterData() {// 아답터ID 조회(검색)
	otpAdt.getAll(cbSrchAdtGetAllSucessFunc, cbSrchAdtGetAllFailFunc);
}

function setAdapterAttData(aid) { // 아답터 속성 조회
	otpAdt.attGet(aid, cbAdtAttGetSucessFunc, cbAdtAttGetFailFunc);
}

function setAdapterFunctionData(aid) { // 아답터 기능 조회
	otpAdt.funcGet(aid, cbAdtFunctionGetSucessFunc, cbAdtFunctionGetFailFunc);
}

function setInstanceData(aid) {// 인스턴스 조회
	otpIns.searchByAid(aid, cbInsSearchByAidSucessFunc,
			cbInsSearchByAidFailFunc);
	$(".instanceState").text("");	
}

function setInstanceAllData(iid,did,ip){
	otpIns.all(iid,did,ip,cbInsAllGetSucessFunc,cbInsAllgetFailFunc);
}

function setSessionData(iid) {// 세션목록 조회
	otpSession.searchByIid(iid, cbSessionSearchByIidSucessFunc,
			cbSessionSearchByIidFailFunc);
}

function setControlerValuePopup(val){ //제어를 위해 최신정보를 서버에 다시 요청하여 받아옴
	if(val==0){//아직 팝업이 떠있지 않는 상태 그래서 팝업을 뛰어야하는 경우 호출됨(속성)
		otpCtrl.read(selectedSessionId, selectedControlKey.split('?')[0], cbAttControlSucessFuncAndGrid,
				cbAttControlFailFunc);
	}else if(val==1){ //팝업이 떠있는 상태에서 새로고침을 눌렀을때 호출됨(속성)
		otpCtrl.read(selectedSessionId, selectedControlKey.split('?')[0], cbControlSucessFunc,
				cbControlFailFunc);
	}
}

function setFunctionData(iid) {// 기능목록 조회
	otpInsFunc.searchByIid(iid, cbInsFuncSearchByIidSucessFunc,
			cbInsFuncSearchByIidFailFunc);
}

function setAttributeData(iid) {// 속성목록 조회
	otpInsAtt.searchByIid(iid, cbInsAttSearchByIidSucessFunc,
			cbInsAttSearchByIidFailFunc);
}

function setAttControlData(sid) {// 제어목록 속성조회
	otpSession.attGetAll(sid, cbSessionAttGetBySidSucessFunc,
			cbSessionAttGetBySidFailFunc);
}

function setFuncControlData(sid) {// 제어목록 기능조회
	otpSession.funcGetAll(sid, cbSessionFuncGetBySidSucessFunc,
			cbSessionFuncGetBySidFailFunc);
}

function setDevicePoolData() { // 장치풀ID 조회
	otpDevPool.getAll(cbDevPoolGetAllSucessFunc, cbDevPoolGetAllFailFunc);
}

function setSearchDevicePoolData() {// 장치풀ID 조회(검색)
	otpDevPool.getAll(cbSrchDevPoolGetAllSucessFunc,
			cbSrchDevPoolGetAllFailFunc);
}

function setDeviceData(dpid) { // 장치ID 조회
	otpDev.searchByDpid(dpid, cbDevSearchByDpidSucessFunc,cbDevSearchByDpidFailFunc);
}

function setSearchDeviceData(dpid) { // 장치ID 조회(검색)
	otpDev.searchByDpid(dpid, cbSrchDevSearchByDpidSucessFunc,
			cbSrchDevSearchByDpidFailFunc);
	
}

function setUserPoolData() { // 유저풀 조회
	otpUsePool.getAll(cbUsePoolGetAllSucessFunc, cbUsePoolGetAllFailFunc);
}

function setSearchUserPoolData() {// 유저풀ID 조회(검색)
	otpUsePool.getAll(cbSrchUsePoolGetAllSucessFunc,
			cbSrchUsePoolGetAllFailFunc);
}

function setUserData(upid) { // 유저 조회
	otpUse.searchByUpid(upid, cbUseSearchByUpidSucessFunc,
			cbUseSearchByUpidFailFunc);
}

function setUserFilterData(uid) { // 유저 필터 조회
	otpUseFilt.searchByUid(uid, cbUseFilterSearchByUidSucessFunc,
			cbUseFilterSearchByUidFailFunc);
}

function setDomainData(dType) { // 도메인 조회
	otpDomain.searchByType(dType, cbDomSearchByTypeSucessFunc,
			cbDomSearchByTypeFailFunc);
}

/* Put Set Del Functions */

function putInstanceDatas(iid, content, contentType) {// 인스턴스
	otpIns.put(iid, content, 'json', insInstancePutSucessFunc, cbPutFailFunc);
}

function setInstanceDatas(iid, content, contentType) {// 인스턴스
	otpIns.set(iid, content, 'json', insInstanceSetSucessFunc, cbSetFailFunc);
}

function delInstanceDatas(iid) {// 인스턴스
	otpIns.del(iid, insInstanceDelSucessFunc, cbDelFailFunc);
}

function putInstanceFuncDatas(iid, funcKey, content, contentType) {// 인스턴스 기능
	otpInsFunc.put(iid, funcKey, content, 'json', insFuncPutSucessFunc,
			cbPutFailFunc);
}

function setInstanceFuncDatas(iid, funcKey, content, contentType) {// 인스턴스 기능
	otpInsFunc.set(iid, funcKey, content, 'json', insFuncSetSucessFunc,
			cbSetFailFunc);
}

function delInstanceFuncDatas(iid, funcKey) {// 인스턴스 기능
	otpInsFunc.del(iid, funcKey, insFuncDelSucessFunc, cbDelFailFunc);
	refreshFunctionList();
}

function putInstanceAttrDatas(iid, attrKey, content, contentType) {// 인스턴스 속성
	otpInsAtt
			.put(iid, attrKey, content, 'json', insAttPutSucessFunc, cbPutFailFunc);
}

function setInstanceAttrDatas(iid, attrKey, content, contentType) {// 인스턴스 속성
	otpInsAtt.set(iid, attrKey, content, 'json', insAttSetSucessFunc, cbSetFailFunc);
}

function delInstanceAttrDatas(iid, attrKey) {// 인스턴스 속성
	otpInsAtt.del(iid, attrKey, insAttDelSucessFunc, cbDelFailFunc);
	refreshAttributeList();
}

function putDevicePoolDatas(dpid, content, contentType) {// 장치풀
	otpDevPool.put(dpid, content, 'json', devDevpoolPutSucessFunc, cbPutFailFunc);
}

function setDevicePoolDatas(dpid, content, contentType) {// 장치풀
	otpDevPool.set(dpid, content, 'json', devDevpoolSetSucessFunc, cbSetFailFunc);
}

function delDevicePoolDatas(dpid) {// 장치풀
	otpDevPool.del(dpid, devDevpoolDelSucessFunc, cbDelFailFunc);
}

function putDeviceDatas(did, content, contentType) {// 장치
	otpDev.put(did, content, 'json', devDevPutSucessFunc, cbPutFailFunc);
}

function setDeviceDatas(did, content, contentType) {// 장치
	otpDev.set(did, content, 'json', devDevSetSucessFunc, cbSetFailFunc);
}

function delDeviceDatas(did) {// 장치
	otpDev.del(did, devDevDelSucessFunc, cbDelFailFunc);
}

function putUserPoolDatas(upid, content, contentType) {// 유저풀
	otpUsePool.put(upid, content, 'json', userPoolPutSucessFunc, cbPutFailFunc);
}

function setUserPoolDatas(upid, content, contentType) {// 유저풀
	otpUsePool.set(upid, content, 'json', userPoolSetSucessFunc, cbSetFailFunc);
}

function delUserPoolDatas(upid) {// 유저풀
	otpUsePool.del(upid, userPoolDelSucessFunc, cbDelFailFunc);
}

function putUserDatas(uid, content, contentType) {// 유저
	otpUse.put(uid, content, 'json', userUserPutSucessFunc, cbPutFailFunc);
}

function setUserDatas(uid, content, contentType) {// 유저
	otpUse.set(uid, content, 'json', userUserSetSucessFunc, cbSetFailFunc);
}

function delUserDatas(uid) {// 유저
	otpUse.del(uid, userUserDelSucessFunc, cbDelFailFunc);
}

function putUserFilterDatas(uid, authFilter, content, contentType) {// 유저필터
	otpUseFilt.put(uid, authFilter, content, 'json', userFilterPutSucessFunc,
			cbPutFailFunc);
}

function setUserFilterDatas(uid, authFilter, content, contentType) {// 유저필터
	otpUseFilt.set(uid, authFilter, content, 'json', userFilterSetSucessFunc,
			cbSetFailFunc);
}

function delUserFilterDatas(uid, authFilter) {// 유저필터
	otpUseFilt.del(uid, authFilter, userFilterDelSucessFunc, cbDelFailFunc);
}

// 영진추가 삭제와, 리플레쉬를 한번에 하기위한 공통함수
function delNrefreshInstanceFuncDatas(iid, funcKey) {// 인스턴스 기능
	otpInsFunc.del(iid, funcKey, cbDelSucessFunc, cbDelFailFunc);
}
function delNrefreshInstanceAttrDatas(iid, attrKey) {// 인스턴스 속성
	otpInsAtt.del(iid, attrKey, cbDelSucessFunc, cbDelFailFunc);
	refreshAttributeList();
}

/* Button Events */
//function instanceStart(iid) {
//	otpIns.start(iid, cbInsStartSucessFunc, cbInsStartFailFunc);
//}
//
//function instanceStop(iid) {
//	otpIns.stop(iid, cbInsStopSucessFunc, cbInsStopFailFunc);
//}
//
//function instanceSuspend(iid) {
//	otpIns.suspend(iid, cbInsSuspendSucessFunc, cbInsSuspendFailFunc);
//}

function sessionDisconnect(sid) {
	otpSession.disConnect(sid, cbSessionDisconnectSucessFunc,
			cbSessionDisconnectFailFunc);
}

/* Call backs */
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
	commonAlert("경고", "서버와의 연결이 종료 되었습니다. 로그인페이지로 이동합니다.", "인증객체 복원에 실패했습니다.");
	window.location.href = "./index.html";
}

//function cbPutSucessFunc(evt) { // PUT 성공 인스턴스 등록
//	console.log(evt);
//	/* alert("입력되었습니다."); */
//	/*
//	 * bootbox.alert("입력되었습니다.", function(){ console.log("Put success!"); });
//	 */
//	commonAlert("알림", "인스턴스 등록이 완료 되었습니다.", "입력성공");
//}

//영진추가 시작 성공함수(리플레시)
function userUserPutSucessFunc(evt) { // PUT 성공
	$('.modal').modal('hide');
	console.log(evt);
	commonAlert("알림", "유저 등록이 완료되었습니다.", "입력성공");
	
	if(selectedUserPoolId)
		refreshUserList();
}
function userFilterPutSucessFunc(evt) { // PUT 성공
	$('.modal').modal('hide');
	console.log(evt);
	commonAlert("알림", "필터 등록이 완료되었습니다.", "입력성공");
	refreshUserFilterList();
}
function userPoolPutSucessFunc(evt) { // PUT 성공
	$('.modal').modal('hide');
	console.log(evt);
	commonAlert("알림", "유저풀 등록이 완료되었습니다.", "입력성공");
	refreshUserPoolList();
}

function userUserSetSucessFunc(evt) { // SET 성공
	$('.modal').modal('hide');
	refreshUserList();
	console.log(evt);
	commonAlert("알림", "유저 수정이 완료되었습니다.", "수정성공");
}
function userFilterSetSucessFunc(evt) { // PUT 성공
	$('.modal').modal('hide');
	console.log(evt);
	commonAlert("알림", "필터 수정이 완료되었습니다.", "수정성공");
	refreshUserFilterList();
}
function userPoolSetSucessFunc(evt) { // set 성공
	$('.modal').modal('hide');
	console.log(evt);
	commonAlert("알림", "유저풀 수정이 완료되었습니다.", "수정성공");
	refreshUserPoolList();
}

function userUserDelSucessFunc(evt) { // SET 성공
	refreshUserList();
	console.log(evt);
	commonAlert("알림", "유저 삭제가 완료되었습니다.", "삭제성공");
}
function userFilterDelSucessFunc(evt) { // PUT 성공
	console.log(evt);
	commonAlert("알림", "필터  삭제가 완료되었습니다..", "삭제성공");
	refreshUserFilterList();
}
function userPoolDelSucessFunc(evt) { // PUT 성공
	console.log(evt);
	commonAlert("알림", "유저풀 삭제가 완료되었습니다.", "삭제성공");
	refreshUserPoolList();
}


function insInstancePutSucessFunc(evt) { // 인스턴스 등록 put
	$('.modal').modal('hide');
	console.log(evt);
	commonAlert("알림", "인스턴스 등록이 완료되었습니다.", "입력성공");
	if(selectedAdapterId)
		refreshInstanceList();
}
function insFuncPutSucessFunc(evt) { // 인스턴스 기능 PUT 성공
	$('.modal').modal('hide');	
	console.log(evt);
	commonAlert("알림", "인스턴스기능 등록이 완료되었습니다. 인스턴스가 동작중인 경우 다시 기동시 반영됩니다.", "입력성공");
	refreshFunctionList();
}
function insAttPutSucessFunc(evt) { // PUT 성공
	$('.modal').modal('hide');
	console.log(evt);
	commonAlert("알림", "인스턴스속성 등록이 완료되었습니다. 인스턴스가 동작중인 경우 다시 기동시 반영됩니다.", "입력성공");
	refreshAttributeList();
}

function insInstanceSetSucessFunc(evt) { // set 성공
	$('.modal').modal('hide');
	console.log(evt);
	commonAlert("알림", "인스턴스 수정이 완료되었습니다. 인스턴스가 동작중인 경우  다시 기동시 반영됩니다.", "수정성공");
	refreshInstanceList();
}
function insFuncSetSucessFunc(evt) { // SET 성공
	$('.modal').modal('hide');		
	console.log(evt);
	commonAlert("알림", "인스턴스기능 수정이 완료되었습니다. 인스턴스가 동작중인 경우 다시 기동시 반영됩니다.", "수정성공");
	refreshFunctionList();	
}
function insAttSetSucessFunc(evt) { // SET 성공
	$('.modal').modal('hide');
	console.log(evt);
	commonAlert("알림", "인스턴스속성 수정이 완료되었습니다. 인스턴스가 동작중인 경우 다시 기동시 반영됩니다.", "수정성공");
	refreshAttributeList();
}

function insInstanceDelSucessFunc(evt) { // DEL 성공
	console.log(evt);
	commonAlert("알림", "인스턴스 삭제가 완료되었습니다.", "삭제성공");
	refreshInstanceList();
}
function insFuncDelSucessFunc(evt) { // DEL 성공
	console.log(evt);
	commonAlert("알림", "인스턴스기능 삭제가 완료되었습니다.", "삭제성공");
	refreshFunctionList();
}
function insAttDelSucessFunc(evt) { // DEL 성공
	console.log(evt);
	commonAlert("알림", "인스턴스속성 삭제가 완료되었습니다.", "삭제성공");
	refreshAttributeList();
}


function devDevpoolPutSucessFunc(evt) { // PUT 성공
	$('.modal').modal('hide');
	console.log(evt);
	commonAlert("알림", "장치풀 등록이 완료되었습니다.", "입력성공");
	refreshDevicePoolList();
}
function devDevPutSucessFunc(evt) { // PUT 성공
	$('.modal').modal('hide');
	console.log(evt);
	commonAlert("알림", "장치 등록이 완료되었습니다.", "입력성공");
	
	if(selectedDevicePoolId)
		refreshDeviceList();
}

function devDevpoolSetSucessFunc(evt) { // PUT 성공
	$('.modal').modal('hide');
	console.log(evt);
	commonAlert("알림", "장치풀 수정이 완료되었습니다.", "수정성공");
	refreshDevicePoolList();
}
function devDevSetSucessFunc(evt) { // PUT 성공
	$('.modal').modal('hide');
	console.log(evt);
	commonAlert("알림", "장치 수정이 완료되었습니다.", "수정성공");
	refreshDeviceList();
}

function devDevpoolDelSucessFunc(evt) { // PUT 성공
	console.log(evt);
	commonAlert("알림", "장치풀 삭제가 완료되었습니다.", "삭제성공");
	refreshDevicePoolList();
}
function devDevDelSucessFunc(evt) { // PUT 성공
	console.log(evt);
	commonAlert("알림", "장치 삭제가 완료되었습니다.", "입력성공");
	refreshDeviceList();
}



//영진추가 끝

function cbPutFailFunc(evt) { // PUT 실패
	console.log(evt);
	commonErrorMessage(evt);
}
function cbSetSucessFunc(evt) { // SET 성공
	console.log(evt);
	/* alert("수정되었습니다."); */
	/*
	 * bootbox.alert("수정되었습니다.", function(){ console.log("Change Success!"); });
	 */
	commonAlert("알림", "수정되었습니다.", "수정성공");
}
function cbSetFailFunc(evt) { // SET 실패
	console.log(evt);
	commonErrorMessage(evt);
}
function cbDelSucessFunc(evt) { // DEL 성공
	console.log(evt);
	/* alert("삭제되었습니다."); */
	/*
	 * bootbox.alert("삭제 되었습니다.", function(){ console.log("Delete Success!");
	 * });
	 */
	commonAlert("알림", "삭제 되었습니다.", "삭제 성공");
}
function cbDelFailFunc(evt) { // DEL 실패
	console.log(evt);
	commonErrorMessage(evt);
	/*
	 * bootbox.alert("삭제를 실패했습니다.", function(){ console.log("Delete Fail!"); });
	 */
}

/** ***************************** API SCRIPTS END ***************************** */

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


/** ***************************** JSON PARSER START ***************************** */
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
		var description = value['adapter.description'];
		var hyperlink=value['adapter.hyperlink'];
		
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
			description:description,
			hyperlink:hyperlink
			
		});
	});

	return rowData;
}
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
	
	var processEvent = {
		"-1" : "none",
		0 : "Init",
		1 : "Request",
		2 : "Response",
		4 : "Event"
	};

	var processEventState = { 
		"-1" : "none",
		0 : "Init",
		1 : "진행중",//begin
		2 : "성공",//sucess
		4 : "실패",//fail
		8 : "에러",//error
		16 : "내부전송",//inbound_transfer
		32 : "외부전송"//outbound_transfer
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
			regDate : value['registration.date'],
			processEvent : processEvent[value['process.event']],
			processState : processEventState[value['process.state.event']]
		});
	});

	return rowData;
}

function parseAllInstanceJsonData(evt_data) {

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
	
	var processEvent = {
			"-1" : "none",
			0 : "init",
			1 : "request",
			2 : "response",
			4 : "event"
		};

		var processEventState = { 
			"-1" : "none",
			0 : "init",
			1 : "진행중",//begin
			2 : "성공",//sucess
			4 : "실패",//fail
			8 : "에러",//error
			16 : "내부전송",//inbound_transfer
			32 : "외부전송"//outbound_transfer
		};
	
	console.log(tmp_evt_json);

	$.each(tmp_evt_json, function(index, value) {
		$.each(value,function(index,value){
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
				regDate : value['registration.date'],
				processEvent : processEvent[value['process.event']],
				processState : processEventState[value['process.state.event']]
			});			
		});
	});

	return rowData;
}

function parseSessionJsonData(evt_data) {

	var data = evt_data.content;
	var tmp_evt_json = JSON.parse(data);
	var rowData = [];
	

	$.each(tmp_evt_json, function(index, value) {
		rowData.push({
			deviceId : value['device.id'],
			devicePoolId : value['device.pool.id'],
			deviceName : value['device.name'],
			isUse : value['is.use'],
			ip : value['ip'],
			port : value['port'],
			lat : value['latitude'],
			lon : value['longitude'],
			userId : value['user.id'],
			sessionId : value['session.id'],
			sessionStatus : value['session.status'],
			sessionTimeout : value['session.timeout'],
			remark : value['remark'],
			alterDate : value['alter.date'],
			regDate : value['registration.date']
		});

	});

	return rowData;
}

function parseFunctionJsonData(evt_data) {

	var data = evt_data.content;
	var tmp_evt_json = JSON.parse(data);
	console.log(tmp_evt_json);
	var rowData = [];

	$.each(tmp_evt_json, function(index, value) {

		var alterDate = value['alter.date'];
		var contentType = value['content.type'];
		var functionKey = value['function.key'];
		var functionDescription = value['function.description'];
		var instanceId = value['instance.id'];
		var param1 = value['param1'];
		var param2 = value['param2'];
		var param3 = value['param3'];
		var param4 = value['param4'];
		var param5 = value['param5'];
		var paramType1 = value['param.type1'];
		var paramType2 = value['param.type2'];
		var paramType3 = value['param.type3'];
		var paramType4 = value['param.type4'];
		var paramType5 = value['param.type5'];
		var registrationDate = value['registration.date'];
		var remark = value['remark'];

		rowData.push({
			key : functionKey,
			name : functionDescription,
			param1 : param1,
			paramTp1 : paramType1,
			param2 : param2,
			paramTp2 : paramType2,
			param3 : param3,
			paramTp3 : paramType3,
			param4 : param4,
			paramTp4 : paramType4,
			param5 : param5,
			paramTp5 : paramType5,
			type : contentType,
			remark : remark,
			altDate : alterDate,
			regDate : registrationDate,
			id : instanceId,
		});
	});

	return rowData;
}

function parseAttributeJsonData(evt_data) {

	var data = evt_data.content;
	var tmp_evt_json = JSON.parse(data);
	var rowData = [];
	console.log(tmp_evt_json);

	$.each(tmp_evt_json, function(index, value) {

		var alterDate = value['alter.date'];
		var attributionDescription = value['attribution.description'];
		var attributionKey = value['attribution.key'];
		var attributionValue = value['attribution.value'];
		var attributionValueType = value['attribution.value.type'];
		var instanceId = value['instance.id'];
		var registrationDate = value['registration.date'];
		var remark = value['remark'];

		rowData.push({
			key : attributionKey,
			description : attributionDescription,
			value : attributionValue,
			type : attributionValueType,
			remark : remark,
			altDate : alterDate,
			regDate : registrationDate,
			id : instanceId,
		});
	});

	return rowData;
}

function parseAdapterAttributeJsonData(evt_data) {

	var data = evt_data.content;
	var tmp_evt_json = JSON.parse(data);
	var rowData = [];

	// console.log("-------------속성-------------");

	$.each(tmp_evt_json, function(index, value) {

		var adapterAttribution = value['adapter.attribution'];
		var adapterId = value['adapter.id'];

		// console.log(adapterAttribution +" "+adapterId);

		rowData.push({
			att : adapterAttribution,
			id : adapterId,
		});

	});

	return rowData;
}

function parseAdapterFunctionJsonData(evt_data) {

	var data = evt_data.content;
	var tmp_evt_json = JSON.parse(data);
	var rowData = [];

	// console.log("-------------함수-------------");

	$.each(tmp_evt_json, function(index, value) {

		var adapterFunction = value['adapter.function'];
		var adapterId = value['adapter.id'];
		// console.log(adapterFunction +" "+adapterId);
		rowData.push({
			adaptFunction : adapterFunction,
			id : adapterId,
		});

	});

	return rowData;
}

function parseAttControlJsonData(evt_data) {

	var data = evt_data.content;
	var tmp_evt_json = JSON.parse(data);
	var rowData = [];
	console.log(tmp_evt_json);
	$.each(tmp_evt_json, function(index, value) {
		rowData.push({
			instanceId : value['instance.id'],
			attKey : value['attribution.key'],
			description : value['attribution.description'],
			attType : value['attribution.value.type'],
			attValue : value['attribution.value'],
			remark : value['remark'],
			alterDate : value['alter.date'],
			regDate : value['registration.date']
		});

	});
	return rowData;
} 

function parseFuncControlJsonData(evt_data) {

	var data = evt_data.content;
	var tmp_evt_json = JSON.parse(data);
	var rowData = [];
	console.log(tmp_evt_json);
	$.each(tmp_evt_json, function(index, value) {
		rowData.push({
			instanceId : value['instance.id'],
			contentsType : value['content.type'],
			functionKey : value['function.key'],
			param1 : value['param1'],
			param1Type : value['param.type1'],
			param2 : value['param2'],
			param2Type : value['param.type2'],
			param3 : value['param3'],
			param3Type : value['param.type3'],
			param4 : value['param4'],
			param4Type : value['param.type4'],
			param5 : value['param5'],
			param5Type : value['param.type5'],
			regDate : value['registration.date'],
			alterDate : value['alter.date'],
			remark : value['remark']
		});

	});
	return rowData;
}

function parseDevicePoolJsonData(evt_data) { // 장치풀

	var data = evt_data.content;
	var tmp_evt_json = JSON.parse(data);
	var rowData = [];

	console.log(tmp_evt_json);
	$.each(tmp_evt_json, function(index, value) {

		var alterDate = value['alter.date'];
		var devicePoolId = value['device.pool.id'];
		var deviecePoolName = value['device.pool.name'];
		var registrationDate = value['registration.date'];
		var remark = value['remark'];

		rowData.push({
			altDate : alterDate,
			id : devicePoolId,
			devPoolNm : deviecePoolName,
			regDate : registrationDate,
			remark : remark,
		});

	});

	return rowData;
}

function parseDeviceJsonData(evt_data) { // 장치

	var data = evt_data.content;
	var tmp_evt_json = JSON.parse(data);
	var rowData = [];
	console.log(tmp_evt_json);
	$.each(tmp_evt_json, function(index, value) {

		var alterDate = value['alter.date'];
		var deviceId = value['device.id'];
		var devieceName = value['device.name'];
		var devicePoolId = value['device.pool.id'];
		var ip = value['ip'];
		var isUse = value['is.use'];
		var latitude = value['latitude'];
		var longitude = value['longitude'];
		var port = value['port'];
		var registrationDate = value['registration.date'];
		var remark = value['remark'];
		var sessionTimeout = value['session.timeout'];

		rowData.push({
			id : deviceId,
			devPoolId : devicePoolId,
			devNm : devieceName,
			isUse : isUse,
			ip : ip,
			port : port,
			lat : latitude,
			lng : longitude,
			remark : remark,
			altDate : alterDate,
			regDate : registrationDate,
			sessTimeout : sessionTimeout,
		});

	});

	return rowData;
}

function parseUserPoolJsonData(evt_data) { // 유저풀

	var data = evt_data.content;
	var tmp_evt_json = JSON.parse(data);
	var rowData = [];

	$.each(tmp_evt_json, function(index, value) {

		var alterDate = value['alter.date'];
		var registrationDate = value['registration.date'];
		var remark = value['remark'];
		var userPoolId = value['user.pool.id'];
		var userPoolName = value['user.pool.name'];

		rowData.push({
			altDate : alterDate,
			id : userPoolId,
			usePoolNm : userPoolName,
			regDate : registrationDate,
			remark : remark,
		});

	});

	return rowData;
}

function parseUserJsonData(evt_data) { // 유저

	var data = evt_data.content;
	var tmp_evt_json = JSON.parse(data);
	var rowData = [];

	$.each(tmp_evt_json, function(index, value) {

		var id = value['user.id'];
		var userPoolId = value['user.pool.id'];
		var userName = value['user.name'];
		var userPassword = value['user.password'];
		var companyName = value['company.name'];
		var departmentName = value['department.name'];
		var jobTitle = value['job.title'];
		var userType = value['user.type'];
		var remark = value['remark'];
		var alterDate = value['alter.date'];
		var registrationDate = value['registration.date'];

		rowData.push({
			id : id,
			usePoolId : userPoolId,
			usePass : userPassword,
			useType : userType,
			useNm : userName,
			compNm : companyName,
			depNm : departmentName,
			jobTitle : jobTitle,
			altDate : alterDate,
			regDate : registrationDate,
			remark : remark,
		});
	});
	return rowData;
}

function parseUserFilterJsonData(evt_data) { // 유저필터

	var data = evt_data.content;
	var tmp_evt_json = JSON.parse(data);
	var rowData = [];

	$.each(tmp_evt_json, function(index, value) {

		var alterDate = value['alter.date'];
		var authorityFilter = value['authority.filter'];
		var id = value['user.id'];
		var registrationDate = value['registration.date'];
		var remark = value['remark'];

		rowData.push({
			id : id,
			authFilt : authorityFilter,
			altDate : alterDate,
			regDate : registrationDate,
			remark : remark,
		});

	});

	return rowData;
}

function parseDomainJsonData(evt_data) { // 도메인

	var data = evt_data.content;
	var tmp_evt_json = JSON.parse(data);
	var rowData = [];
	console.log(tmp_evt_json);

	$.each(tmp_evt_json, function(index, value) {

		var id = value['domain.id'];
		var domainName = value['domain.name'];
		var domainType = value['domain.type'];
		var registrationDate = value['registration.date'];
		var alterDate = value['alter.date'];
		var remark = value['remark'];

		rowData.push({
			id : id,
			domNm : domainName,
			domTp : domainType,
			regDate : registrationDate,
			altDate : alterDate,
			remark : remark,
		});
	});

	return rowData;
}

/**
 * 서버 응답 Json데이터로 이벤트로그 그리드 row를 생성
 * 
 * @param evt_data
 * @returns {Array}
 */
function parseEventLogJsonData(evt_data) {
	var data = evt_data.content;
	var tmp_evt_json = JSON.parse(data);
	var rowData = [];
	
	rowData.push({
		sessionId : tmp_evt_json['session.id'],
		transmission : tmp_evt_json['transmission'],
		instanceId : tmp_evt_json['instance.id'],
		instanceName : tmp_evt_json['instance.name'],
		targetId : tmp_evt_json['target.id'],
		targetName : tmp_evt_json['device.name'],
		controlPath : tmp_evt_json['attribution.key'],
		controlDesc : tmp_evt_json['attribution.description'],
		commandKey : tmp_evt_json['command.key'],
		commandValue : tmp_evt_json['command.value'],
		contentType : tmp_evt_json['content.type'],
		content : tmp_evt_json['content'],
		eventId : tmp_evt_json['event.id'],
		eventTime : tmp_evt_json['event.time']
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

/****************JSON PARSER END ************************/
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
/***********************BOOTBOX.js POPUP DIALOG START****************************/
function commonErrorMessage(evt_data,comp) { // 에러 메세지
	
	console.log(comp);
	
	var errorMsg = evt_data.msg;
	var errorCode = evt_data.msgCode;
	var errorType = evt_data.msgType;

	/*
	 * bootbox.alert(errorCode+"\n "+errorMsg, function(){ });
	 */
	if(errorType==="info" && comp==="blankInsAtt"){//인스턴스
		$(".blankInsAtt").text("데이터가 존재하지 않습니다.");
	}else if(errorType==="info" && comp==="blankInsFunc"){
		$(".blankInsFunc").text("데이터가 존재하지 않습니다.");
	}else if(errorType==="info" && comp==="blankInsInstance"){
		$(".blankInsInstance").text("데이터가 존재하지 않습니다.");
	}else if(errorType==="info" && comp==="blankSession"){//모니터링
		$(".blankSession").text(errorMsg);
	}else if(errorType==="info" && comp==="blankAdapter"){
		$(".blankAdapter").text("데이터가 존재하지 않습니다.");
	}else if(errorType==="info" && comp==="blankInstance"){
		$(".blankInstance").text("데이터가 존재하지 않습니다.");	
	}else if(errorType==="info" && comp==="blankAdtAdapter"){//아답터
		$(".blankAdtAdapter").text("데이터가 존재하지 않습니다.");
	}else if(errorType==="info" && comp==="blankAdtFunc"){
		$(".blankAdtFunc").text("데이터가 존재하지 않습니다.");
	}else if(errorType==="info" && comp==="blankAdtAtt"){
		$(".blankAdtAtt").text("데이터가 존재하지 않습니다.");
	}else if(errorType==="info" && comp==="blankDevDevicepool"){//장치
		$(".blankDevDevicepool").text("데이터가 존재하지 않습니다.");
	}else if(errorType==="info" && comp==="blankDevDevice"){//장치
		$(".blankDevDevice").text("데이터가 존재하지 않습니다.");
	}else if(errorType==="info" && comp==="blankUserUserpool"){//유저
		$(".blankUserUserpool").text("데이터가 존재하지 않습니다.");
	}else if(errorType==="info" && comp==="blankUserUser"){
		$(".blankUserUser").text("데이터가 존재하지 않습니다.");
	}else if(errorType==="info" && comp==="blankUserFilter"){
		$(".blankUserFilter").text("데이터가 존재하지 않습니다.");
	}else if(errorType==="info" && comp==="blankInsViewAtt"){
		$(".blankInsViewAtt").text("데이터가 존재하지 않습니다.");
	}else if(errorType==="info" && comp==="blankInsViewFunc"){
		$(".blankInsViewFunc").text("데이터가 존재하지 않습니다.");
	}else if(errorType==="info" && comp==="blankInsViewSession"){
		$(".blankInsViewSession").text("데이터가 존재하지 않습니다.");
	}else if(errorType==="info" && comp==="blankInsViewControl"){
		$(".blankInsViewControl").text("데이터가 존재하지 않습니다.");
	}else if(errorType==="info" && comp==="blankDeviceViewControl"){
		$(".blankDeviceViewControl").text("데이터가 존재하지 않습니다.");
	}else if(errorType=="error" && errorCode=="101"){
		var sessionTimeoutEvent=function(){
			location.href="index.html";
		};
		
		customConfirm("에러", errorMsg,sessionTimeoutEvent);		
		$(".bootbox-close-button").click(sessionTimeoutEvent);
				
	}
	else{
		commonAlert("에러",errorMsg, errorCode + "\n " + errorMsg);
	}
}

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
//   var login_ip= document.location.href.split("//")[1].split(":")[0];
//   var loginInfo=sessionStorage.getItem("authObj");
//   
//   console.log(loginInfo);
   
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
   $("#smartIotId").text("제품ID : GS인증 완료버젼");
   
}

function navIconLogout(){
	commonConfirm("알림","로그아웃 하시겠습니까?",function(){  
		otpAuth.logout();
		location.href="index.html";

		sessionStorage.clear();
	});
}

function setAttControlUpdateData() {//제어목록 업데이트(콤보인지/텍스트인지/라벨인지)
	
	updateValue=null;		
	
	bootbox.dialog({
		title:"속성 제어",
		message :
		 '<div class="col-xs-12 form-group">'+
			 '<div class="col-xs-3"><label class="modalLabel">장치ID</label></div>'+
			 '<div class="col-xs-9"><span class="textStyle" id="sessionInstanceId"/></div>'+
		 '</div>'+
		 '<div class="col-xs-12 form-group">'+
			 '<div class="col-xs-3"><label class="modalLabel">속성키</label></div>'+
			 '<div class="col-xs-9"><span class="textStyle" id="sessionKey"/></div>'+
		 '</div>'+
		 '<div class="col-xs-12 form-group">'+
			 '<div class="col-xs-3"><label class="modalLabel">속성명 </label></div>'+
			 '<div class="col-xs-9"><span class="textStyle" id="sessionAttName"/></div>'+
		 '</div>'+
		 '<div class="col-xs-12 form-group">'+
		     '<div class="col-xs-3 sessionValueName"><label class="modalLabel">속성값 </label></div>'+
		     '<div class="btn-group col-xs-4 sessionType1">'+
				  '<button class="btn dropdown-toggle col-xs-8 sessionControlValue1" data-toggle="dropdown" href="#">기동상태'+
				    '<span class="caret"></span>'+
				  '</button>'+
				  '<ul class="dropdown-menu">'+
				   '<li><a href="#">on</a></li>'+
				   '<li><a href="#">off</a></li>'+
				  '</ul>'+
			 '</div>'+
			 '<div class="col-xs-6 sessionType2"><label id="sessionControlValue2" class="modalLabel" style="width: 100%;overflow: hidden;text-overflow: ellipsis;white-space: nowrap;"/></div>'+
			 '<div class="col-xs-6 sessionType3">'+
			 	'<input id="sessionControlValue3" type="text" placeholder="값을 입력해주세요" class="form-control">'+
			 '</div>'+
			 '<div class="col-xs-6 sessionType4">'+
			 	'<input id="sessionControlValue4" data-slider-id="sessionControlValue4Slider" type="text" data-slider-min="0" data-slider-max="100" data-slider-step="1" data-slider-value="50"/>'+
			 '</div>'+
			 /*윤영진수정시작*/
			 '<div class="col-xs-5 sessionType5"><label id="sessionControlValue5" class="modalLabel"/></div>'+
			 '<div class="col-xs-2"><input type="button" class="btn linkBtn" onclick="pageReplace()" value = "바로가기"></input></div>' + 
			 /*아마도 수정이 끝날곳*/
			 
			 '<div class="col-xs-2"><input type="button" class="btn updateControlBtn" onclick="controlerAttUpdateBtn()" value="업데이트"></input></div>'+
			 '<div class="col-xs-1"><a href="#" id="controlerValueRepreshBtn" class="controlerValueBtn" title="새로고침"><span class="glyphicon glyphicon-refresh" aria-hidden="true"></span></a></div>'+
		 '</div>'+
		 '<div class="col-xs-12 form-group">'+
			 '<div class="col-xs-3"><label class="modalLabel">이벤트</label></div>'+
			 '<div class="col-xs-9"><span class="textStyle" id="currentControlEventView">이벤트가 표시되는 곳입니다.</span></div>'+
		 '</div>'+
		 '<div id="output" class="col-xs-12 form-group"></div>'+
		 '<div class="frame_inner_search"></div>', 
		buttons:{
			cancle:{
				className:"modalDefaultBtn cancleBtn",
				label:"닫기",
				callback : function(){
					attControlerFlag=true;
				}
			}	
		}
	});	
	
	$(".close").click(function(){	
		console.log("?");
		attControlerFlag=true;
	});
					
	$("#sessionInstanceId").html(selectedSessionId);
	$("#sessionKey").html(selectedControlKey.split('?')[0]);
	$("#sessionAttName").html(selectedControlDescruption);
			
	$("#controlerValueRepreshBtn").click(function(){
		setControlerValuePopup(1);
	});
	
	$(".dropdown-menu li a").click(function(){
		updateValue = $(this).text();
		
		 $(".sessionControlValue1").parents('.btn-group').find('.dropdown-toggle').html(updateValue+'<span class="caret"></span>');
	});		
	
	$('#sessionControlValue4').slider({
		formatter: function(value) {
			return value;
		}
	});
	if (selectedControlType == "smartiot.onoff") {
		$(".sessionType1").show();
		$(".sessionType2").hide();
		$(".sessionType3").hide();
		$(".sessionType4").hide();
		$(".sessionType5").hide();
		$(".updateControlBtn").show();
		$(".linkBtn").hide();
		if(selectedControlValue=="on"||selectedControlValue=="off"){
			$(".sessionControlValue1").parents('.btn-group').find('.dropdown-toggle').html(selectedControlValue+'<span class="caret"></span>');
		}
		else {
			$(".sessionControlValue1").parents('.btn-group').find('.dropdown-toggle').html('off<span class="caret"></span>');
			$("#currentControlEventView").text("유효하지 않은 값이 전달되었습니다.");
		}
		updateValue = selectedControlValue;
		
	}else if(selectedControlType == "smartiot.readonly"){
		$(".sessionType1").hide();
		$(".sessionType2").show();
		$(".sessionType3").hide();
		$(".sessionType4").hide();
		$(".sessionType5").hide();
		$(".updateControlBtn").hide();
		$(".linkBtn").hide();
		$("#sessionControlValue2").text(selectedControlValue);
		updateValue=selectedControlValue;
	}else if(selectedControlType == "smartiot.text"){
		$(".sessionType1").hide();
		$(".sessionType2").hide();
		$(".sessionType3").show();
		$(".updateControlBtn").show();
		$(".sessionType4").hide();
		$(".sessionType5").hide();
		$(".linkBtn").hide();
		$("#sessionControlValue3").val(selectedControlValue);
	}else if(selectedControlType == "smartiot.percent"){
		$(".sessionType1").hide();
		$(".sessionType2").hide();
		$(".sessionType3").hide();
		$(".sessionType4").show();
		$(".sessionType5").hide();
		$(".linkBtn").hide();
		$(".updateControlBtn").show();	
		$("#sessionControlValue4").slider("setValue",parseInt(selectedControlValue));
	}else if(selectedControlType == "smartiot.link"){
		$(".sessionType1").hide();
		$(".sessionType2").hide();
		$(".sessionType3").hide();
		$(".sessionType4").hide();
		$(".sessionType5").show();
		$(".linkBtn").show();
		$(".updateControlBtn").hide();
		$("#sessionControlValue5").text(selectedControlValue);
		updateValue=selectedControlValue;
	}else{
		$(".sessionType1").hide();
		$(".sessionType2").hide();
		$(".sessionType3").show();
		$(".updateControlBtn").show();
		$(".sessionType4").hide();
		$(".sessionType5").hide();
		$(".linkBtn").hide();
		$("#sessionControlValue3").val(selectedControlValue);
	}
}

setAttControlUpdateData.prototype.qrPopUp=function(){

	$('.updateControlBtn').val('QR생성');
	$('#controlerValueRepreshBtn').hide();
	
	$('.modal-title').text('QR생성기');
	
	$('.updateControlBtn').removeAttr('controlerAttUpdateBtn');
	$('.updateControlBtn').attr('onclick','qrGenerator()');
	
}

function setFuncControlUpdateData() {//제어목록 업데이트(콤보인지/텍스트인지/라벨인지)
	
	bootbox.dialog({
		title:"기능 제어 요청",
		message :
		 '<div class="col-xs-12 form-group">'+
			 '<div class="col-xs-3"><label class="modalLabel">장치ID</label></div>'+
			 '<div class="col-xs-9"><span class="textStyle" id="funcControlDeviceID"/></div>'+
		 '</div>'+
		 '<div class="col-xs-12 form-group">'+
			 '<div class="col-xs-3"><label class="modalLabel ">기능키</label></div>'+
			 '<div class="col-xs-9"><span class="textStyle" id="funcControlKey"/></div>'+
		 '</div>'+
		 '<div class="col-xs-12 form-group">'+
			 '<div class="col-xs-3"><label class="modalLabel">컨텐츠타입</label></div>'+
			 '<div class="col-xs-9"><span class="textStyle" id="funcControlContentsType"/></div>'+
		 '</div>'+
		 '<div id="funcControlParamField1" class="col-xs-12 form-group">'+
			 '<div class="col-xs-3"><label id="funcControlParam1" class="modalLabel">파라미터1</label></div>'+
			 '<div class="col-xs-6"><input id="funcControlParamValue1" type="text" placeholder="값을 입력해주세요" class="form-control"></div>'+
			 '<div class="col-xs-3"><label id="funcControlParamType1" class="textStyle">smartiot.text</label></div>'+
		 '</div>'+
		 '<div id="funcControlParamField2" class="col-xs-12 form-group">'+
			 '<div class="col-xs-3"><label id="funcControlParam2" class="modalLabel">파라미터2</label></div>'+
			 '<div class="col-xs-6"><input id="funcControlParamValue2" type="text" placeholder="값을 입력해주세요" class="form-control"></div>'+
			 '<div class="col-xs-3"><label id="funcControlParamType2" class="textStyle">smartiot.text</label></div>'+
		 '</div>'+
		 '<div id="funcControlParamField3" class="col-xs-12 form-group">'+
			 '<div class="col-xs-3"><label id="funcControlParam3" class="modalLabel">파라미터3</label></div>'+
			 '<div class="col-xs-6"><input id="funcControlParamValue3" type="text" placeholder="값을 입력해주세요" class="form-control"></div>'+
			 '<div class="col-xs-3"><label id="funcControlParamType3" class="textStyle">smartiot.text</label></div>'+
		 '</div>'+
		 '<div id="funcControlParamField4" class="col-xs-12 form-group">'+
			 '<div class="col-xs-3"><label id="funcControlParam4" class="modalLabel">파라미터4</label></div>'+
			 '<div class="col-xs-6"><input id="funcControlParamValue4" type="text" placeholder="값을 입력해주세요" class="form-control"></div>'+
			 '<div class="col-xs-3"><label id="funcControlParamType4" class="textStyle">smartiot.text</label></div>'+
		 '</div>'+
		 '<div id="funcControlParamField5" class="col-xs-12 form-group">'+
			 '<div class="col-xs-3"><label id="funcControlParam5" class="modalLabel">파라미터5</label></div>'+
			 '<div class="col-xs-6"><input id="funcControlParamValue5" type="text" placeholder="값을 입력해주세요" class="form-control"></div>'+
			 '<div class="col-xs-3"><label id="funcControlParamType5" class="textStyle">smartiot.text</label></div>'+
		 '</div>'+
		 '<div id="funcControlJsonContentsField" class="col-xs-12 form-group">'+
			 '<div class="col-xs-12"><label class="modalLabel">JSON CONTENTS</label></div>'+
			 '<div class="col-xs-12" style="padding-left: 30px"><textarea id="funcControlJsonContents" type="text" placeholder="값을 입력해주세요" class="form-control" rows=5></textarea></div>'+
		 '</div>'+
		 '<div>'+
			 '<div class="col-xs-12 form-group">'+
			 '<div class="col-xs-3"><label class="modalLabel">이벤트</label></div>'+
			 '<div class="col-xs-7"><span class="textStyle" id="funcControlEventView">이벤트가 표시되는 곳입니다.</span></div>'+
			 '<div class="col-xs-2"><input type="button" class="btn updateControlBtn" onclick="controlerFuncUpdateBtn()" value="호출"></input></div>'+
		 '</div>',		 
		buttons:{
			cancle:{
				className:"modalDefaultBtn",
				label:"닫기",
				callback : function(){
					funcControlerFlag=true;
				}
			}
		}
	});	
	
	var funcControlCommand = funcControlScope.gridOptions.api.selectionController.selectedRows[0];
	var funcControlContentsType = funcControlCommand.contentsType;

	///////////////////////////////////////
	/////////////////init//////////////////
	///////////////////////////////////////
	$("#funcControlDeviceID").html(selectedSessionId);
	$("#funcControlKey").html(funcControlCommand.functionKey);
	$("#funcControlContentsType").html(funcControlContentsType);

	$("#funcControlParamField1").hide();
	$("#funcControlParamField2").hide();
	$("#funcControlParamField3").hide();
	$("#funcControlParamField4").hide();
	$("#funcControlParamField5").hide();
	$("#funcControlJsonContentsField").hide();
	
	if(funcControlCommand.param1!=""){
		$("#funcControlParam1").text(funcControlCommand.param1);
		$("#funcControlParamType1").text(funcControlCommand.param1Type);
		$("#funcControlParamField1").show();
	}if(funcControlCommand.param2!=""){
		$("#funcControlParam2").text(funcControlCommand.param2);
		$("#funcControlParamType2").text(funcControlCommand.param2Type);
		$("#funcControlParamField2").show();
	}if(funcControlCommand.param3!=""){
		$("#funcControlParam3").text(funcControlCommand.param3);
		$("#funcControlParamType3").text(funcControlCommand.param3Type);
		$("#funcControlParamField3").show();
	}if(funcControlCommand.param4!=""){
		$("#funcControlParam4").text(funcControlCommand.param4);
		$("#funcControlParamType4").text(funcControlCommand.param4Type);
		$("#funcControlParamField4").show();
	}if(funcControlCommand.param5!=""){
		$("#funcControlParam5").text(funcControlCommand.param5);
		$("#funcControlParamType5").text(funcControlCommand.param5Type);
		$("#funcControlParamField5").show();
	}
	
	if(funcControlContentsType.split('-')[0]=='json'){
		$("#funcControlJsonContentsField").show();	
	}

	console.log(funcControlCommand);
}

/***********************BOOTBOX.js POPUP DIALOG END****************************/



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

/***************************** AddItem ValidateCheck Start******************************/

function ipValidateCheck(ip){//ip
	
	var pattern = /[ㄱ-ㅎ가-힣]/g;
	
	if(pattern.test(ip)){
		commonAlert("알림","아이피가 유효하지 않습니다.","아이피가 유효하지 않음");
		return true;
	}
	
	pattern = /^((0|1[0-9]{0,2}|2[0-9]?|2[0-4][0-9]|25[0-5]|[3-9][0-9]?)\.){3}(0|1[0-9]{0,2}|2[0-9]?|2[0-4][0-9]|25[0-5]|[3-9][0-9]?)/;
	
	if(!pattern.test(ip)){		
		if(ip=="")	return false;
		else{
			commonAlert("알림","아이피가 유효하지 않습니다.","아이피가 유효하지 않음");
			return true;
		
		}
		
	}
}
function portValidateCheck(port){ //포트
		
	var pattern =  /^[0-9]{0,5}$/ ;  
	
	if (!pattern.test(port)) {
		commonAlert("알림","포트번호가 유효하지 않습니다.","포트번호가 유효하지 않음");
	    return true;
	}else{
		return false;
	}
}

function sessionValidateCheck(session){ //세션
		
	var pattern =  /^[0-9]{0,19}$/ ;  
	
	if (!pattern.test(session)) {
		commonAlert("알림","세션타임아웃 시간이 유효하지 않습니다.","세션타임아웃 시간이 유효하지 않음");
	    return true;
	}else{		
		return false;
	}
}

function longitudeValidateCheck(lng){//경도 +180.000000(East)동경 180도 ~ -180.000000(West)

	if(lng==""){
		return false;
	}
	
	var pattern =  /^[0-9][.0-9+]*$/ ;  
	 
	if (!pattern.test(lng)) {
		commonAlert("알림","경도가 유효하지 않습니다.","경도가 유효하지 않음");
	    return true;
	}	
	  
	var lngN=parseFloat(lng)
	
	if(lngN<=180&&lngN>=-180){
		return false;
	}else{
		if(isNaN(lngN))
			return false;
		else{
			commonAlert("알림","경도가 유효하지 않습니다.","경도가 유효하지 않음");
			return true;
		}
	}
}

function latitudeValidateCheck(lat){//위도  +90.000000(North)북위 90도 ~ -90.000000(South)

	if(lat==""){
		return false;
	}
	
	var pattern =  /^[0-9][.0-9+]*$/ ;  
	 
	
	if (!pattern.test(lat)) {
		commonAlert("알림","위도가 유효하지 않습니다.","위도가 유효하지 않음");
	    return true;
	}	

	var latN=parseFloat(lat)
	  
	if(latN<=90&&latN>=-90){
		return false;
	}else{
		if(isNaN(latN))
			return false;
		else{
			commonAlert("알림","위도가 유효하지 않습니다.","위도가 유효하지 않음");
			return true;
		}
	}
}

function html5SpecialCharCode(str){
	if(str!=null){
		str=str.replace(/ /gi,"&nbsp;");
		str=str.replace(/!/gi,"&nbsp;");
		str=str.replace(/</gi,"&lt;");
		str=str.replace(/>/gi,"&gt;");
		str=str.replace(/"/gi,"&quot;");
		
		return str;	
	}
}

function html5SpecialCharDeCode(str){
	if(str!=null){
		str=str.replace(/&nbsp;/gi," ");
		str=str.replace(/&nbsp;/gi,"!");
		str=str.replace(/&lt;/gi,"<");
		str=str.replace(/&gt;/gi,">");
		str=str.replace(/&quot;/gi,"\"");
		
		return str;	
	}
}

function uniqueIDValidateCheck(obj,target){

	var pattern =  /^[.A-Za-z0-9+]*$/ ;  
	 
	if (!pattern.test(obj)) {
		commonAlert("알림",target+"영문자와 숫자 특수문자(.) 만을 입력하세요","영문자와 숫자 특수문자(.)만을 입력하세요");
	    return true;
	  }
	  return false;	
}

function uniqueKeyValidateCheck(obj,target){
	
	var pattern =  /^[/A-Za-z0-9+]*$/ ;
	
	if (!pattern.test(obj)) {
		commonAlert("알림",target+"영문자와 숫자 특수문자(/) 만을 입력하세요","영문자와 숫자 특수문자(/)만을 입력하세요");
	    return true;
	  }
	  return false;	
}

function filterValidateCheck(obj){
	
	var pattern =  /^[/.*A-Za-z0-9+]*$/ ;
	
	if (!pattern.test(obj)) {
		commonAlert("알림","권한필터는 영문자와 숫자 특수문자(/, ., *) 만을 입력하세요","영문자와 숫자 특수문자(/, ., *)만을 입력하세요");
	    return true;
	  }
	  return false;	
}

/***************************** AddItem ValidateCheck END******************************/

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


/*************Start Stop Suspend Btn Activity NonActivity Start****************/

function setBtnState(state,event){	
	
//	console.log(state +"    "+event);
	
	if(state==undefined&&event==undefined){
		$("#stopActivityBtn").hide();
		$("#stopNonActivityBtn").show();
		$("#suspendActivityBtn").hide();
		$("#suspendNonActivityBtn").show();
		$("#startActivityBtn").hide();
		$("#startNonActivityBtn").show();	
	}else{
		$("#stopActivityBtn").show();
		$("#stopNonActivityBtn").hide();
		$("#suspendActivityBtn").show();
		$("#suspendNonActivityBtn").hide();
		$("#startActivityBtn").show();
		$("#startNonActivityBtn").hide();
		
		if((state=="완료")&&event=="기동"){
			$("#startActivityBtn").hide();
			$("#startNonActivityBtn").show();
		}else if((state=="완료")&&event=="일시정지"){
			$("#suspendActivityBtn").hide();
			$("#suspendNonActivityBtn").show();
		}else if((state=="완료")&&event=="없음"){
			$("#stopActivityBtn").hide();
			$("#stopNonActivityBtn").show();
		}else if((state=="없음")&&event=="없음"){
			$("#stopActivityBtn").hide();
			$("#stopNonActivityBtn").show();
			$("#suspendActivityBtn").hide();
			$("#suspendNonActivityBtn").show();
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


/* Load Datas */
function loadData(scope, rowDataByJson) { // Load datas to grid
scope.gridOptions.api.setRowData(rowDataByJson);
}

/* Timestamp to date(yyyymmddhhmmss) */
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

var viewSynchronize=function(scopeArgs1, scopeArgs2, scopeArgs3, scopeArgs4, scopeArgs5){
	
	if((scopeArgs1!=null||typeof scopeArgs1=='undefined')&&(scopeArgs2!=null || typeof scopeArgs2=='undefined')&&(scopeArgs3!=null||typeof scopeArgs3=='undefined')&&(scopeArgs4!=null||typeof scopeArgs4=='undefined')&&(scopeArgs5!=null||typeof scopeArgs5=='undefined')){
	
		initAPI();
		sessionEndMsgInit();
		clearInterval(scopeSynchronizeIntervalId);
	}	
}



