/*
 * ANSIN style mobile first 
 * Author: 니콜라 (PIBS)
 *
 * Dependencies : jQuery.js
 *
 * SUMMARY:
   1)  HIDE ADRESS BAR 
   2)  CUSTOM SELECTBOX WRAP
   3)  SET DIMENSION AGAIN ON RESIZE
   4)  APPEND OVERLAY HTML
   5)  INIT CODE HIGHLIGHT PLUGIN
   5)  SET DIMENSION
   6)  OPEN/CLOSE MENU EVENT
   7)  INDEX TRACKING NAV
   8)  NICESCROLL INIT ON MENU
   9)  SUBMENU
   10) ACCORDION
   11) CLOUD COMPONENT
   12) SHOW MENU ON SWIPE LEFT
   13) INIT SWIPE
   14) LOAD MORE
   15) IS LARGE SCREEN FUNCTION
*/

jQuery(function($){

/***************************************/
/* HIDE ADRESS BAR */
/***************************************/	
window.scrollTo(0,1); 



/***************************************/
/* CUSTOM SELECTBOX WRAP */
/***************************************/
$('select').wrap('<span class="select"></span>');



/***************************************/
/* SET DIMENSION AGAIN ON RESIZE */
/***************************************/
if('orientation' in window){
	$(window).on('orientationchange',set_dimension);
}else{
	$(window).on('resize',set_dimension);
}



/***************************************/
/* APPEND OVERLAY HTML */
/***************************************/	
$('body').append('<div id="overlay"></div>')



/***************************************/
/* INIT CODE HIGHLIGHT PLUGIN  */
/* https://highlightjs.org */
/***************************************/
if(typeof hljs != "undefined"){
	hljs.initHighlightingOnLoad();
}



/***************************************/
/* SET DIMENSION */
/***************************************/
function set_dimension(){
	
    var header_height = $('#header_inner').outerHeight();
	var window_height = $(window).height();
	var section_height = window_height-header_height;
    
	/* set menu height*/
	//$('#menu_container').height(window_height);
	
	/* set main swipe section height */
	$('.sub .monitoring_component').height(section_height);
	$('.main_maps_container').height(section_height);
    $('#main_swipe > div > section, .sub.page_shipping-stop .monitoring_component').height(section_height);
	
}
set_dimension();



/***************************************/
/* OPEN/CLOSE MENU EVENT */
/***************************************/
$('#menu_close_btn').on('click',function(){
	$('#menu_container, #overlay').toggleClass('open');
	return false;
})

$('#btn_menu, #overlay').on('touchstart click',function(){
	$('#menu_container, #overlay').toggleClass('open');
	return false;
})



/***************************************/
/* NICESCROLL INIT ON MENU */
/***************************************/
$('#menu_container').niceScroll({
	autohidemode :true,
	cursorcolor  :"rgba(0,0,0,0)",
	cursorborder :"none"
})



/***************************************/
/* ADD ACTIVE CLASS ON CURRENT MENU */
/***************************************/
var current_file = location.pathname.substr(location.pathname.lastIndexOf("/")+1);

$('#navbar li a[href="'+current_file+'"]').parent().addClass('active').parent().parent().addClass('active');


/***************************************/
/* ACCORDION */
/***************************************/

/* DEFAULT ACCORDION */
function board_accordion(selector){

	$(selector).on('click',function(){
		 
		 if($(this).parent().hasClass('open')){
	     
			 $(this).siblings('.accordion_content').stop().slideUp(function(){
				 $(this).parent().removeClass('open');
			 });		 
		 
		 }else{
			 
			 $('.accordion_content.open').stop().slideUp(function(){
				 $(this).removeClass('open');
			 });
			 
			 $(this).siblings('.accordion_content').stop().slideDown(function(){
				 $(this).addClass('open')
			 });
					 
			 $('.board > ul > li.open').removeClass('open');
			 $(this).parent().addClass('open');
			 
			 $(this).siblings('.accordion_content').find('.shipping_monitoring_map').each(function(){
			 
				maps_init_07($(this)[0]);
			 
			 });
		 
		 }
		 
		 return false;
		 
	});

}
board_accordion('.board > ul > li > a');


/* STAT ACCORDION */
function stat_accordion(selector){

	$(selector).on('click',function(){
		 
		 if($(this).siblings('.stat_accordion_content:visible').length > 0){
	     
			 $(this).siblings('.stat_accordion_content').stop().slideUp();		 
		 
		 }else{
			 
			 $(this).siblings('.stat_accordion_content').stop().slideDown();
		 
		 }
		 
		 return false;
		 
	});

}
stat_accordion('#stat_options_list > ul > li > a');



/***************************************/
/* CLOUD COMPONENT */
/***************************************/
function init_cloud(){

	$('.cloud').each(function(){
		
		var $this = $(this);
		var data = $this.attr('data-cloud');
		data = $.parseJSON(data);
		
		$this.jQCloud(data);
	
	})

}

init_cloud();

$(window).resize(function(){ 
    
	var $cloud = $('.cloud');
	var $cloud_parent = $cloud.parent();
	var cloud_data = $cloud.attr('data-cloud') ;
	
	$cloud.remove();
	$cloud_parent.prepend("<div id='cloud' class='cloud' data-cloud='"+cloud_data+"'></div>");
	init_cloud();

});



/***************************************/
/* GOOGLE COLUMN CHART */
/***************************************/
function draw_column_chart() {

	var el = document.getElementById('columnchart_01');
	
	if(el){
		
		var data_head = ['Element', 'Density', { role: 'annotation' } ];
		var data = el.getAttribute("data-column") ;
        var height = 300;
		
		if(is_large_screen()){
		    height = 140;
		}
		
		data = $.parseJSON(data);
		data.unshift(data_head);

		var chart_data = google.visualization.arrayToDataTable(data);

		var options = {
			title: "",
			width: el.innerWidth,
			height: height,
			bar: {groupWidth: '10%'},
			chartArea: {'width': '85%', 'height': '80%'},
			legend: { position: 'none' },
			animation:{ duration: 1000, easing: 'out'}
		};
		var chart = new google.visualization.ColumnChart(el);
		chart.draw(chart_data, options);
		
	}
}
google.setOnLoadCallback(draw_column_chart);

$(window).resize(draw_column_chart);

/***************************************/
/* GOOGLE COLUMN CHART MESSAGE */
/***************************************/
function draw_column_chart2() {

	var el = document.getElementById('columnchart_02');
	
	if(el){
		
		 var data_head = ['', '전체', 'SMS', 'SNS'];
		 var data = el.getAttribute("data-column") ;
		 
		 data = $.parseJSON(data);
		 data.unshift(data_head);
		
		
		 var data = google.visualization.arrayToDataTable(data);

		  var options = {
			hAxis: {title: ''}
		  };
		
		  var chart = new google.visualization.ColumnChart(el);
		
		  chart.draw(data, options);
		
	}
}
google.setOnLoadCallback(draw_column_chart2);

$(window).resize(draw_column_chart2);


/***************************************/
/* GOOGLE AREA CHART */
/***************************************/
function draw_area_chart() {
	
	var el = document.getElementById('area_chart');
	
	if(el){

		var data = el.getAttribute("data-area") ;
		data = $.parseJSON(data);

        var size = {height : 320}
		
		if(is_large_screen()){
			size = {width: 250 , height: 105}
		}

		var data = google.visualization.arrayToDataTable(data);

		var options = {
			vAxis: {minValue: 0},
			chartArea: size
		};

		var chart = new google.visualization.AreaChart(el);
		chart.draw(data, options);
		
	}
	
}
google.setOnLoadCallback(draw_area_chart);

$(window).resize(draw_area_chart);


/***************************************/
/* SHOW MENU ON SWIPE LEFT */
/***************************************/
function swipe_menu(){

	var xDown = null;                                                        
	var yDown = null;                                                        
	
	var el = document.getElementById("main_swipe_before")
	
	if(el){
		
		function handleTouchStart(evt) {                                         
			xDown = evt.touches[0].clientX;                                      
			yDown = evt.touches[0].clientY;                                      
		};                                                
		
		function handleTouchMove(evt) {
			if ( ! xDown || ! yDown ) {
				return;
			}
		
			var xUp = evt.touches[0].clientX;                                    
			var yUp = evt.touches[0].clientY;
		
			var xDiff = xDown - xUp;
			var yDiff = yDown - yUp;
		
			if ( Math.abs( xDiff ) > Math.abs( yDiff ) ) {/*most significant*/
				if ( xDiff > 0 ) {
				    /* left swipe */ 
				}else{
					$('#menu_container, #overlay').addClass('open');
				}                   
			} else {
				if ( yDiff > 0 ) {
					/* up swipe */ 
				} else { 
					/* down swipe */
				}                                                                 
			}
			/* reset values */
			xDown = null;
			yDown = null;                                             
		};
		
		el.addEventListener('touchstart', handleTouchStart, false);        
		el.addEventListener('touchmove', handleTouchMove, false);
		
	}
	
}
swipe_menu();



/***************************************/
/* INIT SWIPE */
/***************************************/

/* main swipe*/
/*
mainSwipe = $('#main_swipe').Swipe({  
    auto: 0,  
    continuous: false,
	bounce: false,
	callback: function(index, elem) {
			
		var num_slides = mainSwipe.getNumSlides();
		num_slides = num_slides -1;
		
		$('#main_swipe_nav span').removeClass('current');
		$('#main_swipe_nav span:eq('+index+')').addClass('current');
		
	}
}).data('Swipe'); 
*/
/*
$('.main #btn_news').on('click', function(){
   mainSwipe.slide(1, 500);
   return false;
});
*/

/*
$('#main_swipe_prev').on('click', mainSwipe.prev);
$('#main_swipe_next').on('click', mainSwipe.next);
*/


 


/***************************************/
/* LOAD MORE */
/***************************************/
 // load more
var double_click_check = false;
var post_per_page = parseInt($('.paging_loadmore a').attr('data-posts'));
var total_posts = $('.paging_loadmore a').attr('data-total');
var posts_count= post_per_page;
var page_count = 2;

if(total_posts <= post_per_page){
	$('#load_more').hide();
}

$('.paging_loadmore a').click( function() {
  
  var $container = $('.paging_loadmore');
  
  $container.addClass("loading");
  
  if(double_click_check == false){	
			
		double_click_check = true;
		
		var next_page_url = $(this).attr('href')+'?page='+page_count+'/';
		var append_target = $(this).attr('data-appendto');

		$.get(next_page_url, function( response, status, xhr ) {

			  //If ajax Error
			  if (status == "error") { 
				var msg = "Sorry but there was an error: "; 
				$container.html("<span id='load_error'>"+msg + xhr.status + " " + xhr.statusText+"</span>");
				
			  //If no more posts
			  }else if(response === "" || response === null) {
				$container.fadeOut().remove();
				//$("#load_more").html("<span id='no_more'>No more posts</span>");
				
			  //Else if the post are display	
			  }else{
				
				$(append_target).append(response);
				
				board_accordion('.board > ul > li > a');
				  
				$container.removeClass("loading");
				page_count++
				double_click_check = false;						 
				 
				 // remove loadmore btn if no more post 
				 posts_count = posts_count+post_per_page;
				 if(posts_count >= total_posts){				       
					 $container.remove();
				 }//endif
			  }//endif
				  
		});// End $.get
  }
  
  return false;
  
});




/***************************************/
/* STAT OPTIONS */
/***************************************/

// Add option
$('#stat_options_list .stat_accordion_content li').click(function(){

    var $this = $(this);    

    if(!$this.hasClass('selected')){
        
		 var text = $this.text();
		 var id = $this.attr('data-id');
		 
		 $('#stat_cart_list').append('<li data-id="'+id+'">'+text+'<span class="remove"></span></li>');
		 $this.addClass('selected');
		 
		 remove_stat_option();
		   
   }

})

// Remove option
function remove_stat_option(){

	$('#stat_cart_list li .remove').click(function(){
		 
		 var $this = $(this).parent();
		 var id = $this.attr('data-id');
		 $this.css('background','#E4A9A9').fadeOut(200,function(){
			 $this.remove();			 
			 $('#stat_options_list .stat_accordion_content li[data-id="'+id+'"]').removeClass('selected');
		 })
	})

}
remove_stat_option()


/***************************************/
/* MODAL */
/***************************************/
function init_modal(){
    
	$('body').append('<div class="modal_overlay"></div>');
	$('.modal_component').prepend('<a class="modal_close_btn" href="#">×</a>')
	open_modal('.add_position, .add_equipment');
    close_modal('.modal_close_btn');

}
init_modal();

function open_modal(btn_id){
	
	$(btn_id).click(function(){		
		
		$('.modal_overlay, .modal_component').show();	
		maps_init_08();		
		return false;
		
	})
	
}

function close_modal(btn_id){
	
	$(btn_id).click(function(){
							 
		$('.modal_overlay, .modal_component').hide();
		return false;
		
	})
	
}


/***********************************************/
/* TABS */
/***********************************************/
function tabs(el){
	
	$(el).each(function(){
	    
		var $this = $(this);
		
		$this.find('> div > div').hide();
		$this.find('> div > div:first').show();
		$this.find('> ul > li:first').addClass('active');
		 
		$this.find('> ul li a').click(function(){
			
			$that = $(this);
			$this.find('> ul li').removeClass('active');
			$that.parent().addClass('active');
			var target_index = $that.parent().index();
			console.log(target_index)
			$this.find('> div > div').hide();
			$this.find('> div > div:eq('+target_index+')').show();
			
			return false;
		
		});
		
	})
}
//Init
tabs('.tabs_component');
	

/***************************************/
/* LARGE SCREEN JS */
/***************************************/

/* IS LARGE SCREEN HELPER */
function is_large_screen(){
	
    if($(window).width() > 1000){
	    return true;
	}else{
	    return false;
	}
	
} 

if(is_large_screen()){

	/* REMOVE CLICK EVENT ON MOBILE */ 
	$('.main_nav a').off('click');
	
	
	/* INSPECTION ACCORDION TO CONTENT SWITCHER*/
	$('.inspector .board > ul > li > a').off('click');
	
    $(".inspector .list_component").after('<section class="components info_component">');
	
	$(".inspector .info_component").html($('.inspector .board > ul > li:nth-child(2) > a').siblings('.accordion_content').html());
	
	$('.inspector .board > ul > li > a').click(function(){
		
		var html = $(this).siblings('.accordion_content').html();
		$('.info_component').html(html);
		
		return false;
		
	})
	

		
}

}) // END jQuery
