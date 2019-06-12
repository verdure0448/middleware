//Grid Columns 
var controlAttColumnDefs = [
	/*{headerName: "인스턴스ID", field: "instanceId", filter: 'text',width:150},*/
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

//GET Params Value
var html_param = String(window.location);
	html_param = html_param.split("iid=");
	console.log(html_param[1]);
	selectedInstanceId = html_param[1];

//Global IID
//var globalIid = null;

//Control Read Array
//var controlReadArr = [];
//var controlRowData = [];
//var controlRowLength = 0;
//var readCount = 0;
//Control Value Column
var controlValueColumn = null;
var setSelectionOptions = ['on','off'];

$( window ).unload(function() {
	if($("#startActivityBtn").css("display")=="none"){
		return stopDeviceEventLog2(selectedSessionId);
	}	
});


$(document).ready(function(){

	scopeSynchronizeIntervalId=setInterval("viewSynchronize(sessionScope,attControlScope,logEventScope)",200);
});
	/******************************* CONTROLLERS START ******************************/
	agGridModule.controller("sessionCtrl", function($scope) {
		
		sessionScope = $scope;

		$scope.gridOptions = {
			columnDefs: sessionColumnDefs,
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

		/* Normal Functions */
		function rowSelectedFunc(event) { //Session List Click Event Function
			clearControlData();
			if(selectedSessionId!=event.node.data.deviceId){
				stopEventLog();
				clearDeviceEventLog();
				testFunction();
			}
			selectedSessionId = event.node.data.deviceId;
			
			setAttControlData(selectedSessionId);
			setFuncControlData(selectedSessionId);				

		}
		
		console.log($scope.gridOptions);
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
	
	/**
	 * 로그이벤트 데이터 그리드 컬럼 정의
	 */
	var logEventColumnDefs = [
	    {headerName: "이벤트TIME", field: "eventTime", filter: 'text',width:120},
		{headerName: "통신구분", field: "transmission", filter: 'text',width:200},
		{headerName: "인스턴스ID", field: "instanceId", filter: 'text',width:150},
		{headerName: "인스턴스명", field: "instanceName", filter: 'text',width:80},
		{headerName: "장치ID", field: "targetId", filter: 'text',width:150},
		{headerName: "장치명", field: "targetName", filter: 'text',width:80},
		{headerName: "제어경로", field: "controlPath", filter: 'text',width:80},
		{headerName: "제어설명", field: "controlDesc", filter: 'text',width:80},
		{headerName: "명령", field: "commandKey", filter: 'text',width:80},
		{headerName: "명령값", field: "commandValue", filter: 'text',width:80},
		{headerName: "컨텐츠 타입", field: "contentType", filter: 'text',width:80},
		{headerName: "세션ID", field: "sessionId", filter: 'text',width:50},
		/*{headerName: "컨텐츠", field: "content", filter: 'text',width:150},*/
		{headerName: "이벤트ID", field: "eventId", filter: 'text',width:150}
	];
	/**
	 * 로그 이벤트 컨트롤러 
	 */
	var rowData = [];
	agGridModule.controller("logEventCtrl", function($scope) {
		
		logEventScope = $scope;
		
		 
		//$scope.gridOptions.columnDefs = logEventColumnDefs;
		rowData = [];
		$scope.gridOptions = {
			columnDefs: logEventColumnDefs,
			rowSelection: 'single',
			rowData: rowData,
			enableSorting: true,
			enableFilter: true,
			enableColResize: true,
			showToolPanel: false,
			toolPanelSuppressValues: true,
			toolPanelSuppressPivot: true,
		};
		var idx = 0;
		$scope.addNewItem = function(addRows){
			//rowData.push({ sessionId: 'Test insert1 ', transmission: 'Test insert1' });
//			var updatedNodes = [];
//			$scope.gridOptions.api.forEachNode( function(node) {
//				updatedNodes.push({ sessionId:node.data.sessionId, transmission:node.data.transmission});
//			});
			//idx = idx + 1;
		
			for (var idx in addRows) {
				rowData.unshift(addRows[idx]);
			};
			
			//$scope.gridOptions.api.setRowData($scope.data);
			//$scope.gridOptions.api.refreshRows($scope.data);
			//$scope.gridOptions.api.refreshView();
			//$scope.gridOptions.api.refreshRows(updatedNodes);
			$scope.gridOptions.api.setRowData(rowData);
	    };
	    
	    $scope.rowClear = function(){
	    	rowData = [];
	    	$scope.gridOptions.api.setRowData(rowData);
	    }
	});
	
	function GridCellToSelect(params) {
		
		if(selectedSessionId != null){

			var selectFlag = params.data.attType.includes("onoff");
			var editing = false;
			var eCell = document.createElement('span');
			var eLabel = document.createTextNode(params.value);

			eCell.appendChild(eLabel);

			if(selectFlag){
				var eSelect = document.createElement("select");

				setSelectionOptions.forEach(function(item) {
					var eOption = document.createElement("option");
					eOption.setAttribute("attValue", item);
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


	/**
	 * 장치 이벤트 로그 개시
	 * @param did
	 */
	function startDeviceEventLog(did){
		otpEvent.devMsgStart(did, cbDevMsgStartSucessFunc, cbDevMsgStartFailFunc, cbDevMsgEventFunc);
	}
	
	/**
	 * 장치 이벤트 로그 정지
	 */
	function stopDeviceEventLog(did){
		otpEvent.devMsgStop(did, cbDevMsgStopSucessFunc, cbDevMsgStopFailFunc);
	}	
	function stopDeviceEventLog2(did){
		otpEvent.devMsgStop(did);
	}
	
	function cbDevMsgEventFunc(evt) {
		console.log("장치 이벤트 수신.");
		var rowDataByJson = parseEventLogJsonData(evt);
		//logEventScope.gridOptions.gridData.push(rowDataByJson);
		//logEventScope.gridOptions.data.splice(1, 0, rowDataByJson);
		logEventScope.addNewItem(rowDataByJson);
		
		transmission_cnt = transmission_cnt + rowDataByJson.length;
	}

	function cbDevMsgStartSucessFunc(evt) {//장치 이벤트 로그 개시 성공.
		$(".blankDeviceViewControlGraph").text("Start");
	}
	
	function cbDevMsgStartFailFunc(evt) {//장치 이벤트 로그 개시 실패.
		commonErrorMessage(evt,"장치 이벤트 로그 개시 실패");
	}
	
	function cbDevMsgStopSucessFunc(evt) { //장치 이벤트 로그 정지 성공.
		$(".blankDeviceViewControlGraph").text("Stop");
	}
	
	function cbDevMsgStopFailFunc(evt) { //장치 이벤트 정지 실패.
		commonErrorMessage(evt,"장치 이벤트 로그 정지 실패");
	}
	
	function clearDeviceEventLog(){//장치 휴지통모양 눌러서 클리어 처리하는 부분
		$(".blankDeviceViewControlGraph").text("Clear");
		logEventScope.rowClear();		
		
	}
	

	/******************************* CONTROLLERS END ******************************/

	/******************************* API SCRIPTS START ******************************/

	function initGetData() {//최초 세션목록 조회
	  setSessionData(selectedInstanceId);
    }
	
//	function setControlReadData(rowDatas,index) {//제어목록 읽기
//	  //console.log(rowDatas[index].key);
//	  if(rowDatas.length == 0) return;
//	  otpCtrl.read(selectedSessionId, rowDatas[index].attKey, cbControlDeviceReadSucessFunc, cbControlDeviceReadFailFunc);
//    }

	/* Button Events */
	function sessionDisconnect(sid) {
	  otpSession.disconnect(sid, cbSessionDisconnectSucessFunc,	cbSessionDisconnectFailFunc);	
	}


	/* Call backs */

	function cbSessionSearchByIidSucessFunc(evt){// iid로 세션목록 조회 성공
		console.log("인스턴스세션 목록 조회 성공");
		var rowDataByJson = parseSessionJsonData(evt);
		loadData(sessionScope,rowDataByJson);
	}

	function cbSessionSearchByIidFailFunc(evt){// iid로 세션목록 조회 실패
		console.log("인스턴스세션 목록 조회 실패");
		commonAlert("에러",evt.msg,evt.code+"\n"+evt.msg);
		clearSessionData();
	}


	function cbSessionAttGetBySidSucessFunc(evt){// sid로 제어목록 조회 성공
		console.log("세션제어목록(속성) 조회 성공");
		var rowDataByJson = parseAttControlJsonData(evt);
		loadData(attControlScope,rowDataByJson)
//		controlRowLength = rowDataByJson.length;
//		controlRowData = rowDataByJson;
//		setControlReadData(controlRowData,readCount);
		//controlValueColumn.editable = false;
	}

	function cbSessionAttGetBySidFailFunc(evt){// sid로 제어목록 조회 실패
		console.log("세션제어목록 조회 실패");
		commonErrorMessage(evt,"blankDeviceViewControl");
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


//	function cbControlDeviceReadSucessFunc(evt){// 장치속성 값 읽기 성공
//		
//		console.log("장치속성 읽기 성공");
//		console.log("readCount:"+readCount);
//
//		console.log(evt.params[0]);
//		if(evt.params[0].value != null){
//			controlRowData[readCount].value = evt.params[0].value;
//		}
//		readCount++;
//
//		if (readCount == controlRowLength){//모든 READ 값 삽입 끝
//			loadData(controlScope,controlRowData);
//		}else{
//			setControlReadData(controlRowData,readCount);
//		}
//	}

//	function cbControlDeviceReadFailFunc(evt){// 장치속성 값 읽기 실패
//		console.log("장치속성 읽기 실패");
//		commonAlert("에러",evt.msg,evt.code+"\n"+evt.msg);
//	}

	function cbSessionDisconnectSucessFunc(evt){// 세션종료 성공
		console.log("세션종료 성공");
		clearSessionData();
		initGetData();
	}
	function cbSessionDisconnectFailFunc(evt){// 세션종료 실패
		console.log("세션종료 실패");
		commonAlert("에러",evt.msg,evt.code+"\n"+evt.msg);
	}


	/******************************* API SCRIPTS END ******************************/
	
	/* Clear Datas */
	
	function clearSessionData(){ //Clear datas to grid(session) 
		selectedSessionId = null;
		var emptyRow = [];
		/*var emptyRow = [{ id: '' }];*/
		sessionScope.gridOptions.api.setRowData(emptyRow);
		clearControlData();
		//console.log(sessionScope);
		//sessionScope.gridOptions.api.collapseAll();
	}

	function clearControlData(){ //Clear datas to grid(control) 
//		readCount = 0;
		selectedControlId = null;
		selectedControlKey = null;
		selectedControlValue = null;
		selectedControlType = null;
		selectedControlDescruption=null;
		/*var emptyRow = [{ id: '' }];	*/

		var emptyRow = [];	
		attControlScope.gridOptions.api.setRowData(emptyRow);
		funcControlScope.gridOptions.api.setRowData(emptyRow);
		
		//console.log(sessionScope);
		//sessionScope.gridOptions.api.collapseAll();
	}

	/* Init */
//	initAPI(); //Login and Get Data Progress
	
	/****************************** Html Function Event **************************/

	/**
	 * 기능 제어와 속성제어 변경하기
	 */
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

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
