/* Variables */
//Auth
var otpAuth = new bsnc.otp.api.auth(); //Instance Object 
var otpAdt = new bsnc.otp.api.adt(otpAuth);
var otpIns = new bsnc.otp.api.ins(otpAuth);
var otpSession = new bsnc.otp.api.ins.session(otpAuth);

//Module
var agGridModule = angular.module("agApps", ["agGrid"]);

//Scopes
var adapterScope = null;
var instanceScope = null;
var sessionScope = null;
var functionScope = null;

//Grid Columns 
var sessionColumnDefs = [
	{headerName: "Device ID", field: "id", filter: 'text'},
	{headerName: "Device Name", field: "name", filter: 'text'},
	{headerName: "Session", field: "session"},
	{headerName: "IsUse", field: "isuse"} 
];

var instanceColumnDefs = [
	{headerName: "Instance ID", field: "instanceId", width: 120, filter: 'text'},
	{headerName: "Instance Name", field: "instanceName", width: 125, filter: 'text'},
	{headerName: "Type", field: "type", width: 80},
	{headerName: "Status", field: "status", width: 80, filter: 'text'},
	{headerName: "Device ID", field: "deviceId", width: 90, filter: 'text'},
	{headerName: "Adapter ID", field: "adapterId", width: 100, filter: 'text'},
	{headerName: "User", field: "user", width: 90, filter: 'text'}
];

var adapterColumnDefs = [
	{headerName: "Adapter Name", field: "adapterName", width: 125,  filter: 'text'},
	{headerName: "Adapter ID", field: "adapterId", width: 126,  filter: 'text'}
];
var functionColumnDefs = [
	{headerName: "Property Name", field: "propertyName", width: 120, filter: 'text'},
	{headerName: "Property ID", field: "propertyId", width: 120, filter: 'text'},
	{headerName: "Value", field: "value", width: 80},
	{headerName: "Form", field: "form"} 
];
//Selected Value
var selectedAdapterId = null;
var selectedInstanceId = null;
var selectedSessionId = null;
var selectedFunctionId = null;

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
			var sid = event.node.data.id;
			selectedSessionId = sid;
		}

	});

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
		};

		/* Normal Functions */
		function rowSelectedFunc(event) { //Instance List Click Event Function
			clearSessionData();
			var iid = event.node.data.id;
			selectedInstanceId = iid; 
			setSessionData(iid);
		}

	});

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
			var aid = event.node.data.id;
			selectedAdapterId = aid;
			setInstanceData(aid);
		}
		
	});

	agGridModule.controller("functionCtrl", function($scope) {

		functionScope = $scope;

		$scope.gridOptions = {
			columnDefs: functionColumnDefs,
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
		function rowSelectedFun(event) { //Session List Click Event Function
			var fid = event.node.data.id;
			selectedFunctionId = fid;

		}

	});
	/******************************* CONTROLLERS END ******************************/

	/******************************* API SCRIPTS START ******************************/
	function initGetData() {
	  otpAdt.searchAll(cbAdtSearchAllSucessFunc, cbAdtSearchAllFailFunc);
    }

	function setInstanceData(aid) {
  	  otpIns.searchByAid(aid, cbInsSearchByAidSucessFunc, cbInsSearchByAidFailFunc);
    }

//	function setSessionData(iid) {
//	  otpSession.getByIid(iid, cbSessionGetByIidSucessFunc, cbSessionGetByIidFailFunc);
//    }
	
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
	  otpSession.disConnect(sid, cbSessionDisconnectSucessFunc,	cbSessionDisconnectFailFunc);	
	}
	
	/* Call backs */
	function cbLoginSucessFunc(evt){// 로그인 성공 처리
		initGetData();
	}

	function cbLoginFailFunc(evt){// 로그인 실패 처리
		commonAlert("경고","로그인 실패","로그인실패");
	}

	function cbAdtSearchAllSucessFunc(evt){// 아답터 목록 조회 성공
		var rowDataByJson = parseAdapterJsonData(evt);
		loadAdapterData(rowDataByJson);
	}

	function cbAdtSearchAllFailFunc(evt){// 아답터 목록 조회 실패
		clearAdapterData();
		commonErrorMessage(evt, null);
	}

	function cbInsSearchByAidSucessFunc(evt){// 인스턴스 목록 조회 성공
		clearInstanceData();
		var rowDataByJson = parseInstanceJsonData(evt);
		loadInstanceData(rowDataByJson);
	}

	function cbInsSearchByAidFailFunc(evt){// 인스턴스 목록 조회 실패
		commonAlert("알림","인스턴스 목록 조회 실패","인스턴스 목록 조회 실패");
		clearInstanceData();
	}
	
	function cbSessionGetByIidSucessFunc(evt){// iid로 세션목록 조회 성공
		var rowDataByJson = parseSessionJsonData(evt);
		loadSessionData(rowDataByJson);
	}

	function cbSessionGetByIidFailFunc(evt){// iid로 세션목록 조회 실패
		clearSessionData();
		commonErrorMessage(evt, null);
	}
	

	function cbInsStartSucessFunc(evt){// 인스턴스 스타트 성공
		setInstanceData(selectedInstanceId)
		setInstanceData(selectedAdapterId);
	}
	function cbInsStartFailFunc(evt){// 인스턴스 스타트 실패
		console.log("인스턴스 스타트 실패");
		commonErrorMessage(evt, null);
	}

	function cbInsStopSucessFunc(evt){// 인스턴스 스톱 성공
		setInstanceData(selectedInstanceId)
	}
	function cbInsStopFailFunc(evt){// 인스턴스 스톱 실패
		console.log("인스턴스 스톱 실패");
		commonErrorMessage(evt, null);
	}

	function cbInsSuspendSucessFunc(evt){// 인스턴스 일시정지 성공
		setInstanceData(selectedInstanceId)
	}
	function cbInsSuspendFailFunc(evt){// 인스턴스 일시정지 실패
		console.log("인스턴스 일시정지 실패");
		commonErrorMessage(evt, null);
	}
	
	function cbSessionDisconnectSucessFunc(evt){// 세션종료 성공
		console.log("세션종료 성공");
		clearSessionData();
		setSessionData(selectedInstanceId);
	}
	function cbSessionDisconnectFailFunc(evt){// 세션종료 실패
		console.log("세션종료 실패");
		commonErrorMessage(evt, null);
	}

	function cbCloseFunc(evt){// 웹소켓 종료 이벤트 --> 로그아웃 처리
		commonAlert("알림","로그아웃 되었습니다.","웹소켓이 종료되어 로그아웃됨.");
	}
	/******************************* API SCRIPTS END ******************************/

	/******************************* JSON PARSER START ******************************/
	//format rowDatas from pased Json Data
	function parseAdapterJsonData(evt_data){ 

		var data = evt_data.content;
		var tmp_evt_json = JSON.parse(data);
		var rowData = [];

		$.each(tmp_evt_json, function( index, value ) {
			
			var adapterName = value['adapter.name'];
			var defaultDeviceId = value['default.device.id'];
			var sessionTimeout = value['session.timeout']; 
			var ip = value['ip'];
			var latitude = value['latitude'];
			var remark = value['remark'];
			var adapterId = value['adapter.id'];
			var selfPw = value['self.pw'];
			var port = value['port'];
			var adapterType = value['adapter.type'];
			var adapterKind = value['adapter.kind'];
			var initDeviceStatus = value['init.device.status'];
			var selfId = value['self.id'];
			var longitude = value['longitude'];

			rowData.push({
				group: adapterKind,
                id: adapterId,
                instance: adapterName,
                type: adapterType,
                ip: ip,
                port: port,
                status: initDeviceStatus,
				device: defaultDeviceId,
				adapter: adapterId,
				userid: selfId,
            });
			
		});
		
		return rowData;
	}
	
	function parseInstanceJsonData(evt_data){ 
		
		var data = evt_data.content;
		var tmp_evt_json = JSON.parse(data);
		var rowData = [];

		$.each(tmp_evt_json, function( index, value ) {

			var instanceId = value['instance.id'];
			var defaultDeviceId = value['default.device.id'];
			var sessionTimeout = value['session.timeout']; 
			var devicePoolId = value['device.pool.id']; 
			var instanceName = value['instance.name'];
			var ip = value['ip'];
			var latitude = value['latitude'];
			var remark = value['remark'];
			var adapterId = value['adapter.id'];
			var selfPw = value['self.pw'];
			var instanceKind = value['instance.kind'];
			var instanceType = value['instance.type'];
			var port = value['port'];
			var alterDate = value['alter.date'];
			var registrationDate = value['registration.date'];
			var initDeviceStatus = value['init.device.status'];
			var selfId = value['self.id'];
			var isUse = value['is.use'];
			var instanceStatus = value['instance.status'];
			var longitude = value['longitude'];

			rowData.push({
                instanceId: instanceId,
                instanceName: instanceName,
                type: instanceType,
                status: initDeviceStatus,
				deviceId: defaultDeviceId,
				adapterId: adapterId,
				user: isUse
            });
	
		});
		
		return rowData;
	}

	function parseSessionJsonData(evt_data){ 
		
		var data = evt_data.content;
		var tmp_evt_json = JSON.parse(data);
		var rowData = [];

		$.each(tmp_evt_json, function( index, value ) {
		
			var alterDate = value['alter.date'];
			var deviceId = value['device.id']; 
			var deviceName = value['device.name']; 
			var devicePoolId = value['device.pool.id']; 
			var ip = value['ip'];
			var isUse = value['is.use'];
			var latitude = value['latitude'];
			var longitude = value['longitude'];
			var port = value['port'];
			var registrationDate = value['registration.date'];
			var remark = value['remark'];
			var sessionId = value['session.id']; 
			var sessionStatus = value['session.status']; 
			var sessionTimeout = value['session.timeout']; 
			var userId = value['user.id']; 
			
			rowData.push({
                id: deviceId,
                name: deviceName,
                session: sessionId,
                isuse: isUse,
            });
		
		});
		
		return rowData;
	}

	function parseFunctionJsonData(evt_data){ 
		
		var data = evt_data.content;
		var tmp_evt_json = JSON.parse(data);
		var rowData = [];

		$.each(tmp_evt_json, function( index, value ) {
			var alterDate = value['alter.date'];
			var deviceId = value['device.id']; 
			var deviceName = value['device.name']; 
			var devicePoolId = value['device.pool.id']; 
			var ip = value['ip'];
			var isUse = value['is.use'];
			var latitude = value['latitude'];
			var longitude = value['longitude'];
			var port = value['port'];
			var registrationDate = value['registration.date'];
			var remark = value['remark'];
			var sessionId = value['session.id']; 
			var sessionStatus = value['session.status']; 
			var sessionTimeout = value['session.timeout']; 
			var userId = value['user.id']; 
			
			rowData.push({
                propertyId: deviceId,
                propertyName: deviceName,
                type: sessionId,
                form: isUse,
            });
		
		});
		
		return rowData;
	}
	/******************************* JSON PARSER END ******************************/

	/* Load Datas */
	function loadAdapterData(rowDataByJson){ //Load datas to grid(adapter)
		adapterScope.gridOptions.api.setRowData(rowDataByJson);
	}

	function loadInstanceData(rowDataByJson){ //Load datas to grid(instance)
		instanceScope.gridOptions.api.setRowData(rowDataByJson);
	}
	
	function loadSessionData(rowDataByJson){ //Load datas to grid(session)
		sessionScope.gridOptions.api.setRowData(rowDataByJson);
	}

	function loadFunctionData(rowDataByJson){ //Load datas to grid(function)
		functionScope.gridOptions.api.setRowData(rowDataByJson);
	}
	
	/* Clear Datas */
	function clearAdapterData(){ //Clear datas to grid(adapter)
		selectedAdapterId = null;
		/*var emptyRow = [{ adapterId: '', adapterName: '' }];*/
		var emptyRow = [];
		adapterScope.gridOptions.api.setRowData(emptyRow);
		clearInstanceData();
	}

	function clearInstanceData(){ //Clear datas to grid(instance)
		selectedInstanceId = null;
		/*var emptyRow = [{ instanceId: '', instanceName: '', type: '', status: '', deviceId: '', adapterId: '', user: '' }];*/
		var emptyRow = [];
		instanceScope.gridOptions.api.setRowData(emptyRow);
		clearSessionData();
	}
	
	function clearSessionData(){ //Clear datas to grid(session)
		selectedSessionId = null;
		/*var emptyRow = [{ id: '', name: '', session: '', isuse: '' }];*/
		var emptyRow = [];
		sessionScope.gridOptions.api.setRowData(emptyRow);
	}

	function clearFunctionData(){ //Clear datas to grid(function)
		selectedFunctionId = null;
		/*var emptyRow = [{ propertyId: '', propertyName: '', value: '', form: '' }];*/
		var emptyRow = [];
		functionScope.gridOptions.api.setRowData(emptyRow);
		clearControlData();
	}
	
	function clearControlData(){ //Clear datas to grid(Control)
		selectedControlId = null;
		/*var emptyRow = [{ id: '', name: '', session: '', isuse: '' }];*/
		var emptyRow = [];
		sessionScope.gridOptions.api.setRowData(emptyRow);
	}

	/* Init */
//	initAPI(); //Login and Get Data Progress