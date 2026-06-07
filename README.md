# Curriculums & Study Plans API

REST API backend construida con **Spring Boot 3** para la gestión de currículos académicos, planes de estudio y usuarios con autenticación JWT.

---

## Tabla de contenido

- [Descripción](#descripción)
- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Requisitos previos](#requisitos-previos)
- [Variables de entorno](#variables-de-entorno)
- [Ejecución local](#ejecución-local)
- [Ejecución con Docker](#ejecución-con-docker)
- [Endpoints principales](#endpoints-principales)
- [Seguridad](#seguridad)
- [Estructura del proyecto](#estructura-del-proyecto)

---

## Descripción

Integration Project es una API RESTful que permite:

- **Gestión de usuarios**: registro con verificación por correo electrónico, inicio de sesión, cambio de contraseña provisional y eliminación de cuentas.
- **Autenticación JWT**: tokens de acceso firmados con expiración configurable y filtro de autorización por cada petición.
- **Gestión de currículos**: creación y administración de planes curriculares con temas asociados.
- **Planes de estudio**: creación de planes con entradas diarias, asociación de temas por sesión, calificaciones y notas.
- **Roles y asignaciones**: sistema de roles dinámico con asignación a usuarios.
- **Correo electrónico**: envío de códigos de verificación y contraseñas provisionales vía SMTP.

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.5.6 |
| Seguridad | Spring Security + JWT (jjwt 0.11.5) |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | PostgreSQL |
| Correo | Spring Mail (SMTP) |
| Reactividad | Spring WebFlux |
| Build | Gradle 8.4 |
| Contenedor | Docker + Docker Compose |
| Utilidades | Lombok |

---

## Arquitectura

El proyecto sigue una arquitectura en capas estándar de Spring Boot:

```
Controller → Service → Repository → Entity (JPA)
                 ↓
              DTOs (transferencia de datos)
                 ↓
           Security (JWT Filter → SecurityConfig)
```

**Paquetes principales:**

- `auth` — Login y respuesta JWT
- `controller` — Endpoints REST
- `service` — Lógica de negocio
- `repository` — Acceso a datos (Spring Data JPA)
- `entity` — Modelos de base de datos
- `dto` — Objetos de transferencia
- `security` — Configuración JWT y Spring Security
- `exception` — Manejo de errores

---

## Requisitos previos

- Java 17+
- Gradle 8+ (o usar el wrapper incluido `./gradlew`)
- PostgreSQL (o Docker para levantarlo automáticamente)
- Una cuenta SMTP para envío de correos (opcional en desarrollo)

---

## Variables de entorno

El proyecto **no tiene valores por defecto para la base de datos**; si estas variables no están definidas, la aplicación no arranca.

| Variable | Descripción | Requerida |
|---|---|---|
| `DB_URL` | URL JDBC de PostgreSQL | ✅ |
| `DB_USERNAME` | Usuario de la base de datos | ✅ |
| `DB_PASSWORD` | Contraseña de la base de datos | ✅ |
| `PORT` | Puerto del servidor (default: `8080`) | ❌ |
| `JWT_SECRET` | Clave secreta para firmar tokens | ⚠️ Cambiar en producción |
| `JWT_EXPIRATION_SECONDS` | Duración del token (default: `900`) | ❌ |
| `JWT_ISSUER` | Emisor del token (default: `integration-project`) | ❌ |
| `MAIL_HOST` | Host SMTP | ❌ |
| `MAIL_PORT` | Puerto SMTP | ❌ |
| `MAIL_USERNAME` | Usuario SMTP | ❌ |
| `MAIL_PASSWORD` | Contraseña SMTP | ❌ |

### Ejemplo de archivo `.env`

```env
DB_URL=jdbc:postgresql://localhost:5432/integration_db
DB_USERNAME=postgres
DB_PASSWORD=supersecret

JWT_SECRET=mi-clave-secreta-muy-larga-y-segura
JWT_EXPIRATION_SECONDS=3600

MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tucorreo@gmail.com
MAIL_PASSWORD=tu-app-password
```

---

## Ejecución local

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/Integration-project.git
cd Integration-project

# 2. Definir variables de entorno
export DB_URL=jdbc:postgresql://localhost:5432/integration_db
export DB_USERNAME=postgres
export DB_PASSWORD=secret
export JWT_SECRET=dev-secret

# 3. Compilar y ejecutar
./gradlew bootRun
```

La API estará disponible en `http://localhost:8080`.

---

## Ejecución con Docker

### Solo PostgreSQL (desarrollo)

El archivo `compose.yaml` levanta una instancia de PostgreSQL lista para desarrollo:

```bash
docker compose up -d
```

Credenciales por defecto del compose:
- Base de datos: `mydatabase`
- Usuario: `myuser`
- Contraseña: `secret`

### Imagen Docker de la aplicación

El `Dockerfile` usa un build multi-stage con Gradle para compilar y generar la imagen final con JRE 17:

```bash
# Construir la imagen
docker build -t integration-project .

# Ejecutar con variables de entorno
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/mydatabase \
  -e DB_USERNAME=myuser \
  -e DB_PASSWORD=secret \
  -e JWT_SECRET=mi-secreto \
  integration-project
```

---

## Endpoints principales

### Autenticación

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/auth/login` | Login con JWT |

### Usuarios (`/users`)

| Método | Ruta | Descripción | Auth |
|---|---|---|---|
| `GET` | `/users/all` | Listar usuarios | ✅ |
| `POST` | `/users/register` | Registro con verificación por email | ❌ |
| `POST` | `/users/verify` | Verificar cuenta con código | ❌ |
| `POST` | `/users/login` | Login interno | ❌ |
| `POST` | `/users/change-password` | Cambiar contraseña provisional | ❌ |
| `POST` | `/users/set-password` | Admin actualiza contraseña | ✅ |
| `DELETE` | `/users/{userId}` | Eliminar usuario | ✅ |

### Currículos, Planes de Estudio, Temas y Roles

Gestionados a través de sus respectivos controladores (`/curriculums`, `/study-plans`, `/topics`, `/roles`, `/role-assignments`, `/plan-entries`, `/plan-entry-topics`).

> Las rutas bajo `/auth/**` y `/public/**` son públicas. El resto requiere token JWT en el header `Authorization: Bearer <token>`.

---

## Seguridad

- **Autenticación stateless** con JWT — no se usan sesiones HTTP.
- Contraseñas almacenadas con **BCrypt**.
- Verificación de cuenta por **código de 6 dígitos** enviado al correo.
- Contraseña provisional generada automáticamente al verificar la cuenta.
- Rutas protegidas configuradas en `SecurityConfig`.

---

## Estructura del proyecto

```
src/main/java/com/example/Integration/project/
├── auth/                  # Login y JWT response
├── controller/            # Controladores REST
│   ├── UserController
│   ├── CurriculumController
│   ├── StudyPlanController
│   ├── TopicController
│   ├── RoleController
│   ├── RoleAssignmentController
│   ├── PlanEntryController
│   └── PlanEntryTopicController
├── dto/                   # Objetos de transferencia de datos
├── entity/                # Entidades JPA
│   ├── User, Role, RoleAssignment
│   ├── Curriculum, CurriculumTopic
│   ├── StudyPlan, PlanEntry, PlanEntryTopic
│   ├── Topic
│   └── AuditLog
├── repository/            # Repositorios Spring Data
├── security/              # JWT + Spring Security
│   ├── SecurityConfig
│   ├── JwtUtil
│   ├── JwtAuthorizationFilter
│   ├── JwtAuthenticationEntryPoint
│   ├── JwtProperties
│   ├── UserDetailsServiceImpl
│   └── WebConfig
├── service/               # Lógica de negocio
│   ├── UserService
│   ├── CurriculumService
│   ├── StudyPlanService
│   ├── TopicService
│   ├── MailService
│   ├── VerificationService
│   ├── PlanEntryService
│   └── PlanEntryTopicService
└── exception/
    └── ResourceNotFoundException
```

---

## Autor

**Sebastian Estupinan** — Universidad de Santander (UDES)  
Cúcuta, Norte de Santander, Colombia
