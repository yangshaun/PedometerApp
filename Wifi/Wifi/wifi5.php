<?php
header("Content-Type:text/html; charset=utf-8");


$mysql_server_name='140.120.13.242'; //改成自己的mysql資料庫伺服器

$mysql_username='shaun'; //改成自己的mysql資料庫用戶名

$mysql_password='ask175'; //改成自己的mysql資料庫密碼

$mysql_database='mywifi'; //改成自己的mysql資料庫名
    
    $VECTOR=$_POST['vector'];
    $TIME=$_POST['time'];
    $VECTORary = json_decode($VECTOR);
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

    for ($i = 0; $i < count($VECTORary); $i++) {

        $tmp1[] = mysql_real_escape_string($VECTORary[$i]);
        $tmp2 = mysql_real_escape_string($TIMEary[$i]);
        for($x=1;$x<count($tmp1);$x++){
            $tmp_tmp1+=","+$tmp1[$x];
        }
        echo $tmp_tmp1+"sdsdsd";
        $sql = "INSERT INTO wifi2 (TIME,VECTOR) VALUES ('$tmp2','$tmp_tmp1')";
        mysql_query($sql) or die(mysql_error());
     
    } 


mysql_close(); //關閉MySQL連接




?>