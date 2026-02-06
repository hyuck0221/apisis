package com.hshim.apisis.api.escape.entity

import com.hshim.apisis.api.base.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "escape_theme")
data class EscapeTheme(
    @Id
    val refId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escape_cafe")
    var escapeCafe: EscapeCafe,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = true, columnDefinition = "TEXT")
    var description: String?,

    @Column(nullable = false)
    var isOpen: Boolean,

    @Column(nullable = false)
    var photoUrl: String,

    @Column(nullable = false)
    var playtime: Int,

    @Column(nullable = false)
    var price: Int,

    @Column(nullable = false)
    var difficulty: Double,

    @Column(nullable = false)
    var fear: Double,

    @Column(nullable = false)
    var activity: Double,

    @Column(nullable = false)
    var satisfy: Double,

    @Column(nullable = false)
    var problem: Double,

    @Column(nullable = false)
    var story: Double,

    @Column(nullable = false)
    var interior: Double,

    @Column(nullable = false)
    var act: Double,

) : BaseTimeEntity() {
    @OneToMany(targetEntity = EscapeReview::class, mappedBy = "escapeTheme", cascade = [CascadeType.ALL])
    var reviews: MutableSet<EscapeReview> = mutableSetOf()

    val review: EscapeReview?
        get() = reviews.firstOrNull()
}