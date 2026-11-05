# ImageViewer - Visor de Imágenes y Proyecto de Refactorización

Aplicación de escritorio en Java diseñada para la visualización, exploración y gestión secuencial de imágenes en el sistema de archivos local, refactorizada bajo el patrón arquitectónico **Model-View-Presenter (MVP)** y principios de diseño SOLID.

---

<<<<<<< HEAD
## 1. Descripción del Proyecto
=======
ImageViewer es una aplicación desarrollada en Java que permite seleccionar carpetas locales, visualizar imágenes y navegar entre ellas utilizando opciones de avance y retroceso.

El módulo MVP seleccionado para la re-arquitectura corresponde al módulo de interacción y navegación de imágenes. Este módulo fue elegido porque concentra gran parte de la lógica de navegación, carga de imágenes y actualización de la interfaz, generando problemas de mantenibilidad, rendimiento y fiabilidad.
>>>>>>> 1d2ceff1e7c3570825573ea4e868ebebb474f280

**ImageViewer** es una herramienta de escritorio interactiva que permite a los usuarios abrir archivos de imágenes de formatos populares (`.jpg`, `.jpeg`, `.png`, `.gif`, `.bmp`), explorar automáticamente todas las imágenes pertenecientes al mismo directorio mediante navegación secuencial (anterior / siguiente), visualizar dimensiones y resolución en tiempo real, y redimensionar el lienzo manteniendo la relación de aspecto original de la imagen.

El proyecto ha sido sometido a un proceso de reingeniería de software para superar las limitaciones de arquitecturas monolíticas de interfaz gráfica en Java Swing, logrando un desacoplamiento estricto entre el estado de los datos, la lógica de orquestación y el renderizado visual.

---

<<<<<<< HEAD
## 2. Problemas Arquitectónicos Identificados (Deuda Técnica Previa)

Antes del proceso de refactorización, el sistema exhibía los siguientes problemas arquitectónicos comunes en aplicaciones cliente de escritorio:

1. **Antipatrón *God Class* / Monolito en Interfaz Gráfica:**
   - La ventana principal (`JFrame`) concentraba múltiples responsabilidades no relacionadas: captura de eventos del usuario, lógica de navegación, almacenamiento en memoria del estado de las imágenes y cálculo de índices de carrusel.
2. **Fuerte Acoplamiento entre Entrada/Salida (I/O) y Vista:**
   - Las llamadas a `ImageIO.read()`, el filtrado de archivos por extensión y la inspección de directorios estaban incrustados directamente dentro de escuchadores de eventos (`ActionListener`). Cualquier fallo de lectura en disco congelaba el hilo principal de la interfaz (*Event Dispatch Thread*).
3. **Imposibilidad de Realizar Pruebas Unitarias Automatizadas:**
   - Dado que el estado y la lógica residían dentro de clases visuales pesadas de Swing, no era posible ejecutar pruebas sin inicializar un entorno gráfico completo, lo que impedía la integración de pipelines automáticos de CI/CD en entornos sin pantalla (*headless*).
4. **Violación del Principio de Inversión de Dependencias (DIP):**
   - No existía ninguna abstracción para la vista ni separación de capas de servicio; el código dependía rígidamente de implementaciones concretas de componentes de la biblioteca de widgets de Swing.

---

## 3. Arquitectura y Documentación Técnica

El sistema ha sido reestructurado utilizando una arquitectura por capas basada en **MVP (Passive View)**:

- **Modelo (`ImageModel`):** Encapsula el estado de la imagen y la lista de navegación sin dependencias gráficas.
- **Vista (`ImageView` / `MainFrame`):** Vista pasiva desacoplada mediante interfaz abstracta.
- **Presentador (`ImagePresenter`):** Coordina eventos y sincroniza el modelo con la vista.
- **Servicio (`ImageLoader`):** Aísla el acceso al sistema de archivos y la decodificación de imágenes.

### Enlaces a la Documentación:
* 📄 **Documento de Arquitectura Principal:** Consulte [docs/arquitectura.md](docs/arquitectura.md) para conocer la tabla de componentes, decisiones de refactorización y escenarios de calidad.
* 📐 **Diagramas C4 (Contexto, Contenedores y Componentes MVP):** Disponibles en [docs/diagramas/](docs/diagramas/).
* 📋 **Registros de Decisiones de Arquitectura (ADRs):**
  - [ADR-001: Adopción del Patrón Model-View-Presenter (MVP)](docs/adr/ADR-001-adopcion-patron-mvp.md)
  - [ADR-002: Extracción del Servicio de Carga y Validación de Imágenes (ImageLoader)](docs/adr/ADR-002-extraccion-servicio-carga-imagenes.md)
  - [ADR-003: Abstracción de la Vista Mediante la Interfaz ImageView](docs/adr/ADR-003-abstraccion-de-vista-mediante-interfaz.md)

---

## 4. Tecnologías y Herramientas

- **Lenguaje:** Java 17 (LTS)
- **Framework de Interfaz Gráfica:** Java Swing / AWT (con soporte nativo para *System Look & Feel*)
- **Gestor de Construcción y Dependencias:** Apache Maven 3.8+
- **Framework de Pruebas Automatizadas:** JUnit 5 (Jupiter 5.10.2)
- **Control de Versiones:** Git
=======
- Lenguaje: Java
- Interfaz gráfica: Java Swing
- IDE: NetBeans
- Control de versiones: Git
- Repositorio: GitHub

## Problemas Arquitectónicos Identificados

### 1. Concentración de responsabilidades en MainFrame

La clase MainFrame asume múltiples responsabilidades relacionadas con la interfaz gráfica, navegación y coordinación de carga de imágenes.

**Impacto:** dificulta el mantenimiento y las pruebas unitarias.

### 2. Acoplamiento con la implementación de interfaz gráfica

MainFrame depende directamente de SwingImageDisplay.

**Impacto:** dificulta cambios futuros de tecnología de presentación.

### 3. Manejo deficiente de errores de entrada/salida

FileImageLoader controla errores retornando valores nulos o mensajes de consola.

**Impacto:** riesgo de fallas y comportamiento inesperado del sistema.
>>>>>>> 1d2ceff1e7c3570825573ea4e868ebebb474f280

---

<<<<<<< HEAD
## 5. Instrucciones de Ejecución
=======
### Requisitos previos
- Java JDK 8 o superior
- NetBeans (recomendado)
- Git
>>>>>>> 1d2ceff1e7c3570825573ea4e868ebebb474f280

### Requisitos Previos
- **Java Development Kit (JDK):** Versión 17 o superior instalada y configurada en la variable de entorno `JAVA_HOME`.
- **Apache Maven:** Versión 3.8 o superior disponible en el `PATH` de la terminal (`mvn -version`).

### Pasos de Ejecución

#### 1. Clonar el Repositorio
```bash
git clone <URL_DEL_REPOSITORIO>
cd Refactorización_ImageViewer
```

#### 2. Compilar el Proyecto
```bash
mvn clean compile
```

#### 3. Ejecutar las Pruebas Unitarias
Las pruebas automatizadas de `ImageModel` y `ImageLoader` siguen el estándar Maven bajo `src/test/java/`:
```bash
mvn test
```

#### 4. Ejecutar la Aplicación
Para iniciar el visor de imágenes directamente con el plugin de Maven:
```bash
mvn exec:java
```

O empaquetando el archivo JAR ejecutable:
```bash
mvn clean package
java -jar target/imageviewer-1.0.0.jar
```

---

## 6. Estructura del Repositorio

```text
<<<<<<< HEAD
Refactorización_ImageViewer/
├── pom.xml                               # Configuración de compilación Maven y dependencias JUnit 5
├── README.md                             # Documentación principal e instrucciones de ejecución
├── CHANGELOG.md                          # Historial de versiones y cambios del proyecto
├── docs/                                 # Documentación técnica y arquitectónica
│   ├── arquitectura.md                   # Descripción de arquitectura, componentes y decisiones
│   ├── adr/                              # Architecture Decision Records (ADRs)
│   │   ├── ADR-001-adopcion-patron-mvp.md
│   │   ├── ADR-002-extraccion-servicio-carga-imagenes.md
│   │   └── ADR-003-abstraccion-de-vista-mediante-interfaz.md
│   └── diagramas/                        # Diagramas C4 editables (.puml) y renderizados (.png)
│       ├── c4-contexto.puml (.png)
│       ├── c4-contenedores.puml (.png)
│       └── c4-componentes-mvp.puml (.png)
├── src/
│   ├── main/java/imageviewer/            # Código fuente de producción
│   │   ├── Main.java                     # Punto de entrada e inyección de dependencias
│   │   ├── model/                        # Capa de Modelo (ImageModel)
│   │   ├── presenter/                    # Capa de Presentador (ImagePresenter)
│   │   ├── service/                      # Capa de Servicios de E/S (ImageLoader)
│   │   └── view/                         # Capa de Vista (ImageView, MainFrame)
│   └── test/java/imageviewer/            # Batería de pruebas unitarias JUnit 5
│       ├── model/ImageModelTest.java     # Pruebas de navegación y estado
│       └── service/ImageLoaderTest.java  # Pruebas de lectura y validación de archivos
├── tests/                                # Directorio reservado para pruebas adicionales / fixtures
└── resources/                            # Recursos gráficos y activos estáticos
=======
ImageViewer/
├── src/               # Código fuente principal de la aplicación
├── docs/              # Documentación del proyecto
│   ├── diagramas/     # Diagramas de arquitectura, clases y secuencias
│   └── adr/           # Architecture Decision Records (ADRs)
│   └── arquitectura.md
├── tests/             # Pruebas unitarias e integradas
├── resources/         # Recursos estáticos y activos del proyecto
├── .gitignore         # Archivos e historial excluidos de Git
├── CHANGELOG.md       # Historial de cambios
└── README.md          # Documentación general y guía del proyecto
>>>>>>> 1d2ceff1e7c3570825573ea4e868ebebb474f280
```

---

## 7. Integrantes del Equipo

- **Jhon Alexander Montoya**
- **Juan David Morales**
