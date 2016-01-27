<?php
header("Content-Type:text/html; charset=utf-8");


$mysql_server_name='140.120.13.242'; //改成自己的mysql資料庫伺服器

$mysql_username='shaun'; //改成自己的mysql資料庫用戶名

$mysql_password='ask175'; //改成自己的mysql資料庫密碼

$mysql_database='mywifi'; //改成自己的mysql資料庫名
    
$conn=mysql_connect($mysql_server_name,$mysql_username,$mysql_password); //連接資料庫

mysql_query("SET NAMES 'UTF8'"); //資料庫輸出編碼

mysql_select_db($mysql_database); //打開資料庫



 $q=mysql_query("SELECT  BSSID,Level FROM wifi ");

 while($e=mysql_fetch_assoc($q))
    $output[]=$e;

 print(json_encode($output));



mysql_close(); //關閉MySQL連接




?>