$(document).ready(function(){		
	$("#addrIP").text("Smart IoT 2.0 관리자화면 - "+location.host);		
	$("#domainCancleBtn").click(function(){
		location.href="monitoring.html";
	});		
	scopeSynchronizeIntervalId=setInterval("viewSynchronize(eventScope)",200);
});

$( window ).unload(function() {
	var plcDeviceId=sessionStorage.getItem("monitoringDeviceId");
	var plcAttributeKey=sessionStorage.getItem("monitoringAttkey");
	return setPlcMonitoringStop(plcDeviceId,plcAttributeKey);
});


//Grid Columns 
var eventColumnDefs = [
	{headerName: "처리시간", field: "eventTime", filter: 'text', width: 130},
	{headerName: "인스턴스 명", field: "instanceName", filter: 'text',width: 120},
//	{headerName: "인스턴스 ID", field: "instanceID", filter: 'text',width: 150},
	{headerName: "장비명", field: "deviceName",width: 80},
	{headerName: "속성명", field: "attributeName", filter: 'text', width: 130},
    {headerName: "수집주기", field: "gatheringPeriod", filter: 'text', width: 80},	
	{headerName: "디바이스타입", field: "deviceType",width: 130},                           
    {headerName: "시작번지", field: "deviceStartAddr", filter: 'text', width: 80},     
    {headerName: "디바이스점수", field: "deviceScore", filter: 'text', width: 100},
    {headerName: "종료코드", field: "deviceEnd", filter: 'text', width: 80},
    {headerName: "수집값", field: "attributeValue", filter: 'text', width: 200},
    {headerName: "메세지", field: "deviceMessage", filter: 'text', width: 150},	
	//{headerName: "비고", field: "remark"} 
];


	/******************************* CONTROLLERS START ******************************/
	var rowData = [];
	agGridModule.controller("eventCtrl", function($scope) {

		eventScope = $scope;

		rowData = [];
		$scope.gridOptions = {
			columnDefs: eventColumnDefs,
			rowSelection: 'single',
			onRowSelected: rowSelectedFunc,
			rowData: rowData,
			enableSorting: true,
			enableFilter: true,
			enableColResize: true,
			showToolPanel: false,
			toolPanelSuppressValues: true,
			toolPanelSuppressPivot: true,
		};

		/* Normal Functions */
		function rowSelectedFunc(event) { //Session List Click Event Function
			var dpid = event.node.data.id;
		}
		
		$scope.addNewItem = function(addRows){
		
			for (var idx in addRows) {
				rowData.unshift(addRows[idx]);
			};
			
			$scope.gridOptions.api.setRowData(rowData);
	    };
	    
	    $scope.rowClear = function(){
	    	rowData = [];
	    	$scope.gridOptions.api.setRowData(rowData);
	    }

	});
	
	/******************************* CONTROLLERS END ******************************/

	/******************************* API SCRIPTS START ******************************/
	function initGetData() { //도메인 타입 (instance.id, device.pool.id, device.id, user.pool.id, user.id)

		var plcGatheringSearchData=sessionStorage.getItem("deviceViewArrayData");
		var plcDeviceId=sessionStorage.getItem("monitoringDeviceId");
	
		console.log(plcGatheringSearchData);
		setPlcMonitoringStart(plcDeviceId,plcGatheringSearchData);		
    }

	function cbPlcStartSucessFunc(evt){
		
		console.log("PLC SEARCH 성공");
		console.log(evt);
	} 
	
	function cbPlcStartFailFunc(evt){
		
		console.log("PLC SEARCH 실패");
		console.log(evt);
	} 

	function cbPlcStartEventFunc(evt){
		
		console.log(evt);
		var rowDataByJson = parsePlcEventJsonData(evt);
		eventScope.addNewItem(rowDataByJson);
	}	
	
	function cbPlcStopSucessFunc(evt){
		
		console.log("PLC 스톱 성공");
		console.log(evt);
	}
	
	function cbPlcStopFailFunc(evt){
		
		console.log("PLC 스톱 실패");
		console.log(evt);
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
	
	/******************************* API SCRIPTS END ******************************/

	
	/* 윤영진 추가 */
	function refreshEventList(){
		selectedDevicePoolId = null;
		/*var emptyRow = [{ id: '' }];*/	
		rowData=[];
		loadData(eventScope,rowData);
	}

	function showToolPanel(scope){
		if(!scope.gridOptions.api.isToolPanelShowing()){
			scope.gridOptions.api.showToolPanel(true);
			//scope.gridOptions.api.sizeColumnsToFit();
		}else{
			scope.gridOptions.api.showToolPanel(false);
			//scope.gridOptions.api.sizeColumnsToFit();
		}
	}

	function onChangeDomainType(dType){
		clearDomainData();
		setDomainData(dType);
	}
	
	