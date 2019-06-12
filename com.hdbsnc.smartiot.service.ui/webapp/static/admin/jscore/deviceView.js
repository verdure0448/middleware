var attControlerFlag=true;
var funcControlerFlag=true;
var updateValue=null;		
		
$(document).ready(function() {

	$('#MySplitter').width(1200).height(850).split({orientation:'vertical', limit:300, position:'40%'});
	$('#sWrap').split({orientation:'horizontal', limit:0});
	

	$("#addrIP").text("Smart IoT 2.0 관리자화면 - "+location.host);
	
	$("#stopActivityBtn").hide();
	$("#startNonActivityBtn").hide();
	$('#funcControlPanel').hide();
});


function checkSid() {
	if (selectedSessionId == null) {
		commonAlert("알림", "연결된 장치목록을 선택 해 주세요.", "연결된 장치목록을 선택 해 주세요.");
		return false;
	} else {
		sessionDisconnect(selectedSessionId);
	}
}

function qrGeneratorFunction(){
//	$("#deviceId").html(sessionScope.gridOptions.rowData[0].deviceId);
	if (selectedSessionId == null) {
		commonAlert("알림", "연결된 장치목록을을 선택 해 주세요.", "연결된 장치목록을 선택 해 주세요.");
		return false;
	}else if (selectedControlKey == null || selectedControlValue == null) {
		commonAlert("알림", "속성제어목록을 선택 해 주세요.", "속성제어목록을 선택 해 주세요.");
		return false;
	} else {
		
		new setAttControlUpdateData().qrPopUp();
	}
}

function qrGenerator(){
	$('#output').html('');
	
	if(selectedControlType == "smartiot.percent"){
		updateValue=$("#sessionControlValue4").val();
	}else if(selectedControlType == "smartiot.onoff"){
		updateValue=$(".sessionControlValue1").text();
	}else{
		updateValue=$("#sessionControlValue3").val();
	}
	
	if(selectedControlType=='smartiot.readonly'){
		//read
		$('#output').append("<p class='col-xs-3'><label class='modalLabel'>생성기</label></p>");
		$('#output').append("<div id='qrGenerator' class='col-xs-9'></div>");
		$('#qrGenerator').qrcode(selectedControlKey+"?update="+updateValue);
		commonAlert('알림','QR : '+selectedControlKey+" 생성 완료");
	}else{
		//update
		$('#output').append("<p class='col-xs-3'><label class='modalLabel'>생성기</label></p>");
		$('#output').append("<div id='qrGenerator' class='col-xs-9'></div>");
		$('#qrGenerator').qrcode(selectedControlKey+"?update="+updateValue);
		commonAlert('알림','QR : '+selectedControlKey+"?update="+updateValue+" 생성 완료");
	}
}

function attRadioCheck(num){
	
	if(num==1){
		$('#attValueWrapper').hide();
	}else{
		$('#attValueWrapper').show();
	}
}

function attControlBtn() { //호출해서 변경하는곳
		
	if (selectedSessionId == null) {
		commonAlert("알림", "연결된 장치목록을을 선택 해 주세요.", "연결된 장치목록을 선택 해 주세요.");
		return false;
	}else if (selectedControlKey == null || selectedControlValue == null) {
		commonAlert("알림", "속성제어목록을 선택 해 주세요.", "속성제어목록을 선택 해 주세요.");
		return false;
	} else {
		if(attControlerFlag){//attControlerFlag는 유저가 더블클릭 했을 경우를 대비하기 위함
			setControlerValuePopup(0);
		}
		
	}
}

function funcControlBtn() { //호출해서 변경하는곳
	
	if (selectedSessionId == null) {
		commonAlert("알림", "연결된 장치목록을을 선택 해 주세요.", "연결된 장치목록을 선택 해 주세요.");
		return false;
	}else if (selectedControlKey == null || selectedControlValue == null) {
		commonAlert("알림", "기능제어목록을 선택 해 주세요.", "기능제어목록을 선택 해 주세요.");
		return false;
	} else {
		if(funcControlerFlag){
			setFuncControlUpdateData();
		}
	}
}

function pageReplace(evt){
	location.replace($("#sessionControlValue5").text());
}

function controlerAttUpdateBtn(){ //팝업의 변경버튼을 누르면 업데이트를 시키는 함수
	if(selectedControlType == "smartiot.percent"){
		updateValue=$("#sessionControlValue4").val();
		otpCtrl.update(selectedSessionId, selectedControlKey.split('?')[0], updateValue, cbControlUpdateSucessFunc, cbControlUpdateFailFunc);
	}else if(selectedControlType == "smartiot.onoff"){
		updateValue=$(".sessionControlValue1").text();
		otpCtrl.update(selectedSessionId, selectedControlKey.split('?')[0], updateValue, cbControlUpdateSucessFunc, cbControlUpdateFailFunc);
	}else{
		updateValue=$("#sessionControlValue3").val();
		otpCtrl.update(selectedSessionId, selectedControlKey.split('?')[0], updateValue, cbControlUpdateSucessFunc, cbControlUpdateFailFunc);
	}
}

function controlerFuncUpdateBtn(){
	var funcControlCommand = funcControlScope.gridOptions.api.selectionController.selectedRows[0];

	$("#funcControlEventView").text("장치의 기능을 호출중 입니다.");
	if(funcControlCommand.param1==""){
		otpCtrl.funcNonParam(selectedSessionId,$("#funcControlKey").html(), $("#funcControlJsonContents").val() ,cbFuncControlSucessFunc,cbFuncControlFailFunc);
	}else if(funcControlCommand.param2==""){
		otpCtrl.funcOneParam(selectedSessionId,$("#funcControlKey").html(),$("#funcControlParam1").html(),$("#funcControlParamValue1").val(), $("#funcControlJsonContents").val() ,cbFuncControlSucessFunc,cbFuncControlFailFunc);
	}else if(funcControlCommand.param3==""){
		otpCtrl.funcTwoParams(selectedSessionId,$("#funcControlKey").html(),$("#funcControlParam1").html(),$("#funcControlParamValue1").val(),$("#funcControlParam2").html(), $("#funcControlParamValue2").val(),$("#funcControlJsonContents").val() ,cbFuncControlSucessFunc,cbFuncControlFailFunc);
	}else if(funcControlCommand.param4==""){ 
		otpCtrl.funcThreeParams(selectedSessionId,$("#funcControlKey").html(),$("#funcControlParam1").html(),$("#funcControlParamValue1").val(),$("#funcControlParam2").html(), $("#funcControlParamValue2").val(),$("#funcControlParam3").html(),$("#funcControlParamValue3").val(),$("#funcControlJsonContents").val() ,cbFuncControlSucessFunc,cbFuncControlFailFunc);
	}else if(funcControlCommand.param5==""){
		otpCtrl.funcFourParams(selectedSessionId,$("#funcControlKey").html(),$("#funcControlParam1").html(),$("#funcControlParamValue1").val(),$("#funcControlParam2").html(), $("#funcControlParamValue2").val(),$("#funcControlParam3").html(),$("#funcControlParamValue3").val(),$("#funcControlParam4").html(),$("#funcControlParamValue4").val() ,$("#funcControlJsonContents").val() ,cbFuncControlSucessFunc,cbFuncControlFailFunc);
	}else{
		otpCtrl.funcFiveParams(selectedSessionId,$("#funcControlKey").html(),$("#funcControlParam1").html(),$("#funcControlParamValue1").val(),$("#funcControlParam2").html(), $("#funcControlParamValue2").val(),$("#funcControlParam3").html(),$("#funcControlParamValue3").val(),$("#funcControlParam4").html(),$("#funcControlParamValue4").val() ,$("#funcControlParam5").html(), $("#funcControlParamValue5").val(),$("#funcControlJsonContents").val() ,cbFuncControlSucessFunc,cbFuncControlFailFunc);
	}
}

function cbControlUpdateSucessFunc(evt){// 제어 업데이트 성공
	console.log("제어업데이트 성공");
	$("#currentControlEventView").text("장치를 업데이트 했습니다.");
}

function cbControlUpdateFailFunc(evt){// 제어 업데이트 실패
	commonAlert("에러",evt.msg,evt.code+"\n"+evt.msg);
//	clearControlData();
//	setControlData(selectedSessionId);
}
function cbAttControlSucessFuncAndGrid(evt){//팝업 업데이트 조회 성공  0
	console.log("팝업 제어콘트롤러 조회 성공(속성)");
	selectedControlValue=evt.params[0].value;
	console.log(evt.params);
	
//	console.log(evt.params[0].value);
	setAttControlUpdateData();//팝업 그리는 곳
	attControlerFlag=false;
}

function cbAttControlFailFunc(evt){
	console.log("팝업 제어콘트롤러 조회 실패(속성)");
	commonErrorMessage(evt)	
}	

function cbFuncControlSucessFunc(evt){//팝업 업데이트 성공  0
	console.log("팝업 제어콘트롤러 제어 성공(기능)");
	try {
		if(evt.contentType==""){
			evt.contentType='none';
		}
		
		bootbox.dialog({
			title:"기능 제어 응답",
			message :
			 '<div class="col-xs-12 form-group">'+
				 '<div class="col-xs-3"><label class="modalLabel">경로</label></div>'+
				 '<div class="col-xs-9"><span class="textStyle">'+$("#funcControlKey").html()+'</span></div>'+
			 '</div>'+
			 '<div class="col-xs-12 form-group">'+
				 '<div class="col-xs-3"><label class="modalLabel">컨텐츠타입</label></div>'+
				 '<div class="col-xs-9"><span class="textStyle">'+evt.contentType+'</span></div>'+
			 '</div>'+					
			 '<div id="responseParams"></div>'+
			 '<div id="responseJsonContents"></div>'+
			 '<div class="frame_inner_search"></div>', 
			buttons:{
				cancle:{
					className:"modalDefaultBtn",
					label:"닫기"
				}
			}
		});	
		
		if(evt.contentType=='json'||evt.contentType=='none-json'){
			var input = eval('(' +evt.content+ ')');
			$("#responseJsonContents").append('<div class="col-xs-12 form-group">'+
				'<div class="col-xs-12"><label class="modalLabel">JSON CONTENTS</label></div><div class="col-xs-12" style="padding-left: 30px"><pre id="json-renderer"></pre></div></div>');
			$('#json-renderer').jsonViewer(input);	
		}
		
		for(var i=0; i<evt.params.length; i++){				
			$("#responseParams").append('<div class="col-xs-12 form-group">'+
				'<div class="col-xs-3"><label class="modalLabel">'+evt.params[i].key+'</label></div>'+
				'<div class="col-xs-9"><span class="textStyle">'+evt.params[i].value+'</span"></div>'+
			'</div>');
		}
		
		$("#funcControlEventView").text("장치의 기능을 호출 했습니다.");
		
	} catch (error) {
		return customConfirm("에러","JSON 데이터가 올바르지 않습니다.");
		$("#funcControlEventView").text("장치의 기능을 호출에 실패 하였습니다.");
	}
}

function cbFuncControlFailFunc(evt){
	console.log("팝업 제어콘트롤러 제어 실패(기능)");
	commonErrorMessage(evt);	
}

function cbControlSucessFunc(evt){//팝업 새로고침 조회 성공 1

	console.log("팝업 제어콘트롤러 조회 성공");
	selectedControlValue=evt.params[0].value;
	
	if (selectedControlType == "smartiot.onoff") {
		if(selectedControlValue=="on"||selectedControlValue=="off"){
			$(".sessionControlValue1").parents('.btn-group').find('.dropdown-toggle').html(selectedControlValue+'<span class="caret"></span>');
			$("#currentControlEventView").text("장치를 새로고침 했습니다.");
		}
		else {
			$(".sessionControlValue1").parents('.btn-group').find('.dropdown-toggle').html('off<span class="caret"></span>');
			$("#currentControlEventView").text("유효하지 않은 값이 전달되었습니다.");
		}		
		updateValue = selectedControlValue;		
	}else if(selectedControlType == "smartiot.readonly"){
		$("#sessionControlValue2").text(selectedControlValue);
		$("#currentControlEventView").text("장치를 새로고침 했습니다.");
		updateValue=selectedControlValue;
	}else if(selectedControlType == "smartiot.text"){
		$("#sessionControlValue3").val(selectedControlValue);
		$("#currentControlEventView").text("장치를 새로고침 했습니다.");
	}else if(selectedControlType == "smartiot.percent"){
		$("#sessionControlValue4").slider("setValue",parseInt(selectedControlValue));
		$("#currentControlEventView").text("장치를 새로고침 했습니다.");
	}else{
		$("#sessionControlValue3").val(selectedControlValue);
		$("#currentControlEventView").text("장치를 새로고침 했습니다.");
	}
	
}

function cbControlFailFunc(){
	
}

function refreshSessionList() {
	clearSessionData();
	initGetData();
}

function refreshControlList() {
	if (selectedSessionId == null) {
		commonAlert("알림", "연결된 장치목록을 선택 해 주세요.", "연결된 장치목록을 선택 해 주세요.");
		return false;
	} else {
		clearControlData();
		setAttControlData(selectedSessionId);
		setFuncControlData(selectedSessionId);			
	}
}

function showToolPanel(scope) {
	if (!scope.gridOptions.api.isToolPanelShowing()) {
		scope.gridOptions.api.showToolPanel(true);
		// scope.gridOptions.api.sizeColumnsToFit();
	} else {
		scope.gridOptions.api.showToolPanel(false);
		//scope.gridOptions.api.sizeColumnsToFit();
	}
}


/**
 * 이벤트 로그 개시
 */
function startEventLog() {
	if (selectedSessionId == null) {
		commonAlert("에러","연결된 장치를 선택 해 주세요.");
//		alert("연결된 장치를 선택 해 주세요.");
		return false;
	} else {
		startDeviceEventLog(selectedSessionId, cbDevMsgEventFunc);	
		$("#stopActivityBtn").show();
		$("#stopNonActivityBtn").hide();	
		$("#startActivityBtn").hide();	
		$("#startNonActivityBtn").show();		
	}
}

/**
 * 이벤트 로그 중지
 */
function stopEventLog() {
	
//	if (selectedSessionId == null) {
//		commonAlert("에러","연결된 장치를 선택 해 주세요.");
////		alert("연결된 장치를 선택 해 주세요.");
//		return false;
//	} else {
		stopDeviceEventLog(selectedSessionId);
		$("#stopActivityBtn").hide();
		$("#stopNonActivityBtn").show();	
		$("#startActivityBtn").show();	
		$("#startNonActivityBtn").hide();

//	}
}

/**
 * 이벤트 로그 클리어
 */
function clearEventLog() {
	clearDeviceEventLog();
}

function testFunction(){
	tranTimeSeries.clear();
}

/*
 * chart 부분
 * 현재 random으로 값을 출력하고있음.
 */
var tranTimeSeries;
var transmission_cnt = 0;
function createTimeline() {
	tranTimeSeries = new TimeSeries();
	setInterval(function() {
		tranTimeSeries.append(new Date().getTime(), transmission_cnt);
	  transmission_cnt = 0;
	}, 2000);
	
//	var random1 = new TimeSeries();
//	setInterval(function() {
//	  random1.append(new Date().getTime(), res_cnt);
//	  res_cnt = 0;
//	}, 2000);
	
  var chart = new SmoothieChart({millisPerPixel:40, grid:{verticalSections:2}});
  chart.addTimeSeries(tranTimeSeries, {lineWidth:2,strokeStyle:'#00ff00'});
  chart.streamTo(document.getElementById("chart"), 2000);
  
//  chart.addTimeSeries(random1, {lineWidth:2.5,strokeStyle:'rgba(0, 0, 255,0.87)'});
//  chart.streamTo(document.getElementById("chart"), 2000);
}
