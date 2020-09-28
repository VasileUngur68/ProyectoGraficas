<?php
$hostname='localhost';
$database='graficas';
$username='root';
$password='';
$con = new mysqli($hostname,$username,$password,$database);

$fechahora= $_POST["fechahora"];

if ($con->connect_errno) {
    echo "Falló la conexión a MySQL: (" . $con->connect_errno . ") " . $con->connect_error;
}

$consulta = $con->query("SELECT * FROM `datos` WHERE `date` >='$fechahora'ORDER BY `date`");

$i=0;

$ar["success"]=false;
// Se inicializa el array as a null por si no hay datos para que no presente un warning
$as=null;
while ($fila = $consulta->fetch_assoc()) {
     $i++;
     //Se pone a true por recojer el valor booleano en java
     $ar["success"]=true;
     //Se añaden los campos a cada array
     $date["fecha".$i]=$fila['date'];
     $temp["temperatura".$i]=$fila['temperatura'];
     $hum["humedad".$i]=$fila['humedad'];
     $codos["nivelCO2".$i]=$fila['nivelCO2'];
     $mov["movimiento".$i]=$fila['movimiento'];
     $luzext["luzExterior".$i]=$fila['luzExterior'];
     $luzsalon["luzSalon".$i]=$fila['luzSalon'];
     $vent["ventanas".$i]=$fila['ventanas'];
     $puert["puertas".$i]=$fila['puertas'];
     $ar["num"]=$i;
     //Se concatenan los arrays en el array as
     //Por cada bucle se cambia el valor del $ar
     $as=array_merge($ar,$date,$temp,$hum,$codos,$mov,$luzext,$luzsalon,$vent,$puert);

}
 if($as==null){
 $as=$ar;
 }
//Se responde en formato json

 echo json_encode($as);

?>

