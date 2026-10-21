#Nombre 
[ImageViewer]

Visor de imágenes y proyecto de refactorización.

## Descripción

ImageViewer es una aplicación desarrollada en Java que permite seleccionar carpetas locales, visualizar imágenes y navegar entre ellas utilizando opciones de avance y retroceso.

El módulo MVP seleccionado para la re-arquitectura corresponde al módulo de interacción y navegación de imágenes. Este módulo fue elegido porque concentra gran parte de la lógica de navegación, carga de imágenes y actualización de la interfaz, generando problemas de mantenibilidad, rendimiento y fiabilidad.

## Integrantes

- **[Jhon Alexander Montoya]** - *Rol / Responsabilidad*
- **[Juan David Morales]** - *Rol / Responsabilidad*

## Tecnologías

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

## Instrucciones de Ejecución

### Requisitos previos
- Java JDK 8 o superior
- NetBeans (recomendado)
- Git

### Pasos de ejecución
```bash
# 1. Clonar el repositorio
git clone <URL_DEL_REPOSITORIO>

# 2. Ingresar al directorio del proyecto
cd Refactorización_ImageViewer

# 3. Instalar dependencias (cuando aplique)
# npm install / gradle build / maven install

# 4. Ejecutar la aplicación
# npm start / java -jar ...
```

## Estructura de Carpetas

```text
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
```

## Arquitectura

El diseño del proyecto sigue patrones de arquitectura modular. Para más detalle sobre el diseño del sistema y las decisiones tomadas:
- Ver diagramas en [`docs/diagramas/`](file:///c:/Users/user/Desktop/Refactorización_ImageViewer/docs/diagramas/)
- Ver registros de decisiones de arquitectura (ADRs) en [`docs/adr/`](file:///c:/Users/user/Desktop/Refactorización_ImageViewer/docs/adr/)

## Estado Actual

- [x] Estructura inicial del proyecto creada.
- [ ] Definición del modelo base y configuración.
- [ ] Implementación del motor/visor de imágenes.

## Trabajo Futuro

- [ ] Implementación del conjunto de pruebas automáticas en `tests/`.
- [ ] Optimización del renderizado y carga de recursos pesados.
- [ ] Documentación detallada de los componentes en `docs/`.
