$(document).ready(function(){

	$("#addrIP").text("Smart IoT 2.0 관리자화면 - "+location.host);
	scopeSynchronizeIntervalId=setInterval("viewSynchronize(instanceScope)",200);
});

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
	/******************************* CONTROLLERS START ******************************/
	agGridModule.controller("instanceCtrl", function($scope) {
		instanceScope = $scope;

		$scope.gridOptions = {
			columnDefs: instanceColumnDefs,
			rowSelection: 'single',
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
			
			var processEvent=event.data.processEvent;
			var processState=event.data.processState;
			
			if(processState=="실패"||processState=="에러"){
				return {"color":'red'};
			}else if(processState=="none"){
				return {"color":'black'};
			}else{
				return {"color":'blue'};
			}
		}

	});
	/******************************* CONTROLLERS END ******************************/

	/******************************* API SCRIPTS START ******************************/
	
	function instanceGetBtn(){
		var instanceId=$("#instanceId").val();
		var deviceId=$("#deviceId").val();
		var ip=$("#ip").val();
		setInstanceAllData(instanceId,deviceId,ip);
	}

	/* Call backs */
	
	function cbInsAllGetSucessFunc(evt){// 도메인 목록 조회 성공
		console.log('인스턴스 조회 성공');
		var rowDataByJson = parseAllInstanceJsonData(evt);
		loadData(instanceScope,rowDataByJson);
	}

	function cbInsAllgetFailFunc(evt){// 도메인 목록 조회 실패
		console.log('인스턴스 조회 실패');
		commonErrorMessage(evt);
	}
	

	/******************************* API SCRIPTS END ******************************/

	function initGetData(){}
	
	/* Clear Datas */	
	function clearGridData(){ //Clear datas to grid(search device pool) 
		selectedDevicePoolId = null;
		/*var emptyRow = [{ id: '' }];*/	
		var emptyRow = [];	
		instanceScope.gridOptions.api.setRowData(emptyRow);
	}

	/* Init */
//	initAPI(); //Login and Get Data Progress
	
	
	
	/* 윤영진 추가 */
	function refreshDomainList(){
		clearGridData();
		
		/////////////////////////////////
		setDomainData(setInstanceAllData);
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
