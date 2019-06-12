var arrayInstanceFuncKey=new Array();
var arrayInstanceAttKey=new Array();

$(document).ready(function () {

	$('#MySplitter').width(1200).height(850).split({orientation:'vertical', limit:560, position:'25%'});
    $('#lWrap').split({orientation:'vertical', limit:270,position:'50%'});
    $("#sWrap").split({orientation:'horizontal',limit:0});
    $("#addrIP").text("Smart IoT 2.0 관리자화면 - "+location.host);
	scopeSynchronizeIntervalId=setInterval("viewSynchronize(adapterScope,instanceScope,attributeScope,functionScope)",200);

});


/**
 * 아답터 목록 그리드 컬럼 선언
 */
var adapterColumnDefs = [
	//{headerName: 'CK', width: 30, checkboxSelection: true, suppressSorting: true, suppressMenu: true},
	{headerName: "아답터ID", field: "adapterId", filter: 'text',width: 200},
	{headerName: "아답터명", field: "adapterName", filter: 'text',width: 150},
	{headerName: "아답터종류", field: "adapterKind",width: 80},
	{headerName: "아답터구분", field: "adapterType",width: 130},
	{headerName: "디폴트장치ID", field: "defaultDeviceId",filter: 'text',width: 130},
	{headerName: "세션타임아웃", field: "sessionTimeout",filter: 'text',width: 80}, 
	/*{headerName: "장치 초기상태", field: "initDeviceStatus",width: 80},*/
	{headerName: "아이피", field: "ip",filter: 'text',width: 80},
	{headerName: "포트", field: "port",filter: 'text',width: 80},
	{headerName: "위도", field: "lat", filter: 'text'},
 	{headerName: "경도", field: "lon", filter: 'text'},
 	{headerName: "셀프ID", field: "selfId", filter: 'text'},
 	{headerName: "셀프암호", field: "selfPw", filter: 'text'},
 	{headerName: "비고", field: "remark", filter: 'text'}
];
 
var functionColumnDefs = [ //기능
    /*{headerName: "인스턴스ID", field: "id", filter: 'text', width: 150},*/
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


var attributeColumnDefs = [ //속성
    /*{headerName: "인스턴스ID", field: "id", filter: 'text', width: 150},*/
	{headerName: "속성키", field: "key", filter: 'text', width: 100},
	{headerName: "속성이름", field: "description", filter: 'text', width: 100},
	{headerName: "속성값 타입", field: "type", width: 100},
	{headerName: "속성값", field: "value", filter: 'text', width: 100},
	{headerName: "비고", field: "remark", filter: 'text', width: 100},
	{headerName: "변경일시", field: "altDate", filter: 'text', width: 100},
	{headerName: "등록일시", field: "regDate", filter: 'text', width: 100}
];


/**
 * 인스턴스 목록 그리드 컬럼 선언
 */
var instanceColumnDefs = [
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

//var searchAdaptaModal=[
//    {headerName: "아답터ID", field: "adapterId",width:538}             
//];

var searchDevicePoolModal=[
    {headerName: "장치풀ID", field: "id",width:538}
];

var searchDeviceModal=[   
    {headerName: "디폴트장치ID", field: "id",width:538}
];

var instanceAttColumnDefs= [
	{headerName: " ", filter: 'text', width: 30,checkboxSelection: true},          
	{headerName: "속성 키", field: "att",width:480}
];

var instanceFuncColumnDefs= [
	{headerName: " ", filter: 'text', width: 30,checkboxSelection: true},          
	{headerName: "기능 키", field: "adaptFunction",width:480}                         
];
//var searchDataColumnDefs = [
//	{headerName: "ID", field: "adapterId"}
//];
//
//var searchDevicePoolDataColumnDefs = [
//	{headerName: "장치풀ID", field: "id"}
//];

//Variable
var devPoolJsonData = null;

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
		}
		
		
		/* Normal Functions */
		function rowSelectedFunc(event) { //Instance List Click Event Function
			clearFunctionData();
			clearAttributeData();
			
			console.log(event.node.data);
			selectedInstanceId = event.node.data.instanceId;			
			selectedInstanceName = event.node.data.instanceName; 
			selectedInstanceType = event.node.data.instanceType;
			selectedInstanceKind = event.node.data.instanceKind;
			selectedInstanceInitDevStatus = event.node.data.initDeviceStatus;
			selectedInstanceIsUse = event.node.data.isUse;			
			selectedInstanceIp = event.node.data.ip;
			selectedInstancePort = event.node.data.port;
			selectedInstanceLatitude = event.node.data.lat;
			selectedInstanceLongitude = event.node.data.lon;
			selectedDeviceId = event.node.data.defaultDeviceId;
			selectedAdapterId = event.node.data.adapterId;
			selectedDevicePoolId = event.node.data.devicePoolId;
			selectedInstanceUrl=event.node.data.url;
			selectedInstanceSessionTimeout=event.node.data.sessionTimeout;
			selectedInstanceSelfId=event.node.data.selfId;
			selectedInstanceSelfPw=event.node.data.selfPw;
			selectedInstanceRemark=event.node.data.remark;

			//함수 기능을 호출 하는데
			setFunctionData(selectedInstanceId);
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
			clearInstanceData();
			clearFunctionData();
			clearAttributeData();
			selectedAdapterId = event.node.data.adapterId;
			selectedAdapterKind=event.node.data.adapterKind;
			selectedAdapterDescription= event.node.data.description;
			selectedAdapterHyperink=event.node.data.hyperlink;			
	
			setInstanceData(selectedAdapterId);
			
		}
		
	});

	agGridModule.controller("attributeCtrl", function($scope) {

		attributeScope = $scope;

		$scope.gridOptions = {
			columnDefs: attributeColumnDefs,
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
			var aKey = event.node.data.key;
			var aNm = event.node.data.description;
			var aTp = event.node.data.type;
			var aVal = event.node.data.value;
			var aRmrk = event.node.data.remark;

			selectedAttributeKey = aKey;
			selectedAttributeName = aNm;
			selectedAttributeType = aTp;
			selectedAttributeValue = aVal;
			selectedAttributeRemark = aRmrk;
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
		function rowSelectedFunc(event) { //Session List Click Event Function

			var fKey = event.node.data.key;
			var fNm = event.node.data.name;
			var fTp = event.node.data.type;
			var fRmrk = event.node.data.remark;
			var fPrm1 = event.node.data.param1;
			var fPrmTp1 = event.node.data.paramTp1;
			var fPrm2 = event.node.data.param2;
			var fPrmTp2 = event.node.data.paramTp2;
			var fPrm3 = event.node.data.param3;
			var fPrmTp3 = event.node.data.paramTp3;
			var fPrm4 = event.node.data.param4;
			var fPrmTp4 = event.node.data.paramTp4;
			var fPrm5 = event.node.data.param5;
			var fPrmTp5 = event.node.data.paramTp5;
			
			selectedFunctionKey = fKey;
			selectedFunctionName = fNm;
			selectedFunctionType = fTp;
			selectedFunctionRemark = fRmrk;
			selectedFunctionParam1 = fPrm1;
			selectedFunctionParamType1 = fPrmTp1;
			selectedFunctionParam2 = fPrm2;
			selectedFunctionParamType2 = fPrmTp2;
			selectedFunctionParam3 = fPrm3;
			selectedFunctionParamType3 = fPrmTp3;
			selectedFunctionParam4 = fPrm4;
			selectedFunctionParamType4 = fPrmTp4;
			selectedFunctionParam5 = fPrm5;
			selectedFunctionParamType5 = fPrmTp5;

		}

	});

//	agGridModule.controller("searchCtrl", function($scope) {
//
//		searchScope = $scope;
//
//		$scope.gridOptions = {
//			columnDefs: searchDataColumnDefs,
//			rowSelection: 'single',
//			enableColResize: true,
//			onRowSelected: rowSelectedFunc,
//			suppressSizeToFit: true,
//			rowData: null,
//		};
//
//		/* Normal Functions */
//		function rowSelectedFunc(event) { //Session List Click Event Function
//			var id = event.node.data.adapterId;
//			selectedSearchId = id;
//
//			console.log(selectedSearchId);
//			clearSearchDeviceData();	
//			if(adapterSearchFlag) document.getElementById("instanceAId").value = selectedSearchId;
//			else if(devPoolSearchFlag){ 
//				document.getElementById("instanceDPId").value = selectedSearchId; 
//	clearSearchDeviceData();	
//				setSearchDeviceData(selectedSearchId);
//			}
//		}
//
//	});
	
//	agGridModule.controller("adapterSearchCtrl", function($scope) {
//
//		adaptaSearchScope = $scope;
//
//		$scope.gridOptions = {
//			columnDefs: searchAdaptaModal,
//			rowSelection: 'single',
//			enableColResize: true,
//			onRowSelected: rowSelectedFunc,
//			rowData: null
//		};
//
//		/* Normal Functions */
//		function rowSelectedFunc(event) { //Session List Click Event Function
//			var id = event.node.data.adapterId;
//			selectedSearchId = id;
//			
//		};
//	});

//	agGridModule.controller("searchDevGrid", function($scope) {
//
//		searchDevScope = $scope;
//
//		$scope.gridOptions = {
//			columnDefs: searchDataColumnDefs,
//			rowSelection: 'single',
//			enableColResize: true,
//			onRowSelected: rowSelectedFunc,
//			suppressSizeToFit: true,
//			rowData: null,
//		};
//
//		/* Normal Functions */
//		function rowSelectedFunc(event) { //Session List Click Event Function
//			var id = event.node.data.id;
//			selectedSearchDevId = id;instanceSearchDevicePool
//
//			document.getElementById("instanceDId").value = selectedSearchDevId;
//		}
//
//	});

	agGridModule.controller("devicePoolSearchCtrl", function($scope) {

		searchDevPoolScope = $scope;

		$scope.gridOptions = {
			columnDefs: searchDevicePoolModal,
			rowSelection: 'single',
			enableColResize: true,
			onRowSelected: rowSelectedFunc,
			rowData: null
		};

		/* Normal Functions */
		function rowSelectedFunc(event) { //Session List Click Event Function
			var id = event.node.data.id;
			selectedSearchDevPoolId = id;
			
		}

	});
	
	agGridModule.controller("deviceSearchCtrl", function($scope) {

		searchDevScope = $scope;

		$scope.gridOptions = {
			columnDefs: searchDeviceModal,
			rowSelection: 'single',
			enableColResize: true,
			onRowSelected: rowSelectedFunc,
			rowData: null
		};

		/* Normal Functions */
		function rowSelectedFunc(event) { //Session List Click Event Function
			var id = event.node.data.id;
			selectedSearchDevId = id;

		}

	});

	agGridModule.controller("instanceFuncCtrl", function($scope) {

		instanceFuncScope = $scope;

		$scope.gridOptions = {
		    columnDefs: instanceFuncColumnDefs,
		    rowData: null,
		    enableSorting: true,
			enableColResize: true,
		    rowSelection: 'multiple',
			onRowSelected: rowSelectedFunc,			
			onRowDeselected: rowDeselectedFunc,
		    suppressRowClickSelection: true

		};
		
		
		function rowDeselectedFunc(event){
			
			var deSelectID=event.node.data.adaptFunction;
			for(var i=0; i<arrayInstanceFuncKey.length; i++){
				if(deSelectID==arrayInstanceFuncKey[i]){
					arrayInstanceFuncKey.splice(i,1);	break;
				}
			}						
			arrayInstanceFuncKey.pop();
		}
		
		/* Normal Functions */
		function rowSelectedFunc(event) { //attribute List Click Event Function

			arrayInstanceFuncKey.push(event.node.data.adaptFunction);
		}

	});

	agGridModule.controller("instanceAttCtrl", function($scope) {

		instanceAttScope = $scope;

		$scope.gridOptions = {

			columnDefs: instanceAttColumnDefs,
		    rowData: null,
		    enableSorting: true,
			enableColResize: true,
		    rowSelection: 'multiple',
			onRowSelected: rowSelectedFunc,			
			onRowDeselected: rowDeselectedFunc,
		    suppressRowClickSelection: true
		};
		
		function rowDeselectedFunc(event){
			
			var deSelectID=event.node.data.att;
			for(var i=0; i<arrayInstanceAttKey.length; i++){
				if(deSelectID==arrayInstanceAttKey[i]){
					arrayInstanceAttKey.splice(i,1);	break;
				}
			}						
			arrayInstanceAttKey.pop();
		}
		
		/* Normal Functions */
		function rowSelectedFunc(event) { //attribute List Click Event Function
			
			arrayInstanceAttKey.push(event.node.data.att);
		}
	});

	/******************************* CONTROLLERS END ******************************/

	/******************************* API SCRIPTS START ******************************/
	
	function initGetData() {
	  setAdapterData();
    }
	
	/* Call backs */
	function cbLoginSucessFunc(evt){// 로그인 성공 처리
		initGetData();
	}

	function cbLoginFailFunc(evt){// 로그인 실패 처리
		console.log('로그인 실패')
		commonErrorMessage(evt);
	}

	function cbAdtGetAllSucessFunc(evt){// 아답터 목록 조회 성공
		console.log('아답터 조회 성공');
		var rowDataByJson = parseAdapterJsonData(evt);
		
		
		
		loadData(adapterScope,rowDataByJson);
		
	}

	function cbAdtGetAllFailFunc(evt){// 아답터 목록 조회 실패
		console.log('아답터 조회 실패');
		clearAdapterData();
		commonErrorMessage(evt);
	}

	function cbSrchAdtGetAllSucessFunc(evt){// 아답터 목록 조회 성공(검색)
		console.log('아답터 조회 성공');
		var rowDataByJson = parseAdapterJsonData(evt);
		loadData(adaptaSearchScope,rowDataByJson);
	}

	
	function cbSrchAdtGetAllFailFunc(evt){// 아답터 목록 조회 실패(검색)
		console.log('아답터 조회 실패');
		commonErrorMessage(evt);
	}
	
	function cbSrchDevPoolGetAllSucessFunc(evt){// 장치풀 목록 조회 성공(검색)
		console.log('장치풀 조회 성공');
		var rowDataByJson = parseDevicePoolJsonData(evt);
		loadData(searchDevPoolScope,rowDataByJson);
	}

	function cbSrchDevPoolGetAllFailFunc(evt){// 장치풀 목록 조회 실패(검색)
		console.log('장치풀 조회 실패');
		commonErrorMessage(evt);
	}

	function cbSrchDevSearchByDpidSucessFunc(evt){// 장치 목록 조회 성공(검색)
		console.log('장치 조회 성공');
		$("#deviceSearchModal").modal("show");
		var rowDataByJson = parseDeviceJsonData(evt); //장치데이터
		loadData(searchDevScope,rowDataByJson);
	}

	function cbSrchDevSearchByDpidFailFunc(evt){// 장치 목록 조회 실패(검색)
		console.log('장치 조회 실패');		
		$("#deviceSearchModal").modal("hide");
		
		if($("#instanceDPId").val()=="")	commonAlert("알림","장치풀을 먼저 선택해주세요.");
		else	commonErrorMessage(evt);
	}

	function cbInsSearchByAidSucessFunc(evt){// 인스턴스 목록 조회 성공
		var rowDataByJson = parseInstanceJsonData(evt);
		loadData(instanceScope,rowDataByJson);
		$(".blankInsInstance").text("");
	}

	function cbInsSearchByAidFailFunc(evt){// 인스턴스 목록 조회 실패
		clearInstanceData();
		commonErrorMessage(evt,"blankInsInstance");
	}
	
	function cbInsFuncSearchByIidSucessFunc(evt){	// iid로 인스턴스 기능 목록 조회 성공
		console.log("인스턴스기능 목록 조회 성공");
		setAttributeData(selectedInstanceId);//속성값 설정
		var rowDataByJson = parseFunctionJsonData(evt);
		loadData(functionScope,rowDataByJson);
		$(".blankInsFunc").text("");
	}

	//윤영진 수정 해야할 위치
	function cbInsFuncSearchByIidFailFunc(evt){	// iid로 인스턴스 기능 목록 조회 실패
		console.log("인스턴스기능 목록 조회 실패");
		setAttributeData(selectedInstanceId);//속성값 설정
		var errorCode = evt.msgCode;
		commonErrorMessage(evt,"blankInsFunc");
	}
	
	function cbInsAttSearchByIidSucessFunc(evt){// iid로 인스턴스속성 목록 조회 성공
		console.log("인스턴스속성 목록 조회 성공");
		var rowDataByJson = parseAttributeJsonData(evt);
		loadData(attributeScope,rowDataByJson);
		$(".blankInsAtt").text("");
	}
	
	//윤영진 수정 해야할 위치
	function cbInsAttSearchByIidFailFunc(evt){	// iid로 인스턴스속성 목록 조회 실패
		console.log("인스턴스속성 목록 조회 실패");
		commonErrorMessage(evt, "blankInsAtt");
	}

	function cbCloseFunc(evt){// 웹소켓 종료 이벤트 --> 로그아웃 처리
		/*alert("로그아웃됨");*/
		commonAlert("알림","로그아웃 되었습니다.","웹소켓 종료 이벤트 -> 로그아웃 처리");
	}
	
	function cbAdtFunctionGetSucessFunc(evt){// 아답터 기능 목록 조회 성공
		console.log('아답터 기능 목록 조회 성공');
		setAdapterAttData(selectedAdapterId);
		var rowDataByJson = parseAdapterFunctionJsonData(evt);
		loadData(instanceFuncScope,rowDataByJson);
		$(".blankAdtFunc").text("");
	}

	function cbAdtFunctionGetFailFunc(evt){// 아답터 기능 목록 조회 실패
		setAdapterAttData(selectedAdapterId);
		console.log('아답터 기능 조회 실패');
		commonErrorMessage(evt,"blankAdtFunc");	
	}
	function cbAdtAttGetSucessFunc(evt){// 아답터 속성 목록 조회 성공
		console.log('아답터 속성 목록 성공');
		var rowDataByJson = parseAdapterAttributeJsonData(evt);
		loadData(instanceAttScope,rowDataByJson);
		$(".blankAdtAtt").text("");
	}

	function cbAdtAttGetFailFunc(evt){// 아답터 속성 목록 조회 실패
		console.log('아답터 속성 조회 실패');
		commonErrorMessage(evt,"blankAdtAtt");
	}
	/******************************* API SCRIPTS END ******************************/

	/* Clear Datas */
	function clearAdapterData(){ //Clear datas to grid(adapter)
		selectedAdapterId = null;
		selectedAdapterKind=null;
		/*var emptyRow = [{ id: '' }];*/	
		var emptyRow = [];	
		adapterScope.gridOptions.api.setRowData(emptyRow);
		clearInstanceData();
		
	}

	function clearInstanceData(){ //Clear datas to grid(instance)
		selectedInstanceId = null;
		selectedInstanceName = null; 
		selectedInstanceType = null; 
		selectedInstanceKind = null; 
		selectedInstanceInitDevStatus = null;
		selectedInstanceIsUse = null;
		selectedInstanceIp = null;
		selectedInstancePort = null;
		selectedInstanceLatitude = null;
		selectedInstanceLongitude = null;
		selectedDeviceId = null; 
		selectedDevicePoolId = null; 

		/*var emptyRow = [{ id: '' }];*/	
		var emptyRow = [];	
		instanceScope.gridOptions.api.setRowData(emptyRow);
		instanceFuncScope.gridOptions.api.setRowData(emptyRow);
		instanceAttScope.gridOptions.api.setRowData(emptyRow);
		clearFunctionData();
		clearAttributeData();
	}
	function clearFunctionData(){ //Clear datas to grid(function) 

		selectedFunctionKey = null;
		selectedFunctionName = null;
		selectedFunctionType = null;
		selectedFunctionRemark = null;
		selectedFunctionParam1 = null;
		selectedFunctionParamType1 = null;
		selectedFunctionParam2 = null;
		selectedFunctionParamType2 = null;
		selectedFunctionParam3 = null;
		selectedFunctionParamType3 = null;
		selectedFunctionParam4 = null;
		selectedFunctionParamType4 = null;
		selectedFunctionParam5 = null;
		selectedFunctionParamType5 = null;			
		
		var emptyRow = [];
		functionScope.gridOptions.api.setRowData(emptyRow);
		$(".blankInsFunc").text("");
	}

	function clearAttributeData(){ //Clear datas to grid(attribute) 

		selectedAttributeKey = null;
		selectedAttributeName = null;
		selectedAttributeType = null;
		selectedAttributeValue = null;
		selectedAttributeRemark = null;

		/*var emptyRow = [{ id: '' }];*/
		var emptyRow = [];	
		attributeScope.gridOptions.api.setRowData(emptyRow);	
		$(".blankInsAtt").text("");
	}
	
//	function clearSearchAdapterData(){ //Clear datas to grid(search adapter) 
//		selectedSearchId = null;
//		/*var emptyRow = [{ id: '' }];*/	
//		var emptyRow = [];
//		adaptaSearchScope.gridOptions.api.setRowData(emptyRow);
//	}

	function clearSearchDevicePoolData(){ //Clear datas to grid(search device pool) 
		selectedSearchId = null;
		/*var emptyRow = [{ id: '' }];*/	
		var emptyRow = [];	
		searchDevPoolScope.gridOptions.api.setRowData(emptyRow);
	}

	function clearSearchDeviceData(){ //Clear datas to grid(search device pool) 
		/*var emptyRow = [{ id: '' }];*/

		var emptyRow = [];	
		searchDevScope.gridOptions.api.setRowData(emptyRow);
	}
	/* Init */
	//initAPI(); //Login and Get Data Progress
	
	var form_mode = null; //PUT SET 폼 모드
	$(document).ready(function() {
		/**
		 * Instance form
		 */
		//전체 화면에 있는 + 버튼을 눌렀을때  나오는 팝업메뉴의 화면 중 콤보박스 선택 스크립트
		$(".dropdown-menu li a").click(function(){
			
			 var selText = $(this).text();	 
			 $(this).parents('.btn-group').find('.dropdown-toggle').html(selText+'<span class="caret"></span>');
		});
		
		$("#addInstance").click(function() {
			arrayInstanceAttKey=new Array();
			arrayInstanceFuncKey=new Array();
			if(selectedAdapterId == null){
			  /* alert("아답터를 선택 해 주세요"); */
			  /*bootbox.alert("아답터를 선택 해 주세요.", function(){
				  console.log("Not selected Adapt");
			  });*/
			  commonAlert("알림","아답터를 선택 해 주세요.","아답터를 선택하지 않음");
			  return false;
			}else{

				addAndEditInstanceInit();
				
				$("#instanceAddFuncAttGrid").show();
				
				if(selectedAdapterId!=null){
					setAdapterFunctionData(selectedAdapterId);	
				}
				selectedSearchDevPoolId=null;
				adapterIntro();
				
				
				form_mode = 'put';
			    
			    $(".myModalLabel").text("인스턴스 등록");
			    $(".popUpDefaultBtn").text("등록");
			    
			    document.getElementById("instanceId").readOnly = false;			
//				$("#instanceDId").addClass("readonlyStyle");  
				document.getElementById("instanceDId").readOnly = true;
			    
				document.getElementById("instanceId").value = "";
				document.getElementById("instanceName").value = "";				
				$("#instanceKind").parents('.btn-group').find('.dropdown-toggle').html(selectedAdapterKind+'<span class="caret"></span>');
				$("#instanceType").parents('.btn-group').find('.dropdown-toggle').html("Type"+'<span class="caret"></span>');
				$("#instanceInitDevStatus").parents('.btn-group').find('.dropdown-toggle').html("State"+'<span class="caret"></span>');
//				$("#instanceIsUse").parents('.btn-group').find('.dropdown-toggle').html("Retry"+'<span class="caret"></span>');				
				document.getElementById("instanceIp").value = "";
				document.getElementById("instancePort").value = "";
				document.getElementById("instanceLatitude").value = "";
				document.getElementById("instanceLongitude").value = "";
				document.getElementById("instanceAId").value = selectedAdapterId;
				document.getElementById("instanceDPId").value = "";
				document.getElementById("instanceDId").value = "";	
				document.getElementById("instanceSessionTimeout").value="";
				document.getElementById("instanceURL").value="";
				document.getElementById("instanceSelfId").value="";
				document.getElementById("instanceSelfPassword").value="";
				document.getElementById("instanceRemark").value="";
			}
		});
		$("#editInstance").click(function() {
			if(selectedInstanceId == null){
			  /* alert("인스턴스를 선택 해 주세요"); */
			  /*bootbox.alert("인스턴스를 선택 해 주세요.", function(){
					console.log("Not selected Instance");
				});*/
				commonAlert("알림","인스턴스를 선택 해 주세요.","인스턴스를 선택하지 않음");
				
			  return false;
			}else{

				addAndEditInstanceInit();
				adapterIntro();
				
				arrayInstanceAttKey=new Array();
				arrayInstanceFuncKey=new Array();
				
				$("#instanceAddFuncAttGrid").hide();
			  form_mode = 'set';
			  
			    $(".myModalLabel").text("인스턴스 수정");
			    $(".popUpDefaultBtn").text("수정");

				selectedSearchDevPoolId=selectedDevicePoolId;
				
//				$("#instanceDId").removeClass("readonlyStyle");
				
				
				selectedInstanceId=html5SpecialCharDeCode(selectedInstanceId);
				selectedInstanceName=html5SpecialCharDeCode(selectedInstanceName);
				selectedInstanceKind=html5SpecialCharDeCode(selectedInstanceKind);
				selectedFunctionType=html5SpecialCharDeCode(selectedFunctionType);
				selectedInstanceSessionTimeout=html5SpecialCharDeCode(selectedInstanceSessionTimeout);
				selectedInstanceUrl=html5SpecialCharDeCode(selectedInstanceUrl);
				selectedInstanceSelfId=html5SpecialCharDeCode(selectedInstanceSelfId);
				selectedInstanceSelfPw=html5SpecialCharDeCode(selectedInstanceSelfPw);
				
				selectedInstanceRemark=html5SpecialCharDeCode(selectedInstanceRemark);
				
				selectedInstanceIsUse=html5SpecialCharDeCode(selectedInstanceIsUse);
				selectedInstanceIp=html5SpecialCharDeCode(selectedInstanceIp);
				selectedInstancePort=html5SpecialCharDeCode(selectedInstancePort);
				selectedInstanceLatitude=html5SpecialCharDeCode(selectedInstanceLatitude);
				selectedInstanceLongitude=html5SpecialCharDeCode(selectedInstanceLongitude);
				selectedAdapterId=html5SpecialCharDeCode(selectedAdapterId);
				selectedDevicePoolId=html5SpecialCharDeCode(selectedDevicePoolId);
				selectedDeviceId=html5SpecialCharDeCode(selectedDeviceId);
				selectedInstanceIp;
				
//				document.getElementById("instanceDId").readOnly = false;
			    document.getElementById("instanceId").readOnly = true;
				document.getElementById("instanceId").value = selectedInstanceId;
				document.getElementById("instanceName").value = selectedInstanceName;
				/*$("#instanceType").parents('.btn-group').find('.dropdown-toggle').html(selectedInstanceType+'<span class="caret"></span>');*/
				$("#instanceKind").parents('.btn-group').find('.dropdown-toggle').html(selectedInstanceKind+'<span class="caret"></span>');
				/*$("#instanceInitDevStatus").parents('.btn-group').find('.dropdown-toggle').html(selectedInstanceInitDevStatus+'<span class="caret"></span>');*/
				$("#instanceIsUse").parents('.btn-group').find('.dropdown-toggle').html(selectedInstanceIsUse+'<span class="caret"></span>');

				document.getElementById("instanceSessionTimeout").value=selectedInstanceSessionTimeout;
				document.getElementById("instanceURL").value=selectedInstanceUrl;
				document.getElementById("instanceSelfId").value=selectedInstanceSelfId;
				document.getElementById("instanceSelfPassword").value=selectedInstanceSelfPw;
				document.getElementById("instanceRemark").value=selectedInstanceRemark;
				
				document.getElementById("instanceIp").value = selectedInstanceIp;
				document.getElementById("instancePort").value = selectedInstancePort;
				document.getElementById("instanceLatitude").value = selectedInstanceLatitude;
				document.getElementById("instanceLongitude").value = selectedInstanceLongitude;
				document.getElementById("instanceAId").value = selectedAdapterId;
				document.getElementById("instanceDPId").value = selectedDevicePoolId;
				document.getElementById("instanceDId").value = selectedDeviceId;
			}
		});
		$("#delInstance").click(function() {
			if(selectedInstanceId == null){
			  /* alert("인스턴스를 선택 해 주세요"); */
			  /*bootbox.alert("인스턴스를 선택 해 주세요.", function(){
					console.log("Not selected Instance");
			  });*/
				commonAlert("알림","인스턴스를 선택 해 주세요.","인스턴스를 선택하지 않음");
			  return false;
			}else{
				commonConfirm("알림","삭제 하시겠습니까?",delInstanceDatas,selectedInstanceId);
//				delInstanceDatas(selectedInstanceId);
			}
		});		
		
		/**
		 * Function form
		 */
		$("#addInstanceFunction").click(function() {
			if(selectedInstanceId == null){
			  /* alert("인스턴스를 선택 해 주세요"); */
			  /*bootbox.alert("인스턴스를 선택 해 주세요.", function(){
					console.log("Not selected Instance");
			  });*/
				commonAlert("알림","인스턴스를 선택 해 주세요.","인스턴스를 선택하지 않음");

			  return false;
			}else{
			  form_mode = 'put';
			  
			  $(".myModalLabel").text("기능 등록");
			  $(".popUpDefaultBtn").text("등록");

			  /*$("#funtionInstanceId").val(selectedInstanceId);*/
			  document.getElementById("instanceFuncKey").readOnly = false;
			  document.getElementById("instanceFuncKey").value = "";
			  document.getElementById("instanceFuncName").value = "";
			  document.getElementById("instanceFuncType").value = "";
			  document.getElementById("instanceFuncRemark").value = "";
			  document.getElementById("instanceFuncParNm1").value = "";
			  $("#instanceFuncParTp1").parents('.btn-group').find('.dropdown-toggle').html("타입"+'<span class="caret"></span>');
			  document.getElementById("instanceFuncParNm2").value = "";
			  $("#instanceFuncParTp2").parents('.btn-group').find('.dropdown-toggle').html("타입"+'<span class="caret"></span>');
			  document.getElementById("instanceFuncParNm3").value = "";
			  $("#instanceFuncParTp3").parents('.btn-group').find('.dropdown-toggle').html("타입"+'<span class="caret"></span>');
			  document.getElementById("instanceFuncParNm4").value = "";
			  $("#instanceFuncParTp4").parents('.btn-group').find('.dropdown-toggle').html("타입"+'<span class="caret"></span>');
			  document.getElementById("instanceFuncParNm5").value = "";
			  $("#instanceFuncParTp5").parents('.btn-group').find('.dropdown-toggle').html("타입"+'<span class="caret"></span>');

			}
		});
		$("#editInstanceFunction").click(function() {
			if(selectedFunctionKey == null){
			  commonAlert("알림","기능을 선택 해 주세요.","기능을 선택하지 않음");
			  return false;
			}else{
			  form_mode = 'set';			
			  
			  $(".myModalLabel").text("기능 수정");
			  $(".popUpDefaultBtn").text("수정");
			  
			  selectedFunctionKey=html5SpecialCharDeCode(selectedFunctionKey);
			  selectedFunctionName=html5SpecialCharDeCode(selectedFunctionName);
			  selectedFunctionType=html5SpecialCharDeCode(selectedFunctionType);
			  selectedFunctionRemark=html5SpecialCharDeCode(selectedFunctionRemark);
			  selectedFunctionParam1=html5SpecialCharDeCode(selectedFunctionParam1);
			  selectedFunctionParam2=html5SpecialCharDeCode(selectedFunctionParam2);
			  selectedFunctionParam3=html5SpecialCharDeCode(selectedFunctionParam3);
			  selectedFunctionParam4=html5SpecialCharDeCode(selectedFunctionParam4);
			  selectedFunctionParam5=html5SpecialCharDeCode(selectedFunctionParam5);


			  /*$("#funtionInstanceId").val(selectedInstanceId);*/
			  document.getElementById("instanceFuncKey").readOnly = true;
			  document.getElementById("instanceFuncKey").value = selectedFunctionKey;
			  document.getElementById("instanceFuncName").value = selectedFunctionName;
			  document.getElementById("instanceFuncType").value = selectedFunctionType;
			  document.getElementById("instanceFuncRemark").value = selectedFunctionRemark;
			  document.getElementById("instanceFuncParNm1").value = selectedFunctionParam1;
			  $("#instanceFuncParTp1").parents('.btn-group').find('.dropdown-toggle').html(selectedFunctionParamType1+'<span class="caret"></span>');
			  $("#instanceFuncParTp2").parents('.btn-group').find('.dropdown-toggle').html(selectedFunctionParamType2+'<span class="caret"></span>');
			  $("#instanceFuncParTp3").parents('.btn-group').find('.dropdown-toggle').html(selectedFunctionParamType3+'<span class="caret"></span>');
			  $("#instanceFuncParTp4").parents('.btn-group').find('.dropdown-toggle').html(selectedFunctionParamType4+'<span class="caret"></span>');
			  $("#instanceFuncParTp5").parents('.btn-group').find('.dropdown-toggle').html(selectedFunctionParamType5+'<span class="caret"></span>');
//			  $("instanceFuncParTp1").text(selectedFunctionParamType1);
//			  $("instanceFuncParTp1").text(selectedFunctionParamType2);
//			  $("instanceFuncParTp1").text(selectedFunctionParamType3);
//			  $("instanceFuncParTp1").text(selectedFunctionParamType4);
//			  $("instanceFuncParTp1").text(selectedFunctionParamType5);
//			  document.getElementById("instanceFuncParTp1").value = selectedFunctionParamType1;
			  document.getElementById("instanceFuncParNm2").value = selectedFunctionParam2;
//			  document.getElementById("instanceFuncParTp2").value = selectedFunctionParamType2;
			  document.getElementById("instanceFuncParNm3").value = selectedFunctionParam3;
//			  document.getElementById("instanceFuncParTp3").value = selectedFunctionParamType3;
			  document.getElementById("instanceFuncParNm4").value = selectedFunctionParam4;
//			  document.getElementById("instanceFuncParTp4").value = selectedFunctionParamType4;
			  document.getElementById("instanceFuncParNm5").value = selectedFunctionParam5;
//			  document.getElementById("instanceFuncParTp5").value = selectedFunctionParamType5;

			}
		});
		$("#delInstanceFunction").click(function() {
			if(selectedFunctionKey == null){
			  commonAlert("알림","기능을 선택 해 주세요.","기능을 선택하지 않음");

			  return false;
			}else{
				commonConfirm("알림","삭제 하시겠습니까?",delInstanceFuncDatas,selectedInstanceId,selectedFunctionKey);
			}
		});
		
		/**
		 * Attribute form
		 */
		$("#addInstanceAttribute").click(function() {
			if(selectedInstanceId == null){
				commonAlert("알림","인스턴스를 선택 해 주세요.","인스턴스를 선택하지 않음");
			  return false;
			}else{
			  form_mode = 'put';			  
			  
			  $(".myModalLabel").text("속성 등록");
			  $(".popUpDefaultBtn").text("등록");

			  /*$("#attributeInstanceId").val(selectedInstanceId);*/

			  document.getElementById("instanceAttrKey").readOnly = false;
			  document.getElementById("instanceAttrKey").value = "";
			  document.getElementById("instanceAttrName").value = "";
			  document.getElementById("instanceAttrValueType").value = "";
			  document.getElementById("instanceAttrValue").value = "";
			  document.getElementById("instanceAttrRemark").value = ""; 

			}
		});
		$("#editInstanceAttribute").click(function() {
			if(selectedAttributeKey == null){
		
				commonAlert("알림","속성을 선택 해 주세요.","속성을 선택하지 않음");

			  return false;
			}else{
			  form_mode = 'set';			  
			  $(".myModalLabel").text("속성 수정");
			  $(".popUpDefaultBtn").text("수정");
			  

			  selectedAttributeKey=html5SpecialCharDeCode(selectedAttributeKey);
			  selectedAttributeName=html5SpecialCharDeCode(selectedAttributeName);
			  selectedAttributeType=html5SpecialCharDeCode(selectedAttributeType);
			  selectedAttributeValue=html5SpecialCharDeCode(selectedAttributeValue);
			  selectedAttributeRemark=html5SpecialCharDeCode(selectedAttributeRemark);

			  /*$("#attributeInstanceId").val(selectedInstanceId);*/
			  document.getElementById("instanceAttrKey").readOnly = true;
			  document.getElementById("instanceAttrKey").value = selectedAttributeKey;
			  document.getElementById("instanceAttrName").value = selectedAttributeName;
			  document.getElementById("instanceAttrValueType").value = selectedAttributeType;
			  document.getElementById("instanceAttrValue").value = selectedAttributeValue;
			  document.getElementById("instanceAttrRemark").value = selectedAttributeRemark; 

			}
		});
		$("#delInstanceAttribute").click(function() {
			if(selectedAttributeKey == null){
				commonAlert("알림","속성을 선택 해 주세요.","속성을 선택하지 않음");
			  return false;
			}else{
				commonConfirm("알림","삭제 하시겠습니까?",delInstanceAttrDatas,selectedInstanceId,selectedAttributeKey);
			}
		});
	});

	function checkSid(){
		if(selectedSessionId == null){
		  commonAlert("알림","세션을 선택 해 주세요.","세션을 선택하지 않음");
		}else{
		  sessionDisconnect(selectedSessionId);
		}
	}
	
	function refreshAdapterList(){//아답터목록 새로고침
		  clearAdapterData();
		  initGetData();
	}
	
	function refreshInstanceList(){//인스턴스목록 새로고침
		if(selectedAdapterId == null){
			commonAlert("알림","아답터를 선택 해 주세요.","아답터를 선택하지 않음");
		  return false;
		}else{
		  clearInstanceData();
		  setInstanceData(selectedAdapterId);
		}
	}

	function refreshFunctionList(){//기능목록 새로고침
		if(selectedInstanceId == null){
			commonAlert("알림","인스턴스를 선택 해 주세요.","인스턴스를 선택하지 않음");
		  return false;
		}else{
		  clearFunctionData();
		  setFunctionData(selectedInstanceId);
		}
	}

	function refreshAttributeList(){//속성목록 새로고침
		if(selectedInstanceId == null){
			commonAlert("알림","인스턴스를 선택 해 주세요.","인스턴스를 선택하지 않음");
		  return false;
		}else{
		  clearAttributeData();
		  setAttributeData(selectedInstanceId);
		}
	}
	
	
	function showToolPanel(scope){ //옆에있는 새로고침과 설정 버튼인데 아답터명과 ID가 나옴
		if(!scope.gridOptions.api.isToolPanelShowing()){
			scope.gridOptions.api.showToolPanel(true);
			//scope.gridOptions.api.sizeColumnsToFit();
		}else{
			scope.gridOptions.api.showToolPanel(false);
			//scope.gridOptions.api.sizeColumnsToFit();
		}
	}

	var adapterSearchFlag = false;
	var devPoolSearchFlag = false;

//	function showSearchAdapterList(){
//		clearSearchAdapterData();
//		setSearchAdapterData();
		

//		setTimeScope(adaptaSearchScope,searchAdaptaModal);
		
		
//		clearSearchDevicePoolData();
//		setSearchAdapterData(); //아답터ID 조회
//		devPoolSearchFlag = false;
//		adapterSearchFlag = true;
//		searchScope.gridOptions.api.setColumnDefs(searchDataColumnDefs);
//		searchScope.gridOptions.api.sizeColumnsToFit();
//	}
	
	function showSearchDevicePoolList(){
		clearSearchDevicePoolData();
		setSearchDevicePoolData();

//		setTimeScope(searchDevPoolScope,searchDevicePoolModal);
//		setTimeScope(searchDevScope,searchDeviceModal);

		//		clearSearchAdapterData();
//		setSearchDevicePoolData();//장치풀ID 조회
//		devPoolSearchFlag = true;
//		adapterSearchFlag = false;
//		searchScope.gridOptions.api.setColumnDefs(searchDevicePoolDataColumnDefs);
//		searchScope.gridOptions.api.sizeColumnsToFit();
	}
	
	function showSearchDeviceList(){
		clearSearchDeviceData();	
		setSearchDeviceData(selectedSearchDevPoolId);		
	}

	function searchAdapterEvent(){
		
		document.getElementById("instanceAId").value = selectedSearchId;			
		selectedAdapterId=selectedSearchId;
		setAdapterFunctionData(selectedAdapterId);		
		
	}
	function searchDevicePoolEvent(){
		
		document.getElementById("instanceDPId").value = selectedSearchDevPoolId;
	}
	
	function searchDeviceEvent(){
	
		document.getElementById("instanceDId").value = selectedSearchDevId;
	}
	
	function updateInstance(mode){
		
		var nowDate = Date.now();
		
		nowDate = formatTime(nowDate);

		var iId = document.getElementById("instanceId").value;
		var name = document.getElementById("instanceName").value;
		
		var kind = $("#instanceKind").text();
		var type = $("#instanceType").text();
		var stat = $("#instanceInitDevStatus").text();
		var isUse = $("#instanceIsUse").text();
		
		if(kind=="Kind"||kind=="none"){
			kind="";
		}if(type=="Type"||type=="none"){
			type="";
		}if(stat=="State"||stat=="none"){
			stat="";
		}if(isUse=="Restart"||isUse=="none"){
			isUse="";			
		}
		
		var sessionTimeout=document.getElementById("instanceSessionTimeout").value;

		if(sessionTimeout==""){
			console.log(sessionTimeout);
			sessionTimeout="0";
		}
		
		var url=document.getElementById("instanceURL").value;
		var selfId=document.getElementById("instanceSelfId").value;
		var selfPw=document.getElementById("instanceSelfPassword").value;
		var remark=document.getElementById("instanceRemark").value;
		
//		var kind = document.getElementById("instanceKind").value;
//		var type = document.getElementById("instanceType").value;
//		var stat = document.getElementById("instanceStatus").value;
//		var isUse = document.getElementById("instanceIsUse").value;
		
		var ip = document.getElementById("instanceIp").value;
		var port = document.getElementById("instancePort").value;
		var lat = document.getElementById("instanceLatitude").value;
		var lng = document.getElementById("instanceLongitude").value;
		var aId = document.getElementById("instanceAId").value;
		var dPId = document.getElementById("instanceDPId").value;
		var dId = document.getElementById("instanceDId").value;

		if(selectedAdapterKind=="server"){
			if (!iId){
				commonAlert("알림","인스턴스ID를 입력 해 주세요.","인스턴스ID가 입력되지 않음.");
				return false;
			}
			if (!aId){
				commonAlert("알림","아답터ID를 입력 해 주세요.","아답터ID가 입력되지 않음.");
				return false;
			}
			
			if (!dPId){
				commonAlert("알림","장치풀ID를 입력 해 주세요.","장치풀ID가 입력되지 않음.");
				return false;
			}

			if (!ip){
				commonAlert("알림","아이피를 입력 해 주세요.","아이피가 입력되지 않음.");
				return false;
			}
			
			if (!port){
				commonAlert("알림","포트를 입력 해 주세요.","포트가 입력되지 않음.");
				return false;
			}
				
		}else{

			if (!iId){
				commonAlert("알림","인스턴스ID를 입력 해 주세요.","인스턴스ID가 입력되지 않음.");
				return false;
			}
			if (!aId){
				commonAlert("알림","아답터ID를 입력 해 주세요.","아답터ID가 입력되지 않음.");
				return false;
			}
			
			if (!dPId){
				commonAlert("알림","장치풀ID를 입력 해 주세요.","장치풀ID가 입력되지 않음.");
				return false;
			}
			if (!dId){
				commonAlert("알림","디폴트장치ID를 입력 해 주세요.","디폴트장치ID가 입력되지 않음.");
				return false;
			}

			if (!selfId){
				commonAlert("알림","셀프ID를 입력 해 주세요.","셀프ID가 입력되지 않음.");
				return false;
			}
			
			if (!selfPw){
				commonAlert("알림","암호를 입력 해 주세요.","암호가 입력되지 않음.");
				return false;
			}

			if (!ip){
				commonAlert("알림","아이피를 입력 해 주세요.","아이피가 입력되지 않음.");
				return false;
			}
			
			if (!port){
				commonAlert("알림","포트를 입력 해 주세요.","포트가 입력되지 않음.");
				return false;
			}
			
		}
		
		

		if(uniqueIDValidateCheck(iId,"인스턴스ID는 ")){
			return false;
		}
		
		if(ipValidateCheck(ip)){
			return false;
		}
		
		if(sessionValidateCheck(sessionTimeout)){
			return false;
		}
		
		if(portValidateCheck(port)){
			return false;
		}
		if(longitudeValidateCheck(lng)){
			return false;
		}
		if(latitudeValidateCheck(lat)){
			return false;
		}	
		
		
		/*if (!iId){
			commonAlert("알림","인스턴스ID를 입력 해 주세요.","인스턴스ID가 입력되지 않음.");
			return false;
		}
		if (!aId){
			commonAlert("알림","아답터ID를 입력 해 주세요.","아답터ID가 입력되지 않음.");
			return false;
		}
		
		if (!dPId){
			commonAlert("알림","장치풀ID를 입력 해 주세요.","장치풀ID가 입력되지 않음.");
			return false;
		}
		
		if(uniqueIDValidateCheck(iId,"인스턴스ID는 ")){
			return false;
		}
		
		if(ipValidateCheck(ip)){
			return false;
		}
		
		if(sessionValidateCheck(sessionTimeout)){
			return false;
		}
		
		if(portValidateCheck(port)){
			return false;
		}
		if(longitudeValidateCheck(lng)){
			return false;
		}
		if(latitudeValidateCheck(lat)){
			return false;
		}		*/
		
		iId=html5SpecialCharCode(iId);
		name=html5SpecialCharCode(name);
		kind=html5SpecialCharCode(kind);
		type=html5SpecialCharCode(type);
		stat=stat=html5SpecialCharCode(stat);
		sessionTimeout=html5SpecialCharCode(sessionTimeout);
		url=html5SpecialCharCode(url);
		selfId=html5SpecialCharCode(selfId);
		selfPw=html5SpecialCharCode(selfPw);
		remark=html5SpecialCharCode(remark);
		isUse=html5SpecialCharCode(isUse);
		ip=html5SpecialCharCode(ip);
		port=html5SpecialCharCode(port);
		lat=html5SpecialCharCode(lat);
		lng=html5SpecialCharCode(lng);
		aId	=html5SpecialCharCode(aId);
		dPId=html5SpecialCharCode(dPId);
		dId=html5SpecialCharCode(dId);		
		
		
		var instanceObject = new Object();
		instanceObject['instance.id'] = iId;
		instanceObject['instance.name'] = name;
		instanceObject['instance.kind'] = kind;
		instanceObject['instance.type'] = type;
		instanceObject['init.device.status'] = stat;
	
		instanceObject['session.timeout'] = sessionTimeout;
		instanceObject['url'] = url;
		instanceObject['self.id'] = selfId;
		instanceObject['self.pw'] = selfPw;
		instanceObject['remark'] = remark;
		
		instanceObject['is.use'] = isUse;
		instanceObject['ip'] = ip;
		instanceObject['port'] = port;
		instanceObject['latitude'] = lat;
		instanceObject['longitude'] = lng;
		instanceObject['adapter.id'] = aId;
		instanceObject['device.pool.id'] = dPId;
		instanceObject['default.device.id'] = dId;
		
		
		if (mode=='put'){
			instanceObject['registration.date'] = nowDate;
		}else if (mode=='set'){
			instanceObject['alter.date'] = nowDate;
		}
		if(arrayInstanceAttKey.length!=0)
			instanceObject['attribute.list']=arrayInstanceAttKey;
		if(arrayInstanceFuncKey.length!=0)
			instanceObject['function.list']=arrayInstanceFuncKey;

		var instanceJson = JSON.stringify(instanceObject,null,'\t');
		
		console.log(instanceJson);
 
		if (mode=='put'){
			putInstanceDatas(iId,instanceJson,'json');
			
		}else if (mode=='set'){
			setInstanceDatas(iId,instanceJson,'json');
		}
		clearSearchDevicePoolData();
//		clearSearchAdapterData();			
	}

	function updateInstanceFunction(mode){
		
		var nowDate = Date.now();
		nowDate = formatTime(nowDate);

		var fKey = document.getElementById("instanceFuncKey").value;
		var name = document.getElementById("instanceFuncName").value;
		var type = document.getElementById("instanceFuncType").value;
		var remark = document.getElementById("instanceFuncRemark").value;
		
		var paraTp1 = $("#instanceFuncParTp1").text();
		var paraTp2 = $("#instanceFuncParTp2").text();
		var paraTp3 = $("#instanceFuncParTp3").text();
		var paraTp4 = $("#instanceFuncParTp4").text();
		var paraTp5 = $("#instanceFuncParTp5").text();
		
		if(paraTp1=="타입" || paraTp1=="none"){
			paraTp1="string";
		}if(paraTp2=="타입" || paraTp2=="none"){
			paraTp2="string";
		}if(paraTp3=="타입" || paraTp3=="none"){
			paraTp3="string";
		}if(paraTp4=="타입" || paraTp4=="none"){
			paraTp4="string";
		}if(paraTp5=="타입" || paraTp5=="none"){
			paraTp5="string";
		} 

		var paraNm1 = document.getElementById("instanceFuncParNm1").value;
//		var paraTp1 = document.getElementById("instanceFuncParTp1").value;
		var paraNm2 = document.getElementById("instanceFuncParNm2").value;
//		var paraTp2 = document.getElementById("instanceFuncParTp2").value;
		var paraNm3 = document.getElementById("instanceFuncParNm3").value;
//		var paraTp3 = document.getElementById("instanceFuncParTp3").value;
		var paraNm4 = document.getElementById("instanceFuncParNm4").value;
//		var paraTp4 = document.getElementById("instanceFuncParTp4").value;
		var paraNm5 = document.getElementById("instanceFuncParNm5").value;
//		var paraTp5 = `document.getElementById("instanceFuncParTp5").value;
		
		if (!fKey){
			commonAlert("알림","기능키를 입력 해 주세요.","기능키가 입력되지 않음.");
			return false;
		}
		
//		if(uniqueKeyValidateCheck(fKey,"속성 키는 ")){
//			return false;
//		}
		
		paraNm1=html5SpecialCharCode(paraNm1);
		paraNm2=html5SpecialCharCode(paraNm2);
		paraNm3=html5SpecialCharCode(paraNm3);
		paraNm4=html5SpecialCharCode(paraNm4);
		paraNm5=html5SpecialCharCode(paraNm5);
		fKey=html5SpecialCharCode(fKey);
		name=html5SpecialCharCode(name);
		type=html5SpecialCharCode(type);
		remark=html5SpecialCharCode(remark);		
		
		var instanceFuncObject = new Object();
		instanceFuncObject['function.key'] = fKey;
		instanceFuncObject['function.description'] = name;
		instanceFuncObject['instance.id'] = selectedInstanceId;
		instanceFuncObject['content.type'] = type;
		instanceFuncObject['remark'] = remark;
		instanceFuncObject['param1'] = paraNm1;
		instanceFuncObject['param.type1'] = paraTp1;
		instanceFuncObject['param2'] = paraNm2;
		instanceFuncObject['param.type2'] = paraTp2;
		instanceFuncObject['param3'] = paraNm3;
		instanceFuncObject['param.type3'] = paraTp3;
		instanceFuncObject['param4'] = paraNm4;
		instanceFuncObject['param.type4'] = paraTp4;
		instanceFuncObject['param5'] = paraNm5;
		instanceFuncObject['param.type5'] = paraTp5;

		if (mode=='put'){
			instanceFuncObject['registration.date'] = nowDate;
		}else if (mode=='set'){
			instanceFuncObject['alter.date'] = nowDate;
		}

		var instanceFuncJson = JSON.stringify(instanceFuncObject,null,'\t');
		
		console.log(mode);

		if (mode=='put'){
			putInstanceFuncDatas(selectedInstanceId,fKey,instanceFuncJson,'json');
		}else if (mode=='set'){
			setInstanceFuncDatas(selectedInstanceId,fKey,instanceFuncJson,'json');
		}
		
	}

	function updateInstanceAttribute(mode){

		var nowDate = Date.now();
		nowDate = formatTime(nowDate);

		var aKey = document.getElementById("instanceAttrKey").value;
		var name = document.getElementById("instanceAttrName").value;
		var type = document.getElementById("instanceAttrValueType").value;
		var value = document.getElementById("instanceAttrValue").value;
		var remark = document.getElementById("instanceAttrRemark").value; 

		if (!aKey){
			commonAlert("알림","속성 키를 입력 해 주세요.","속성 키가 입력되지 않음.");
			return false;
		}
//		if(uniqueKeyValidateCheck(aKey,"속성 키는 ")){
//			return false;
//		}
		
		/*
		if (!value){
			alert('속성 값을 입력하세요');		
			return false;
		}
		*/
		

		selectedInstanceId=html5SpecialCharCode(selectedInstanceId);
		aKey=html5SpecialCharCode(aKey);
		name=html5SpecialCharCode(name);
		value=html5SpecialCharCode(value);
		type=html5SpecialCharCode(type);
		remark=html5SpecialCharCode(remark);
		
		
		var instanceAttrObject = new Object();
		instanceAttrObject['instance.id'] = selectedInstanceId;
		instanceAttrObject['attribution.key'] = aKey;
		instanceAttrObject['attribution.description'] = name;
		instanceAttrObject['attribution.value'] = value;
		instanceAttrObject['attribution.value.type'] = type;
		instanceAttrObject['remark'] = remark;
		
		/*
		if (mode=='put'){
			instanceAttrObject['registration.date'] = nowDate;
		}else if (mode=='set'){
			instanceAttrObject['alter.date'] = nowDate;
		}
		*/

		var instanceAttrJson = JSON.stringify(instanceAttrObject,null,'\t');
		console.log(instanceAttrJson);
		if (mode=='put'){
			putInstanceAttrDatas(selectedInstanceId,aKey,instanceAttrJson,'json');
		}else if (mode=='set'){
			setInstanceAttrDatas(selectedInstanceId,aKey,instanceAttrJson,'json');
		}
		
		$('.modal').modal('hide');
	}
	
	function adapterIntro(){
		
		var description=$("#adapterDescription");
		var link=$("#adapterLinkAddr a");
		
		description.html(selectedAdapterDescription);
		
		if(selectedAdapterHyperink==""||selectedAdapterHyperink==undefined||selectedAdapterHyperink==null){
			link.hide();
		}else{
			link.show();
			link.prop('href', selectedAdapterHyperink);
		}
	}
	
	function addAndEditInstanceInit(){
		
		if(selectedAdapterKind=="server"){					
			$("#instanceIsUse").html("false"+'<span class="caret"></span>');
			$("#instanceIsUse").addClass("readonlyStyle");
			$("#instanceIsUse").removeAttr("data-toggle");
			
			$("#instanceId").parents(".col-xs-9").siblings(".col-xs-3").html('<span class="essentialKey">*&nbsp;</span>인스턴스 ID');
			$("#instanceName").parents(".col-xs-9").siblings(".col-xs-3").html('&ensp;인스턴스명');
			$("#instanceAId").parents(".col-xs-9").siblings(".col-xs-3").html('<span class="essentialKey">*&nbsp;</span>아답터 ID');
			$("#instanceDPId").parents(".col-xs-9").siblings(".col-xs-3").html('<span class="essentialKey">*&nbsp;</span>장치풀 ID');
			$("#instanceDId").parents(".col-xs-9").siblings(".col-xs-3").html('&ensp;디폴트장치ID');
			$("#instanceSessionTimeout").parents(".col-xs-9").siblings(".col-xs-3").html('&ensp;세션타임아웃');
			$("#instanceURL").parents(".col-xs-9").siblings(".col-xs-3").html('&ensp;URL');
			$("#instanceSelfId").parents(".col-xs-9").siblings(".col-xs-3").html('&ensp;셀프ID');
			$("#instanceSelfPassword").parents(".col-xs-9").siblings(".col-xs-3").html('&ensp;암호');
			$("#instanceIp").parents(".col-xs-9").siblings(".col-xs-3").html('<span class="essentialKey">*&nbsp;</span>아이피');
			$("#instancePort").parents(".col-xs-9").siblings(".col-xs-3").html('<span class="essentialKey">*&nbsp;</span>포트');
			$("#instanceLatitude").parents(".col-xs-9").siblings(".col-xs-3").html('&ensp;위도');
			$("#instanceLongitude").parents(".col-xs-9").siblings(".col-xs-3").html('&ensp;경도');
			$("#instanceRemark").parents(".col-xs-9").siblings(".col-xs-3").html('&ensp;비고');
			
		}else{
			$("#instanceIsUse").parents('.btn-group').find('.dropdown-toggle').html("false"+'<span class="caret"></span>');
			$("#instanceIsUse").removeClass("readonlyStyle");
			$("#instanceIsUse").attr("data-toggle","dropdown");		

			$("#instanceId").parents(".col-xs-9").siblings(".col-xs-3").html('<span class="essentialKey">*&nbsp;</span>인스턴스 ID');
			$("#instanceName").parents(".col-xs-9").siblings(".col-xs-3").html('&ensp;인스턴스명');
			$("#instanceAId").parents(".col-xs-9").siblings(".col-xs-3").html('<span class="essentialKey">*&nbsp;</span>아답터 ID');
			$("#instanceDPId").parents(".col-xs-9").siblings(".col-xs-3").html('<span class="essentialKey">*&nbsp;</span>장치풀 ID');
			$("#instanceDId").parents(".col-xs-9").siblings(".col-xs-3").html('<span class="essentialKey">*&nbsp;</span>디폴트장치ID');
			$("#instanceSessionTimeout").parents(".col-xs-9").siblings(".col-xs-3").html('&ensp;세션타임아웃');
			$("#instanceURL").parents(".col-xs-9").siblings(".col-xs-3").html('&ensp;URL');
			$("#instanceSelfId").parents(".col-xs-9").siblings(".col-xs-3").html('<span class="essentialKey">*&nbsp;</span>셀프ID');
			$("#instanceSelfPassword").parents(".col-xs-9").siblings(".col-xs-3").html('<span class="essentialKey">*&nbsp;</span>암호');
			$("#instanceIp").parents(".col-xs-9").siblings(".col-xs-3").html('<span class="essentialKey">*&nbsp;</span>아이피');
			$("#instancePort").parents(".col-xs-9").siblings(".col-xs-3").html('<span class="essentialKey">*&nbsp;</span>포트');
			$("#instanceLatitude").parents(".col-xs-9").siblings(".col-xs-3").html('&ensp;위도');
			$("#instanceLongitude").parents(".col-xs-9").siblings(".col-xs-3").html('&ensp;경도');
			$("#instanceRemark").parents(".col-xs-9").siblings(".col-xs-3").html('&ensp;비고');
		}			
		
	}
	
	