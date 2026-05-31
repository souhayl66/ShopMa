package com.shopma.app.models;

public class Commande {
    private int id;
    private String date;
    private int nbArticles;
    private double montantTotal;
    private String statut; // "en_cours" or "livree"

    public Commande() {}

    public Commande(String date, int nbArticles, double montantTotal, String statut) {
        this.date = date;
        this.nbArticles = nbArticles;
        this.montantTotal = montantTotal;
        this.statut = statut;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getNbArticles() { return nbArticles; }
    public void setNbArticles(int nbArticles) { this.nbArticles = nbArticles; }

    public double getMontantTotal() { return montantTotal; }
    public void setMontantTotal(double montantTotal) { this.montantTotal = montantTotal; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public boolean isEnCours() {
        return "en_cours".equals(statut);
    }
}
