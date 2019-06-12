/**
 * 관제 & 모니터링 페이지에서 사용하는 자바스크립트
 * 
 * 
 */
$(document).ready(function() {
	
	$('#MySplitter').width(1200).height(850).split({orientation:'vertical', limit:320, position:'25%'});
	$('#sWrap').split({orientation:'horizontal', limit:0});


	$("#addrIP").text("Smart IoT 2.0 관리자화면 - "+location.host);
	$("#stopActivityBtn").hide();
	$("#suspendActivityBtn").hide();
	$("#startActivityBtn").hide();
	
	scopeSynchronizeIntervalId=setInterval("viewSynchronize(adapterScope,instanceScope,sessionScope)",200);
});
function showInstance() {
	if (selectedAdapterId == null) {
		commonAlert("알림","아답터를 선택 해 주세요.","아답터가 선택되지 않음.");
	} else {
		var parameter = "?aid=" + selectedAdapterId;
		var url = "./instance_view.html";
		location.href = url + parameter;
	}
}

function showDevice() {
	if (selectedInstanceId == null) {
		commonAlert("알림","인스턴스를 선택 해 주세요.","인스턴스가 선택되지 않음.");
	} else {
		var parameter = "?iid=" + selectedInstanceId;
		var url = "./device_view.html";
		location.href = url + parameter;
	}
}

function checkIid(param) {
	if (selectedInstanceId == null) {
		commonAlert("알림","인스턴스를 선택 해 주세요.","인스턴스가 선택되지 않음.");
	} else {
		switch (param) {
		case "start":
			instanceStart(selectedInstanceId);
			break;
		case "stop":
			instanceStop(selectedInstanceId);
			break;
		case "suspend":
			instanceSuspend(selectedInstanceId);
			break;
		}	
	}
}

function checkSid() {
	if (selectedSessionId == null) {
		commonAlert("알림","연결된 장치목록을 선택 해 주세요.","연결된 장치목록이 선택되지 않음.");
	} else {
		sessionDisconnect(selectedSessionId);
	}
}

function refreshAdapterList() {// 아답터목록 새로고침
	clearAdapterData();
	initGetData();
	setBtnState();
}

function refreshInstanceList() {// 인스턴스목록 새로고침
	if (selectedAdapterId == null) {
		commonAlert("알림","아답터를 선택 해 주세요.","아답터가 선택되지 않음.");
		return false;
	} else {
		clearInstanceData();
		setInstanceData(selectedAdapterId);
		setBtnState();
	}
}

function refreshSessionList() {// 세션목록 새로고침
	if (selectedInstanceId == null) {
		commonAlert("알림","인스턴스를 선택 해 주세요.","인스턴스가 선택되지 않음.");
		return false;
	} else {
		clearSessionData();
		setSessionData(selectedInstanceId);
	}
}

function showToolPanel(scope) {
	if (!scope.gridOptions.api.isToolPanelShowing()) {
		scope.gridOptions.api.showToolPanel(true);
		// scope.gridOptions.api.sizeColumnsToFit();
	} else {
		scope.gridOptions.api.showToolPanel(false);
		// scope.gridOptions.api.sizeColumnsToFit();
	}
}
