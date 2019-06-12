	/******************************* init Start ******************************/

	$(document).ready(function () {
		$('#MySplitter').width(1200).height(530).split({orientation:'vertical', limit:0, position:'25%'});
		$('#sWrap').split({orientation:'horizontal', limit:0});
		
		$("#addrIP").text("Smart IoT 2.0 관리자화면 - "+location.host);

		$("#stopInstanceActivityBtn").hide();
		$("#startInstanceActivityBtn").hide();
		


		$("#plcMonitoringActivityBtn").hide();
		$("#plcStopActivityBtn").hide();
		$("#plcStartActivityBtn").hide();
		
		
		scopeSynchronizeIntervalId=setInterval("viewSynchronize(adapterScope,instanceScope,attributeScope)",200);
	});
	
	
	/******************************* init end ******************************/


	var instanceColumnDefs = [
	    //{headerName: 'CK', width: 30, checkboxSelection: true, suppressSorting: true, suppressMenu: true},
		{headerName: "인스턴스 이벤트", field: "instanceEventStatus",width:100},
		{headerName: "인스턴스 상태", field: "instanceStatus",width:100},
		{headerName: "인스턴스 명", field: "instanceName", filter: 'text',width:150},
		{headerName: "인스턴스 ID", field: "instanceId", filter: 'text',width:200},
		/*{headerName: "장치명", field: "defaultDeviceId",width:150},*/
		{headerName: "장치ID", field: "defaultDeviceId",width:159},
		{headerName: "IP", field: "ip",width:80},
		{headerName: "PORT", field: "port",width:65}
	];
	
	var attributeColumnDefs = [
	    /*{headerName: " ", field: "attributeCheck", width: 30,checkboxSelection: true},*/
	    {headerName: "상태", field: "eventStatus",width:100},
		{headerName: "속성명", field: "description", filter: 'text', width: 187},
		{headerName: "속성키", field: "key", filter: 'text', width: 187},
		{headerName: "디바이스 구분", field: "deviceType", filter: 'text', width: 150},
		{headerName: "시작번지", field: "startAddr", filter: 'text', width: 110},
		{headerName: "디바이스 점수", field: "deviceScore", filter: 'text', width: 110},
		{headerName: "수집 주기", field: "gathering", filter: 'text', width: 80}
	];


	var adapterColumnDefs = [
	    {headerName: "아답터명", field: "adapterName", filter: 'text', width: 315, cellRenderer: countryCellRendererFunc },
//	    {headerName: "아답터ID", field: "adapterId", filter: 'text', width: 150,}
	];
	
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
		function rowSelectedFunc(event) { //instance List Click Event Function

			plcAttrBtnInit();
			
			$(".blankAttribute").text("");
			
			selectedInstanceId = event.node.data.instanceId;
			selectedDeviceId=event.node.data.defaultDeviceId;			
			selectedInstanceEvent=event.node.data.instanceEventStatus;
			selectedInstanceStatus=event.node.data.instanceStatus;
			
			clearAttData();
			setPlcAttributeData(selectedInstanceId);//속성값 설정	
			
			setBtnState(selectedInstanceStatus,selectedInstanceEvent);					
			
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
			getRowStyle:setFontColorStatus

		};

		
		function setFontColorStatus(event){

			var status=event.node.data.eventStatus;
			
			if(status=="기동"){
				return {"color":'blue'};
			}else{
				return {"color":'black'};
			}
		}
		
		function rowSelectedFunc(event) { //attribute List Click Event Function
//	
			$(".blankAttribute").text("");

			$("#plcMonitoringNonActivityBtn").hide();
			$("#plcMonitoringActivityBtn").show();
						
			attributeKey=event.node.data.key;
			attributeDescription=event.node.data.description;
			attributeDeviceType=event.node.data.deviceType;
			attributeStartAddr=event.node.data.startAddr;
			attributeDeviceScore=event.node.data.deviceScore;
			attributeGathering=event.node.data.gathering;
			
			if(event.node.data.eventStatus=='정지'){
				$("#plcStartNonActivityBtn").hide();
				$("#plcStartActivityBtn").show();
				$("#plcStopActivityBtn").hide();
				$("#plcStopNonActivityBtn").show();
			}else if(event.node.data.eventStatus=='기동'){
				$("#plcStartNonActivityBtn").show();
				$("#plcStartActivityBtn").hide();
				$("#plcStopActivityBtn").show();
				$("#plcStopNonActivityBtn").hide();
			}

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
			rowHeight:50,
			toolPanelSuppressValues: true,
			toolPanelSuppressPivot: true,
		};
		
		/* Normal Functions */
		function rowSelectedFunc(event) { //Adapter List Click Event Function
			var aid = event.node.data.adapterId;
			selectedAdapterId = aid;

			$(".blankInstance").text("");
			$(".blankAttribute").text("");
			
			clearInsData();
			clearAttData();
			setInstanceData(aid);
			
			plcAttrBtnInit();
			plcInstanceBtnInit();
//						
//			setAdapterFunctionData(aid);
		}
		
	});
	
	/******************************* CONTROLLERS END ******************************/
	
	
	/******************************* API SCRIPTS START ******************************/

	function initGetData() {
		setPlcAdapterData();
    }	
	
	function checkIid(param) {
		if (selectedInstanceId == null) {
			commonAlert("알림", "인스턴스를 선택 해 주세요.", "인스턴스가 선택되지 않음.");
		} else {
			switch (param) {
			case "start":
				plcInstanceStart(selectedInstanceId);
	
				break;
			case "stop":
				plcInstanceStop(selectedInstanceId);
				break;
			}
		}
	}
	
	
	function checkMonitoring(param){
		if (selectedDeviceId == null) {
			commonAlert("알림", "인스턴스를 선택 해 주세요.", "인스턴스가 선택되지 않음.");
		} else {
			switch (param) {
			case "start":
	
				if(selectedInstanceStatus=="완료"&&selectedInstanceEvent=="기동"){
					
					var parentJsonArray= new Object();
					
					var childJsonArray= new Array();
					
	//				for(var i=0; i<selectRowData.length; i++){
						var jsonObject=new Object();
						
						jsonObject['device.id']=selectedDeviceId;
						jsonObject['attribution.key']=attributeKey;
						jsonObject['name']=attributeDescription;
						jsonObject['device.type']=attributeDeviceType;
						jsonObject['device.address']=attributeStartAddr;
						jsonObject['device.score']=attributeDeviceScore;
						jsonObject['gathering.period']=attributeGathering;
									
						console.log(jsonObject);
						childJsonArray[0]=jsonObject;	
	//				}
						parentJsonArray['attribution.info']=childJsonArray;
					
					var instanceJson = JSON.stringify(parentJsonArray,null,'\t');	
					console.log(instanceJson);
					
					setPlcGathringStart(selectedDeviceId,instanceJson);
				}else{
					commonAlert("알림","인스턴스를 기동시켜주세요","인스턴스가 기동되지 않음");
				}
				break;
				
			case "stop":
				console.log(selectedDeviceId);
				setPlcGathringStop(selectedDeviceId,attributeKey);
				break;
			case "monitor":	
				/*var selectRowData= attributeScope.gridOptions.onRowSelected.arguments[0].node.data();*/
			
				if(selectedInstanceStatus=="완료"&&selectedInstanceEvent=="기동"){
					
					var parentJsonArray= new Object();
					
					var childJsonArray= new Array();
					
	//				for(var i=0; i<selectRowData.length; i++){
					var jsonObject=new Object();
						
					jsonObject['device.id']=selectedDeviceId;
					jsonObject['attribution.key']=attributeKey;
					jsonObject['attribution.description']=attributeDescription;
					jsonObject['device.type']=attributeDeviceType;
					jsonObject['device.address']=attributeStartAddr;
					jsonObject['device.score']=attributeDeviceScore;
					jsonObject['gathering.period']=attributeGathering;
								
					console.log(jsonObject);
					childJsonArray[0]=jsonObject;	
	//				}
					parentJsonArray['attribution.info']=childJsonArray;
					
					var instanceJson = JSON.stringify(parentJsonArray,null,'\t');	
					console.log(instanceJson);
					
					sessionStorage.setItem("monitoringDeviceId",selectedDeviceId);
					sessionStorage.setItem("monitoringAttkey",attributeKey);
					sessionStorage.setItem("deviceViewArrayData",instanceJson);
					window.location.href = "device_view.html";
				}else{
					commonAlert("알림","인스턴스를 기동시켜주세요","인스턴스가 기동되지 않음");
				}
				break;
			}
		}
	}
	
	///////////////////////////////////////////////////
	/////////////기동 시작 정지 멈춤//////////////////////////
	///////////////////////////////////////////////

	function cbInsStartSucessFunc(evt){// 인스턴스 스타트 성공		
//		commonAlert("알림","인스턴스 상태를 새로고침 해주세요.");
		setBtnState();
		
		refreshInstanceList();
		clearAttData();
	}
	function cbInsStartFailFunc(evt){// 인스턴스 스타트 실패
		console.log("인스턴스 스타트 실패");
		commonErrorMessage(evt);
	}

	function cbInsStopSucessFunc(evt){// 인스턴스 스톱 성공
//		commonAlert("알림","인스턴스 상태를 새로고침 해주세요.");		
		setBtnState();
		

		refreshInstanceList();
		clearAttData();

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
	
	/////////////////////////////////////////////
	
	
	function cbAdtGetAllSucessFunc(evt){// 아답터 목록 조회 성공
		console.log('아답터 조회 성공');
		var rowDataByJson = parseAdapterJsonData(evt);
		loadData(adapterScope,rowDataByJson);
	}

	function cbAdtGetAllFailFunc(evt){// 아답터 목록 조회 실패
		console.log('아답터 조회 실패');
		clearAdapterData();
		commonErrorMessage(evt,"blankAdapter");
	}

	function cbInsSearchByAidSucessFunc(evt){// 인스턴스 목록 조회 성공
		var rowDataByJson = parseInstanceJsonData(evt);
		loadData(instanceScope,rowDataByJson);
		$(".blankInsInstance").text("");
	}

	function cbInsSearchByAidFailFunc(evt){// 인스턴스 목록 조회 실패
//		clearInstanceData();
		commonErrorMessage(evt,"blankInstance");
	}

	
	function cbInsAttSearchByIidSucessFunc(evt){// iid로 인스턴스속성 목록 조회 성공
		console.log("인스턴스속성 목록 조회 성공");
		var rowDataByJson = parsePlcAttributeJsonData(evt);
		loadData(attributeScope,rowDataByJson);
		$(".blankInsAtt").text("");
	}
	
	function cbInsAttSearchByIidFailFunc(evt){	// iid로 인스턴스속성 목록 조회 실패
		console.log("인스턴스속성 목록 조회 실패");
		commonErrorMessage(evt, "blankAttribute");
	}
	
	function cbInsAttStartSucessFunc(evt){
		refreshAttributeList();
		console.log("인스턴스속성 시작 성공");
	}
	
	function cbInsAttStartFailFunc(evt){
		refreshAttributeList();
		console.log("인스턴스속성 시작 실패");
	}	

	function cbInsAttStopSucessFunc(evt){
		refreshAttributeList();
		console.log("인스턴스속성 정지 성공");
	}
	
	function cbInsAttStopFailFunc(evt){	
		refreshAttributeList();
		console.log("인스턴스속성 정지 실패");
	}
	
	function cbInsAttEventFunc(evt){
		console.log(evt);
	}
	
	
	/******************************* API SCRIPTS END ******************************/


	function countryCellRendererFunc(params) {
		
		console.log(params);
		
		return '<img ng-show="flagCode" class="flag" border="0" width="40" height="45" src="'+params.data.plcImage+'"><sapn>&nbsp&nbsp'+params.value+'</>';		
    }

	
	//////////////////////////////////////////////////////////////////
	//////////////////컬럼 선택 제거할수있는 함수/////////////////////////////////
	/////////////////////////////////////////////////////////////////
	function showToolPanel(scope){ //옆에있는 새로고침과 설정 버튼인데 아답터명과 ID가 나옴
		if(!scope.gridOptions.api.isToolPanelShowing()){
			scope.gridOptions.api.showToolPanel(true);
			//scope.gridOptions.api.sizeColumnsToFit();
		}else{
			scope.gridOptions.api.showToolPanel(false);
			//scope.gridOptions.api.sizeColumnsToFit();
		}
	}
	
	/** 그리드 컬럼 지우는 곳 START **/
	function clearInsData(){ //Clear datas to grid(Function) 
		selectedInstanceId=null;
		selectedDeviceId=null;
		var emptyRow = [];
		instanceScope.gridOptions.api.setRowData(emptyRow);
	}
	
	function clearAttData(){ //Clear datas to grid(adapter)
		var emptyRow = [];
		attributeScope.gridOptions.api.setRowData(emptyRow);
	}
	
	function clearAdapterData(){ //Clear datas to grid(Function) 
		selectedAdapterId =null;  
		selectedInstanceId=null;
		selectedDeviceId=null;
		var emptyRow = [];
		adapterScope.gridOptions.api.setRowData(emptyRow);
	}		
	/** 그리드 컬럼 지우는 곳 END **/

	/** 그리드 컬럼 리프레쉬 START **/
	
	function refreshAdapterList(){//아답터목록 새로고침
		
		clearInsData();
		clearAttData();
		clearAdapterData();
		setPlcAdapterData();
		plcAttrBtnInit();
		plcInstanceBtnInit();
	}
	
	function refreshAttributeList(){//속성목록 새로고침
		
		if(selectedAdapterId == null){
		  commonAlert("알림", "아답터를 선택 해 주세요.", "아답터를 선택 해 주세요.");
		  return false;
		}else{
			clearAttData();
			setPlcAttributeData(selectedInstanceId);

			$("#plcMonitoringActivityBtn").hide();
			$("#plcStopActivityBtn").hide();
			$("#plcStartActivityBtn").hide();		
			$("#plcMonitoringNonActivityBtn").show();
			$("#plcStopNonActivityBtn").show();
			$("#plcStartNonActivityBtn").show();		
		}
	}
	
	function refreshInstanceList(){//인스턴스목록 새로고침
		
		if(selectedAdapterId == null){
			commonAlert("알림","아답터를 선택 해 주세요.","아답터를 선택하지 않음");
		  return false;
		}else{
			clearAttData();	
			clearInsData();
			setInstanceData(selectedAdapterId);
			plcAttrBtnInit();
			plcInstanceBtnInit();
		}
	}
	/** 그리드 컬럼 리프레쉬 END **/
	
	
	function plcInstanceBtnInit(){
		
		$("#stopInstanceActivityBtn").hide();
		$("#startInstanceActivityBtn").hide();

		$("#stopInstanceNonActivityBtn").show();
		$("#startInstanceNonActivityBtn").show();
		
	}
	
	function plcAttrBtnInit(){

		$("#plcMonitoringActivityBtn").hide();
		$("#plcStopActivityBtn").hide();
		$("#plcStartActivityBtn").hide();		

		$("#plcMonitoringNonActivityBtn").show();
		$("#plcStopNonActivityBtn").show();
		$("#plcStartNonActivityBtn").show();		
		
	}
	