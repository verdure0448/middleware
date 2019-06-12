$(document).ready(function() {

		$('#MySplitter').width(1200).height(850).split({
			orientation : 'vertical',
			limit : 560,
			position : '50%'
		});
		$("#sWrap").split({
			orientation : 'horizontal',
			limit : 0,
			position : '50%'
		});
		$('#lwrap').split({
			orientation : 'horizontal1',
			limit : 0,
			position : '50%'
		});
		$('#Mysplitter2').split({
			orientation : 'vertical',
			limit : 270
		});

		$("#stopActivityBtn").hide();
		$("#suspendActivityBtn").hide();
		$("#startActivityBtn").hide();
		
		scopeSynchronizeIntervalId=setInterval("viewSynchronize(instanceScope,attributeScope,functionScope,sessionScope,attControlScope)",200);
	});

//Grid Columns 
/*var controlColumnDefs = [
	{headerName: "인스턴스ID", field: "instanceId", filter: 'text',width:150},
	{headerName: "속성키", field: "attKey", filter: 'text',width:80},
	{headerName: "속성명", field: "description",filter: 'text',width:100},
	{headerName: "속성타입", field: "attType",width:100},
	{headerName: "속성값", field: "attValue",filter: 'text',width:80},
	{headerName: "비고", field: "remark",filter: 'text',width:150},
	{headerName: "변경일시", field: "alterDate",filter: 'text',width:80},
	{headerName: "등록일시", field: "regDate",filter: 'text',width:80}
];
*/

var attributeColumnDefs = [ //속성
    {headerName: "인스턴스ID", field: "id", filter: 'text', width: 150},
	{headerName: "속성키", field: "key", filter: 'text', width: 100},
	{headerName: "속성명", field: "description", filter: 'text', width: 100},
	{headerName: "속성값", field: "value", filter: 'text', width: 100},
	{headerName: "속성값 타입", field: "type", width: 100},
	{headerName: "비고", field: "remark", filter: 'text', width: 100},
	{headerName: "변경일시", field: "altDate", filter: 'text', width: 100},
	{headerName: "등록일시", field: "regDate", filter: 'text', width: 100}
];

var functionColumnDefs = [ //기능
    {headerName: "인스턴스ID", field: "id", filter: 'text', width: 150},
	{headerName: "기능키", field: "key", filter: 'text', width: 100},
	{headerName: "기능 이름", field: "name", filter: 'text', width: 100},
	{headerName: "파라미터1", field: "param1", filter: 'text', width: 100},
	{headerName: "파라미터2", field: "param2", filter: 'text', width: 100},
	{headerName: "파라미터3", field: "param3", filter: 'text', width: 100},
	{headerName: "파라미터4", field: "param4", filter: 'text', width: 100},
	{headerName: "파라미터5", field: "param5", filter: 'text', width: 100},
	{headerName: "파라미터1 타입", field: "paramTp1", width: 100},
	{headerName: "파라미터2 타입", field: "paramTp2", width: 100},
	{headerName: "파라미터3 타입", field: "paramTp3", width: 100},
	{headerName: "파라미터4 타입", field: "paramTp4", width: 100},
	{headerName: "파라미터5 타입", field: "paramTp5", width: 100},
	{headerName: "컨텐츠타입", field: "type", filter: 'text', width: 100},
	{headerName: "비고", field: "remark", filter: 'text', width: 100},
	{headerName: "변경일시", field: "altDate", filter: 'text', width: 100},
	{headerName: "등록일시", field: "regDate", filter: 'text', width: 100}
];

var sessionColumnDefs = [
	{headerName: "장치ID", field: "deviceId", filter: 'text',width:150},
	{headerName: "장치풀ID", field: "devicePoolId", filter: 'text',width:150},
	{headerName: "장치명", field: "deviceName", filter: 'text',width:150},
	/*{headerName: "사용여부", field: "isUse", filter: 'text',width:80},*/
	{headerName: "아이피", field: "ip", filter: 'text',width:80},
	{headerName: "포트", field: "port", filter: 'text',width:70},
	{headerName: "위도", field: "lat", filter: 'text',width:70},
	{headerName: "경도", field: "lon", filter: 'text',width:70},
	{headerName: "유저ID", field: "userId", filter: 'text',width:80},
	{headerName: "세션ID", field: "sessionId", filter: 'text',width:150},
	/*{headerName: "세션상태", field: "sessionStatus", filter: 'text',width:80},
	{headerName: "세션타임아웃", field: "sessionTimeout", filter: 'text',width:80},*/
	{headerName: "비고", field: "remark",filter: 'text',width:150},
	{headerName: "변경일시", field: "alterDate",filter: 'text',width:80},
	{headerName: "등록일시", field: "regDate",filter: 'text',width:80}
];


/**
 * 인스턴스 목록 그리드 컬럼 선언
 */
var instanceColumnDefs = [
	{headerName: "인스턴스 이벤트", field: "instanceEventStatus",width:100},
    {headerName: "인스턴스 상태", field: "instanceStatus",width:100},
    {headerName: "인스턴스ID", field: "instanceId", filter: 'text',width:150},
    {headerName: "장치풀ID", field: "devicePoolId", filter: 'text',width:150},
    {headerName: "아답터ID", field: "adapterId", filter: 'text',width:150},
    {headerName: "인스턴스명", field: "instanceName",filter: 'text',width:80},
    {headerName: "인스턴스 종류", field: "instanceKind",width:80}, 
    /*{headerName: "인스턴스 구분", field: "instanceType",width:80},*/
    {headerName: "디폴트장치ID", field: "defaultDeviceId",filter: 'text',width:150},
    {headerName: "Retry", field: "isUse",width:80},
    {headerName: "세션타임아웃", field: "sessionTimeout",filter: 'text',width:80}, 
    /*{headerName: "장치 초기상태", field: "initDeviceStatus",width:80},*/
    {headerName: "아이피", field: "ip",filter: 'text',width:80},
    {headerName: "포트", field: "port",filter: 'text',width:80},
    {headerName: "URL", field: "url",filter: 'text',width:80},
    {headerName: "위도", field: "lat", filter: 'text',width:80},
    {headerName: "경도", field: "lon", filter: 'text',width:80},
    {headerName: "셀프ID", field: "selfId", filter: 'text',width:80},
    {headerName: "셀프암호", field: "selfPw", filter: 'text',width:80},
    {headerName: "비고", field: "remark", filter: 'text',width:150},
    {headerName: "변경일시", field: "alterDate", filter: 'text',width:80},
    {headerName: "등록일시", field: "regDate", filter: 'text',width:80}
];

var controlAttColumnDefs = [
	{headerName: "속성키", field: "attKey", filter: 'text',width:80},
	{headerName: "속성명", field: "description",filter: 'text',width:100},
	{headerName: "속성타입", field: "attType",width:100},
	{headerName: "비고", field: "remark",filter: 'text',width:150},
	{headerName: "변경일시", field: "alterDate",filter: 'text',width:80},
	{headerName: "등록일시", field: "regDate",filter: 'text',width:80}
];

var controlFuncColumnDefs = [
	{headerName: "기능키", field: "functionKey", filter: 'text',width:80},
	{headerName: "컨텐츠타입", field: "contentsType",filter: 'text',width:100},
	{headerName: "파라미터1", field: "param1",width:100},
	{headerName: "파라미터1타입", field: "param1Type",width:100},
	{headerName: "파라미터2", field: "param2",width:100},
	{headerName: "파라미터2타입", field: "param2Type",width:100},
	{headerName: "파라미터3", field: "param3",width:100},
	{headerName: "파라미터3타입", field: "param3Type",width:100},
	{headerName: "파라미터4", field: "param4",width:100},
	{headerName: "파라미터4타입", field: "param4Type",width:100},
	{headerName: "파라미터5", field: "param5",width:100},
	{headerName: "파라미터5타입", field: "param5Type",width:100},
	{headerName: "비고", field: "attType",width:100},
	{headerName: "변경일시", field: "alterDate",filter: 'text',width:80},
	{headerName: "등록일시", field: "regDate",filter: 'text',width:80}
];

//GET Params Value
var html_param = String(window.location);
	html_param = html_param.split("aid=");
	console.log(html_param[1]);
	selectedAdapterId = html_param[1];

//Global IID
//var globalIid = null;

//Attribute Read Array
//var attributeRowData = [];
//var attributeRowLength = 0;
//var attributeReadCount = 0;

//Control Read Array
//var controlRowData = [];
//var controlRowLength = 0;
//var controlReadCount = 0;

//Control Value Column
var controlValueColumn = null;
var setSelectionOptions = ['on','off'];

	/******************************* CONTROLLERS START ******************************/

	agGridModule.controller("instanceCtrl", function($scope) {

		instanceScope = $scope;

		$scope.gridOptions = {
			columnDefs: instanceColumnDefs,
			rowSelection: 'single',
			onRowSelected: rowSelectedFunc,
			rowData: null,
			enableSorting: true,
			enableFilter: true,
			enableColResize: true,
			showToolPanel: false,
			toolPanelSuppressValues: true,
			toolPanelSuppressPivot: true,
			getRowStyle:setFontColorStatus
		};
		
		function setFontColorStatus(event){

			var status=event.node.data.instanceStatus;
			var eventStatus=event.node.data.instanceEventStatus;
			
			if(status=="완료"&&eventStatus=="기동"){
				return {"color":'blue'};
			}else if(status=="장애"){
				return {"color":'red'};
			}else{
				return {"color":'black'};
			}
		}
		
		/* Normal Functions */
		function rowSelectedFunc(event) { //Instance List Click Event Function
			clearSessionData();
			clearFunctionData();
			clearAttributeData();
			var status=event.node.data.instanceStatus;
			var eventStatus=event.node.data.instanceEventStatus;
			var iid = event.node.data.instanceId;
			selectedInstanceId = iid; 
			setFunctionData(iid);
			setSessionData(iid);

			setBtnState(status,eventStatus);
		}
	
	/* Init */
	 //Login and Get Data Progress

	});


	agGridModule.controller("attributeCtrl", function($scope) {

		attributeScope = $scope;

		$scope.gridOptions = {
			columnDefs: attributeColumnDefs,
			rowSelection: 'single',
			//onRowSelected: rowSelectedFunc,
			rowData: null,
			enableSorting: true,
			enableFilter: true,
			enableColResize: true,
			showToolPanel: false,
			toolPanelSuppressValues: true,
			toolPanelSuppressPivot: true,
		};

		/* Normal Functions */
		/*function rowSelectedFunc(event) { //Session List Click Event Function
			var sid = event.node.data.id;
			selectedSessionId = sid;
		}*/

	});

	agGridModule.controller("functionCtrl", function($scope) {

		functionScope = $scope;

		$scope.gridOptions = {
			columnDefs: functionColumnDefs,
			rowSelection: 'single',
			//onRowSelected: rowSelectedFunc,
			rowData: null,
			enableSorting: true,
			enableFilter: true,
			enableColResize: true,
			showToolPanel: false,
			toolPanelSuppressValues: true,
			toolPanelSuppressPivot: true,
		};

		/* Normal Functions */
		/*function rowSelectedFunc(event) { //Session List Click Event Function
			var sid = event.node.data.id;
			selectedSessionId = sid;
		}*/

	});

	agGridModule.controller("sessionCtrl", function($scope) {

		sessionScope = $scope;

		$scope.gridOptions = {
			columnDefs: sessionColumnDefs,
			rowSelection: 'single',
			onRowClicked: rowClickedFunc,
			onRowDoubleClicked: rowDoubleClickFunc,
			rowData: null,
			enableSorting: true,
			enableFilter: true,
			enableColResize: true,
			showToolPanel: false,
			toolPanelSuppressValues: true,
			toolPanelSuppressPivot: true,
		};
		
		function rowDoubleClickFunc(event){

			clearControlData();
//			var sid = event.node.data.id;
//			selectedSessionId = sid;
//			setControlData(sid);

			selectedSessionId = event.node.data.deviceId;
			setAttControlData(selectedSessionId);
			setFuncControlData(selectedSessionId);
			showDevice();	
		}

		/* Normal Functions */
		function rowClickedFunc(event) { 
			clearControlData();
//			var sid = event.node.data.id;
//			selectedSessionId = sid;
//			setControlData(sid);

			selectedSessionId = event.node.data.deviceId;
			setAttControlData(selectedSessionId);
			setFuncControlData(selectedSessionId);
		}

	});
	

	agGridModule.controller("attControlCtrl", function($scope) {
		
		attControlScope = $scope;
		
		$scope.gridOptions = {
			columnDefs: controlAttColumnDefs,
			rowSelection: 'single',
			onRowSelected: rowSelectedFunc,
			rowData: null,
			enableSorting: true,
			enableFilter: true,
			enableColResize: true,
			showToolPanel: false,
			toolPanelSuppressValues: true,
			toolPanelSuppressPivot: true,
		};
		
		controlValueColumn = attControlScope.gridOptions.columnDefs[4];//value columns

		/* Normal Functions */
		function rowSelectedFunc(event) { //Session List Click Event Function

			console.log(event);
			selectedControlId = event.node.data.instanceId;
			selectedControlKey = event.node.data.attKey;
			selectedControlValue = event.node.data.attValue;
			selectedControlType = event.node.data.attType;
			selectedControlDescruption = event.node.data.description;
		}

	});
	
	agGridModule.controller("funcControlCtrl", function($scope) {
		
		funcControlScope = $scope;
		
		$scope.gridOptions = {
			columnDefs: controlFuncColumnDefs,
			rowSelection: 'single',
			onRowSelected: rowSelectedFunc,
			rowData: null,
			enableSorting: true,
			enableFilter: true,
			enableColResize: true,
			showToolPanel: false,
			toolPanelSuppressValues: true,
			toolPanelSuppressPivot: true,
		};
		
		controlValueColumn = funcControlScope.gridOptions.columnDefs[4];//value columns

		/* Normal Functions */
		function rowSelectedFunc(event) { //Session List Click Event Function

			selectedControlId = event.node.data.instanceId;
			selectedControlKey = event.node.data.functionKey;
			selectedControlValue = event.node.data.param1;
			selectedControlType = event.node.data.param1Type;
		}
	});
	
	
	function GridCellToSelect(params) {
		
		if(selectedSessionId != null){

			var selectFlag = false;
			if(params.data.type){
				selectFlag = params.data.type.includes("onoff");
			}
			var editing = false;
			//var eCell = document.createElement('span');
			var eCell = document.createElement('div');
			var eLabel = document.createTextNode(params.value);
			
			eCell.style['height'] = "100%";
			eCell.appendChild(eLabel);

			if(selectFlag){
				var eSelect = document.createElement("select");
				eSelect.style['height'] = "100%";

				setSelectionOptions.forEach(function(item) {
					var eOption = document.createElement("option");
					eOption.setAttribute("value", item);
					eOption.innerHTML = item;
					eSelect.appendChild(eOption);
				});
				eSelect.value = params.value;

				eCell.addEventListener('click', function () {
					if (!editing) {
						eCell.removeChild(eLabel);
						eCell.appendChild(eSelect);
						eSelect.focus();
						editing = true;
					}
				});

				eSelect.addEventListener('blur', function () {
					if (editing) {
						editing = false;
						eCell.removeChild(eSelect);
						eCell.appendChild(eLabel);
					}
				});

				eSelect.addEventListener('change', function () {
					if (editing) {
						editing = false;
						var newValue = eSelect.value;
						params.data[params.colDef.field] = newValue;
						eLabel.nodeValue = newValue;
						eCell.removeChild(eSelect);
						eCell.appendChild(eLabel);
					}
				});
			}
		
			return eCell;
		}
	}


	/******************************* CONTROLLERS END ******************************/

	/******************************* API SCRIPTS START ******************************/
	function initGetData() {//인스턴스 목록 조회
  	  setInstanceData(selectedAdapterId);
    }

//	function setControlReadData(rowDatas,index) {//제어목록 읽기
//	  console.log(rowDatas[index].key);
//	  otpCtrl.read(selectedSessionId, rowDatas[index].attKey, cbControlDeviceReadSucessFunc, cbControlDeviceReadFailFunc);
//    }

	/* Button Events */
	function instanceStart(iid) {
	  otpIns.start(iid, cbInsStartSucessFunc, cbInsStartFailFunc);
	}
	
	function instanceStop(iid) {
	  otpIns.stop(iid, cbInsStopSucessFunc, cbInsStopFailFunc);
	}
	
	function instanceSuspend(iid) {
	  otpIns.suspend(iid, cbInsSuspendSucessFunc, cbInsSuspendFailFunc);
	}
	
	function sessionDisconnect(sid) {
	  otpSession.disconnect(sid, cbSessionDisconnectSucessFunc,	cbSessionDisconnectFailFunc);	
	}

//	function setControlUpdateData() {//제어목록 업데이트
//	  otpCtrl.update(selectedSessionId, selectedControlKey, selectedControlValue, cbControlUpdateSucessFunc, cbControlUpdateFailFunc);
//    }

	
	//////////////////////////////////////////////////////////////////////////
	// 콜백함수
    //////////////////////////////////////////////////////////////////////////
	function cbLoginSucessFunc(evt){// 로그인 성공 처리
		initGetData();
	}

	function cbLoginFailFunc(evt){// 로그인 실패 처리
//		alert("로그인실패");
		commonAlert("알림", "로그인을 실패했습니다.", "로그인을 실패했습니다.");
	}

	function cbInsSearchByAidSucessFunc(evt){// 인스턴스 목록 조회 성공
		console.log("인스턴스 목록 조회 성공");
		clearInstanceData();
		var data=JSON.parse(evt.content);
		var rowDataByJson = parseInstanceJsonData(evt);
		var status=data[0]['instance.status'];
		var eventStatus=data[0]['instance.event'];
		
		loadData(instanceScope,rowDataByJson);
		
//		setBtnState(status,eventStatus);
	}

	function cbInsSearchByAidFailFunc(evt){// 인스턴스 목록 조회 실패
		console.log("인스턴스 목록 조회 실패");
		clearInstanceData();
		commonErrorMessage(evt, null);
	}
	
	function cbSessionSearchByIidSucessFunc(evt){// iid로 세션목록 조회 성공
		console.log("인스턴스세션 목록 조회 성공");
		var rowDataByJson = parseSessionJsonData(evt);
		loadData(sessionScope,rowDataByJson);
		$(".blankInsViewSession").text("");
	}

	function cbSessionSearchByIidFailFunc(evt){// iid로 세션목록 조회 실패
		console.log("인스턴스세션 목록 조회 실패");
		clearSessionData();
		commonErrorMessage(evt, "blankInsViewSession");
	}

	function cbInsAttSearchByIidSucessFunc(evt){// iid로 인스턴스속성 목록 조회 성공
		console.log("인스턴스속성 목록 조회 성공");
		setSessionData(selectedInstanceId);//세션값 설정
		var rowDataByJson = parseAttributeJsonData(evt);
		loadData(attributeScope,rowDataByJson);
		$(".blankInsViewAtt").text("");
	}

	function cbInsAttSearchByIidFailFunc(evt){	// iid로 인스턴스속성 목록 조회 실패
		console.log("인스턴스속성 목록 조회 실패");
		commonErrorMessage(evt, "blankInsViewAtt");
	}

	function cbInsFuncSearchByIidSucessFunc(evt){	// iid로 인스턴스 기능 목록 조회 성공
		console.log("인스턴스기능 목록 조회 성공");
		setAttributeData(selectedInstanceId);//속성값 설정
		var rowDataByJson = parseFunctionJsonData(evt);
		loadData(functionScope,rowDataByJson);
		$(".blankInsViewFunc").text("");
	}
	function cbInsFuncSearchByIidFailFunc(evt){	// iid로 인스턴스 기능 목록 조회 실패
		console.log("인스턴스기능 목록 조회 실패");
		commonErrorMessage(evt, "blankInsViewFunc");
		setAttributeData(selectedInstanceId);
	}

	function cbSessionAttGetBySidSucessFunc(evt){// sid로 제어목록 조회 성공
		console.log("세션제어목록(속성) 조회 성공");
		var rowDataByJson = parseAttControlJsonData(evt);
		loadData(attControlScope,rowDataByJson);
//		controlRowLength = rowDataByJson.length;
//		controlRowData = rowDataByJson;
//		if( controlRowLength > 0) 
//		setControlReadData(controlRowData,controlReadCount);
		$(".blankInsViewControl").text("");
	}

	function cbSessionAttGetBySidFailFunc(evt){// sid로 제어목록 조회 실패
		console.log("세션제어목록 조회 실패");
		commonErrorMessage(evt, "blankInsViewControl");
	}

	function cbSessionFuncGetBySidSucessFunc(evt){// sid로 제어목록 조회 성공
		console.log("세션제어목록(기능) 조회 성공");
		var rowDataByJson = parseFuncControlJsonData(evt);
		loadData(funcControlScope,rowDataByJson)
//		controlRowLength = rowDataByJson.length;
//		controlRowData = rowDataByJson;
//		setControlReadData(controlRowData,readCount);
		//controlValueColumn.editable = false;
	}
	
	function cbSessionFuncGetBySidFailFunc(evt){// sid로 제어목록 조회 실패
		console.log("세션제어목록 조회 실패");
		commonErrorMessage(evt,"blankDeviceViewControl");
	}

	function cbControlDeviceReadFailFunc(evt){// 장치속성 값 읽기 실패
		console.log("장치속성 읽기 실패");
		commonAlert("에러",evt.msg,evt.code+"\n"+evt.msg);
	}

	function cbInsStartSucessFunc(evt){// 인스턴스 스타트 성공
		console.log("인스턴스 스타트 성공");
		commonAlert("알림","인스턴스 상태를 새로고침 해주세요.");
//		initGetData();
		setBtnState();
	}
	function cbInsStartFailFunc(evt){// 인스턴스 스타트 실패
		console.log("인스턴스 스타트 실패");
		commonAlert("에러",evt.msg,evt.code+"\n"+evt.msg);
	}

	function cbInsStopSucessFunc(evt){// 인스턴스 스톱 성공
		console.log("인스턴스 스톱 성공");
//		initGetData();
		commonAlert("알림","인스턴스 상태를 새로고침 해주세요.");
		setBtnState();
	}
	function cbInsStopFailFunc(evt){// 인스턴스 스톱 실패
		console.log("인스턴스 스톱 실패");
		commonAlert("에러",evt.msg,evt.code+"\n"+evt.msg);
	}

	function cbInsSuspendSucessFunc(evt){// 인스턴스 일시정지 성공
		console.log("인스턴스 일시정지 성공");
//		initGetData();
		commonAlert("알림","인스턴스 상태를 새로고침 해주세요.");
		setBtnState();
	}
	function cbInsSuspendFailFunc(evt){// 인스턴스 일시정지 실패
		console.log("인스턴스 일시정지 실패");
		commonAlert("에러",evt.msg,evt.code+"\n"+evt.msg);
	}
	
	function cbSessionDisconnectSucessFunc(evt){// 세션종료 성공
		console.log("세션종료 성공");
		clearSessionData();
		setSessionData(selectedInstanceId);
	}
	function cbSessionDisconnectFailFunc(evt){// 세션종료 실패
		console.log("세션종료 실패");
		commonAlert("에러",evt.msg,evt.code+"\n"+evt.msg);
	}
	
	function cbCloseFunc(evt){// 웹소켓 종료 이벤트 --> 로그아웃 처리
		commonAlert("알림", "로그아웃 되었습니다.", "로그아웃 성공");
	}
	
	/******************************* API SCRIPTS END ******************************/

	
	/* Clear Datas */
	function clearInstanceData(){ //Clear datas to grid(instance)
		selectedInstanceId = null;
		/*var emptyRow = [{ id: ''}];*/
		var emptyRow = [];
		instanceScope.gridOptions.api.setRowData(emptyRow);
		clearSessionData();
		clearFunctionData();
		clearAttributeData();
	}
	
	function clearSessionData(){ //Clear datas to grid(session) 
		selectedSessionId = null;
		var emptyRow = [];
		/*var emptyRow = [{ id: '', name: '', session: '', isuse: '' }];*/
		sessionScope.gridOptions.api.setRowData(emptyRow);
		clearControlData();
		$(".blankInsViewSession").text("");
	}

	function clearControlData(){ //Clear datas to grid(control) 
		controlReadCount = 0;
		selectedControlId = null;
		selectedControlKey = null;
		selectedControlValue = null;
		selectedControlType = null;
		
		var emptyRow = [];
		/*var emptyRow = [{ id: ''}];*/

		attControlScope.gridOptions.api.setRowData(emptyRow);
		funcControlScope.gridOptions.api.setRowData(emptyRow);
		$(".blankInsViewControl").text("");
	}

	function clearFunctionData(){ //Clear datas to grid(function) 
		/*var emptyRow = [{ id: ''}];*/
		var emptyRow = [];
		functionScope.gridOptions.api.setRowData(emptyRow);
		$(".blankInsViewFunc").text("");
	}

	function clearAttributeData(){ //Clear datas to grid(attribute) 
		/*var emptyRow = [{ id: ''}];*/
		var emptyRow = [];
		attributeScope.gridOptions.api.setRowData(emptyRow);
		$(".blankInsViewAtt").text("");
	}
	
	function cbAttControlSucessFuncAndGrid(evt){//팝업 업데이트 조회 성공  0
		console.log("팝업 제어콘트롤러 조회 성공(속성)");
		selectedControlValue=evt.params[0].value;
		
//		console.log(evt.params[0].value);
		setAttControlUpdateData();//팝업 그리는 곳
		attControlerFlag=false;
	}

	function cbAttControlFailFunc(evt){
		console.log("팝업 제어콘트롤러 조회 실패(속성)");
		commonErrorMessage(evt)	
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

	function cbControlUpdateSucessFunc(evt){// 제어 업데이트 성공
		console.log("제어업데이트 성공");
		$("#currentControlEventView").text("장치를 업데이트 했습니다.");
	}

	function cbControlUpdateFailFunc(evt){// 제어 업데이트 실패
		commonAlert("에러",evt.msg,evt.code+"\n"+evt.msg);
//		clearControlData();
//		setControlData(selectedSessionId);
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
	
	function controlChange(){
		
		selectedControlKey=null;
		selectedControlValue=null;
		
		if($('#attControlPanel').css('display')=='none'){
			$('#attControlPanel').show();
			$('#funcControlPanel').hide();
		}else{
			$('#attControlPanel').hide();
			$('#funcControlPanel').show();
		}		
	}
