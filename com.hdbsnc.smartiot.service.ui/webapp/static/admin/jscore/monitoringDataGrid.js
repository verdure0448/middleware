/**
 * 연결된 장치(세션) 목록 그리드 컬럼 선언
 */
var sessionColumnDefs = [
 	{headerName: "장치ID", field: "deviceId", filter: 'text',width:150},
 	{headerName: "장치풀ID", field: "devicePoolId", filter: 'text',width:150},
 	{headerName: "장치명", field: "deviceName", filter: 'text',width:100},
    {headerName: "Retry", field: "isUse",width:80},
 	{headerName: "아이피", field: "ip", filter: 'text',width:80},
 	{headerName: "포트", field: "port", filter: 'text',width:80},
 	{headerName: "위도", field: "lat", filter: 'text',width:80},
 	{headerName: "경도", field: "lon", filter: 'text',width:80},
 	{headerName: "유저ID", field: "userId", filter: 'text',width:80},
 	{headerName: "세션ID", field: "sessionId", filter: 'text',width:80},
 	/*{headerName: "세션상태", field: "sessionStatus", filter: 'text',width:80},*/
 	/*{headerName: "세션타임아웃", field: "sessionTimeout", filter: 'text',width:80},*/
 	{headerName: "비고", field: "remark",filter: 'text',width:150},
 	{headerName: "변경일시", field: "alterDate",filter: 'text',width:80},
 	{headerName: "등록일시", field: "regDate",filter: 'text',width:80}
 ];


/**
 * 인스턴스 목록 그리드 컬럼 선언
 */
var instanceColumnDefs = [
    {headerName: "인스턴스ID", field: "instanceId", filter: 'text',width:150},
	{headerName: "인스턴스 이벤트", field: "instanceEventStatus",width:100},
    {headerName: "인스턴스 상태", field: "instanceStatus",width:100},
  	{headerName: "프로세서 이벤트", field: "processEvent", filter: 'text',width:100},
  	{headerName: "프로세스 상태", field: "processState", filter: 'text',width:100},
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

/**
 * 아답터 목록 그리드 컬럼 선언
 */

var adapterColumnDefs = [
	//{headerName: 'CK', width: 30, checkboxSelection: true, suppressSorting: true, suppressMenu: true},
	{headerName: "아답터명", field: "adapterName", filter: 'text',width: 250},
	{headerName: "아답터종류", field: "adapterKind",width: 80},
	{headerName: "아답터구분", field: "adapterType",width: 130},
	{headerName: "디폴트장치ID", field: "defaultDeviceId",filter: 'text',width: 130},
	{headerName: "세션타임아웃", field: "sessionTimeout",filter: 'text',width: 80}, 
	/*{headerName: "장치 초기상태", field: "initDeviceStatus",width: 80},*/
	{headerName: "아이피", field: "ip",filter: 'text',width: 80},
	{headerName: "포트", field: "port",filter: 'text',width: 80},
	{headerName: "아답터ID", field: "adapterId", filter: 'text',width: 50},
	{headerName: "위도", field: "lat", filter: 'text'},
 	{headerName: "경도", field: "lon", filter: 'text'},
 	{headerName: "셀프ID", field: "selfId", filter: 'text'},
 	{headerName: "셀프암호", field: "selfPw", filter: 'text'},
 	{headerName: "비고", field: "remark", filter: 'text'}
];

	/******************************* CONTROLLERS START ******************************/
	
	/**
	 * 연결된 장치(세션) 목록 그리드 컨드롤러
	 */
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
			toolPanelSuppressPivot: true
		};
		
		function rowDoubleClickFunc(event){
			console.log(event);
			var sid = event.node.data.deviceId;
			selectedSessionId = sid;
			showDevice();
		}

		/* Normal Functions */
		function rowClickedFunc(event) { //Session List Click Event Function
			var sid = event.node.data.deviceId;
			selectedSessionId = sid;

			console.log(event.node.data);
			
		}
	});

	/**
	 * 인스턴스 목록 그리드 컨드롤러
	 */
	agGridModule.controller("instanceCtrl", function($scope) {

		instanceScope = $scope;

		$scope.gridOptions = {
			columnDefs: instanceColumnDefs,
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
		
		function rowDoubleClickFunc(event){
			clearSessionData();
			var iid = event.node.data.instanceId;
			selectedInstanceId = iid; 
//			setSessionData(iid);
			showInstance();	
		}

		/* Normal Functions */
		function rowClickedFunc(event) { 
			clearSessionData();
			var iid = event.node.data.instanceId;
			var status=event.node.data.instanceStatus;
			var eventStatus=event.node.data.instanceEventStatus;
			selectedInstanceId = iid; 
			setSessionData(iid);
			
			console.log(event.node.data);

			setBtnState(status,eventStatus);
			
			/* 영진추가 */
			/*console.log("인스턴스 상태 : " + event.node.data.instanceStatus);*/

			
			
			//			if(event.node.data.instanceStatus ==="start"){
//				$(".instanceState").text("인스턴스가 기동 되었습니다.");
//			}else if(event.node.data.instanceStatus ==="stop"){
//				$(".instanceState").text("인스턴스가 정지 되었습니다.");
//			}else if(event.node.data.instanceStatus ==="suspend"){
//				$(".instanceState").text("인스턴스가 일시정지 되었습니다.");
//			}else {
//				$(".instanceState").text("인스턴스 상태 정보가 없습니다.");
//			}
//
//			console.log(event);
//			
//			event.node.deviceId.rowStyle({'color':'red'});
			
//			cellStyle : {color:'red'}
		}
	});

	/**
	 * 아답터 목록 그리드 컨드롤러
	 */
	agGridModule.controller("adapterCtrl", function($scope) {
		
		adapterScope = $scope;


		$scope.gridOptions = {
			columnDefs: adapterColumnDefs,
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
		function rowSelectedFunc(event) { //Adapter List Click Event Function
			var aid = event.node.data.adapterId;
			selectedAdapterId = aid;
			setInstanceData(aid);
			setBtnState();
		}
		
	});
	/******************************* CONTROLLERS END ******************************/

	/******************************* API SCRIPTS START ******************************/
	function initGetData() {
	  setAdapterData();
    }

	/* Button Events */
	function instanceStart(iid) {
	  otpIns.start(iid, cbInsStartSucessFunc, cbInsStartFailFunc);
	}
	
	function instanceStop(iid) {
	  console.log(iid);
	  otpIns.stop(iid, cbInsStopSucessFunc, cbInsStopFailFunc);
	}
	
	function instanceSuspend(iid) {
	  otpIns.suspend(iid, cbInsSuspendSucessFunc, cbInsSuspendFailFunc);
	}
	
	function sessionDisconnect(sid) {
	  otpSession.disconnect(sid, cbSessionDisconnectSucessFunc,	cbSessionDisconnectFailFunc);	
	}
	
	/* Call backs */


	function cbAdtGetAllSucessFunc(evt){// 아답터 목록 조회 성공
		console.log('아답터 조회 성공');
		var rowDataByJson = parseAdapterJsonData(evt);
		loadData(adapterScope,rowDataByJson);
		$(".blankAdpater").text("");
		
	}

	function cbAdtGetAllFailFunc(evt){// 아답터 목록 조회 실패
		console.log('아답터 조회 실패');
		commonErrorMessage(evt,"blankAdapter");
		clearAdapterData();
	}

	function cbInsSearchByAidSucessFunc(evt){// 인스턴스 목록 조회 성공
		clearInstanceData();
		var data=JSON.parse(evt.content);
		var rowDataByJson = parseInstanceJsonData(evt);
		
		loadData(instanceScope,rowDataByJson);
		
		$(".blankInstance").text("");
		
	}

	function cbInsSearchByAidFailFunc(evt){// 인스턴스 목록 조회 실패
		clearInstanceData();
		commonErrorMessage(evt,"blankInstance");
		
	}
	
	function cbSessionSearchByIidSucessFunc(evt){// iid로 세션목록 조회 성공
		var rowDataByJson = parseSessionJsonData(evt);	
		loadData(sessionScope,rowDataByJson);		
	}

	function cbSessionSearchByIidFailFunc(evt){// iid로 세션목록 조회 실패
		clearSessionData();
		var errorCode = evt.msgCode;
		commonErrorMessage(evt,"blankSession");		
	}
	

	function cbInsStartSucessFunc(evt){// 인스턴스 스타트 성공		
		commonAlert("알림","인스턴스 상태를 새로고침 해주세요.");
		setBtnState();
	}
	function cbInsStartFailFunc(evt){// 인스턴스 스타트 실패
		console.log("인스턴스 스타트 실패");
		commonErrorMessage(evt);

	}
	function cbInsStopSucessFunc(evt){// 인스턴스 스톱 성공
		commonAlert("알림","인스턴스 상태를 새로고침 해주세요.");		
		setBtnState();

	}
	function cbInsStopFailFunc(evt){// 인스턴스 스톱 실패
		console.log("인스턴스 스톱 실패");
		commonErrorMessage(evt);
	}

	function cbInsSuspendSucessFunc(evt){// 인스턴스 일시정지 성공
		commonAlert("알림","인스턴스 상태를 새로고침 해주세요.");		
		setBtnState();

	}
	function cbInsSuspendFailFunc(evt){// 인스턴스 일시정지 실패
		console.log("인스턴스 일시정지 실패");
		commonErrorMessage(evt);
	}
	
	function cbSessionDisconnectSucessFunc(evt){// 세션종료 성공
		console.log("세션종료 성공");
		clearSessionData();
		setSessionData(selectedInstanceId);
	}
	function cbSessionDisconnectFailFunc(evt){// 세션종료 실패
		console.log("세션종료 실패");
		commonErrorMessage(evt);
		
	}

	/******************************* API SCRIPTS END ******************************/

	/* Clear Datas */
	function clearAdapterData(){ //Clear datas to grid(adapter)
		selectedAdapterId = null;
		/*var emptyRow = [{ id: ''}];*/
		var emptyRow = [];
		adapterScope.gridOptions.api.setRowData(emptyRow);
		clearInstanceData();
	}

	function clearInstanceData(){ //Clear datas to grid(instance)
		selectedInstanceId = null;
		/*var emptyRow = [{ id: ''}];*/
		var emptyRow = [];
		instanceScope.gridOptions.api.setRowData(emptyRow);
		clearSessionData();
	}
	
	function clearSessionData(){ //Clear datas to grid(session)
		selectedSessionId = null;
		/*var emptyRow = [{ id: ''}];*/
		var emptyRow = [];
		sessionScope.gridOptions.api.setRowData(emptyRow);
		$(".blankSession").text("");
	}

	/* Init */
//	initAPI(); //Login and Get Data Progress