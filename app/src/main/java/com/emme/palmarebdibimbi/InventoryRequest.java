package com.emme.palmarebdibimbi;

public class InventoryRequest {
    public String codice;
    public int quantita;
    public String negozio;
    public String ubicazione;
    public int anno;
    public int numeroSparata;

    public InventoryRequest(String codice, int quantita, String negozio, String ubicazione, int anno, int numeroSparata) {
        this.codice = codice;
        this.quantita = quantita;
        this.negozio = negozio;
        this.ubicazione = ubicazione;
        this.anno = anno;
        this.numeroSparata = numeroSparata;
    }
}
