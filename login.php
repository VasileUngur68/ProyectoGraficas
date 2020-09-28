<?php
$hostname='localhost';
$database='graficas';
$username='root';
$password='';
$con = new mysqli($hostname,$username,$password,$database);

$user  = $_POST["usuario"];
$pass  = $_POST["clave"];
$statement =mysqli_prepare($con,"SELECT * FROM identidad WHERE usuario = ? AND password = ?");
mysqli_stmt_bind_param($statement,"ss",$user,$pass);
mysqli_stmt_execute($statement);

mysqli_stmt_store_result($statement);
mysqli_stmt_bind_result($statement,$idusuario,$usuario,$clave);
 //La respuesta de la base de datos la codifico en json
 //El parametro que se envia se llama "success" y es de tipo boolean
$response =array();
$response["success"] = false;

while(mysqli_stmt_fetch($statement)){
  $response["success"]=true;
  $response["usuario"]=$usuario;
  $response["clave"]=$clave;
}
  //Se responde en formato json
echo json_encode($response);
?>
