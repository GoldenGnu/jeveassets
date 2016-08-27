<?php
include 'conn.php';

//Get versions
$dbh = con();
$version_array = getArray($dbh, 'version');
$java_array = getArray($dbh, 'java');
$os_array = getArray($dbh, 'os');

$admin = null !== filter_input(INPUT_GET, 'admin');

$order_in = filter_input(INPUT_GET, 'order');
$order = makeSafe(strtolower($order_in), array("date", "count", "id", "status"), 'date');

$desc_in = filter_input(INPUT_GET, 'desc');
$desc = makeSafe(strtoupper($desc_in), array("DESC", "ASC"), 'DESC');

$version_in = filter_input(INPUT_GET, 'version');
$version = makeSafe($version_in, $version_array, 'All');

$java_in = filter_input(INPUT_GET, 'java');
$java = makeSafe($java_in, $java_array, 'All');

$os_in = filter_input(INPUT_GET, 'os');
$os = makeSafe($os_in, $os_array, 'All');

$wontfix = "hide" === filter_input(INPUT_GET, 'wontfix');
$reopened = "hide" === filter_input(INPUT_GET, 'reopened');
$new = "hide" === filter_input(INPUT_GET, 'new');
$accepted = "hide" === filter_input(INPUT_GET, 'accepted');
$started = "hide" === filter_input(INPUT_GET, 'started');
$fixed = "hide" === filter_input(INPUT_GET, 'fixed');
$fixreleased = "hide" === filter_input(INPUT_GET, 'fixreleased');
$compact = "hide" === filter_input(INPUT_GET, 'compact');
$javabug = "hide" === filter_input(INPUT_GET, 'javabug');

$edit = filter_input(INPUT_POST, 'edit');
if ($edit == password()) {
	$id = filter_input(INPUT_POST, 'id');
	$status = filter_input(INPUT_POST, 'status');
	update($dbh, $id, $status);
	header("Location: .?".$_SERVER['QUERY_STRING']."#bugid".$id);
}

$delete = filter_input(INPUT_POST, 'delete');
if ($delete == password()) {
	$id = filter_input(INPUT_POST, 'id');
	delete($dbh, $id, $status);
	header("Location: .?".$_SERVER['QUERY_STRING']);
}

?><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
	<meta http-equiv="cache-control" content="max-age=0" />
	<meta http-equiv="cache-control" content="no-cache" />
	<meta http-equiv="expires" content="0" />
	<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />
	<meta http-equiv="pragma" content="no-cache" />
	<title><?php echo name() . ' Bug Database' ?></title>
	<link rel="icon" type="image/png" href="favicon.ico" />
	<link href="https://fonts.googleapis.com/css?family=Jura" rel="stylesheet"> 
	<link href="https://fonts.googleapis.com/css?family=Press+Start+2P" rel="stylesheet"> 
	<style>
		h1 {
			font-family: 'Press Start 2P', cursive;
		}
		label, .status {
			font-family: 'Jura', sans-serif;
		}
	</style>
	<script> 
	function toggle(id, name) {
		var otherName;
		if (name === 'delete') {
			otherName = 'edit';
		} else {
			otherName = 'delete';
		}
		var ele = document.getElementById(name + id);
		if(document.getElementById(name + id).style.display === "inline") {
			//Hide span
			document.getElementById(name + id).style.display = "none";
			//Update text
			document.getElementById('button' + name + id).value = document.getElementById('button' + name + id).value.toString().replace("Hide", "Show");
			//Enable both buttons
			document.getElementById('button' + name + id).disabled = false;
			document.getElementById('button' + otherName + id).disabled = false;
			//document.getElementById('button' + name + id).style.display = "inline";
			
			
			//document.getElementById('button' + otherName + id).style.display = "inline";
		} else {
			//Show span
			document.getElementById(name + id).style.display = "inline";
			
			document.getElementById('button' + name + id).value = document.getElementById('button' + name + id).value.toString().replace("Show", "Hide");
			//Hide other span
			document.getElementById(otherName + id).style.display = "none";
			//Disable other button
			//document.getElementById('button' + otherName + id).style.display = "none";
			document.getElementById('button' + otherName + id).disabled = true;
		}
	}
	function showhide(elementId) {
		if(document.getElementById(elementId).style.display === "inline") {
			//Hide span
			document.getElementById(elementId).style.display = "none";
		} else {
			//Show span
			document.getElementById(elementId).style.display = "inline";
		}
	}
	function admin() {
		if (window.location.search.indexOf("admin") < 0) {
			window.location.search += "&admin=admin";
		} else {
			window.location.search = window.location.search.replace("?admin=admin", "").replace("&admin=admin", "");
		}
	}
	window.onkeyup = function(e) {
		var key = e.keyCode ? e.keyCode : e.which;
		if (key === 65 && e.altKey) {
			admin();
		}
	};
 
window.addEventListener('load', function(){
 	var box1 = document.getElementById('title');
	var startx = 0;
	var dist = 0;
	var minus = 0;
	var plus = 0;

	box1.addEventListener('touchstart', function(e){
		var touchobj = e.changedTouches[0]; // reference first touch point (ie: first finger)
		startx = parseInt(touchobj.clientX); // get x position of touch point relative to left edge of browser
		dist = 0;
		minus = 0;
		plus = 0;
		e.preventDefault();
	}, false);

	box1.addEventListener('touchmove', function(e){
		var touchobj = e.changedTouches[0]; // reference first touch point for this event
		var travled = parseInt(touchobj.clientX) - startx;
		if (travled < 0) {
			minus = Math.min(minus, travled);
		} else {
			plus = Math.max(plus, travled);
		}
		dist = Math.abs(minus) + plus;
		e.preventDefault();
	}, false);

	box1.addEventListener('touchend', function(e){
		if (Math.abs(dist) > 100) {
			admin();
		}
		e.preventDefault();
	}, false);
 
}, false);
 
</script>
</head>
<body>
	<h1 id="title"><?php echo name() . ' Bug Database' ?></h1>
	<hr>
<?php
echo '<form method="get" action="." style="display: inline;">'.PHP_EOL;
echo '<input type="submit" value="Reset">'.PHP_EOL;
echo '</form>'.PHP_EOL;

echo '<form method="get" action="" style="display: inline;">'.PHP_EOL;

echo '&nbsp;&nbsp;&nbsp;<label>'.PHP_EOL;
echo 'Version'.PHP_EOL;
select($version_array, 'version', $version);
echo '</label>'.PHP_EOL;

echo '&nbsp;&nbsp;&nbsp;<label>'.PHP_EOL;
echo 'Java'.PHP_EOL;
select($java_array, 'java', $java);
echo '</label>'.PHP_EOL;

echo '&nbsp;&nbsp;&nbsp;<label>'.PHP_EOL;
echo 'OS'.PHP_EOL;
select($os_array, 'os', $os);
echo '</label>'.PHP_EOL;

echo '&nbsp;&nbsp;&nbsp;<label>'.PHP_EOL;
echo 'Order By'.PHP_EOL;
select(array("Date", "Count", "ID", "Status"), 'order', $order);
select(array("DESC", "ASC"), 'desc', $desc);
echo '</label>'.PHP_EOL;

if ($admin) {
	echo '<input type="hidden" name="admin" value="admin">'.PHP_EOL;
}
checkbox($compact, 'compact', "Compact");
echo '<hr>'.PHP_EOL;
echo '<div>'.PHP_EOL;
checkbox($wontfix, 'wontfix', "Won't Fix");
checkbox($reopened, 'reopened', "Re-Opened");
checkbox($new, 'new', "New");
checkbox($accepted, 'accepted', "Accepted");
checkbox($started, 'started', "Started");
checkbox($fixed, 'fixed', "Fixed");
checkbox($fixreleased, 'fixreleased', "Fix Released");
checkbox($javabug, 'javabug', "Java Bugs");
echo '</div>'.PHP_EOL;
//echo '<input type="submit" value="Submit">'.PHP_EOL;
echo '</form>'.PHP_EOL;
echo "<hr>".PHP_EOL;

$sql = "SELECT * FROM ".table();
$and = false;
if ($version != 'All' || $java != 'All' || $os != 'All' || $wontfix || $reopened || $new || $accepted || $started || $fixed || $fixreleased || $javabug) {
	$sql = $sql . " WHERE ";
}
add_where($sql, $and, $version, 'version');
add_where($sql, $and, $java, 'java');
add_where($sql, $and, $os, 'os');
checkbox_where($sql, $and, $wontfix, '-2');
checkbox_where($sql, $and, $reopened, '-1');
checkbox_where($sql, $and, $new, '0');
checkbox_where($sql, $and, $accepted, '1');
checkbox_where($sql, $and, $started, '2');
checkbox_where($sql, $and, $fixed, '3');
checkbox_where($sql, $and, $fixreleased, '4');
search_where($sql, $and, $javabug, 'log', 'net.nikr');
$sql = $sql." ORDER BY $order $desc";

$statement = $dbh->prepare($sql);
$statement->execute();
$rows = $statement->fetchAll(PDO::FETCH_ASSOC);
foreach ($rows as &$row) {
	echo '<div style="width: 400px; float: left;">'.PHP_EOL;;
	switch ($row['status']) {
		case -2:
			echo '<span class="status" style="background: Gainsboro;">&nbsp;Won\'t Fix&nbsp;</span>';
			break;
		case -1:
			echo '<span class="status" style="background: #ff33ff;">&nbsp;Re-Opened&nbsp;</span>';
			break;
		case 0:
			echo '<span class="status" style="background: #cc3300;">&nbsp;New&nbsp;</span>';
			break;
		case 1:
			echo '<span class="status" style="background: #ff9933;">&nbsp;Accepted&nbsp;</span>';
			break;
		case 2:
			echo '<span class="status" style="background: #ffcc33;">&nbsp;Started&nbsp;</span>';
			break;
		case 3:
			echo '<span class="status" style="background: LightSkyBlue ;">&nbsp;Fixed&nbsp;</span>';
			break;
		case 4:
			echo '<span class="status" style="background: LimeGreen  ;">&nbsp;Fix Released&nbsp;</span>';
			break;
	}
	if (strpos(format($row['log']), 'net.nikr') === false) {
		echo ' <span class="status" style="color: #cc3300;">&nbsp;Java&nbsp;Bug&nbsp;</span>';
	}
	echo " <b>Date:</b> ".formatDate($row['date']);
	echo " <b>Count:</b> ";
	if ($row['count'] > 10) {
		echo '<span style="border: 3px #cc3300 solid; padding: 0px 1px 0px 1px">';
	} elseif ($row['count'] > 6) {
		echo '<span style="border: 3px #ff9933 solid; padding: 0px 1px 0px 1px">';
	} elseif ($row['count'] > 2) {
		echo '<span style="border: 3px #ffcc33 solid; padding: 0px 1px 0px 1px">';
	} else {
		echo "<span>";
	}
	echo format($row['count']);
	echo "</span>";
	echo ' <b>Id:</b> <a href="#bugid'.format($row['id']).'" id="bugid'.format($row['id']).'">'.format($row['id']).'</a>';
	echo "</div>".PHP_EOL;s;
	if ($admin) {
		echo '<div>';
		//Edit
		echo ' <input type="button" onclick="toggle(\''.$row['id'].'\', \'edit\')" id="buttonedit'.$row['id'].'" value="Show Edit" style="width: 110px;"><span id="edit'.$row['id'].'" style="display:none">';
		echo '<form method="post" action="" style="display: inline;">';
		echo ' <select name="status">';
		echo '<option value="-2">Won\'t Fix</option>';
		echo '<option value="-1">Re-Opened</option>';
		echo '<option value="0">New</option>';
		echo '<option value="1">Accepted</option>';
		echo '<option value="2">Started</option>';
		echo '<option value="3">Fixed</option>';
		echo '<option value="4">Fix Released</option>';
		echo '</select>';
		echo '<input type="hidden" name="id" value="'.$row['id'].'">';
		echo ' <input type="password" name="edit">';
		echo ' <input type="submit" value="Update" style="display: inline;">';
		echo '</form>';
		echo '</span>';
		//Delete
		echo ' <input type="button" onclick="toggle(\''.$row['id'].'\', \'delete\')" id="buttondelete'.$row['id'].'" value="Show Delete" style="width: 110px;"><span id="delete'.$row['id'].'" style="display:none">';
		echo '<form method="post" action="" style="display: inline;">';
		echo '<input type="hidden" name="id" value="'.$row['id'].'">';
		echo ' <input type="password" name="delete">';
		echo ' <input type="submit" value="Delete" style="display: inline;">';
		echo '</form>';
		echo '</span>';
		echo "</div>";
	}
	echo '<div style="clear: both;">';
	echo "<b>OS:</b> ".format_list($row['os'])."<br>";
	echo "<b>Java:</b> ".format_list($row['java'])."<br>";
	echo "<b>Version:</b> ".format_list($row['version'])."<br>";
	if ($compact) {
		echo "<b>Bug:</b><br>";
		echo format($row['log']);
	} else {
		echo "<b>Bug:</b> ".strtok($row['log'], "\n")."<br>";
		echo "<button type=\"button\" onclick=\"showhide('log".$row['id']."')\">Show Log</button><br><div id=\"log".$row['id']."\" style=\"display:none\">".format($row['log'])."</div><br>";
	}
	echo "<hr>";
	echo "</div>";
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
function formatDate($string) {
	$db_date = new DateTime($string);
    $today = new DateTime();
    $interval = $db_date->diff($today);
	$string = $interval->format('%a days ago');
	if ($string == '0 days ago') {
		return 'Today';
	} else {
		return $string;
	}
}
function format_list($string) {
	$string = str_replace(";", "&nbsp;&nbsp;&nbsp;&nbsp;", $string);
	return format_space($string); 
}
function select($values, $name, $selected) {
	echo '<select name="'.$name.'" onchange="this.form.submit()">'.PHP_EOL;
	foreach ($values as &$value) {
		$select = '';
		if (strtolower($selected) == strtolower($value)) {
			$select = ' selected="selected"';
		}
		echo '	<option value="'.$value.'"'.$select.'>'.$value.' </option>'.PHP_EOL;
	}
	echo "</select>".PHP_EOL;
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
function getArray($dbh, $column) {
	$statement = $dbh->prepare("SELECT $column FROM ".table());
	$statement->execute();
	$rows = $statement->fetchAll(PDO::FETCH_ASSOC);
	$array = array('All');
	foreach ($rows as &$row) {
		$temp = explode(';', $row[$column]);
		$temp = array_merge($temp, $array);
		$array = array_unique($temp);
	}
	sort($array);
	return $array;
}
function update($dbh, $id, $status) {
	$statement = $dbh->prepare("UPDATE ".table()." SET status=$status WHERE id=$id");
	$statement->execute();
}
function delete($dbh, $id) {
	$statement = $dbh->prepare("DELETE FROM ".table()." WHERE id=$id");
	$statement->execute();
}
function add_where(&$sql, &$and, $value, $column) {
	if ($value != 'All') {
		if ($and) {
			$sql = $sql . " AND ";
		}
		$sql = $sql . " $column regexp '(^|;)$value(;|$)' ";
		$and = true;
	}
}
function checkbox($ignore, $value, $name) {
	echo '<input type="hidden" name="'.$value.'" value="hide">'.PHP_EOL;
	$fg = 'Black';
	switch ($value) {
		case 'wontfix':
			$bg = 'Gainsboro';
			break;
		case 'reopened':
			$bg = '#ff33ff';
			break;
		case 'new':
			$bg = '#cc3300';
			break;
		case 'accepted':
			$bg = '#ff9933';
			break;
		case 'started':
			$bg = '#ffcc33';
			break;
		case 'fixed':
			$bg = 'LightSkyBlue';
			break;
		case 'fixreleased':
			$bg = 'LimeGreen';
			break;
		case 'javabug':
			if (!$ignore) {
				$fg = '#cc3300';
			}
			break;
	}

	if (isset($bg)) {
		if ($ignore) {
			//echo '<label style="color: '.$bg.'; border: 1px '.$bg.' solid; padding: 1px 3px 1px 3px;"><input type="checkbox" name="'.$value.'" value="show" onchange="this.form.submit()">'.$name.'</label>'.PHP_EOL;
			echo '<label style="border: 1px '.$bg.' solid; padding: 1px 3px 1px 3px; color: '.$fg.'"><input type="checkbox" name="'.$value.'" value="show" onchange="this.form.submit()">'.$name.'</label>'.PHP_EOL;
		} else {
			echo '<label style="background: '.$bg.'; border: 1px '.$bg.' solid; padding: 1px 3px 1px 3px; color: '.$fg.'""><input type="checkbox" name="'.$value.'" value="show" checked="checked" onchange="this.form.submit()">'.$name.'</label>'.PHP_EOL;
		}
	} else {
		if ($ignore) {
			echo '<label style="padding: 2px 4px 2px 4px; color: '.$fg.'""><input type="checkbox" name="'.$value.'" value="show" onchange="this.form.submit()">'.$name.'</label>'.PHP_EOL;
		} else {
			echo '<label style="padding: 2px 4px 2px 4px; color: '.$fg.'""><input type="checkbox" name="'.$value.'" value="show" checked="checked" onchange="this.form.submit()">'.$name.'</label>'.PHP_EOL;
		}
	}
}
function checkbox_where(&$sql, &$and, $ignore, $value) {
	if ($ignore) {
		if ($and) {
			$sql = $sql . " AND ";
		}
		$sql = $sql . " status != $value ";
		$and = true;
	}
}
function search_where(&$sql, &$and, $ignore, $column, $value) {
	if ($ignore) {
		if ($and) {
			$sql = $sql . " AND ";
		}
		$sql = $sql . " $column LIKE '%$value%' ";
		$and = true;
	}
}
?>
</body>
</html>