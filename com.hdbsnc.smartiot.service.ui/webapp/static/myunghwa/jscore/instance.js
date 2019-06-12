	$(document).ready(function () {
		
		$('#MySplitter').width(1200).height(530).split({orientation:'vertical', limit:0, position:'25%'});	
		$('#sWrap').split({orientation:'horizontal', limit:0});
		
		$("#addrIP").text("Smart IoT 2.0 관리자화면 - "+location.host);
		
		scopeSynchronizeIntervalId=setInterval("viewSynchronize(adapterScope,instanceScope,attributeScope)",200);
	});

	/**
	 * 아답터 목록 그리드
	 */
	var instanceColumnDefs = [
		{headerName: "인스턴스 이벤트", field: "instanceEventStatus",width:100},
		{headerName: "인스턴스 상태", field: "instanceStatus",width:100},
		{headerName: "인스턴스 명", field: "instanceName", filter: 'text',width:150},
		{headerName: "인스턴스 ID", field: "instanceId", filter: 'text',width:200},
		{headerName: "장치ID", field: "defaultDeviceId",width:159},
		{headerName: "IP", field: "ip",width:80},
		{headerName: "PORT", field: "port",width:65}
	];
	
	var attributeColumnDefs = [
		{headerName: "속성명", field: "description", filter: 'text', width: 202},
		{headerName: "속성키", field: "key", filter: 'text', width: 202},
		{headerName: "디바이스 구분", field: "deviceType", filter: 'text', width: 150},
		{headerName: "시작번지", field: "startAddr", filter: 'text', width: 110},
		{headerName: "디바이스 점수", field: "deviceScore", filter: 'text', width: 110},
		{headerName: "수집 주기", field: "gathering", filter: 'text', width: 80}
	];
	
	
	var adapterColumnDefs = [
        {headerName: "아답터명", field: "adapterName", filter: 'text', width: 315, cellRenderer: countryCellRendererFunc }
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

		/* Normal Functions */
		function rowSelectedFunc(event) { //instance List Click Event Function
			selectedInstanceId = event.node.data.instanceId;
			clearAttData();

			setPlcAttributeData(selectedInstanceId);//속성값 설정					
			
		}
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
			toolPanelSuppressPivot: true
		};
		
		/* Normal Functions */
		function rowSelectedFunc(event) { //attribute List Click Event Function
			
			selectedAttributeKey = event.node.data.key;

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
			rowHeight:50,
			showToolPanel: false,
			toolPanelSuppressValues: true,
			toolPanelSuppressPivot: true,
		};
		
		/* Normal Functions */
		function rowSelectedFunc(event) { //Adapter List Click Event Function
			
			selectedAdapterId = event.node.data.adapterId;

			clearInsData();
			clearAttData();
			setInstanceData(selectedAdapterId);
		}
		
	});
	
	/******************************* CONTROLLERS END ******************************/
	
	
	/******************************* API SCRIPTS START ******************************/

	function countryCellRendererFunc(params) {console.log(params);
	
	return '<img ng-show="flagCode" class="flag" border="0" width="40" height="45" src="'+params.data.plcImage+'"><sapn>&nbsp&nbsp'+params.value+'</>';
    }
	
	function initGetData() {
		setPlcAdapterData();
    }
	
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
		$(".blankInstance").text("");
	}

	function cbInsSearchByAidFailFunc(evt){// 인스턴스 목록 조회 실패
		commonErrorMessage(evt,"blankInstance");
	}

	
	function cbInsAttSearchByIidSucessFunc(evt){// iid로 인스턴스속성 목록 조회 성공
		console.log("인스턴스속성 목록 조회 성공");
		var rowDataByJson = parsePlcAttributeJsonData(evt);
		loadData(attributeScope,rowDataByJson);
		$(".blankAttribute").text("");
	}
	
	function cbInsAttSearchByIidFailFunc(evt){	// iid로 인스턴스속성 목록 조회 실패
		console.log("인스턴스속성 목록 조회 실패");
		commonErrorMessage(evt, "blankAttribute");
	}
	
	/******************************* API SCRIPTS END ******************************/
	
	$(document).ready(function(){	
		
		$(".dropdown-menu li a").click(function(){
			
			 var selText = $(this).text();	 
			 $(this).parents('.btn-group').find('.dropdown-toggle').html(selText+'<span class="caret"></span>');
		});
		
		$("#addAttribute").click(function() {			
			
			if(selectedInstanceId == null){
				commonAlert("알림","인스턴스를 선택 해 주세요.","인스턴스를 선택하지 않음");
			  return false;
			}else{
			  form_mode = 'put';			  
			  
			  $(".myModalLabel").text("속성 등록");
			  $(".popUpDefaultBtn").text("등록");
	
			  document.getElementById("instanceAttrKey").readOnly = false;
			  
			  document.getElementById("instanceAttrKey").value = "";
			  document.getElementById("instanceAttrName").value = "";
			  $("#instanceAttrRange").parents('.btn-group').find('.dropdown-toggle').html("Device"+'<span class="caret"></span>');
			  document.getElementById("instanceAttrStartAddr").value = "";
			  document.getElementById("instanceAttrScore").value = "";
			  document.getElementById("instanceAttrCycle").value = "";
			}
		});			
		
		$("#delAttribute").click(function() {
			if(selectedAttributeKey == null){
				commonAlert("알림","속성을 선택 해 주세요.","속성을 선택하지 않음");
			  return false;
			}else{
				commonConfirm("알림","삭제 하시겠습니까?",delPlcInstanceAttrDatas,selectedInstanceId,selectedAttributeKey);
			}
		});		
	});
	
	function updateInstanceAttribute(mode){

		var nowDate = Date.now();
		nowDate = formatTime(nowDate);

		var aKey = document.getElementById("instanceAttrKey").value;
		var name = document.getElementById("instanceAttrName").value;
				
		var startAddr = document.getElementById("instanceAttrStartAddr").value ;
		var score = document.getElementById("instanceAttrScore").value;
		var cycle = document.getElementById("instanceAttrCycle").value

		var type=$("#instanceAttrRange").text();

		if (!aKey){
			commonAlert("알림","속성 키를 입력 해 주세요.","속성 키가 입력되지 않음.");
			return false;
		}
		if(type=="Device"){
			commonAlert("알림","디바이스 구분을 선택 해 주세요.","디바이스 구분이 입력되지 않음.");
			return false;
		}
		
		if(!startAddr){
			commonAlert("알림","시작번지를 입력 해 주세요.","시작번지가 입력되지 않음.");
			return false;
		}
		if(!score){
			commonAlert("알림","디바이스점수를 입력 해 주세요.","디바이스점수가 입력되지 않음.");
			return false;
		}
		if(!cycle){
			commonAlert("알림","수집주기를 입력 해 주세요.","수집주기가 입력되지 않음.");
			return false;
		}
		type= type.replace(/[ㄱ-ㅎ가-힣() ]/g, "");
		
		
		var instanceAttrObject = new Object();
		instanceAttrObject['instance.id'] = selectedInstanceId;
		instanceAttrObject['attribution.key'] = aKey;
		instanceAttrObject['attribution.description'] = name;
		
		instanceAttrObject['device.type'] = type;
					
		instanceAttrObject['device.address'] = startAddr;
		instanceAttrObject['device.score'] = score;
		instanceAttrObject['gathering.period'] = cycle;		
		
		
		instanceAttrObject['attribution.value.type'] = "";		
		instanceAttrObject['attribution.value'] = "";
		instanceAttrObject['remark'] = "";
		
		/*
		if (mode=='put'){
			instanceAttrObject['registration.date'] = nowDate;
		}else if (mode=='set'){
			instanceAttrObject['alter.date'] = nowDate;
		}
		*/

		var instanceAttrJson = JSON.stringify(instanceAttrObject,null,'\t');
		console.log(instanceAttrJson);
		putPlcInstanceAttrDatas(selectedInstanceId,aKey,instanceAttrJson,'json');
		
		
		$('.modal').modal('hide');
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
		selectedAttributeKey=null;
		var emptyRow = [];
		instanceScope.gridOptions.api.setRowData(emptyRow);
	}
	
	function clearAttData(){ //Clear datas to grid(adapter)
		selectedAttributeKey=null;
		var emptyRow = [];
		attributeScope.gridOptions.api.setRowData(emptyRow);
	}
	
	function clearAdapterData(){ //Clear datas to grid(Function) 
		selectedAdapterId =null;  
		selectedInstanceId=null;
		var emptyRow = [];
		adapterScope.gridOptions.api.setRowData(emptyRow);
	}		
	/** 그리드 컬럼 지우는 곳 END **/

	/** 그리드 컬럼 리프레쉬 START **/
	
	function refreshAdapterList(){//아답터목록 새로고침
		selectedAdapterId =null;  		
		clearInsData();
		clearAttData();
		clearAdapterData();
		setPlcAdapterData();
	}
	
	function refreshAttributeList(){//속성목록 새로고침
		if(selectedAdapterId == null){
		  commonAlert("알림", "아답터를 선택 해 주세요.", "아답터를 선택 해 주세요.");
		  return false;
		}else{
			clearAttData();
			setPlcAttributeData(selectedInstanceId);
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
		}
	}
	/** 그리드 컬럼 리프레쉬 END **/