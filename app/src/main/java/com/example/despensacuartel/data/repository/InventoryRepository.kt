package com.example.despensacuartel.data.repository

import com.example.despensacuartel.data.model.Category
import com.example.despensacuartel.data.model.CategorySummary
import com.example.despensacuartel.data.model.InventoryItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class InventoryRepository(
    private val useDummyData: Boolean = true
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection("inventario")

    fun getInventoryStream(): Flow<List<InventoryItem>> = callbackFlow {
        if (useDummyData) {
            trySend(getDummyData())
            awaitClose { }
            return@callbackFlow
        }

        val listener = collectionRef
            .orderBy("categoriaID", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        InventoryItem(
                            id = doc.id,
                            nombre = doc.getString("nombre") ?: "",
                            categoriaID = doc.getString("categoriaID") ?: "",
                            cantidadActual = doc.getLong("cantidadActual")?.toInt() ?: 0,
                            cantidadMaxima = doc.getLong("cantidadMaxima")?.toInt() ?: 10,
                            unidad = doc.getString("unidad") ?: "",
                            actualizadoPor = doc.getString("actualizadoPor") ?: "",
                            fechaActualizacion = doc.getDate("fechaActualizacion")?.time ?: System.currentTimeMillis()
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(items)
            }

        awaitClose { listener.remove() }
    }

    suspend fun getInventory(): List<InventoryItem> = if (useDummyData) {
        getDummyData()
    } else {
        try {
            val snapshot = collectionRef.get().await()
            snapshot.documents.mapNotNull { doc ->
                try {
                    InventoryItem(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        categoriaID = doc.getString("categoriaID") ?: "",
                        cantidadActual = doc.getLong("cantidadActual")?.toInt() ?: 0,
                        cantidadMaxima = doc.getLong("cantidadMaxima")?.toInt() ?: 10,
                        unidad = doc.getString("unidad") ?: "",
                        actualizadoPor = doc.getString("actualizadoPor") ?: "",
                        fechaActualizacion = doc.getDate("fechaActualizacion")?.time ?: System.currentTimeMillis()
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getCategorySummaries(items: List<InventoryItem>): List<CategorySummary> {
        return Category.entries.map { category ->
            val categoryItems = items.filter { it.categoriaID == category.id }
            CategorySummary.fromItems(category, categoryItems)
        }
    }

    private fun getDummyData(): List<InventoryItem> {
        val now = System.currentTimeMillis()
        return listOf(
            // Frutas (Top - 0°)
            InventoryItem("manzana", "Manzana", "frutas", 8, 10, "pieza", "test_device", now),
            InventoryItem("platano", "Plátano", "frutas", 6, 10, "pieza", "test_device", now),
            InventoryItem("naranja", "Naranja", "frutas", 10, 10, "pieza", "test_device", now),

            // Carnes (Top-Right - 45°)
            InventoryItem("pollo", "Pollo", "carnes", 2, 5, "kg", "test_device", now),
            InventoryItem("res", "Res", "carnes", 3, 5, "kg", "test_device", now),

            // Pan (Right - 90°)
            InventoryItem("pan_blanco", "Pan Blanco", "pan", 1, 8, "pieza", "test_device", now),
            InventoryItem("pan_integral", "Pan Integral", "pan", 4, 8, "pieza", "test_device", now),

            // Café (Bottom-Right - 135°)
            InventoryItem("cafe_negro", "Café Negro", "cafe", 1, 3, "paquete", "test_device", now),

            // Lácteos (Bottom - 180°)
            InventoryItem("leche", "Leche", "lacteos", 3, 6, "litro", "test_device", now),
            InventoryItem("yogur", "Yogur", "lacteos", 5, 10, "pieza", "test_device", now),
            InventoryItem("queso", "Queso", "lacteos", 0, 3, "kg", "test_device", now),

            // Medicamentos (Bottom-Left - 225°)
            InventoryItem("paracetamol", "Paracetamol", "medicamentos", 2, 10, "pieza", "test_device", now),

            // Cerveza (Left - 270°)
            InventoryItem("cerveza_lager", "Cerveza Lager", "cerveza", 12, 24, "botella", "test_device", now),
            InventoryItem("cerveza_amber", "Cerveza Amber", "cerveza", 6, 12, "botella", "test_device", now),

            // Verduras (Top-Left - 315°)
            InventoryItem("lechuga", "Lechuga", "verduras", 2, 5, "pieza", "test_device", now),
            InventoryItem("tomate", "Tomate", "verduras", 4, 10, "pieza", "test_device", now),
            InventoryItem("zanahoria", "Zanahoria", "verduras", 0, 8, "pieza", "test_device", now)
        )
    }
}
