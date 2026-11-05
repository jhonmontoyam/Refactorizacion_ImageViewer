# ADR-002: Extracción del Servicio de Carga y Validación de Imágenes (ImageLoader)

* **Estado:** Aceptado
* **Fecha:** 2026-10-21
* **Autores:** Jhon Alexander Montoya, Juan David Morales
* **Módulo Afectado:** `src/main/java/imageviewer/service/ImageLoader.java`

## Contexto y Problema

En el diseño original o monolítico de visualizadores de imágenes, la lógica de lectura de archivos con `ImageIO.read()`, el filtrado por extensiones de archivo (`.jpg`, `.png`, etc.) y el manejo de excepciones de entrada/salida (`IOException`, `FileNotFoundException`) suele encontrarse incrustada directamente en los controladores de eventos de la interfaz gráfica (`ActionListener`).

Esta mezcla presenta serios problemas:
1. **Violación del Principio de Responsabilidad Única (SRP):** La interfaz gráfica no debe conocer detalles de bajo nivel del sistema de archivos, lectura de streams o validaciones de formato de imagen.
2. **Duplicación de lógica:** Las comprobaciones de archivos válidos y la lectura de directorios se repiten en diferentes lugares del código.
3. **Manejo deficiente de errores:** Las fallas de lectura o formatos no soportados no se capturan ni se transforman de manera consistente, provocando bloqueos o fallos silenciosos en la UI.
4. **Baja testeabilidad:** No se puede probar la lógica de carga, filtrado y ordenamiento de archivos de manera aislada sin instanciar la vista.

## Decisión

Se decide extraer toda la lógica relacionada con el acceso, lectura, filtrado y validación de archivos de imagen a un servicio especializado denominado `ImageLoader` ubicado en el paquete `imageviewer.service`:

- **Responsabilidades de `ImageLoader`:**
  - Validar existencia, tipo y permisos de lectura del archivo a cargar.
  - Decodificar imágenes a través de `ImageIO.read` y validar que el resultado no sea nulo.
  - Proveer la lista de formatos compatibles (`.jpg`, `.jpeg`, `.png`, `.gif`, `.bmp`).
  - Explorar directorios, filtrar únicamente archivos compatibles y ordenarlos alfabéticamente para permitir navegación secuencial natural.
  - Lanzar excepciones explícitas y documentadas (`FileNotFoundException`, `IOException`, `IllegalArgumentException`) para que el presentador las gestione adecuadamente.

## Alternativas Consideradas

1. **Dejar la carga de imágenes dentro del modelo (`ImageModel`):**
   - *Desventaja:* Acopla el modelo del dominio con la infraestructura de entrada/salida (`java.io` y `ImageIO`), impidiendo que el modelo sea puramente en memoria y fácil de probar sin efectos secundarios en disco.
2. **Dejar la carga de imágenes en el presentador (`ImagePresenter`):**
   - *Desventaja:* Sobrecarga al presentador con detalles técnicos de I/O y validación de formatos, reduciendo su cohesión como orquestador de UI.

## Consecuencias

### Positivas
- **Alta cohesión:** `ImageLoader` se enfoca únicamente en el acceso y lectura de imágenes en disco.
- **Testeabilidad exhaustiva:** Es posible probar el servicio de forma unitaria contra sistemas de archivos reales o directorios temporales (`@TempDir` de JUnit 5) verificando archivos válidos, corruptos, inexistentes y directorios completos sin depender de la UI.
- **Reutilización:** Cualquier nuevo componente (ej. visor en miniaturas, convertidor de imágenes, carrusel automatizado) puede reutilizar `ImageLoader`.

### Negativas / Compensaciones
- Introduce una dependencia adicional que debe ser inyectada al presentador a través del constructor.
