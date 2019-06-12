(function () {
	
	/******************************* MAIN BOARD ******************************/
	var otpAuth = new bsnc.otp.api.auth();

    var servername = ["마스터 서버", "슬레이브 서버1", "슬레이브 서버2",];
    	
	var category = ["Master", "Slave", "Slave"];

    var ids = ["19212801","19212803","19212802"];
	
	var thread = ["4","4","1"];
	
	var status = ["기동중", "기동중", "기동중"];


    var columnDefs = [
        {headerName: 'Check', width: 79, checkboxSelection: true, suppressSorting: true, suppressMenu: true},
        {headerName: "서버명", field: "servername", width: 180},
        {headerName: "아이디", field: "ids", width: 160},
		{headerName: "구분", field: "category", width: 150},
        {headerName: "스레드", field: "thread", width: 120},
        {headerName: "상태", field: "status", width: 150} 
    ];

    var gridOptions = {
        columnDefs: columnDefs,
        rowData: createRowData(),
        // a callback that gets called whenever the grids data changes
        onModelUpdated: modelUpdated
    };
	
    // wait for the document to be loaded, otherwise
    // ag-Grid will not find the div in the document.
    document.addEventListener("DOMContentLoaded", function() {

        var myGrid = document.querySelector('#myGrid');
        myGrid.setGridOptions(gridOptions);
        //myGrid.setAttribute('row-height', 55);

        // add events to grid option 1 - add an event listener
        myGrid.addEventListener('columnresized', function(event) {
            console.log('event via option 1: ' + event.agGridDetails);
        });

        // add events to grid option 2 - callback on the element
        myGrid.oncolumnresized = function(event) {
            console.log('event via option 2: ' + event.agGridDetails);
        };

        // add events to grid option 3 - callback on the grid options
        gridOptions.onColumnResized = function(event) {
            console.log('event via option 3: ' + event.agGridDetails);
        };

        addQuickFilterListener();
        //addRefreshDataViaApi();
        addRefreshDataViaElement();
        //addDestroyListener();
    });

    function addQuickFilterListener() {
        var eInput = document.querySelector('#quickFilterInput');
        eInput.addEventListener("input", function () {
            var text = eInput.value;
            gridOptions.api.setQuickFilter(text);
        });
    }

    function addRefreshDataViaApi() {
        var eButton = document.querySelector('#btRefreshDataViaApi');
        eButton.addEventListener("click", function () {
            var data = createRowData();
            gridOptions.api.setRowData(data);
        });
    }

    function addRefreshDataViaElement() {
        var eButton = document.querySelector('#btRefreshDataViaElement');
        eButton.addEventListener("click", function () {
            var myGrid = document.querySelector('#myGrid');
            var data = createRowData();
            myGrid.rowData = data;
        });
    }

    function modelUpdated() {
        var model = gridOptions.api.getModel();
        var totalRows = gridOptions.rowData.length;
        var processedRows = model.getVirtualRowCount();
        var eSpan = document.querySelector('#rowCount');
        eSpan.innerHTML = processedRows.toLocaleString() + ' / ' + totalRows.toLocaleString();
    }

    function createRowData() {
        var rowData = [];

        for (var i = 0; i < 3; i++) {
            //for (var i = 0; i < 10000; i++) {
            var countryData = ids[i % ids.length];

            rowData.push({
                servername: servername[i % servername.length],
                thread: thread[i % thread.length],
                ids: countryData,
                status: status[i % status.length],
				category: category[i % category.length]
            });
        }
		
        return rowData;
    }

	function initGetData() {
      
    }
	
	/******************************* END MAIN BOARD ******************************/

})();