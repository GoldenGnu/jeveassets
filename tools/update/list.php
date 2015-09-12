<?php

dirToArray(__DIR__);

function dirToArray($dir, $fix = "", &$first = true) {
	$handle = opendir($dir);
	while (($file = readdir($handle)) !== false) {
		if ($file == '.' || $file == '..') {
			continue;
		}
		if (is_dir($dir . DIRECTORY_SEPARATOR . $file)) {
			dirToArray($dir . DIRECTORY_SEPARATOR . $file, $fix . $file . "/", $first);
		} elseif (!str_ends_with($file, ".php") 
				&& !str_ends_with($file, ".md5")
				&& !str_ends_with($file, ".dat")
				&& !str_starts_with($file, "installer")
				) {
			if ($first) {
				$first = false;
			} else {
				print "\r\n";
			}
			print $fix . $file;
		}
	}
	closedir($handle);
}

function str_ends_with($haystack, $needle) {
	// search forward starting from end minus needle length characters
	return $needle === "" || (($temp = strlen($haystack) - strlen($needle)) >= 0 && strpos($haystack, $needle, $temp) !== FALSE);
}

function str_starts_with($haystack, $needle) {
    // search backwards starting from haystack length characters from the end
    return $needle === "" || strrpos($haystack, $needle, -strlen($haystack)) !== FALSE;
}

?>