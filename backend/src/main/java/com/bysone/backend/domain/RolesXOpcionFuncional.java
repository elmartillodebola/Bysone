package com.bysone.backend.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Relación muchos a muchos: Roles × Opciones Funcionales.
 * Define qué opciones tiene acceso cada rol.
 */
@Entity
@Table(name = "roles_x_opcion_funcional")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@IdClass(RolesXOpcionFuncionalId.class)
public class RolesXOpcionFuncional {

    @Id
    @Column(name = "id_rol")
    private Long idRol;

    @Id
    @Column(name = "id_opcion")
    private Long idOpcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", insertable = false, updatable = false)
    private Role rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_opcion", insertable = false, updatable = false)
    private OpcionFuncional opcionFuncional;
}
