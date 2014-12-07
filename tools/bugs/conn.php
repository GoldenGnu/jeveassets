<?php
function con() {
	return new PDO('mysql:host=localhost;dbname=database', 'user', 'pass');
}
function table() {
	return "jeveasset";
}
function name() {
	return "jEveAssets Bug Database";
}
?>