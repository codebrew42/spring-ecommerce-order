package ecommerce.util

import ecommerce.dto.CreateProductRequest
import ecommerce.dto.UpdateProductRequest
import ecommerce.dto.member.RegisterRequest
import ecommerce.model.Member
import ecommerce.model.Product
import ecommerce.model.ProductOption

fun CreateProductRequest.toModel(id: Long? = null) = Product(name, price, quantity, imageUrl, id)

fun UpdateProductRequest.toModel(
    id: Long,
    existingProduct: Product,
) = Product(
    name = name ?: existingProduct.name,
    price = price ?: existingProduct.price,
    quantity = quantity ?: existingProduct.quantity,
    imageUrl = imageUrl ?: existingProduct.imageUrl,
    id = id,
)

fun RegisterRequest.toModel(hashedPassword: String) = Member(email, hashedPassword, name, role)

data class ProductResponse(
    val id: Long?,
    val name: String,
    val price: Double,
    val quantity: Int,
    val imageUrl: String,
    val productOptions: List<ProductOptionResponse> = emptyList(),
)

data class ProductOptionResponse(
    val id: Long?,
    val name: String,
    val quantity: Int,
)

fun Product.toResponse() =
    ProductResponse(
        id = id,
        name = name,
        price = price,
        quantity = quantity,
        imageUrl = imageUrl,
    )

fun ProductOption.toResponse() =
    ProductOptionResponse(
        id = id,
        name = name,
        quantity = quantity,
    )
