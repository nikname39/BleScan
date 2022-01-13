$( document ).ready(function() {
    // Create device
    var device = new Device("arduino.local");    
			
		
    // Button
    $('#LED1on').click(function() {
      
	  document.getElementById("DoorVal").innerHTML = "상태:열림";
    });
    $('#LED1off').click(function() {
      document.getElementById("DoorVal").innerHTML = "상태:닫힘";
    });

    
});