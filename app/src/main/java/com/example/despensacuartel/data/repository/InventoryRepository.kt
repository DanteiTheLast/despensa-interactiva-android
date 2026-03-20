package com.example.despensacuartel.data.repository

import com.example.despensacuartel.data.model.Category
import com.example.despensacuartel.data.model.CategorySummary
import com.example.despensacuartel.data.model.InventoryItem
import com.example.despensacuartel.data.model.ProductCatalog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class InventoryRepository(
    private val useDummyData: Boolean = false
) {
    // Lazy initialization of FirebaseFirestore to prevent crashes during Compose Previews
    // where FirebaseApp is not initialized.
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val collectionRef by lazy { firestore.collection("inventario") }

    fun getInventoryStream(): Flow<List<InventoryItem>> = callbackFlow {
        if (useDummyData) {
            trySend(getDummyData())
            awaitClose { }
            return@callbackFlow
        }

        val listener = collectionRef
            .orderBy("categoriaID", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                Log.d("FIRESTORE", "Snapshot received: error=${error?.message}, docs=${snapshot?.documents?.size}")
                
                if (error != null) {
                    Log.e("FIRESTORE", "Error fetching data: ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Log.d("FIRESTORE", "Doc: ${doc.id} -> nombre=${doc.getString("nombre")}, categoriaID=${doc.getString("categoriaID")}, cantidadActual=${doc.getLong("cantidadActual")}")
                        InventoryItem(
                            id = doc.id,
                            nombre = doc.getString("nombre") ?: "",
                            categoriaID = doc.getString("categoriaID") ?: "",
                            cantidadActual = doc.getLong("cantidadActual")?.toInt() ?: 0,
                            cantidadMaxima = doc.getLong("cantidadMaxima")?.toInt() ?: 10,
                            unidad = doc.getString("unidad") ?: "",
                            actualizadoPor = doc.getString("actualizadoPor") ?: "",
                            fechaActualizacion = doc.getLong("fechaActualizacion") ?: System.currentTimeMillis()
                        )
                    } catch (e: Exception) {
                        Log.e("FIRESTORE", "Error parsing doc: ${e.message}")
                        null
                    }
                } ?: emptyList()

                Log.d("FIRESTORE", "Total items: ${items.size}")
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
                        fechaActualizacion = doc.getLong("fechaActualizacion") ?: System.currentTimeMillis()
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
            InventoryItem("manzana", "Manzana", "frutas", 8, 10, "pieza", "default", "test_device", now),
            InventoryItem("platano", "Plátano", "frutas", 6, 10, "pieza", "default", "test_device", now),
            InventoryItem("naranja", "Naranja", "frutas", 10, 10, "pieza", "default", "test_device", now),

            // Carnes (Top-Right - 45°)
            InventoryItem("pollo", "Pollo", "carnes", 2, 5, "kg", "default", "test_device", now),
            InventoryItem("res", "Res", "carnes", 3, 5, "kg", "default", "test_device", now),

            // Pan (Right - 90°)
            InventoryItem("pan_blanco", "Pan Blanco", "pan", 1, 8, "pieza", "default", "test_device", now),
            InventoryItem("pan_integral", "Pan Integral", "pan", 4, 8, "pieza", "default", "test_device", now),

            // Café (Bottom-Right - 135°)
            InventoryItem("cafe_negro", "Café Negro", "cafe", 1, 3, "paquete", "default", "test_device", now),

            // Lácteos (Bottom - 180°)
            InventoryItem("leche", "Leche", "lacteos", 3, 6, "litro", "default", "test_device", now),
            InventoryItem("yogur", "Yogur", "lacteos", 5, 10, "pieza", "default", "test_device", now),
            InventoryItem("queso", "Queso", "lacteos", 0, 3, "kg", "default", "test_device", now),

            // Medicamentos (Bottom-Left - 225°)
            InventoryItem("paracetamol", "Paracetamol", "medicamentos", 2, 10, "pieza", "default", "test_device", now),

            // Cerveza (Left - 270°)
            InventoryItem("cerveza_lager", "Cerveza Lager", "cerveza", 12, 24, "botella", "default", "test_device", now),
            InventoryItem("cerveza_amber", "Cerveza Amber", "cerveza", 6, 12, "botella", "default", "test_device", now),

            // Verduras (Top-Left - 315°)
            InventoryItem("lechuga", "Lechuga", "verduras", 2, 5, "pieza", "default", "test_device", now),
            InventoryItem("tomate", "Tomate", "verduras", 4, 10, "pieza", "default", "test_device", now),
            InventoryItem("zanahoria", "Zanahoria", "verduras", 0, 8, "pieza", "default", "test_device", now)
        )
    }

    suspend fun syncToFirestore(): Result<Int> {
        return try {
            val products = ProductCatalog.toInventoryItems("initial_sync")
            val batch = firestore.batch()

            products.forEach { item ->
                val docRef = collectionRef.document(item.id)
                val data = mapOf(
                    "nombre" to item.nombre,
                    "categoriaID" to item.categoriaID,
                    "cantidadActual" to item.cantidadActual,
                    "cantidadMaxima" to item.cantidadMaxima,
                    "unidad" to item.unidad,
                    "actualizadoPor" to item.actualizadoPor,
                    "fechaActualizacion" to item.fechaActualizacion
                )
                batch.set(docRef, data, SetOptions.merge())
            }

            batch.commit().await()
            Result.success(products.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateItemInFirestore(item: InventoryItem): Result<Unit> {
        return try {
            val docRef = collectionRef.document(item.id)
            val data = mapOf(
                "cantidadActual" to item.cantidadActual,
                "fechaActualizacion" to item.fechaActualizacion,
                "actualizadoPor" to item.actualizadoPor
            )
            docRef.update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FIRESTORE", "Error updating item: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun addItemToFirestore(item: InventoryItem): Boolean {
        return try {
            val docRef = collectionRef.document()
            val data = mapOf(
                "nombre" to item.nombre,
                "categoriaID" to item.categoriaID,
                "cantidadActual" to item.cantidadActual,
                "cantidadMaxima" to item.cantidadMaxima,
                "unidad" to item.unidad,
                "tipoIcon" to item.tipoIcon,
                "actualizadoPor" to item.actualizadoPor,
                "fechaActualizacion" to item.fechaActualizacion
            )
            docRef.set(data).await()
            Log.d("FIRESTORE", "Item added: ${docRef.id}")
            true
        } catch (e: Exception) {
            Log.e("FIRESTORE", "Error adding item: ${e.message}")
            false
        }
    }
}
