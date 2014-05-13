<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
	<meta http-equiv="cache-control" content="max-age=0" />
	<meta http-equiv="cache-control" content="no-cache" />
	<meta http-equiv="expires" content="0" />
	<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />
	<meta http-equiv="pragma" content="no-cache" />
	<title>jEveAssets Bug Database</title>
	<link rel="icon" type="image/png" href="favicon.ico" />
	<script> 
	function toggle(elementId) {
		var ele = document.getElementById(elementId);
		if(ele.style.display == "block") {
			ele.style.display = "none";
		} else {
			ele.style.display = "block";
		}
	}
	</script>
</head>
<body>
	<h1>jEveAssets Bug Database</h1>
	<hr>
<?php
include 'conn.php';

$order_in = filter_input(INPUT_POST, 'order');
$order = makeSafe(strtolower($order_in), array("date", "count", "id"), 'date');

$desc_in = filter_input(INPUT_POST, 'desc');
$desc = makeSafe(strtoupper($desc_in), array("DESC", "ASC"), 'DESC');

echo '<form method="post" action="">';
select(array("Date", "Count", "ID"), 'order', $order);
select(array("DESC", "ASC"), 'desc', $desc);
echo '<input type="submit" value="Submit">';
echo '</form">';
echo "<hr>";

$dbh = con();
$statement = $dbh->prepare("SELECT * FROM jeveasset ORDER BY $order $desc");
$statement->execute();
$rows = $statement->fetchAll(PDO::FETCH_ASSOC);
foreach ($rows as &$row) {
	echo " <b>Status:</b> ";
	switch ($row['status']) {
		case -1:
			echo "Re-Opened";
			break;
		case 0:
			echo "New";
			break;
		case 1:
			echo "Accepted";
			break;
		case 2:
			echo "Started";
			break;
		case 3:
			echo "Fixed";
			break;
		case 4:
			echo "Released";
			break;
	}
	echo " <b>Date:</b> ".format($row['date']);
	echo " <b>Count:</b> ".format($row['count']);
	echo " <b>Id:</b> ".format($row['id'])."<br>";
	echo "<b>OS:</b> ".format_list($row['os'])."<br>";
	echo "<b>Java:</b> ".format_list($row['java'])."<br>";
	echo "<b>Version:</b> ".format_list($row['version'])."<br>";
	echo "<button type=\"button\" onclick=\"toggle('log".$row['id']."')\">Show Log</button><br><div id=\"log".$row['id']."\" style=\"display:none\">".format($row['log'])."</div><br>";
	echo "<hr>";
}

function format_space($string) {
	$string = preg_replace('/[\r\n]+/', '<br>', $string);
	$string = preg_replace('/\t/', '&nbsp;&nbsp;&nbsp;&nbsp;', $string);
	$string = preg_replace('/\s/', '&nbsp;', $string);
	return $string;
}

function format($string) {
	$string = htmlentities($string);
	$string = format_space($string);
	return $string;
}
function format_list($string) {
	$string = str_replace(";", "&nbsp;&nbsp;&nbsp;&nbsp;", $string);
	return format_space($string); 
}
function select($values, $name, $selected) {
	print '<select name="'.$name.'">';
	foreach ($values as &$value) {
		$select = '';
		if (strtolower($selected) == strtolower($value)) {
			$select = ' selected="selected"';
		}
		print '<option value="'.$value.'"'.$select.'>'.$value.' </option>';
	}
	print "<select>";
}
function makeSafe($find, $in, $default) {
	$key = array_search($find, $in);
	$value = $in[$key];
	if (empty($value)) {
		return $default;
	} else {
		return $value;
	}
}
?>
</body>
</html>