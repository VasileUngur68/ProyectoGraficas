
<?php
$hostname='localhost';
$database='graficas';
$username='root';
$password='';
$con = new mysqli($hostname,$username,$password,$database);

if ($con->connect_errno) {
    echo "Falló la conexión a MySQL: (" . $con->connect_errno . ") " . $con->connect_error;
}

$consulta = $con->query("SELECT * FROM `datos` WHERE `date` =  (SELECT MAX(`date`) from `datos`)");
$i=0;
 // Se inicializa el array as a null por si no hay datos para que no presente un warning
$as=null;
while ($fila = $consulta->fetch_assoc()) {
      $i++;
     //Se añaden los campos a cada array

     $mov["movimiento"]=$fila['movimiento'];
     $luzext["luzExterior"]=$fila['luzExterior'];
     $luzsalon["luzSalon"]=$fila['luzSalon'];
     $vent["ventanas"]=$fila['ventanas'];
     $puert["puertas"]=$fila['puertas'];
     $ar["num"]=$i;
     //Se concatenan los arrays en el array as
     $as=array_merge($ar,$mov,$luzext,$luzsalon,$vent,$puert);

}
 if($as==null){
 $as=$ar;
 }
//Se responde en formato json
 echo json_encode($as);
?>

