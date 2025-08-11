package ecommerce.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "members",
    indexes = [
        Index(name = "idx_member_email", columnList = "email"),
    ],
)
class Member(
    @Column(name = "email", nullable = false, unique = true)
    val email: String,
    @Column(name = "password", nullable = false)
    val password: String,
    @Column(name = "name", nullable = false)
    val name: String,
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    val role: Role = Role.USER,
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

    override fun toString(): String {
        return "Member(id=$id, email=$email, name=$name, role=$role)"
    }
}
