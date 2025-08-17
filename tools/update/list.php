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
				&& (!str_ends_with($file, ".dat") || $file == "data.dat")
				&& !str_starts_with($file, "installer")
				&& !str_starts_with($file, ".")
				&& $file != "github.update"
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

?>