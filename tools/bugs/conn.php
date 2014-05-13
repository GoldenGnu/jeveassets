<?php
function con() {
	return new PDO('mysql:host=localhost;dbname=database', 'user', 'pass');
}
?>