<?php

define("FILE", "citadel.json");

function fetchURL() {
	//Update
	if (isset($_GET["update"])) { //Cron job
		logToFile("Updating CRON");
		downloadPage();
	} elseif (!file_exists(FILE)) { //New
		logToFile("Updating NEW");
		downloadPage();
	} elseif (file_exists(FILE) && ((filemtime(FILE) + 4500) < time())) { // Update every 4500 seconds (1 hour and 15 minutes)
		logToFile("Updating CACHE");
		downloadPage();
	}
	//Serve
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
		logToFile("Update SUCCESSFUL");
		file_put_contents(FILE, $file, LOCK_EX); // Save the cache
	} else { //Download failed
		logToFile("Update FAILED");
		touch(FILE); //Lets try again later
	}
}
function logToFile($log) {
	file_put_contents('log-'.date("Y-m-d").'.txt', date("Y-m-d H:i").": ".$log.PHP_EOL, FILE_APPEND);
}

fetchURL(); // Execute the function