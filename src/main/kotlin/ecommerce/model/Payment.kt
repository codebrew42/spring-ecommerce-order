package ecommerce.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "payments")
class Payment(
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    val order: Order,
    @Column(name = "stripe_payment_intent_id", nullable = false)
    val stripePaymentIntentId: String,
    @Column(name = "amount", nullable = false)
    val amount: Double,
    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    val currency: Currency,
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: PaymentStatus = PaymentStatus.PENDING,
    @Column(name = "payment_method", nullable = true)
    var paymentMethod: String? = null,
    @Column(name = "stripe_charge_id", nullable = true)
    var stripeChargeId: String? = null,
    @Column(name = "failure_reason", nullable = true)
    var failureReason: String? = null,
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    fun markAsCompleted(
        stripeChargeId: String,
        paymentMethod: String,
    ) {
        this.status = PaymentStatus.COMPLETED
        this.stripeChargeId = stripeChargeId
        this.paymentMethod = paymentMethod
        this.failureReason = null
    }

    fun markAsFailed(reason: String) {
        this.status = PaymentStatus.FAILED
        this.failureReason = reason
    }

    fun markAsRefunded() {
        this.status = PaymentStatus.REFUNDED
    }

    fun isCompleted(): Boolean {
        return status == PaymentStatus.COMPLETED
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Payment) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "Payment(id=$id, stripePaymentIntentId='$stripePaymentIntentId', amount=$amount, status=$status)"
    }
}
