package algorithm.bagofword;

/**
 * Created by augta on 2016/3/30.
 */
public class Atributo {
    private final String nome;
    private final String tipo;

    public Atributo(String nome, String tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }
}