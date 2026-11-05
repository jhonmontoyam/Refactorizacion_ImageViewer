# ADR-003: Abstracción de la Vista Mediante la Interfaz ImageView

* **Estado:** Aceptado
* **Fecha:** 2026-10-21
* **Autores:** Jhon Alexander Montoya, Juan David Morales
* **Módulo Afectado:** `src/main/java/imageviewer/view/ImageView.java`

## Contexto y Problema

Cuando el presentador (`ImagePresenter`) mantiene una referencia directa a una clase concreta de la interfaz gráfica (como `MainFrame` que extiende `javax.swing.JFrame`), se produce una violación del **Principio de Inversión de Dependencias (DIP)**: el módulo de alto nivel (la lógica de presentación) depende directamente de un detalle de implementación de bajo nivel (un framework específico de GUI como Swing).

Esto acarrea graves consecuencias:
1. **Dificultad de pruebas:** Para probar el presentador, sería obligatorio instanciar ventanas Swing reales, lo cual genera lentitud, problemas de concurrencia con el Event Dispatch Thread (EDT) y fallos en entornos de integración continua sin interfaz gráfica (headless).
2. **Falta de flexibilidad tecnológica:** Reemplazar o complementar Swing en el futuro (por ejemplo, con JavaFX, SWT o una interfaz web/CLI) requeriría modificar el presentador.
3. **Acoplamiento bidireccional rígido:** La ventana conoce al presentador y el presentador conoce los widgets internos de la ventana.

## Decisión

Se decide definir un contrato formal para la vista a través de la interfaz Java `ImageView` en el paquete `imageviewer.view`:

- El presentador solo interactúa con la interfaz `ImageView`, desconociendo por completo si la vista está construida con Swing, JavaFX o un mock de pruebas.
- El contrato define métodos semánticos orientados a lo que la vista debe mostrar, no a cómo debe dibujarlo:
  - `displayImage(BufferedImage image, String imageName, int width, int height, int currentIndex, int totalImages)`
  - `showErrorMessage(String title, String message)`
  - `showStatus(String message)`
  - `updateNavigationControls(boolean hasPrevious, boolean hasNext)`
  - `clearDisplay()`
  - `setPresenter(ImagePresenter presenter)`
- `MainFrame` implementa `ImageView`, asociando los eventos de sus botones y menús a llamadas a los métodos del presentador.

## Alternativas Consideradas

1. **Acoplar el Presentador directamente a `MainFrame`:**
   - *Desventaja:* Impide el uso de dobles de prueba (mocks o stubs) para verificar la lógica de presentación sin dependencias gráficas.
2. **Utilizar eventos genéricos de publicación/suscripción (EventBus):**
   - *Desventaja:* Agrega complejidad innecesaria y dificulta el seguimiento del flujo de control para una aplicación de escritorio de este alcance.

## Consecuencias

### Positivas
- **Inversión de dependencias:** Se cumple DIP; el presentador depende de una abstracción (`ImageView`) y la implementación concreta (`MainFrame`) también depende de la misma abstracción.
- **Testeabilidad superior:** Permite crear implementaciones simuladas (Mock Views) para verificar el comportamiento de `ImagePresenter` sin desplegar ventanas Swing.
- **Mantenibilidad:** Si se cambian componentes visuales (por ejemplo, cambiar un botón por un menú desplegable), el contrato permanece inalterado y el presentador no sufre modificaciones.

### Negativas / Compensaciones
- Se requiere mantener sincronizado el contrato `ImageView` si surgen nuevas necesidades de visualización en la UI.
