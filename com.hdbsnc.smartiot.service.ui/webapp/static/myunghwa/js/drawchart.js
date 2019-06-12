google.setOnLoadCallback(drawChart);

	// Some raw data (not necessarily accurate)
	var rowData1 = [['Month', 'Bolivia', 'Ecuador', 'Madagascar', 'DRCongo',
	'Rwanda', 'Average'],
	['2004/05', 165, 938, 522, 998, 450, 114.6],
	['2005/06', 135, 1120, 599, 1268, 288, 382],
	['2006/07', 157, 1167, 587, 807, 397, 623],
	['2007/08', 139, 1110, 615, 968, 215, 409.4],
	['2008/09', 136, 691, 629, 1026, 366, 569.6]];
	var rowData2 = [['Month', 'Bolivia', 'Ecuador', 'Madagascar', 'DRCongo',
	'Rwanda', 'Average'],
	['2004/05', 122, 638, 722, 998, 450, 614.6],
	['2005/06', 100, 1120, 899, 1268, 288, 682],
	['2006/07', 183, 167, 487, 207, 397, 623],
	['2007/08', 200, 510, 315, 1068, 215, 609.4],
	['2008/09', 123, 491, 829, 826, 366, 569.6]];

	// Create and populate the data tables.
	var data = [];
	data[0] = google.visualization.arrayToDataTable(rowData1);
	data[1] = google.visualization.arrayToDataTable(rowData2);

	var options = {
		width: 600,
		height: 140,
		vAxis: {
			title: "Cups"}
		,
		hAxis: {
			title: "Month"}
		,
		seriesType: "bars",
		series: {
			5: {
				type: "line"}
		}
		,
		animation:{
			duration: 1000,
			easing: 'out'
		}
		,
	}
	;
	var current = 0;
	// Create and draw the visualization.
	var chart = new google.visualization.ComboChart(document.getElementById('visualization'));
	var button = document.getElementById('b1');
	function drawChart() {
		// Disabling the button while the chart is drawing.
		button.disabled = true;
		google.visualization.events.addListener(chart, 'ready',
		function() {
		button.disabled = false;
		button.value = 'Switch to ' + (current ? 'Tea' : 'Coffee');
		});
		options['title'] = 'Monthly ' + (current ? 'Coffee' : 'Tea') + ' Production by Country';

		chart.draw(data[current], options);
	}
	drawChart();

	button.onclick = function() {
		current = 1 - current;
		drawChart();
	}