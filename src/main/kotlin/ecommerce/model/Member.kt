package ecommerce.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "members")
class Member(
    @Column(name = "email", nullable = false, unique = true)
    var email: String,
    @Column(name = "password", nullable = false)
    val password: String,
    @Column(name = "name", nullable = false)
    var name: String,
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    val role: Role = Role.USER,
    @UpdateTimestamp
    var updatedAt: LocalDateTime? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    constructor(
        email: String,
        name: String,
    ) : this(
        email = email,
        password = "",
        name = name,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Member) return false

        if (id != null && other.id != null) {
            return id == other.id
        }

        return email == other.email
    }

    override fun hashCode(): Int {
        return email.hashCode()
    }

    fun updateProfile(
        newEmail: String,
        newName: String,
    ) {
        this.email = newEmail
        this.name = newName
    }

    fun updateTimestamp() {
        this.updatedAt = LocalDateTime.now()
    }

    override fun toString(): String {
        return "Member(id=$id, email=$email, name=$name, role=$role)"
    }
}
