import java.io.*;
import java.util.*;

public class AnalizadorCodigoPostal {
    
    //hashmap para almacenar el codigo postal
    private Map<String, List<String>> mapaCodigoPostal;
    
    //hashmap para contar
    private Map<String, Integer> contarCodigoPostal;
    
    //archivo csv
    private static final String ARCHIVO_CSV = "codigos_postales_hmo.csv";
    
    public AnalizadorCodigoPostal() {
        mapaCodigoPostal = new HashMap<>();
        contarCodigoPostal = new HashMap<>();
    }
    
    public boolean leerArchivoCSV(String nombreArchivo) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(nombreArchivo));
            String linea;
            boolean esPrimeraLinea = true;
            
            System.out.println("Leyendo archivo: " + nombreArchivo + "...");
            System.out.println();
            
            while ((linea = reader.readLine()) != null) {
                //saltar encabezados
                if (esPrimeraLinea) {
                    esPrimeraLinea = false;
                    if (linea.toLowerCase().contains("asentamiento") || 
                        linea.toLowerCase().contains("codigo") ||
                        linea.toLowerCase().contains("postal")) {
                        continue;
                    }
                }
                
                procesarLinea(linea);
            }
            
            System.out.println("Archivo procesado correctamente");
            return true;
            
        } catch (FileNotFoundException e) {
            System.out.println("Error: No se pudo encontrar el archivo " + nombreArchivo);
            System.out.println("Verifique que el archivo existe en el directorio actual");
            return false;
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
            return false;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                System.out.println("Error al cerrar el archivo: " + e.getMessage());
            }
        }
    }
    
    private void procesarLinea(String linea) {
        if (linea == null || linea.trim().isEmpty()) {
            return;
        }
        
        //divir por comas
        String[] partes = analizarLineaCSV(linea);
        
        if (partes.length >= 2) {
            String primero = partes[0].trim();
            String segundo = partes[1].trim();
            
            String asentamiento;
            String codigoPostal;
            
            //detectar cual columna es el codigo postal
            if (esCodigoPostal(primero)) {
                codigoPostal = primero;
                asentamiento = segundo;
            } else if (esCodigoPostal(segundo)) {
                codigoPostal = segundo;
                asentamiento = primero;
            } else {
                return; //no hay codigo postal valido
            }
            
            if (!codigoPostal.isEmpty() && !asentamiento.isEmpty()) {
                //agregar a la lista
                mapaCodigoPostal.computeIfAbsent(codigoPostal, k -> new ArrayList<>()).add(asentamiento);
                
                //incrementar contador
                contarCodigoPostal.put(codigoPostal, contarCodigoPostal.getOrDefault(codigoPostal, 0) + 1);
            }
        }
    }
    
    //verificar si es codigo postal
    private boolean esCodigoPostal(String texto) {
        if (texto == null || texto.isEmpty()) {
            return false;
        }
        
        //remover espacios
        texto = texto.trim();
        
        //verificar si es numero de 5 digitos entre 83000 y 83357
        if (texto.matches("\\d{5}")) {
            int numero = Integer.parseInt(texto);
            return numero >= 83000 && numero <= 83357;
        }
        
        return false;
    }
    
    private String[] analizarLineaCSV(String linea) {
        List<String> resultado = new ArrayList<>();
        boolean entreComillas = false;
        StringBuilder campoActual = new StringBuilder();
        
        for (char c : linea.toCharArray()) {
            if (c == '"') {
                entreComillas = !entreComillas;
            } else if (c == ',' && !entreComillas) {
                resultado.add(campoActual.toString());
                campoActual = new StringBuilder();
            } else {
                campoActual.append(c);
            }
        }
        resultado.add(campoActual.toString());
        
        return resultado.toArray(new String[0]);
    }
    
    public void mostrarResultados() {
        if (contarCodigoPostal.isEmpty()) {
            System.out.println("No se encontraron datos para procesar.");
            return;
        }
        
        System.out.println("\nResultados del análisis:");
        System.out.println("========================");
        
        //ordenar codigos postales
        List<String> codigosOrdenados = new ArrayList<>(contarCodigoPostal.keySet());
        codigosOrdenados.sort(String::compareTo);
        
        //mostrar resultados
        for (String codigoPostal : codigosOrdenados) {
            int cantidad = contarCodigoPostal.get(codigoPostal);
            System.out.println("Código postal: " + codigoPostal + " - Número de asentamientos: " + cantidad);
        }
        
        System.out.println("\nAnálisis completado.");
        System.out.println("Total de códigos postales procesados: " + codigosOrdenados.size());
    }
    
    public static void main(String[] args) {
        System.out.println("Analizador de Códigos Postales - Hermosillo");
        System.out.println("Procesando archivo: codigos_postales_hmo.csv");
        System.out.println();
        
        AnalizadorCodigoPostal analizador = new AnalizadorCodigoPostal();
        
        if (analizador.leerArchivoCSV(ARCHIVO_CSV)) {
            analizador.mostrarResultados();
        } else {
            System.out.println("Error: No se pudo procesar el archivo " + ARCHIVO_CSV);
            System.out.println("Verifique que el archivo existe en el directorio actual.");
        }
    }
}