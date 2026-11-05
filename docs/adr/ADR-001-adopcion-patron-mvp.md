# ADR-001: Adopción del Patrón Model-View-Presenter (MVP)

* **Estado:** Aceptado
* **Fecha:** 2026-10-21
* **Autores:** Jhon Alexander Montoya, Juan David Morales
* **Módulo Afectado:** `src/main/java/imageviewer/`

## Contexto y Problema

En las implementaciones tradicionales de aplicaciones de escritorio con Java Swing, suele acoplarse fuertemente la lógica de presentación, el manejo de eventos, la manipulación de datos y la interfaz de usuario dentro de una misma clase que hereda de `JFrame`.

Esta práctica genera una "God Class" o clase monolítica donde:
1. Resulta prácticamente imposible realizar pruebas unitarias automáticas sin levantar el entorno gráfico completo (lo cual ralentiza los tests y suele fallar en entornos de Integración Continua sin servidor X o display virtual).
2. Cualquier cambio visual (como reorganizar botones o añadir atajos) arriesga romper la lógica de navegación o manipulación de imágenes.
3. La mantenibilidad y reusabilidad del código se degradan sensiblemente.

## Decisión

Se decide adoptar el patrón arquitectónico **Model-View-Presenter (MVP)**, específicamente la variante **Passive View**:

- **Model (`ImageModel`):** Encapsula exclusivamente el estado de la imagen en memoria (`BufferedImage`), la referencia al archivo actual y la lista ordenada de archivos (playlist) para navegación. No conoce a Swing ni a los eventos de usuario.
- **View (`ImageView` / `MainFrame`):** Es una vista pasiva. Implementa una interfaz limpia (`ImageView`) y solo se encarga de renderizar los elementos gráficos y propagar las interacciones del usuario hacia el presentador. No toma decisiones de negocio ni consulta directamente al modelo.
- **Presenter (`ImagePresenter`):** Contiene toda la lógica de control, coordina las solicitudes del usuario, interactúa con el servicio de carga, actualiza el modelo y envía a la vista exactamente lo que debe desplegar.

## Alternativas Consideradas

1. **MVC (Model-View-Controller tradicional):**
   - *Desventaja:* En MVC clásico, la vista suele observar o consultar directamente al modelo. En interfaces Swing esto tiende a acoplar componentes gráficos a los datos del dominio, dificultando el aislamiento para pruebas unitarias.
2. **MVVM (Model-View-ViewModel):**
   - *Desventaja:* Requiere un mecanismo de *data binding* bidireccional que no es nativo en Java Swing estándar sin librerías externas pesadas.
3. **Mantener estructura monolítica en `JFrame`:**
   - *Desventaja:* Inviable frente a los requisitos de calidad de testeabilidad y mantenibilidad.

## Consecuencias

### Positivas
- **Testeabilidad:** La lógica de navegación y sincronización del estado ahora puede probarse de forma aislada e independiente de Swing.
- **Separación de responsabilidades:** Alta cohesión y bajo acoplamiento entre la presentación y el estado.
- **Modularidad:** Facilita la evolución del diseño visual sin impacto sobre la lógica de la aplicación.

### Negativas / Compensaciones
- Aumenta el número de clases e interfaces en comparación con una implementación monolítica inicial.
- Requiere un mecanismo explícito de ensamblado e inyección en el punto de entrada (`Main`).
