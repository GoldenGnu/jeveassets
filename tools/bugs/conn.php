<?php
function con() {
	return new PDO('mysql:host=localhost;dbname=database', 'user', 'pass');
}
function table() {
	return "jeveasset";
}
function name() {
	return "jEveAssets";
}
function buglink() {
	return 'https://localhost';
}
function password() {
	return "pass";
}
?>