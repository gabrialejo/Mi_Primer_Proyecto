(function ($) {
  $.fn.zoomableGalaxyTab  = function (method,id) {
 
    return this.each(function (index, value) {
	  // restore data, if there is any for this element
      var zoomData;
	  if ($(this).data('zoomData') == null) {
		zoomData = {
		  busy: false,
		  x_fact: 0.9,
		  currentZoom: 1,
		  originalMap: null,
		  currentX: 0,
		  currentY: 0,
		  isSetIE: false,
		  minWidth: 0,
		  minHeight: 0
		  
		};
		$(this).data('zoomData', zoomData);
	  }
	  else
		zoomData = $(this).data('zoomData');
	  
	  var init = function() {
		if (value.useMap != "") {
		  var tempOriginalMap = document.getElementById("originalMap");
		  zoomData.originalMap = tempOriginalMap.cloneNode(true);
		  // for IE6, we need to manually copy the areas' coords
		  for (var i = 0; i < zoomData.originalMap.areas.length; i++)
			zoomData.originalMap.areas[i].coords = tempOriginalMap.areas[i].coords;
		}

		$(value).css('position', 'relative').css('left', '0').css('top', 0).css('margin', '0');




		// jquery mousewheel not working in FireFox for some reason
		if ($.browser.mozilla) {
		  value.addEventListener('DOMMouseScroll', function (e) {
			e.preventDefault();
			zoomMouse(-e.detail);
            zoomMap();

		  }, false);
		  if (value.useMap != "") {
		    $(value.useMap)[0].addEventListener('DOMMouseScroll', function (e) {
			  e.preventDefault();
			  zoomMouse(-e.detail);
              zoomMap();

			}, false);
		  }
		}
		else {
		  $(value).bind('mousewheel', function (e) {
		    e.preventDefault();
			zoomMouse(e.wheelDelta);
              zoomMap();
	      });
		  if (value.useMap != "") {
			$(value.useMap).bind('mousewheel', function (e) {
			  e.preventDefault();
			  zoomMouse(e.wheelDelta);
              zoomMap();
			});
		  }
		}

		$(value).bind('mousemove', function (e) {
		  zoomData.currentX = e.pageX - $(document).scrollLeft() - $('#imageCanvas').offset().left;
	      zoomData.currentY = e.pageY - $(document).scrollTop() - $('#imageCanvas').offset().top;
		});
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


        do
        {
            if((parseInt((value.width * zoomData.x_fact)) >= parseInt(520))||(parseInt((value.height * zoomData.x_fact)) >= parseInt(380)))
            {
                if (!zoomData.busy) {
        		    zoomFit(zoomData.x_fact, left()+parent.offsetLeft+(value.width/2), top()+parent.offsetTop+(value.height/2));

                }
            }else{
                break;
            }
        }
        while ((parseInt((value.width * zoomData.x_fact)) >= parseInt(520))||(parseInt((value.height * zoomData.x_fact)) >= parseInt(380)));

        zoomFitFinal(zoomData.x_fact, left()+parent.offsetLeft+(value.width/2), top()+parent.offsetTop+(value.height/2));

        zoomData.x_fact = 0.5;
        zoomData.minWidth = value.width;
        zoomData.minHeight = value.height;
    	zoomData.busy = false;

		dojo.byId("progressLoader").style.display = "none";
	  };

	  
      var zoomMouse = function (delta) {
   // zoom out ---------------
        if (delta < 0) {
          zoom(1 / zoomData.x_fact, zoomData.currentX, zoomData.currentY);
        }

        // zoom in -----------
        else if (delta > 0) {
          zoom(zoomData.x_fact, zoomData.currentX, zoomData.currentY);
        }
        
              };

      var zoomMap = function () {
        // resize image map
        var map = document.getElementById(value.useMap.substring(1));
        if (map != null) {
          for (var i = 0; i < map.areas.length; i++) {
            var area = map.areas[i];
            var originalArea = zoomData.originalMap.areas[i];
            var coords = originalArea.coords.split(',');
            for (var j = 0; j < coords.length; j++) {
                  coords[j] = Math.round((coords[j]) * zoomData.currentZoom);
            }

            dojo.style(dojo.byId('callout_'+i), {top: (value.offsetTop+coords[1]-2)+"px"});
            dojo.style(dojo.byId('callout_'+i), {left: (value.offsetLeft+coords[0]-2)+"px"});
            dojo.style(dojo.byId('callout_'+i), {width: (coords[2]-coords[0]+4)+"px"});
            dojo.style(dojo.byId('callout_'+i), {height: (coords[3]-coords[1]+4)+"px"});

            
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
          if((parseInt(new_w) >= parseInt(520))||(parseInt(new_h) >= parseInt(380))){
          
          zoomData.currentZoom = Math.round(zoomData.currentZoom * fact/0.001)*0.001;
          // calculate new X and y based on mouse position
          var parent = $(value).parent()[0];
          mouseX = mouseX - parent.offsetLeft
          var newImageX = (mouseX - xi) * fact;
          xi = mouseX - newImageX;

          mouseY = mouseY - parent.offsetTop
          var newImageY = (mouseY - yi) * fact;
          yi = mouseY - newImageY;


          dojo.style(dojo.byId('image'), {left: "0px"});
          dojo.style(dojo.byId('image'), {top: "0px"});
          dojo.style(dojo.byId('image'), {height: new_h+"px"});
          dojo.style(dojo.byId('image'), {width: new_w+"px"});
          zoomData.busy = false;
          //zoomMap();
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


          dojo.style(dojo.byId('image'), {left: "0px"});
          dojo.style(dojo.byId('image'), {top: "0px"});
          dojo.style(dojo.byId('image'), {height: new_h+"px"});
          dojo.style(dojo.byId('image'), {width: new_w+"px"});
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


          dojo.style(dojo.byId('image'), {left: xi+"px"});
          dojo.style(dojo.byId('image'), {top: yi+"px"});
          dojo.style(dojo.byId('image'), {height: new_h+"px"});
          dojo.style(dojo.byId('image'), {width: new_w+"px"});
          zoomData.busy = false;
          zoomMap();
          }else{
          dojo.style(dojo.byId('image'), {left: "0px"});
          dojo.style(dojo.byId('image'), {top: "0px"});
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