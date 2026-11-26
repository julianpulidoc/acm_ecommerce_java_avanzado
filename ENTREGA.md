# Entrega final: ciclo completo CI/CD

**Repositorio:** https://github.com/julianpulidoc/acm_ecommerce_java_avanzado

**Aplicación desplegada:** https://ecommerce-0jwb.onrender.com (versión en https://ecommerce-0jwb.onrender.com/version)

**Pipeline:** https://github.com/julianpulidoc/acm_ecommerce_java_avanzado/actions

**Equipo:** [@julianpulidoc](https://github.com/julianpulidoc) · [@Julsanchezc](https://github.com/Julsanchezc) · [@dsantiag0g](https://github.com/dsantiag0g)

## Descripción

Automatizamos el ciclo de vida de una API de e-commerce: un `git push` a `main` compila, prueba, publica una imagen Docker y dispara el despliegue en la nube sin intervención manual.

**Tecnologías:** Spring Boot 4, Java 25, PostgreSQL, Docker, GitHub Actions, GHCR y Render (opción estándar PaaS).

## Flujo

```mermaid
flowchart LR
    A[git push] --> B[Compilar y probar]
    B --> C[Imagen Docker]
    C --> D[Publicar en GHCR]
    D --> E[Deploy Hook de Render]
    E --> F[URL pública]
```

El workflow tiene 3 jobs encadenados con `needs`:

1. **Compilar y probar (CI):** levanta un PostgreSQL temporal, instala Java 25 y ejecuta `./gradlew clean build`, que compila y corre la prueba de integración.
2. **Imagen Docker → GHCR:** construye la imagen con el SHA del commit y la publica con los tags `<sha>` y `latest`.
3. **Desplegar en Render (CD):** llama al Deploy Hook y ejecuta un smoke test que consulta `/version` hasta que devuelve el SHA del commit.

Los jobs 2 y 3 solo corren con un push a `main`, y únicamente si el job anterior pasó.

## Cambios principales

| Commit | Cambio |
|---|---|
| [`6bf0cd0`](https://github.com/julianpulidoc/acm_ecommerce_java_avanzado/commit/6bf0cd0) | Java 25 en la imagen, springdoc 3, configuración por variables de entorno y docker compose completo |
| [`46aeee4`](https://github.com/julianpulidoc/acm_ecommerce_java_avanzado/commit/46aeee4) | Página de demo, endpoint `/version` y seguridad por lista blanca |
| [`0b02d04`](https://github.com/julianpulidoc/acm_ecommerce_java_avanzado/commit/0b02d04) | Despliegue continuo hacia Render |
| [`6ae077f`](https://github.com/julianpulidoc/acm_ecommerce_java_avanzado/commit/6ae077f) | Dockerfile multi-stage |
| [`978e5c3`](https://github.com/julianpulidoc/acm_ecommerce_java_avanzado/commit/978e5c3) | SHA real del commit en producción |
| [`df93052`](https://github.com/julianpulidoc/acm_ecommerce_java_avanzado/commit/df93052) | Pipeline separado en 3 jobs con smoke test |

## Decisiones técnicas

- **Imagen multi-stage:** compila con JDK 25 y entrega solo el JRE y el JAR, con un usuario sin privilegios.
- **Trazabilidad:** cada imagen lleva el SHA de su commit, visible en `/version`.
- **Memoria:** `-XX:MaxRAMPercentage=75` para contenedores de 512 MB.
- **Secretos:** las credenciales de CI y el Deploy Hook se guardan en GitHub Secrets; GHCR usa el `GITHUB_TOKEN` de cada ejecución.

## Evidencias

Todo corresponde a la [ejecución 45887476634](https://github.com/julianpulidoc/acm_ecommerce_java_avanzado/actions/runs/45887476634) (commit `df93052`, 3 jobs en verde, 5 min 8 s):

| Requisito | Evidencia |
|---|---|
| Disparo automático y pruebas | Evento `push` a `main`; job 1 con `BUILD SUCCESSFUL` |
| Imagen publicada | `ghcr.io/julianpulidoc/acm_ecommerce_java_avanzado`, tags `df93052…` y `latest` con el mismo digest |
| Despliegue automático | Render respondió HTTP 200 (`dep-dc48vatg1s2s73dmaaug`) |
| Verificación | Smoke test: `✅ Versión … (df93052…) desplegada en https://ecommerce-0jwb.onrender.com` |

## Demostración

1. Cambiar `app.message` en `src/main/resources/application.yaml`.
2. `git add -u && git commit -m "demo: cambio en vivo" && git push`
3. Mostrar en Actions los 3 jobs: pruebas, imagen publicada, y despliegue con smoke test.
4. Recargar https://ecommerce-0jwb.onrender.com y comparar el commit de `/version` con `git rev-parse HEAD`.

**Ejecución local:** `docker compose up -d --build` y abrir http://localhost:8080
