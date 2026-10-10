package ma.projet.classes;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LigneCommandeProduitPK implements Serializable {

    private int commande;
    private int produit;

    public LigneCommandeProduitPK() {
    }

    public LigneCommandeProduitPK(int commande, int produit) {
        this.commande = commande;
        this.produit = produit;
    }

    public int getCommande() {
        return commande;
    }

    public void setCommande(int commande) {
        this.commande = commande;
    }

    public int getProduit() {
        return produit;
    }

    public void setProduit(int produit) {
        this.produit = produit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LigneCommandeProduitPK that = (LigneCommandeProduitPK) o;
        return commande == that.commande && produit == that.produit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(commande, produit);
    }

    @Override
    public String toString() {
        return "LigneCommandeProduitPK{" +
                "commande=" + commande +
                ", produit=" + produit +
                '}';
    }
}
