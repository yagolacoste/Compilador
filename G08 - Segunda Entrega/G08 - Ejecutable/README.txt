La aplicacion se ejecuta por consola, recibe el archivo a compilar como primer argumento, y el archivo donde se desea guardar la salida como segundo argumento.
Ejemplo:
	java -jar "G08 - Compilador.jar" Entrada.txt Salida.txt assembler.asm

Siempre es necesario indicar el nombre del archivo de entrada y el nombre del archivo de salida.
Si la aplicación se encuentra en el mismo lugar que los archivos, no es necesario especificar la ruta de cada archivo. En caso contrario se deberá completar, por ejemplo:
	java -jar "G08 - Compilador.jar" "C:\Users\Alumno\TP Compiladores\Entrada.txt" "C:\Users\Alumno\Salidas\Salida.txt" "C:\Users\Alumno\Assembler\Assembler.asm"

Aconsejamos que los casos de prueba que se adjuntan en la carpeta del mismo nombre, se prueben por bloques, es decir, de a uno por vez según los comentarios que encabezan a cada bloque

Para visualizar la salida correctamente, se debe abrir el archivo de salida en un editor de texto tipo Notepad++, ya que utilizamos el formato UTF-8. 
Si se abre en un block de notas, no se reconocen correctamente los saltos de línea.
