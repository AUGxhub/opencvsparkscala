package algorithm.bagofword;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by augta on 2016/3/30.
 */
public class BagOfWord {

    public BagOfWord(int qtdeAtributos) {
        this.qtdeAtributos = qtdeAtributos;
        bagOfWord = new ArrayList<>();
        this.atributos = new ArrayList<>();
        this.classes = new ArrayList<>();
    }

    private final int qtdeAtributos;
    private final ArrayList<Texto> bagOfWord;
    private final List<Atributo> atributos;
    private final List<String> classes;

    public ArrayList<Texto> getBagOfWord() {
        return bagOfWord;
    }


    public void addValoresDosAtributos(double atributos[], String nomeClasse) {
        if (atributos.length != qtdeAtributos) {
            throw new RuntimeException("quantidade de atributos errada");
        }


        //usar arrayClone porque array sao passados por referencia em java
        bagOfWord.add(new Texto((double[]) atributos.clone(), nomeClasse));

    }

    public void addAtributo(String nome, String tipo) {
        this.atributos.add(new Atributo(nome, tipo));
    }

    public void addListAtributos(List<Atributo> atributos) {
        this.atributos.addAll(atributos);
    }

    public void addClasse(String nomeClasse) {
        this.classes.add(nomeClasse);
    }

    public void addListClasses(List<String> classes) {
        this.classes.addAll(classes);
    }

    public void createArffFile(String nome) {
        StringBuilder sb = new StringBuilder();
        sb.append("@RELATION Tabela_de_Resultados\n\n");
        for (Atributo atributo : atributos) {
            sb.append("@ATTRIBUTE ").append(atributo.getNome()).append(" ").append(atributo.getTipo()).append("\n");
        }
        sb.append("@ATTRIBUTE classe {");
        sb.append(classes.toString().replace("[", "").replace("]", ""));
        sb.append("}\n\n");
        sb.append("@DATA\n\n");
        for (Texto texto : bagOfWord) {
            sb.append(Arrays.toString(texto.getValoresAtributos()).replace("[", "").replace("]", ""));
            sb.append(",").append(texto.getClasse()).append("\n");
        }

        new Arquivo().escreverArquivo(nome + ".arff", sb.toString());

    }
}