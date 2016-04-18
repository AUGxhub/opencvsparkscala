package algorithm.bagofword;

/**
 * Created by augta on 2016/3/30.
 */
public class Texto {
    private double[] valoresAtributos;
    private String classe;

    Texto(double valoresAtributos[], String nomeClasse) {
        this.valoresAtributos = valoresAtributos;
        this.classe = nomeClasse;
    }

    public double[] getValoresAtributos() {
        return valoresAtributos;
    }

    public void setValoresAtributos(double[] valoresAtributos) {
        this.valoresAtributos = valoresAtributos;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

}
