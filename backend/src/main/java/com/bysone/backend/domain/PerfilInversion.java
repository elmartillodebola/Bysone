package com.bysone.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "perfiles_inversion")
@Getter @Setter @NoArgsConstructor
public class PerfilInversion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_perfil_inversion")
    private Long id;

    @Column(name = "nombre_perfil_inversion", nullable = false, length = 100)
    private String nombrePerfil;

    @OneToMany(mappedBy = "perfilInversion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PerfilPortafolio> portafolios = new ArrayList<>();

    @OneToMany(mappedBy = "perfilInversion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FormulaExposicion> formulasExposicion = new ArrayList<>();
}
