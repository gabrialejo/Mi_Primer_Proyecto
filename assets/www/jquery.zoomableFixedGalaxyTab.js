(function ($) {
  $.fn.zoomableFixed = function (method,id) {
 
    return this.each(function (index, value) {
	  // restore data, if there is any for this element
      var zoomData;
	  if ($(this).data('zoomData') == null) {
		zoomData = {
		  busy: false,
		  x_fact: 0.5,
		  currentZoom: 1,
		  originalMapViews: null,
		  currentX: 0,
		  currentY: 0,
		  isSetIE: false,
          minWidth: 1024,
          minHeight: 600
		  
		};
		$(this).data('zoomData', zoomData);
	  }
	  else
		zoomData = $(this).data('zoomData');
	  
	  var init = function() {
		if (value.useMap != "") {
		  var temporiginalMapViews = document.getElementById("originalMapViews");
		  zoomData.originalMapViews = temporiginalMapViews.cloneNode(true);
		  // for IE6, we need to manually copy the areas' coords
		  for (var i = 0; i < zoomData.originalMapViews.areas.length; i++)
			zoomData.originalMapViews.areas[i].coords = temporiginalMapViews.areas[i].coords;
		}

		$(value).css('position', 'relative').css('left', '0').css('top', 0).css('margin', '0');

		// jquery mousewheel not working in FireFox for some reason
		if ($.browser.mozilla) {
		  value.addEventListener('DOMMouseScroll', function (e) {
			e.preventDefault();
			zoomMouse(-e.detail);

		  }, false);
		  if (value.useMap != "") {
		    $(value.useMap)[0].addEventListener('DOMMouseScroll', function (e) {
			  e.preventDefault();
			  zoomMouse(-e.detail);

			}, false);
		  }
		}
		else {
		  $(value).bind('mousewheel', function (e) {
		    e.preventDefault();
			zoomMouse(e.wheelDelta);
	      });
		  if (value.useMap != "") {
			$(value.useMap).bind('mousewheel', function (e) {
			  e.preventDefault();
			  zoomMouse(e.wheelDelta);
			});
		  }
		}
		
      };

	  var left = function() {
		return parseInt($(value).css('left'));
	  };
	  
	  var top = function() {
		return parseInt($(value).css('top'));
	  }
	  
	  var zoomIn = function() {
		// zoom as if mouse is in centre of image
		var parent = $(value).parent()[0];
		zoom(zoomData.x_fact, left()+parent.offsetLeft+(value.width/2), top()+parent.offsetTop+(value.height/2));
	  };
	  
	  var zoomOut = function() {
		// zoom as if mouse is in centre of image
        var yi = parseInt($(value).css('top'));
		var parent = $(value).parent()[0];
		zoom(1 / zoomData.x_fact, left()+parent.offsetLeft+(value.width/2), top()+parent.offsetTop+(value.height/2));
	  };

	  var zoomFitToControl = function() {
		// zoom until fit to div canvas
        var yi = parseInt($(value).css('top'));
		var parent = $(value).parent()[0];


        zoomFitFinal(zoomData.x_fact, left()+parent.offsetLeft+(value.width/2), top()+parent.offsetTop+(value.height/2));

        zoomData.x_fact = 0.5;

    	zoomData.busy = false;
        
        dojo.query("#fi").style({left:(dojo.byId("externalImage").offsetLeft-100)+"px"});
        dojo.query("#fd").style({left:(dojo.byId("externalImage").offsetLeft+dojo.byId("externalImage").width-10)+"px"});
        
		
	  };

	  
      var zoomMouse = function (delta) {
   // zoom out ---------------
        if (delta < 0) {
            previousView();
        }

        // zoom in -----------
        else if (delta > 0) {
            nextView();
        }
        
              };

      var zoomMap = function () {
        // resize image map
        var map = document.getElementById(value.useMap.substring(1));
        if (map != null) {
          for (var i = 0; i < map.areas.length; i++) {
            var area = map.areas[i];
            var originalArea = zoomData.originalMapViews.areas[i];
            var coords = originalArea.coords.split(',');
            for (var j = 0; j < coords.length; j++) {
              if(dojo.isChrome){
                  coords[j] = Math.round((coords[j]) * zoomData.currentZoom);
              }else{
                  coords[j] = Math.round((coords[j]) * zoomData.currentZoom);
              }
            }
            if(dojo.isIE){
                if(!zoomData.isSetIE){
                    dojo.style(dojo.byId('viewcallout_'+i), {top: (coords[1]-4)+"px"});
                    zoomData.isSetIE = true;
                }else{
                    dojo.style(dojo.byId('viewcallout_'+i), {top: (value.offsetTop+coords[1]-4)+"px"});
                }
                dojo.style(dojo.byId('viewcallout_'+i), {left: (value.offsetLeft+coords[0]-4)+"px"});
            }else{
                dojo.style(dojo.byId('viewcallout_'+i), {top: (value.offsetTop+coords[1]-4)+"px"});
                dojo.style(dojo.byId('viewcallout_'+i), {left: (value.offsetLeft+coords[0]-4)+"px"});
            }
            
            dojo.style(dojo.byId('viewcallout_'+i), {width: (coords[2]-coords[0]+8)+"px"});
            dojo.style(dojo.byId('viewcallout_'+i), {height: (coords[3]-coords[1]+8)+"px"});

            
            var coordsString = "";
            for (var k = 0; k < coords.length; k++) {
              if (k > 0)
                coordsString += ",";
              coordsString += 0;
            }
            area.coords = coordsString;
          }
        }
      };


      var zoomFit = function (fact, mouseX, mouseY) {
       if (!zoomData.busy) {
          zoomData.busy = true;

          var xi = left();
          var yi = top();

          mouseX = Math.round(mouseX/10)*10;
          mouseY = Math.round(mouseY/10)*10;
          
          var new_h = (value.height * fact);
          var new_w = (value.width * fact);
          if((parseInt(new_w) >= parseInt(zoomData.minWidth))||(parseInt(new_h) >= parseInt(zoomData.minHeight))){
          
          zoomData.currentZoom = Math.round(zoomData.currentZoom * fact/0.001)*0.001;
          // calculate new X and y based on mouse position
          var parent = $(value).parent()[0];
          mouseX = mouseX - parent.offsetLeft
          var newImageX = (mouseX - xi) * fact;
          xi = mouseX - newImageX;

          mouseY = mouseY - parent.offsetTop
          var newImageY = (mouseY - yi) * fact;
          yi = mouseY - newImageY;


          dojo.style(value, {left: "0px"});
          dojo.style(value, {top: "0px"});
          dojo.style(value, {height: new_h+"px"});
          dojo.style(value, {width: new_w+"px"});
          zoomData.busy = false;
          zoomMap();
          }else{
            zoomData.busy = false;
          }

        }
      };


      var zoomFitFinal = function (fact, mouseX, mouseY) {
       if (!zoomData.busy) {
          zoomData.busy = true;

          var xi = left();
          var yi = top();

          mouseX = Math.round(mouseX/10)*10;
          mouseY = Math.round(mouseY/10)*10;
          
          var new_h = (value.height * fact);
          var new_w = (value.width * fact);
          
          zoomData.currentZoom = Math.round(zoomData.currentZoom * fact/0.001)*0.001;
          // calculate new X and y based on mouse position
          var parent = $(value).parent()[0];
          mouseX = mouseX - parent.offsetLeft
          var newImageX = (mouseX - xi) * fact;
          xi = mouseX - newImageX;

          mouseY = mouseY - parent.offsetTop
          var newImageY = (mouseY - yi) * fact;
          yi = mouseY - newImageY;

          var topfix = 323;
          if(actualView==3){
            topfix = 323;
          }else if(actualView==5){
            topfix = 365;
          } 
          var topn =  (new_h - topfix)-16;
          
          dojo.style(value, {left: "0px"});
          dojo.style(value, {top: topn.toString()+"px"});
          dojo.style(value, {height: new_h+"px"});
          dojo.style(value, {width: new_w+"px"});
          zoomData.busy = false;
          zoomMap();

        }
      };     
      
      
       var zoom = function (fact, mouseX, mouseY) {
        if (!zoomData.busy) {
          zoomData.busy = true;

          var xi = left();
          var yi = top();

          mouseX = Math.round(mouseX/10)*10;
          mouseY = Math.round(mouseY/10)*10;
          
          var new_h = (value.height * fact);
          var new_w = (value.width * fact);
          if(((Math.round(new_w/100)*100) >= (Math.round(zoomData.minHeight/100)*100))||((Math.round(new_h/100)*100) >= (Math.round(zoomData.minHeight/100)*100))){
          
          zoomData.currentZoom = Math.round(zoomData.currentZoom * fact/0.001)*0.001;
          // calculate new X and y based on mouse position
          var parent = $(value).parent()[0];
          mouseX = mouseX - parent.offsetLeft
          var newImageX = (mouseX - xi) * fact;
          xi = mouseX - newImageX;

          mouseY = mouseY - parent.offsetTop
          var newImageY = (mouseY - yi) * fact;
          yi = mouseY - newImageY;


          dojo.style(value, {left: xi+"px"});
          dojo.style(value, {top: yi+"px"});
          dojo.style(value, {height: new_h+"px"});
          dojo.style(value, {width: new_w+"px"});
          zoomData.busy = false;
          zoomMap();
          }else{
          dojo.style(value, {left: "0px"});
          dojo.style(value, {top: "0px"});
          zoomMap();
            zoomData.busy = false;
          }

        }
      };
	  
	  if (method == "zoomIn")
		zoomIn();
	  else if (method == "zoomOut")
		zoomOut();
	  else if (method == "zoomFitToControl")
		zoomFitToControl();
	  else
		init();
    });
  };
})(jQuery);