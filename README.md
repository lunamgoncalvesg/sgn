# SGN – Sistema de Gestión de Notas

**Escuela Técnica N° 35 "Ing. Eduardo Latzina" (ET35)**

Plataforma web integral para la administración académica, registro de calificaciones y gestión de inscripciones.

---

## 1. Descripción

El **Sistema de Gestión de Notas (SGN)** fue concebido para digitalizar y centralizar la gestión de datos académicos de la Escuela Ing. E. Latzina (ET35). Sustituye los registros en papel y planillas aisladas por un sistema cliente-servidor seguro y accesible mediante la web.

La plataforma implementa un control de acceso basado en roles (**RBAC**) con 6 perfiles diferenciados:

* **`ADMIN`**: Administración general de usuarios, restablecimiento de credenciales, configuración de oferta escolar y asignación de bloques académicos.
* **`OFICINA`**: Gestión de la nómina de alumnos, administración de legajos e inscripciones por ciclo lectivo.
* **`PROFESOR`**: Ingreso y actualización de calificaciones por bimestre/final.
* **`PRECEPTOR`**: Supervisión general de alumnos.
* **`ALUMNO`**: Consulta del boletín digital de calificaciones y números orientativos.
* **`DIRECTIVO`**: Supervisión general y consulta de informes estadísticos consolidados de rendimiento (acceso de solo lectura).

---

## 2. Características principales

* **Autenticación segura:** Emisión de tokens de sesión JWT firmados con algoritmo HMAC512 y contraseñas hasheadas mediante BCrypt.

* **Flujo de primer ingreso forzado:** Redirección automática obligatoria a cambio de clave (`/cambiar-password`) para usuarios nuevos o cuentas restablecidas (`primerIngreso = true`).

* **Control de acceso por roles (RBAC):** Protección de endpoints REST mediante anotaciones `@PreAuthorize` en Spring Security y navegación en el frontend mediante Guards (`auth.ts`).

* **Gestión de Bloques Académicos:** Vinculación tripartita de un Docente, una Materia y un Curso/División para cada año lectivo.

* **Servicio de Notificaciones por Correo:** Integración con `Spring Mail` para el envío automático de credenciales temporales en el alta de usuarios y reseteos administrativos.

* **Auditoría e Historial:** Registro persistente de eventos críticos y operaciones realizadas por los usuarios en la base de datos.

---

## 3. Tecnologías utilizadas

| Tecnología                | Categoría              | Uso en el Proyecto                                | Versión     |
| ------------------------- | ---------------------- | ------------------------------------------------- | ----------- |
| **Java**                  | Lenguaje Backend       | Desarrollo de la lógica de servidor               | JDK 17 / 21 |
| **Spring Boot**           | Framework Backend      | API REST, servicios y persistencia                | 3.x         |
| **Spring Security**       | Seguridad              | Filtro de autenticación, autorización y CORS      | 6.x         |
| **JWT (io.jsonwebtoken)** | Autenticación          | Generación y validación de tokens de sesión       | 0.11.x      |
| **Spring Mail**           | Notificaciones         | Envío de correos SMTP con credenciales temporales | 3.x         |
| **PostgreSQL**            | Base de Datos          | Persistencia relacional de datos                  | 14+         |
| **Next.js**               | Framework Frontend     | Renderizado web, App Router y maquetación         | 14.x        |
| **React**                 | Biblioteca Frontend    | Componentes de la interfaz de usuario             | 18.x        |
| **TypeScript**            | Lenguaje Frontend      | Tipado estático de la aplicación cliente          | 5.x         |
| **CSS**           | Estilos UI             | Maquetación adaptativa y diseño visual            | 3.x         |
| **Apache Maven**          | Gestor Build Backend   | Compilación y gestión de dependencias Java        | 3.8+        |
| **npm**                   | Gestor Paquetes Client | Gestión de dependencias JavaScript/TypeScript     | 9.x / 10.x  |

---

## 4. Arquitectura del sistema

La solución adopta un patrón cliente-servidor desacoplado mediante servicios Web RESTful:

```text
┌─────────────────────────────────────────────────────────────┐
│                       Navegador Web                         │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                  Frontend Next.js 14                        │
│           (React 18 + TypeScript + TailwindCSS)             │
│                                                             │
│   App Router (Páginas y Paneles por Rol)                    │
│   Cliente de API (lib/api.ts con inyección de JWT)          │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
                     JSON / HTTPS (Puerto 8080)
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                  Backend Spring Boot 3                      │
│                                                             │
│   ┌─────────────────────────────────────────────────────┐   │
│   │ REST Controllers (com.sgn.ctrl)                     │   │
│   └──────────────────────────┬──────────────────────────┘   │
│                              ▼                              │
│   ┌─────────────────────────────────────────────────────┐   │
│   │ Security Filter Chain & JwtFilter (com.sgn.config)  │   │
│   └──────────────────────────┬──────────────────────────┘   │
│                              ▼                              │
│   ┌─────────────────────────────────────────────────────┐   │
│   │ Services & Mail (com.sgn.srv)                       │   │
│   └──────────────────────────┬──────────────────────────┘   │
│                              ▼                              │
│   ┌─────────────────────────────────────────────────────┐   │
│   │ Repositories / Spring Data JPA (com.sgn.repo)       │   │
│   └──────────────────────────┬──────────────────────────┘   │
└──────────────────────────────┼──────────────────────────────┘
                               │
                ┌──────────────┴──────────────┐
                ▼                             ▼
┌──────────────────────────┐      ┌──────────────────────────┐
│ PostgreSQL 14+ (sgn_db)  │      │   Servidor Spring Mail   │
└──────────────────────────┘      └──────────────────────────┘
```

---

## 5. Estructura del proyecto

```text
sgn-notas/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/sgn/
│   │   │   │   ├── config/      # Configuración de Seguridad, JWT, CORS y Beans
│   │   │   │   ├── ctrl/        # REST Controllers (Endpoints de la API)
│   │   │   │   ├── dto/         # Data Transfer Objects (Requests y Responses)
│   │   │   │   ├── enm/         # Enumeraciones de Dominio (Role, Turno, etc.)
│   │   │   │   ├── ent/         # Entidades JPA (Usuario, Curso, Materia, etc.)
│   │   │   │   ├── repo/        # Repositorios Spring Data JPA
│   │   │   │   ├── srv/         # Lógica de Negocio y Servicio de Mail
│   │   │   │   └── util/        # Utilitarios de firma JWT y Hasheado
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/                # Pruebas unitarias e integradas (JUnit 5)
│   └── pom.xml                  # Configuración Maven
│
├── frontend/
│   ├── app/                     # Rutas de Next.js (App Router)
│   │   ├── (auth)/              # Rutas /login y /cambiar-password
│   │   └── dashboard/           # Vistas separadas según perfil
│   ├── components/              # Componentes UI reutilizables
│   ├── lib/                     # Cliente de API (api.ts) y helpers de sesión
│   ├── .env.local               # Configuración de punto de enlace API
│   ├── package.json             # Dependencias npm
│   └── tsconfig.json            # Configuración TypeScript
│
├── Documentacion/               # Manuales y documentación técnica oficial
└── README.md
```

---

## 6. Requisitos previos

Para instalar y ejecutar el SGN localmente se requiere:

* **Java Development Kit (JDK):** Versión 17 o 21 instalada y configurada en la variable `JAVA_HOME`.
* **Apache Maven:** Versión 3.8 o superior.
* **Node.js:** Versión LTS v18.x o v20.x.
* **npm:** Versión 9.x o 10.x.
* **PostgreSQL:** Versión 14, 15 o 16 activa en el puerto `5432`.
* **Navegador Web:** Google Chrome, Mozilla Firefox, Microsoft Edge o Safari actualizados.

> **Nota:** El proyecto no requiere el uso obligatorio de contenedores Docker para la ejecución en modo desarrollo.

---

## 7. Instalación y configuración

### Paso 1: Configurar PostgreSQL

Acceda a la consola PostgreSQL (`psql` o pgAdmin) y cree la base de datos y el usuario:

```sql
CREATE DATABASE sgn_db;
```

### Paso 2: Configurar el Backend

Ingrese al directorio:

```text
backend/src/main/resources/
```

Edite el archivo `application.yml` o establezca las siguientes variables de entorno:

| Variable                     | Descripción                        | Valor de ejemplo                            |
| ---------------------------- | ---------------------------------- | ------------------------------------------- |
| `SERVER_PORT`                | Puerto de escucha del backend      | `8080`                                      |
| `SPRING_DATASOURCE_URL`      | Cadena JDBC de PostgreSQL          | `jdbc:postgresql://localhost:5432/sgn_db`   |
| `SPRING_DATASOURCE_USERNAME` | Usuario de PostgreSQL              | `sgn_user`                                  |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de PostgreSQL           | `<TU_PASSWORD_POSTGRES>`                    |
| `SPRING_MAIL_HOST`           | Servidor SMTP para correos         | `smtp.gmail.com`                            |
| `SPRING_MAIL_PORT`           | Puerto del servidor SMTP           | `587`                                       |
| `SPRING_MAIL_USERNAME`       | Cuenta remitente SMTP              | `mailescuela@gmail.com`                     |
| `SPRING_MAIL_PASSWORD`       | Clave de aplicación/SMTP           | `<TU_APP_PASSWORD_SMTP>`                    |
| `JWT_SECRET`                 | Clave de firma HMAC512 para tokens | `<CLAVE_SECRETA_ALEATORIA_MINIMO_64_BYTES>` |

> **Funcionamiento de Hibernate (`ddl-auto`):** Configurado en modo `update`. Hibernate creará y actualizará automáticamente las tablas relacionales al iniciar la aplicación.

### Paso 3: Configurar el Frontend

Ingrese a la carpeta:

```bash
cd frontend
```

Cree un archivo llamado `.env.local` con el origen de la API REST:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

---

## 8. Ejecución del proyecto

Para ejecutar el sistema en entorno local, abra dos consolas de comandos independientes.

### Consola 1 — Servidor Backend

```bash
cd backend
mvn spring-boot:run
```

El servicio backend iniciará en:

```text
http://localhost:8080
```

### Consola 2 — Cliente Frontend

```bash
cd frontend
npm install
npm run dev
```

El cliente frontend iniciará en:

```text
http://localhost:3000
```

---

## 9. Flujo de autenticación y primer ingreso

```text
[ Administrador ]
       │
       │ Alta de usuario
       ▼
[ Backend AuthSrv ]
       │
       ▼
Genera clave temporal aleatoria
       │
       ▼
Hash BCrypt → Guardar DB
       │
       ▼
Set "primerIngreso = true"
       │
       ▼
Dispara EmailService
       │
       ▼
Envío de correo SMTP
       │
       ▼
[ Usuario ]
       │
       │ Ingresa clave temporal
       ▼
POST /api/auth/login
       │
       ├──────────────────────────────► Retorna Token JWT
       │                               con primerIngreso: true
       │
       ▼
Cambio obligatorio
       │
       ▼
[ Guard Next.js ]
       │
       ▼
Redirección a /cambiar-password
       │
       ▼
POST /api/auth/cambiar-password
       │
       ▼
Set "primerIngreso = false"
y actualización del hash
```

---

## 10. Roles del sistema

| Rol             | Descripción                          | Módulos / Acceso principal                                                             |
| --------------- | ------------------------------------ | -------------------------------------------------------------------------------------- |
| **`ADMIN`**     | Administrador global del sistema     | `/dashboard/admin` — Gestión de usuarios, reset de claves, cursos, materias y bloques. |
| **`OFICINA`**   | Personal administrativo y secretaría | `/dashboard/oficina` — Gestión de legajos, inscripciones a cursos y constancias.       |
| **`PROFESOR`**  | Personal docente                     | `/dashboard/profesor` — Carga de notas por trimestre/final y ausentismo de materia.    |
| **`PRECEPTOR`** | Preceptores de curso                 | `/dashboard/preceptor` — Notas por curso y alumno.        |
| **`ALUMNO`**    | Estudiantes matriculados             | `/dashboard/alumno` — Consulta personal del boletín digital y asistencias.             |
| **`DIRECTIVO`** | Equipo directivo de la institución   | `/dashboard/directivo` — Informes estadísticos globales e índices de rendimiento.      |

---

## 11. Endpoints principales de la API REST

| Método | Endpoint                            | Permiso / Rol                 | Descripción                                       |
| ------ | ----------------------------------- | ----------------------------- | ------------------------------------------------- |
| `POST` | `/api/auth/login`                   | Público                       | Autenticación de usuario y retorno de JWT.        |
| `POST` | `/api/auth/cambiar-password`        | Autenticado                   | Cambio obligatorio de clave tras primer ingreso.  |
| `GET`  | `/api/usuarios`                     | `ADMIN`, `OFICINA`            | Listado completo de usuarios registrados.         |
| `POST` | `/api/usuarios`                     | `ADMIN`                       | Alta de nuevo usuario y envío de clave temporal.  |
| `PUT`  | `/api/usuarios/{id}/reset-password` | `ADMIN`                       | Restablecimiento administrativo de contraseña.    |
| `POST` | `/api/cursos`                       | `ADMIN`                       | Creación de curso, división y ciclo lectivo.      |
| `POST` | `/api/bloques`                      | `ADMIN`                       | Asignación de Bloque (Docente - Materia - Curso). |
| `POST` | `/api/notas/cargar`                 | `PROFESOR`                    | Carga/edición de notas por bloque académico.      |
| `GET`  | `/api/notas/boletin/{alumnoId}`     | `ALUMNO`, `PROFESOR`, `ADMIN` | Consulta de calificaciones consolidada.           |        |
| `GET`  | `/api/auditoria`                    | `ADMIN`, `DIRECTIVO`          | Consulta del registro de auditoría del sistema.   |

---

## 12. Vistas del Frontend (Next.js 14)

| Ruta Frontend          | Perfil destinatario | Función principal                                             |
| ---------------------- | ------------------- | ------------------------------------------------------------- |
| `/login`               | Público             | Formulario de autenticación de usuarios.                      |
| `/cambiar-password`    | Autenticado         | Cambio obligatorio de contraseña inicial/temporal.            |
| `/dashboard/admin`     | `ADMIN`             | Panel de gestión de usuarios, reseteo de claves y bloques.    |
| `/dashboard/oficina`   | `OFICINA`           | Módulo de inscripciones y legajos de alumnos.                 |
| `/dashboard/profesor`  | `PROFESOR`          | Planilla de carga e ingreso de calificaciones por asignatura. |
| `/dashboard/preceptor` | `PRECEPTOR`         | Visualización de notas.                 |
| `/dashboard/alumno`    | `ALUMNO`            | Visualización del boletín digital e historial de faltas.      |
| `/dashboard/directivo` | `DIRECTIVO`         | Reportes consolidados y estadísticas de aprobación.           |

---

## 13. Pruebas del sistema

Las pruebas unitarias y de integración se ejecutan desde el backend utilizando JUnit 5 y Spring Boot Test:

```bash
cd backend
mvn test
```

### Áreas cubiertas por pruebas automatizadas

* **`AuthSrvTest`**: Generación y validación de tokens JWT, autenticación exitosa y rechazo de usuarios inactivos.
* **`UsuarioSrvTest`**: Verificación de unicidad en registros por email/DNI y reseteo de clave.
* **`NotaSrvTest`**: Cálculo de promedios trimestrales y persistencia de calificaciones.
* **`SgnApplicationTests`**: Carga correcta del contexto de Spring Boot (`contextLoads`).

---

## 14. Documentación adicional

El proyecto cuenta con documentación técnica y funcional complementaria, disponible en la carpeta `Documentacion/`.

### Documentación del proyecto

* `Documentacion/01_Formulacion_del_Proyecto.md` — Formulación general del proyecto, objetivos, alcance y definición de la propuesta.
* `Documentacion/02_Requerimientos_del_Sistema.md` — Requerimientos funcionales y no funcionales del sistema.
* `Documentacion/03_Analisis_del_Sistema.md` — Análisis del sistema, procesos, actores y funcionalidades principales.
* `Documentacion/04_Diseno_Detallado.md` — Diseño detallado de la solución, estructura y componentes del sistema.
* `Documentacion/05_Estudio_de_Factibilidad.md` — Estudio de factibilidad técnica, económica y operativa del proyecto.
* `Documentacion/07_Manual_Usuario.md` — Manual de Usuario para el público general.
* `Documentacion/08_Manual_Administrador.md` — Manual del Administrador del sistema.
* `Documentacion/09_Manual_Instalacion.md` — Guía paso a paso para la puesta en marcha del sistema.
* `Documentacion/10_Manual_Tecnico.md` — Especificación de la arquitectura interna del software.
* `Documentacion/11_Plan_y_Resultados_de_Pruebas.md` — Plan de pruebas, casos de prueba y resultados obtenidos.

---

## 15. Solución de problemas frecuentes

### 1. Fallo de conexión JDBC con PostgreSQL al iniciar el Backend

**Causa:** El servicio de PostgreSQL no está activo o los datos de usuario/contraseña en `application.yml` son incorrectos.

**Solución:** Verifique el estado del servicio PostgreSQL y confirme que los parámetros `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD` coincidan con la base de datos local `sgn_db`.

### 2. Los correos con contraseñas temporales no se envían

**Causa:** Datos SMTP incorrectos o bloqueo de seguridad por autenticación en dos pasos en cuentas Gmail.

**Solución:** Verifique el servidor y puerto SMTP en `application.yml` y utilice una **Contraseña de Aplicación** generada desde la cuenta de Google institucional.

---

## 16. Estado del proyecto

El proyecto SGN se encuentra funcional y verificado en entorno de desarrollo local.

Las funcionalidades principales de:

* autenticación JWT,
* flujo de primer ingreso,
* roles RBAC,
* gestión de usuarios,
* asignación de bloques,
* carga de notas,

han sido implementadas e integradas satisfactoriamente.

---

## 17. Información del proyecto

**Escuela:** Escuela Técnica N° 35 DE 18 "Ing. Eduardo Latzina"

**Proyecto:** Sistema de Gestión de Notas (SGN)

**Materia:** Prácticas Profesionalizantes

**Autor/es:** Domínguez Victoria, Galíndez Sofía, Goncalves Luna, Iannopollo Angeline

**Curso:** 6°2, T.M, C.

**Año:** 2026