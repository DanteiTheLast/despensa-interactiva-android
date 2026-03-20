# DespensaCuartel - Contexto

## Descripcion
App Android de gestion de despensa con rueda radial interactiva.

## Stack
Kotlin + Jetpack Compose + Material 3 + Firebase Firestore + MVVM

## Estructura de Datos

Coleccion: "inventario"
| Campo | Tipo |
|-------|------|
| id | string |
| nombre | string |
| categoriaID | string |
| cantidadActual | int |
| cantidadMaxima | int |
| unidad | string |
| tipoIcon | string |
| actualizadoPor | string |
| fechaActualizacion | long |

Categorias (8): FRUTAS, CARNES, PAN, CAFE, LACTEOS, MEDICAMENTOS, CERVEZA, VERDURAS

## Navegacion

| Ruta | Screen |
|------|--------|
| home | RadialWheel |
| category/{id} | CategoryScreen |
| product/{id} | ProductDetailScreen |
| add_product | AddProductScreen |

## Archivos Clave

| Archivo | Proposito |
|---------|-----------|
| RadialWheel.kt | Rueda radial interactiva |
| InventoryState.kt | Modelos: Category, SectionColor, InventoryItem |
| InventoryRepository.kt | Acceso a Firestore |
| InventoryViewModel.kt | Estado de inventario |
| AddProductScreen.kt | Formulario para anadir productos |
| AddProductViewModel.kt | Estado del formulario Add Product |
| SuccessAnimation.kt | Animacion confetti success/error |
| Navigation.kt | Rutas de navegacion |
| Theme.kt | Material 3 theme con dark mode |

## Features Implementadas

- Rueda radial con 8 secciones
- Colores dinamicos por nivel de inventario
- Anadir productos (AddProductScreen con grid de iconos)
- Animacion success/error con confetti
- Boton central "+" para anadir productos
- Navegacion entre pantallas
- Sincronizacion Firestore (lectura/escritura)
- Dark mode automatico
- Paleta verde esmeralda

## Problemas Conocidos
Ninguno actualmente.
