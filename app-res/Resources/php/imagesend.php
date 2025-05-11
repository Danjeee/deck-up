<?php
header("Access-Control-Allow-Methods: GET, POST, OPTIONS"); // Métodos permitidos
header("Access-Control-Allow-Headers: Content-Type, Authorization"); // Cabeceras permitidas
header('Content-Type: application/json');
header('Cache-Control: no-cache, must-revalidate');
header('Expires: Mon, 26 Jul 1997 05:00:00 GMT');
//Opcion
if (isset($_GET['opc']))
  $opc = $_GET['opc'];
else if (isset($_POST['opc']))
  $opc = $_POST['opc'];


$ruta_destino_archivo = "../img/users/";
if ($opc == "ADD") {
  //Archivo a añadir
  if (isset($_GET['pfp']))
    $img = $_GET['pfp'];
    else if (isset($_POST['pfp']))
      $img = $_POST['pfp'];
    $archivoImagen = $_FILES['pfp']; // subir el archivo al servidor 
  if ($archivoImagen) {
    $nombre_archivo = $archivoImagen['name'];
    $nombre_archivo = str_replace(' ', '', $nombre_archivo);
    $idunico = time();
    $nombre_archivo = $idunico . "_" . $nombre_archivo;
    $archivo_ok = move_uploaded_file($archivoImagen['tmp_name'], $ruta_destino_archivo . $nombre_archivo);
  }
  echo json_encode(['name' => $nombre_archivo]);
} else {
  //Nombre archivo a borrar
  if (isset($_GET['name']))
    $name = $_GET['name'];
    else if (isset($_POST['name']))
      $name = $_POST['name'];
  unlink($ruta_destino_archivo . $name);
  echo json_encode(['ok' => $ruta_destino_archivo . $name]);
}
?>