$(document).ready(function(){

	$("#addrIP").text("Smart IoT 2.0 관리자화면 - "+location.host);
	scopeSynchronizeIntervalId=setInterval("viewSynchronize(domainScope)",200);
});

//Grid Columns 
var domainColumnDefs = [
	{headerName: "도메인ID", field: "id", filter: 'text', width: 250},
	{headerName: "도메인명", field: "domNm", filter: 'text', width: 250},
	{headerName: "도메인 타입", field: "domTp", width: 250},
	{headerName: "등록일시", field: "regDate",filter: 'text', width: 204}, 
	{headerName: "변경일시", field: "altDate",filter: 'text', width: 204} 
	/* 2015/01/21 비고는 비표시 되도록 수정 dhkang */
	//{headerName: "비고", field: "remark"} 
];


	/******************************* CONTROLLERS START ******************************/
	agGridModule.controller("domainCtrl", function($scope) {

		domainScope = $scope;

		$scope.gridOptions = {
			columnDefs: domainColumnDefs,
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
			var dpid = event.node.data.id;
			selectedDomainId = dmid;
		}

	});
	/******************************* CONTROLLERS END ******************************/

	/******************************* API SCRIPTS START ******************************/
	function initGetData() { //도메인 타입 (instance.id, device.pool.id, device.id, user.pool.id, user.id)
		var domainType = document.getElementById('domain_type').value;
		setDomainData(domainType);
    }

	/* Call backs */
	
	function cbDomSearchByTypeSucessFunc(evt){// 도메인 목록 조회 성공
		console.log('도메인 조회 성공');
		var rowDataByJson = parseDomainJsonData(evt);
		loadData(domainScope,rowDataByJson);
	}

	function cbDomSearchByTypeFailFunc(evt){// 도메인 목록 조회 실패
		console.log('도메인 조회 실패');
		commonErrorMessage(evt);
	}
	

	/******************************* API SCRIPTS END ******************************/

	/* Clear Datas */
	
	function clearDomainData(){ //Clear datas to grid(search device pool) 
		selectedDevicePoolId = null;
		/*var emptyRow = [{ id: '' }];*/	
		var emptyRow = [];	
		domainScope.gridOptions.api.setRowData(emptyRow);
	}

	/* Init */
//	initAPI(); //Login and Get Data Progress
	
	
	
	/* 윤영진 추가 */
	function refreshDomainList(){
		clearDomainData();
		var domainType = document.getElementById('domain_type').value;
		setDomainData(domainType);
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
	
	