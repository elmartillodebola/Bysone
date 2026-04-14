package com.bysone.backend.domain;

import java.io.Serializable;
import java.util.Objects;

public class PerfilPortafolioId implements Serializable {

    private Long perfilInversion;
    private Long portafolioInversion;

    public PerfilPortafolioId() {}

    public PerfilPortafolioId(Long perfilInversion, Long portafolioInversion) {
        this.perfilInversion = perfilInversion;
        this.portafolioInversion = portafolioInversion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PerfilPortafolioId)) return false;
        PerfilPortafolioId that = (PerfilPortafolioId) o;
        return Objects.equals(perfilInversion, that.perfilInversion) &&
               Objects.equals(portafolioInversion, that.portafolioInversion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(perfilInversion, portafolioInversion);
    }
}
