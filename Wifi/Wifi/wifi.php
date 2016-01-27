<?php
header("Content-Type:text/html; charset=utf-8");


$mysql_server_name='140.120.13.242'; //改成自己的mysql資料庫伺服器

$mysql_username='shaun'; //改成自己的mysql資料庫用戶名

$mysql_password='ask175'; //改成自己的mysql資料庫密碼

$mysql_database='mywifi'; //改成自己的mysql資料庫名
    
    $SSID=$_POST['ssid'];
    $LEVEL=$_POST['level'];
    $BSSID=$_POST['bssid'];
    $TIME=$_POST['time'];
    $ACCELER=$_POST['acceler'];
    $CHECK= $_POST['check'];
    $STEPS=$_POST['steps'];
    $DISTANCE=$_POST['distance'];
    $SPEED=$_POST['speed'];
    $DIRECTION=$_POST['direction']; 
    $TURN=$_POST['turn'];


    $SSIDary = json_decode($SSID);
    $LEVELary = json_decode($LEVEL);
    $BSSIDary = json_decode($BSSID);
    $TIMEary=json_decode($TIME);


    $conn=mysql_connect($mysql_server_name,$mysql_username,$mysql_password); //連接資料庫

mysql_query("SET NAMES 'UTF8'"); //資料庫輸出編碼

mysql_select_db($mysql_database); //打開資料庫


 if(@mysql_select_db("mywifi"))
        {
         echo "db is exist";
        }else{
               echo "db is not exist";
        }

  for ($i = 0; $i < count($SSIDary); $i++) {

        $tmp1 = mysql_real_escape_string($SSIDary[$i]);

        $tmp2 = mysql_real_escape_string($LEVELary[$i]);


        $tmp3 = mysql_real_escape_string($BSSIDary[$i]);
        $tmp4=mysql_real_escape_string($TIMEary[$i]);
        if($CHECK==0)
            $sql = "INSERT INTO wifi (SSID, Level,BSSID,TIME,STEPS,DISTANCE,SPEED,DIRECTION,TURN) VALUES ('$tmp1', '$tmp2','$tmp3','$tmp4','$STEPS','$DISTANCE','$SPEED','$DIRECTION','$TURN')";
        else
            $sql = "INSERT INTO wifi2 (SSID, Level,BSSID,TIME) VALUES ('$tmp1', '$tmp2','$tmp3','$tmp4')";
        $sql2="insert into accelerator(accelerate,time) values ('$ACCELER','$tmp4')";
        mysql_query($sql) or die(mysql_error());
        mysql_query($sql2) or die(mysql_error());
    } 


mysql_close(); //關閉MySQL連接




?>