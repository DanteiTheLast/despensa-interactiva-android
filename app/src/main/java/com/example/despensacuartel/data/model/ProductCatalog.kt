package com.example.despensacuartel.data.model

data class CatalogItem(
    val id: String,
    val nombre: String,
    val categoriaId: String,
    val unidad: String,
    val cantidadMaxima: Int
)

object ProductCatalog {

    private val items = listOf(
        CatalogItem("leche", "Leche", "lacteos", "litro", 6),
        CatalogItem("huevos", "Huevos", "lacteos", "pieza", 30),
        CatalogItem("queso", "Queso", "lacteos", "kg", 3),
        CatalogItem("yogurt_griego", "Yogurt Griego", "lacteos", "pieza", 10),

        CatalogItem("platano", "Plátano", "frutas", "pieza", 10),
        CatalogItem("fruta_congelada", "Fruta Congelada", "frutas", "paquete", 5),

        CatalogItem("jitomate", "Jitomate", "verduras", "pieza", 10),
        CatalogItem("cebolla", "Cebolla", "verduras", "pieza", 5),
        CatalogItem("salsa_tomate", "Salsa de Tomate", "verduras", "pieza", 3),
        CatalogItem("col", "Col", "verduras", "pieza", 3),
        CatalogItem("calabacin", "Calabacín", "verduras", "pieza", 5),
        CatalogItem("zanahoria", "Zanahoria", "verduras", "pieza", 8),
        CatalogItem("brocoli", "Brócoli", "verduras", "pieza", 5),
        CatalogItem("apio", "Apio", "verduras", "pieza", 5),

        CatalogItem("salami", "Salami", "carnes", "kg", 2),

        CatalogItem("pan_bimbo", "Pan Bimbo", "pan", "pieza", 8),
        CatalogItem("tortillas", "Tortillas", "pan", "paquete", 5),
        CatalogItem("tortillas_harina", "Tortillas de Harina", "pan", "paquete", 3),
        CatalogItem("spaghetti", "Spaghetti", "pan", "paquete", 5),
        CatalogItem("sopa_maruchan", "Sopa Maruchan", "pan", "pieza", 10),
        CatalogItem("maruchan_ramen", "Maruchan Ramen", "pan", "pieza", 10),
        CatalogItem("sopa_china", "Sopa China", "pan", "pieza", 10),

        CatalogItem("cafe", "Café", "cafe", "paquete", 3),
        CatalogItem("cafe_descafeinado", "Café Descafeinado", "cafe", "paquete", 2),
        CatalogItem("te_manzanilla", "Té de Manzanilla", "cafe", "caja", 3),
        CatalogItem("te_tila", "Té de Tila", "cafe", "caja", 3),
        CatalogItem("te_azahares", "Té de Azahares", "cafe", "caja", 3),
        CatalogItem("te_frutal", "Té Frutal", "cafe", "caja", 3),
        CatalogItem("ketchup", "Ketchup", "cafe", "pieza", 2),
        CatalogItem("mayonesa", "Mayonesa", "cafe", "pieza", 2),
        CatalogItem("mostaza", "Mostaza", "cafe", "pieza", 2),

        CatalogItem("cerveza_sin_alcohol", "Cerveza Sin Alcohol", "cerveza", "botella", 12),
        CatalogItem("cerveza", "Cerveza", "cerveza", "botella", 24),
        CatalogItem("whisky", "Whisky", "cerveza", "botella", 6),
        CatalogItem("brandy", "Brandy", "cerveza", "botella", 4),
        CatalogItem("anis", "Anís", "cerveza", "botella", 3),
        CatalogItem("rompope", "Rompope", "cerveza", "botella", 3),

        CatalogItem("tiamazol", "Tiamazol", "medicamentos", "pieza", 10),
        CatalogItem("propanolol", "Propanolol", "medicamentos", "pieza", 10),
        CatalogItem("rosel", "Rosel", "medicamentos", "pieza", 10),
        CatalogItem("bio_electro", "Bio-Electro", "medicamentos", "pieza", 10)
    )

    fun getAll(): List<CatalogItem> = items

    fun getByCategory(categoriaId: String): List<CatalogItem> =
        items.filter { it.categoriaId == categoriaId }

    fun toInventoryItems(deviceId: String = "initial_sync"): List<InventoryItem> {
        val now = System.currentTimeMillis()
        return items.map { item ->
            InventoryItem(
                id = item.id,
                nombre = item.nombre,
                categoriaID = item.categoriaId,
                cantidadActual = 0,
                cantidadMaxima = item.cantidadMaxima,
                unidad = item.unidad,
                tipoIcon = "default",
                actualizadoPor = deviceId,
                fechaActualizacion = now
            )
        }
    }
}
