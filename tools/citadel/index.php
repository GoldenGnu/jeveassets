<?php

define("FILE", "citadel.json");

function fetchURL() {
	if (!file_exists(FILE)) { //New
		downloadPage();
	} elseif (file_exists(FILE) && ((filemtime(FILE) + 3600) < time())) { // Update every 3600 seconds (1 hour)
		downloadPage();
	}
	if (file_exists(FILE)) { // If cache exist
		ob_start(); //Object: start
		ob_start("ob_gzhandler"); //GZip: start
		echo file_get_contents(FILE); // Echo the file from cache.
		ob_end_flush(); //GZip: end
		header('Content-Length: '.ob_get_length()); //Set content length of the GZip object
		ob_end_flush(); //Object: end
	} else { // If all else fails: Redirect to the API
		header('Location: https://stop.hammerti.me.uk/api/citadel/all');
	}
}
function downloadPage() {
	ini_set('default_socket_timeout', 20); // 20 Seconds timeout
	$url = "https://stop.hammerti.me.uk/api/citadel/all";
	$file = file_get_contents($url); // Fetch the file
	if ($file !== false) { //Download successful 
		file_put_contents(FILE, $file, LOCK_EX); // Save the cache
	} else { //Download failed
		touch(FILE); //Let try again in an hour...
	}
}

fetchURL(); // Execute the function