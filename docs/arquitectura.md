# Arquitectura del Sistema - ImageViewer

## 1. Descripción de la Arquitectura

### 1.1 Visión General
**ImageViewer** es una aplicación de escritorio diseñada para la visualización, navegación y gestión de archivos de imágenes locales. Para resolver los problemas de acoplamiento, baja mantenibilidad e imposibilidad de realizar pruebas automáticas típicos de las aplicaciones Swing tradicionales, el sistema ha sido refactorizado aplicando el patrón arquitectónico **Model-View-Presenter (MVP)** en su variante **Passive View**, complementado con una capa de servicios de infraestructura para el acceso al sistema de archivos.

### 1.2 Principios Fundamentales de Diseño
- **Separación de Responsabilidades (SRP):** Cada componente tiene una única razón de cambio. La interfaz de usuario no manipula archivos ni calcula índices de navegación; el modelo no conoce componentes gráficos.
- **Inversión de Dependencias (DIP):** El presentador no depende de componentes visuales concretos de Swing, sino de una abstracción expresada en la interfaz `ImageView`.
- **Alta Testeabilidad:** La lógica de negocio y presentación puede ejecutarse y validarse de forma unitaria en entornos sin entorno gráfico (*headless*).
- **Inyección de Dependencias por Constructor:** El punto de entrada (`Main`) se encarga de crear las instancias de los componentes y enlazarlas explícitamente sin acoplamiento estático oculto.

Para visualizar la arquitectura en diferentes niveles de abstracción, consultar los diagramas C4 en [docs/diagramas/](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/docs/diagramas/):
- Contexto: [c4-contexto.puml](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/docs/diagramas/c4-contexto.puml)
- Contenedores: [c4-contenedores.puml](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/docs/diagramas/c4-contenedores.puml)
- Componentes del módulo MVP: [c4-componentes-mvp.puml](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/docs/diagramas/c4-componentes-mvp.puml)

---

## 2. Tabla de Componentes

La siguiente tabla describe la responsabilidad, dependencias y ubicación física de cada componente del sistema refactorizado:

| Componente | Responsabilidad | Dependencias Principales | Archivo / Paquete Actual |
| :--- | :--- | :--- | :--- |
| **`Main`** | Punto de entrada del programa. Configura el *Look & Feel* del sistema operativo, instancia el grafo de objetos (modelo, servicio, vista y presentador) e inicia la aplicación en el hilo de despacho de eventos de Swing (*Event Dispatch Thread*). | `ImageModel`, `ImageLoader`, `MainFrame`, `ImagePresenter`, `SwingUtilities` | [`src/main/java/imageviewer/Main.java`](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/src/main/java/imageviewer/Main.java) (`imageviewer`) |
| **`ImageModel`** | Encapsula el estado en memoria de la aplicación: imagen actual (`BufferedImage`), archivo activo (`File`), lista ordenada de imágenes de la carpeta actual (*playlist*) y el índice de navegación. Provee operaciones puras sin efectos secundarios ni dependencias gráficas. | `java.awt.image.BufferedImage`, `java.io.File`, `java.util.List` | [`src/main/java/imageviewer/model/ImageModel.java`](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/src/main/java/imageviewer/model/ImageModel.java) (`imageviewer.model`) |
| **`ImageView`** | Contrato de la vista pasiva. Expone métodos abstractos para desplegar la imagen, actualizar controles de navegación, emitir mensajes informativos o de error y asociar el presentador. | `java.awt.image.BufferedImage`, `ImagePresenter` | [`src/main/java/imageviewer/view/ImageView.java`](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/src/main/java/imageviewer/view/ImageView.java) (`imageviewer.view`) |
| **`MainFrame`** | Implementación concreta de `ImageView` en Java Swing (`JFrame`). Contiene la barra de herramientas (`JToolBar`), el lienzo de renderizado escalado (`ImageCanvasPanel`) y la barra de estado. Captura las acciones del usuario (clicks de botón, selección de archivo) y las delega inmediatamente al presentador. | `ImageView`, `ImagePresenter`, `javax.swing.*`, `java.awt.*` | [`src/main/java/imageviewer/view/MainFrame.java`](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/src/main/java/imageviewer/view/MainFrame.java) (`imageviewer.view`) |
| **`ImagePresenter`** | Orquestador central de la interacción. Recibe eventos del usuario desde la vista, solicita al servicio la lectura de imágenes, actualiza el estado en el modelo y ordena a la vista qué y cómo desplegar los datos y el estado de los controles. | `ImageView`, `ImageModel`, `ImageLoader` | [`src/main/java/imageviewer/presenter/ImagePresenter.java`](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/src/main/java/imageviewer/presenter/ImagePresenter.java) (`imageviewer.presenter`) |
| **`ImageLoader`** | Servicio de acceso al sistema de archivos. Valida la existencia y permisos de archivos, filtra por extensiones compatibles (`.jpg`, `.jpeg`, `.png`, `.gif`, `.bmp`), recupera y ordena listas de imágenes por directorio y decodifica imágenes en memoria con `ImageIO`. | `javax.imageio.ImageIO`, `java.awt.image.BufferedImage`, `java.io.File` | [`src/main/java/imageviewer/service/ImageLoader.java`](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/src/main/java/imageviewer/service/ImageLoader.java) (`imageviewer.service`) |

---

## 3. Decisiones de Refactorización

A continuación se detallan las decisiones de refactorización clave implementadas para transformar la base de código inicial en una arquitectura mantenible y comprobable:

### Decisión 1: Adopción del Patrón Model-View-Presenter (MVP) con Vista Pasiva
- **Problema:** En el código heredado o tradicional de Swing, la ventana `MainFrame` concentraba toda la lógica: control de navegación (anterior/siguiente), gestión de índices, llamadas a librerías de dibujo y almacenamiento de estado. Esto constituía un antipatrón *God Class* imposible de testear de forma automatizada.
- **Cambio propuesto:** Reestructurar la aplicación separando estrictamente responsabilidades: `ImageModel` retiene los datos y la lista de navegación; `MainFrame` actúa exclusivamente como una vista pasiva de presentación; y `ImagePresenter` asume la orquestación y el flujo de control.
- **Código afectado:** 
  - [`src/main/java/imageviewer/model/ImageModel.java`](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/src/main/java/imageviewer/model/ImageModel.java) (creado)
  - [`src/main/java/imageviewer/presenter/ImagePresenter.java`](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/src/main/java/imageviewer/presenter/ImagePresenter.java) (creado)
  - [`src/main/java/imageviewer/view/MainFrame.java`](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/src/main/java/imageviewer/view/MainFrame.java) (refactorizado para eliminar lógica de negocio)
- **Atributo de calidad favorecido:** **Mantenibilidad** y **Testeabilidad**. La lógica de navegación y sincronización de índices queda aislada de la biblioteca gráfica.
- **Referencia ADR:** [ADR-001: Adopción del Patrón MVP](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/docs/adr/ADR-001-adopcion-patron-mvp.md)

### Decisión 2: Extracción del Servicio de Carga y Validación de Archivos (`ImageLoader`)
- **Problema:** Las operaciones de lectura de archivos con `ImageIO`, la validación de extensiones y el escaneo de directorios se realizaban dentro de los *listeners* de eventos de la GUI. Esto generaba duplicación de lógica, manejo inconsistente de errores (`IOException`) y acoplaba la interfaz con detalles de disco.
- **Cambio propuesto:** Extraer toda la lógica de validación, filtrado y lectura de imágenes hacia un servicio reutilizable e independiente (`ImageLoader`), que expone métodos semánticos (`loadImage`, `getImageFilesInDirectory`, `isImageFile`) con excepciones tipadas.
- **Código afectado:** 
  - [`src/main/java/imageviewer/service/ImageLoader.java`](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/src/main/java/imageviewer/service/ImageLoader.java) (creado)
  - [`src/test/java/imageviewer/service/ImageLoaderTest.java`](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/src/test/java/imageviewer/service/ImageLoaderTest.java) (batería de pruebas unitarias automatizadas con `@TempDir`)
- **Atributo de calidad favorecido:** **Modularidad**, **Robustez / Confiabilidad** y **Reutilización**.
- **Referencia ADR:** [ADR-002: Extracción del Servicio ImageLoader](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/docs/adr/ADR-002-extraccion-servicio-carga-imagenes.md)

### Decisión 3: Desacoplamiento de la Vista mediante la Interfaz `ImageView`
- **Problema:** Si el presentador conoce directamente la clase concreta `MainFrame`, queda acoplado al framework Swing. Esto impide escribir pruebas unitarias para el presentador sin instanciar ventanas gráficas reales, lo que genera fallos en servidores de CI/CD que operan en modo *headless*.
- **Cambio propuesto:** Introducir la interfaz `ImageView` que define el contrato de comunicación vista-presentador mediante métodos de alto nivel (`displayImage`, `showStatus`, `showErrorMessage`, `updateNavigationControls`). `MainFrame` implementa esta interfaz y `ImagePresenter` solo depende de ella.
- **Código afectado:** 
  - [`src/main/java/imageviewer/view/ImageView.java`](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/src/main/java/imageviewer/view/ImageView.java) (creado)
  - [`src/main/java/imageviewer/view/MainFrame.java`](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/src/main/java/imageviewer/view/MainFrame.java) (implementa `ImageView`)
  - [`src/main/java/imageviewer/presenter/ImagePresenter.java`](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/src/main/java/imageviewer/presenter/ImagePresenter.java) (recibe `ImageView` en el constructor)
- **Atributo de calidad favorecido:** **Testeabilidad** (permite dobles de prueba/Mocks) y **Flexibilidad Tecnológica** (facilidad para cambiar de framework de UI).
- **Referencia ADR:** [ADR-003: Abstracción de Vista Mediante Interfaz](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/docs/adr/ADR-003-abstraccion-de-vista-mediante-interfaz.md)

---

## 4. Escenarios de Calidad

Los siguientes escenarios de calidad formalizan los requerimientos no funcionales prioritarios del sistema:

### Escenario 1: Mantenibilidad (Evolución de la Interfaz de Usuario)
- **Fuente del estímulo:** Desarrollador de la aplicación.
- **Estímulo:** Requerimiento de modificar los componentes de control de la interfaz gráfica (por ejemplo, reemplazar botones de texto por íconos o agregar atajos de teclado para la navegación).
- **Artefacto afectado:** Paquete de vista (`imageviewer.view.MainFrame`).
- **Entorno:** Tiempo de diseño y desarrollo.
- **Respuesta:** La modificación se implementa exclusivamente en la vista `MainFrame` sin necesidad de alterar `ImagePresenter`, `ImageModel` ni `ImageLoader`.
- **Medida de respuesta:** Cero líneas de código modificadas fuera del paquete `imageviewer.view`; el 100% de las pruebas unitarias existentes continúan pasando sin cambios.

### Escenario 2: Testeabilidad (Verificación Automatizada sin Servidor Gráfico)
- **Fuente del estímulo:** Pipeline de integración continua (CI) o desarrollador ejecutando la suite de pruebas.
- **Estímulo:** Ejecución del comando de pruebas automáticas (`mvn test`) en un entorno sin pantalla física (*headless Linux/Windows*).
- **Artefacto afectado:** `ImageModel`, `ImageLoader`, `ImagePresenter`.
- **Entorno:** Tiempo de compilación / testing automatizado.
- **Respuesta:** El sistema ejecuta todas las pruebas unitarias de navegación, límites de índice, lectura y validación de archivos sin arrojar `HeadlessException` ni bloquear hilos de ejecución.
- **Medida de respuesta:** Cobertura de pruebas superior al 90% en la lógica de dominio y servicios, con un tiempo total de ejecución inferior a 5 segundos.

### Escenario 3: Robustez y Confiabilidad (Manejo de Archivos Inválidos o Dañados)
- **Fuente del estímulo:** Usuario final interactuando con la aplicación.
- **Estímulo:** El usuario intenta abrir un archivo que no es una imagen válida, un archivo corrupto, un archivo sin permisos de lectura o una ruta inexistente.
- **Artefacto afectado:** `ImageLoader`, `ImagePresenter` y `ImageView`.
- **Entorno:** Tiempo de ejecución normal.
- **Respuesta:** `ImageLoader` detecta la anomalía y lanza la excepción correspondiente (`FileNotFoundException`, `IOException` o `IllegalArgumentException`). `ImagePresenter` captura la excepción sin propagar caídas no controladas, notificando al usuario mediante `showErrorMessage()` en la vista y conservando el estado previo de la aplicación.
- **Medida de respuesta:** La aplicación no termina abruptamente (0 crashes); el usuario recibe un mensaje de error descriptivo en menos de 200 ms y la interfaz permanece operativa.

### Escenario 4: Rendimiento en Carga de Imágenes
- **Fuente del estímulo:** Usuario final navegando entre imágenes de alta resolución.
- **Estímulo:** El usuario presiona el botón "Siguiente" o "Anterior" en una carpeta con imágenes de resolución estándar (hasta 4K).
- **Artefacto afectado:** `ImagePresenter`, `ImageLoader` y el panel de renderizado `ImageCanvasPanel`.
- **Entorno:** Tiempo de ejecución en máquina de usuario estándar.
- **Respuesta:** El sistema lee el archivo desde el disco local, decodifica el buffer, escala la imagen manteniendo la relación de aspecto y actualiza la ventana gráfica.
- **Medida de respuesta:** Tiempo de transición visible entre imágenes inferior a 300 ms en discos de estado sólido (SSD) convencionales.

---

## 5. Índice de Registros de Decisiones de Arquitectura (ADRs)

Para conocer los antecedentes, alternativas consideradas y compensaciones técnicas detalladas de cada decisión, consulte la carpeta [`docs/adr/`](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/docs/adr/):

1. [ADR-001: Adopción del Patrón Model-View-Presenter (MVP)](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/docs/adr/ADR-001-adopcion-patron-mvp.md)
2. [ADR-002: Extracción del Servicio de Carga y Validación de Imágenes (ImageLoader)](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/docs/adr/ADR-002-extraccion-servicio-carga-imagenes.md)
3. [ADR-003: Abstracción de la Vista Mediante la Interfaz ImageView](file:///c:/Users/user/Desktop/Refactorizaci%C3%B3n_ImageViewer/docs/adr/ADR-003-abstraccion-de-vista-mediante-interfaz.md)
